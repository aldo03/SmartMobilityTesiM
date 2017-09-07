package infrastructure;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import model.CurrentTimes;
import model.ExpectedNumberOfVehicles;
import model.PendingUsers;
import model.TravelTimesByNumberOfVehicles;
import model.UpdateTravelTimesThread;
import model.interfaces.ICurrentTimes;
import model.interfaces.IExpectedNumberOfVehicles;
import model.interfaces.IInfrastructureNode;
import model.interfaces.IInfrastructureNodeImpl;
import model.interfaces.INodePath;
import model.interfaces.IPair;
import model.interfaces.IPendingUsers;
import model.interfaces.ITemperatureHumidityObserver;
import model.interfaces.ITemperatureHumiditySensor;
import model.interfaces.ITravelTimesByNumberOfVehicles;
import model.interfaces.msg.IPathAckMsg;
import model.interfaces.msg.IRequestTravelTimeMsg;
import model.interfaces.msg.IResponseTravelTimeMsg;
import model.interfaces.msg.ITravelTimeAckMsg;
import model.msg.PathAckMsg;
import model.msg.RequestTravelTimeMsg;
import model.msg.ResponseTravelTimeMsg;
import utils.json.JSONMessagingUtils;
import utils.messaging.MessagingUtils;
import utils.mom.MomUtils;
import utils.mongodb.MongoDBUtils;

public class InfrastructureDevice extends Thread implements ITemperatureHumidityObserver{
	private static final int TEMP_THRESHOLD = 3;
	private static final int HUM_THRESHOLD = 30;
	private static final double DEF_TEMP = 25;
	private static final double DEF_HUM = 50;
	
	private String id;
	private Set<IPair<String, List<String>>> nearNodesWeighted;
	private String brokerHost;
	private ConnectionFactory factory;
	private ITravelTimesByNumberOfVehicles travelTimes;
	private IExpectedNumberOfVehicles expectedVehicles;
	private IPendingUsers pendingUsers;
	private ICurrentTimes curTimes;
	private double currentTemperature;
	private double currentHumidity;
	private boolean mock;
	
	public InfrastructureDevice(String id, Set<IPair<String, List<String>>> nearNodesWeighted, String brokerHost, boolean mock) {
		this.id = id;
		this.nearNodesWeighted = nearNodesWeighted;
		this.brokerHost = brokerHost;
		this.mock = mock;
	}
	
	public InfrastructureDevice(IInfrastructureNodeImpl node, String brokerHost, boolean mock){
		this.id = node.getNodeID();
		this.nearNodesWeighted = node.getNearNodesWeighted();
		this.brokerHost = brokerHost;
		this.mock = mock;
	}
	
	/**
	 * Data structures are initialized
	 */
	private void initializeDataStructures(){
		this.travelTimes = new TravelTimesByNumberOfVehicles(this.id);
		this.expectedVehicles = new ExpectedNumberOfVehicles(this.id);
		this.pendingUsers = new PendingUsers();
		this.curTimes = new CurrentTimes(this.id);
		UpdateTravelTimesThread updateThread = new UpdateTravelTimesThread(this.curTimes, this.travelTimes);
		for(IPair<String, List<String>> p : this.nearNodesWeighted){
			this.travelTimes.initTravelTimes(p.getFirst(), p.getSecond().size());
			this.expectedVehicles.initVehicles(p.getFirst());
			this.curTimes.initTimes(p.getFirst());
			updateThread.initNode(p.getFirst());
		}
		this.currentTemperature = DEF_TEMP;
		this.currentHumidity = DEF_HUM;
		MongoDBUtils.initTempHum(this.id, this.currentTemperature, this.currentHumidity);
		ITemperatureHumiditySensor sensor = null;
		if(this.mock){
			sensor = new TemperatureHumiditySensorMock(22, 30);
		} else{
			sensor = new TemperatureHumiditySensor();
		}
		TemperatureHumidityThread sensorThread = new TemperatureHumidityThread(sensor);
		sensorThread.attachObserver(this);
		sensorThread.start();
		updateThread.start();
	}
	

	@Override
	public void run() {
		try {
			System.out.println("[Infrastructure Device "+this.id+" started... Initializing data structures");
			this.initializeDataStructures();
			System.out.println("[Infrastructure Device "+this.id+": Initialization DONE");
			Channel channel = initChannel();
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {
					String message = new String(body, "UTF-8");
					System.out.println("[Infrastructure Device "+id+" received "+message);
					try {
						switchArrivedMessage(message);
					} catch (TimeoutException e) {
						e.printStackTrace();
					}
				}
			};
			channel.basicConsume(this.id, true, consumer);
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}

	private Channel initChannel() throws IOException, TimeoutException {
		this.factory = new ConnectionFactory();
		this.factory.setHost(this.brokerHost);
		Connection connection = this.factory.newConnection();
		Channel channel = connection.createChannel();
		channel.queueDeclare(this.id, false, false, false, null);
		return channel;
	}

	private void switchArrivedMessage(String message)
			throws UnsupportedEncodingException, IOException, TimeoutException {
		try {
			int num = MessagingUtils.getIntId(message);
			switch (num) {
			case 1:
				handlePathAckMsg(message);
				break;
			case 3:
				handleRequestTravelTimeMsg(message);
				break;
			case 6:
				handleTravelTimeAckMsg(message);
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * a path ack msg is received
	 * @param message the message received
	 */
	private void handlePathAckMsg(String message) throws JSONException, UnsupportedEncodingException, IOException, TimeoutException {
		IPathAckMsg msg = JSONMessagingUtils.getPathAckMsgFromString(message);
		//At this point, sets the user among the ones that are going to move across a certain path
		int travelTime = msg.getTravelTime();
		this.expectedVehicles.addVehicle(msg.getNextNode().getNodeID(), travelTime);
	}

	/**
	 * a request travel time msg is received
	 * @param message the message received
	 */
	private void handleRequestTravelTimeMsg(String message)
			throws JSONException, UnsupportedEncodingException, IOException, TimeoutException {
		IRequestTravelTimeMsg msg = JSONMessagingUtils.getRequestTravelTimeMsgFromString(message);
		INodePath path = msg.getPath();
		path.removeFirstNode();					 //The path is forwarded without the current node
		if (path.getPathNodes().size() > 0) {    //This is not the last node of the path
			IInfrastructureNode nextNode = path.getPathNodes().get(0);
			int totalTime = msg.getCurrentTravelTime() + getTravelTime(nextNode, msg.getCurrentTravelTime());
			//the user is added to the pending users
			this.pendingUsers.addPendingUser(msg.getUserID(), msg.getTravelID(), totalTime);
			boolean frozenDanger = msg.frozenDanger();
			if(this.currentTemperature<TEMP_THRESHOLD&&this.currentHumidity>HUM_THRESHOLD){
				frozenDanger = true;
			}
			IRequestTravelTimeMsg msgToSend = new RequestTravelTimeMsg(msg.getUserID(),
					MessagingUtils.REQUEST_TRAVEL_TIME, totalTime, path,
					msg.getTravelID(), frozenDanger);
			String strToSend = JSONMessagingUtils.getStringfromRequestTravelTimeMsg(msgToSend);
			MomUtils.sendMsg(factory, nextNode.getNodeID(), strToSend);
		} else { 								//This is the last node of the path
			IResponseTravelTimeMsg m = new ResponseTravelTimeMsg(MessagingUtils.RESPONSE_TRAVEL_TIME, msg.getCurrentTravelTime(), msg.getTravelID(), msg.frozenDanger());
			String sToSend = JSONMessagingUtils.getStringfromResponseTravelTimeMsg(m);
			MomUtils.sendMsg(factory, msg.getUserID(), sToSend);
		}
	}

	/**
	 * a travel time ack msg is received. The travel time acked is set as one of the current times of the specific path.
	 * @param message the message received
	 */
	private void handleTravelTimeAckMsg(String message) throws JSONException {
		ITravelTimeAckMsg msg = JSONMessagingUtils.getTravelTimeAckMsgFromString(message);
		this.curTimes.addTime(msg.getSecondNode().getNodeID(), msg.getTravelTime());
	}

	/**
	 * 
	 * @param node the target node
	 * @param time
	 * @return the travel time expected from this node to a certain node at a certain time
	 */
	private int getTravelTime(IInfrastructureNode node, int time) {
		int numOfVehiclesExpected = this.expectedVehicles.getVehicles(node.getNodeID(), time);
		int travelTime = this.travelTimes.getTravelTime(node.getNodeID(), numOfVehiclesExpected);
		return travelTime;
	}

	@Override
	public void setTemperature(double temperature) {
		this.currentTemperature = temperature;
		//MongoDBUtils.setTemp(this.id, temperature);
	}

	@Override
	public void setHumidity(double humidity) {
		this.currentHumidity = humidity;
		//MongoDBUtils.setHum(this.id, humidity);
	}


}
