package user;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;

import com.rabbitmq.client.*;

import model.NodePath;
import model.Pair;
import model.interfaces.ICoordinates;
import model.interfaces.IGPSObserver;
import model.interfaces.IInfrastructureNode;
import model.interfaces.INodePath;
import model.interfaces.msg.IPathAckMsg;
import model.interfaces.msg.IRequestPathMsg;
import model.interfaces.msg.IRequestTravelTimeMsg;
import model.interfaces.msg.IResponsePathMsg;
import model.interfaces.msg.IResponseTravelTimeMsg;
import model.interfaces.msg.ITravelTimeAckMsg;
import model.msg.PathAckMsg;
import model.msg.RequestPathMsg;
import model.msg.RequestTravelTimeMsg;
import model.msg.TravelTimeAckMsg;
import utils.gps.GpsMock;
import utils.http.HttpUtils;
import utils.json.JSONMessagingUtils;
import utils.messaging.MessagingUtils;
import utils.mom.MomUtils;

public class UserDevice extends Thread implements IGPSObserver {

	private String userID;
	private String brokerAddress;
	private ConnectionFactory factory;
	private Integer travelID;
	private List<Pair<INodePath, Integer>> pathsWithTravelID;
	private List<Pair<Integer, Integer>> travelTimes;
	private INodePath chosenPath;
	private IInfrastructureNode start;
	private IInfrastructureNode end;
	private int currentIndex;
	private long timerValue;
	private List<Integer> prefixedTimes;
	private boolean testLearning;
	private String respMsgTest;
	private int startingDelay;

	/**
	 * this constructor is used to create a User which goal is to travel from start to end. The user
	 * sends a Request Path msg to the server.
	 * @param start
	 * @param end
	 */
	public UserDevice(IInfrastructureNode start, IInfrastructureNode end){
		this.travelID = 0;
		this.userID = "newuser";
		this.chosenPath = new NodePath(new ArrayList<>());
		this.pathsWithTravelID = new ArrayList<>();
		this.start = start;
		this.end = end;
		this.testLearning = false;
		this.startingDelay = 0;
	}
	
	/**
	 * this constructor is used to create a User which goal is to travel from start to end. The user
	 * sends a Request Path msg to the server. The user moves through the path after a delay (startingDelay) and
	 * with the given times (prefixedTimes)
	 * @param start
	 * @param end
	 * @param prefixedTimes
	 * @param startingDelay
	 */
	public UserDevice(IInfrastructureNode start, IInfrastructureNode end, List<Integer> prefixedTimes, int startingDelay){
		this.travelID = 0;
		this.userID = "newuser";
		this.chosenPath = new NodePath(new ArrayList<>());
		this.pathsWithTravelID = new ArrayList<>();
		this.start = start;
		this.end = end;
		this.prefixedTimes = prefixedTimes;
		this.testLearning = false;
		this.startingDelay = startingDelay;
	}
	
	/**
	 * this constructor is used to create a User that moves through a prefixed path. The user does not send a request path
	 * msg to the server. The user moves through the path after a delay (startingDelay) and
	 * with the given times (prefixedTimes)
	 * @param start
	 * @param end
	 * @param prefixedTimes
	 * @param msg
	 * @param startingDelay
	 */
	public UserDevice(IInfrastructureNode start, IInfrastructureNode end, List<Integer> prefixedTimes, String msg, int startingDelay){
		this.travelID = 0;
		this.userID = "newuser";
		this.chosenPath = new NodePath(new ArrayList<>());
		this.pathsWithTravelID = new ArrayList<>();
		this.start = start;
		this.end = end;
		this.prefixedTimes = prefixedTimes;
		this.testLearning = true;
		this.respMsgTest = msg;
		this.startingDelay = startingDelay;
	}
	
	/**
	 * The RabbitMQ channel is initialized
	 */
	private Channel initChannel() throws IOException, TimeoutException {
		this.factory = new ConnectionFactory();
		this.factory.setHost(brokerAddress);
		Connection connection = this.factory.newConnection();
		Channel channel = connection.createChannel();
		channel.queueDeclare(this.userID, false, false, false, null);
		return channel;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(this.startingDelay*1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		this.travelTimes = new ArrayList<Pair<Integer, Integer>>();
		this.currentIndex = 0;
		if(this.testLearning){ //this is a learning test. The user moves through a prefixed path with prefixed times.
			try {
				this.handleResponsePathMsg(this.respMsgTest);
			} catch (JSONException | IOException | TimeoutException e) {
				e.printStackTrace();
			}
		} else{               //this is not a learning test. The user first requests the path to the server.
			this.requestPaths(start, end);
		}
		
	}
	
	private void startReceiving() throws IOException, TimeoutException {
		Channel channel = null;
		try {
			channel = initChannel();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.println(" [User] "+userID+" received  '" + message + "'");
				try {
					switchArrivedMsg(message);
				} catch (TimeoutException e) {
					e.printStackTrace();
				}
			}
		};

		try {
			channel.basicConsume(this.userID, true, consumer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * the paths are requested to the server
	 * @param start
	 * @param end
	 */
	private void requestPaths(IInfrastructureNode start, IInfrastructureNode end) {
		IRequestPathMsg requestMsg = new RequestPathMsg(MessagingUtils.REQUEST_PATH, this.start, this.end, this.userID);
		try {
			String requestPathString = JSONMessagingUtils.getStringfromRequestPathMsg(requestMsg);
			this.handleResponsePathMsg(HttpUtils.POST(requestPathString));
			//System.out.println(requestPathString);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}

	/**
	 * when all the response travel time messages have been received, the best path is evaluated.
	 * @return the best path among the ones received.
	 */
	private INodePath evaluateBestPath() {
		int min = Integer.MAX_VALUE;
		int minTravelID = -1;
		INodePath bestPath = null;
		for (Pair<Integer, Integer> p : this.travelTimes) {
			if (p.getSecond() < min) {
				min = p.getSecond();
				minTravelID = p.getFirst();
			}
		}
		for (Pair<INodePath, Integer> p : this.pathsWithTravelID) {
			if (p.getSecond() == minTravelID) {
				bestPath = p.getFirst();
			}
		}
		this.timerValue = System.currentTimeMillis();
		this.travelID = minTravelID;
		return bestPath;
	}

	private void switchArrivedMsg(String msg) throws UnsupportedEncodingException, IOException, TimeoutException {
		try {
			int n = MessagingUtils.getIntId(msg);
			switch (n) {
			case 1:
				//handlePathAckMsg(msg);
				break;
			case 4:
				handleResponsePathMsg(msg);
				break;
			case 5:
				//handleResponseTravelTimeMsg(msg);
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * a path ack msg is received. This means that the coordinates are received from the server
	 * @param msg the message received
	 */
	/*private void handlePathAckMsg(String msg) throws JSONException {
		System.out.println("[User "+this.userID+" received: "+msg);
		IPathAckMsg message = JSONMessagingUtils.getPathAckWithCoordinatesMsgFromString(msg);
		this.chosenPath = message.getPath();
		INodePath path = new NodePath(new ArrayList<>(this.chosenPath.getPathNodes()));
		GpsMock gps = new GpsMock(path, this.prefixedTimes); //the GPS signal is Mock
		gps.attachObserver(this);
		gps.start();
	}*/

	/**
	 * a response path msg is received.
	 * @param msg the message received
	 */
	private void handleResponsePathMsg(String msg)
			throws JSONException, UnsupportedEncodingException, IOException, TimeoutException {
		System.out.println("[User "+this.userID+" received: "+msg);
		IResponsePathMsg message = JSONMessagingUtils.getResponsePathMsgFromString(msg);
		List<INodePath> paths;
		paths = message.getPaths();
		this.userID = message.getUserID();
		this.brokerAddress = message.getBrokerAddress();
		try {
			this.startReceiving();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int j = 0; j < paths.size(); j++) {
			this.pathsWithTravelID.add(new Pair<INodePath, Integer>(paths.get(j), j));
		}
		for (int i = 0; i < paths.size(); i++) {
			IRequestTravelTimeMsg requestMsg = new RequestTravelTimeMsg(userID, MessagingUtils.REQUEST_TRAVEL_TIME, 0,
					paths.get(i), i, false);
			String toSend = JSONMessagingUtils.getStringfromRequestTravelTimeMsg(requestMsg);
			MomUtils.sendMsg(factory, paths.get(i).getPathNodes().get(0).getNodeID(), toSend);
		}
	}
	
	/**
	 * a response travel time msg is received.
	 * @param msg the message received
	 */
	/*private void handleResponseTravelTimeMsg(String msg) throws JSONException {
		IResponseTravelTimeMsg message = JSONMessagingUtils.getResponseTravelTimeMsgFromString(msg);
		this.travelID = message.getTravelID();
		int time = message.getTravelTime();
		if(message.frozenDanger()){
			System.out.println("Frozen Danger on path number "+message.getTravelID());
		}
		this.travelTimes.add(new Pair<Integer, Integer>(this.travelID, time));
		System.out.println("[User "+this.userID+" PathsWithTravelID size:"+ this.pathsWithTravelID.size());
		if(this.travelTimes.size()==this.pathsWithTravelID.size()){
			System.out.println("[User "+this.userID+": All times received");
			this.chosenPath = this.evaluateBestPath();
			this.requestCoordinates();
			try {
				this.sendAckToNode();
			} catch (IOException | TimeoutException e) {
				e.printStackTrace();
			}
		}
	}*/
	
	/**
	 * an ack is sent to the first node of the chosen path.
	 */
	/*private void sendAckToNode() throws JSONException, UnsupportedEncodingException, IOException, TimeoutException{
		IPathAckMsg ackMsgToNode = new PathAckMsg(this.userID, MessagingUtils.PATH_ACK, this.chosenPath, this.travelID);
	    String ackToSend = JSONMessagingUtils.getStringfromPathAckMsg(ackMsgToNode);
	    MomUtils.sendMsg(this.factory, this.chosenPath.getPathNodes().get(0).getNodeID(), ackToSend);
	}*/
	
	/**
	 * method invoked when the user is next to the next node of the chosen path
	 * @param time
	 */
	private void nearNextNode(int time) throws JSONException, UnsupportedEncodingException, IOException, TimeoutException{
		ITravelTimeAckMsg msg = new TravelTimeAckMsg(this.userID, MessagingUtils.TRAVEL_TIME_ACK,
				chosenPath.getPathNodes().get(this.currentIndex), this.chosenPath.getPathNodes().get(this.currentIndex + 1), time);
		String travelTimeAck = JSONMessagingUtils.getStringfromTravelTimeAckMsg(msg);
		MomUtils.sendMsg(this.factory, this.chosenPath.getPathNodes().get(this.currentIndex).getNodeID(), travelTimeAck);
		this.currentIndex++;
	}

	@Override
	public void notifyGps(ICoordinates coordinates) {		//we always check the next node. If the signal is lost, the range is too small.
		if(this.chosenPath.getPathNodes().get(this.currentIndex+1).getCoordinates().isCloseEnough(coordinates)){
			int time = (int) (System.currentTimeMillis()-this.timerValue);
			time/=1000;
			this.timerValue = System.currentTimeMillis();
			try {
				this.nearNextNode(time);
			} catch (JSONException | IOException | TimeoutException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * coordinates are requested to the server
	 */
	/*private void requestCoordinates(){
		IPathAckMsg pathAckMsg = new PathAckMsg(this.userID, MessagingUtils.PATH_ACK, this.chosenPath, this.travelID);
		try {
			String pathAckString = JSONMessagingUtils.getStringfromPathAckMsg(pathAckMsg);
			this.handlePathAckMsg(HttpUtils.POST(pathAckString));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
	
	public String toString(){
		String stringTimes="";
		for(int i : this.prefixedTimes){
			stringTimes+=i+"  ";
		}
		String m="";
		try {
			IResponsePathMsg r = JSONMessagingUtils.getResponsePathMsgFromString(respMsgTest);
			m+="User ID: "+r.getUserID()+" - Broker Addr: "+r.getBrokerAddress()+" -  Path: ";
			for(IInfrastructureNode n : r.getPaths().get(0).getPathNodes()){
				m+=n.getNodeID()+" - ";
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return m+"- Prefixed Times: "+stringTimes+" - Initial Delay: "+this.startingDelay;
	}

	@Override
	public void cellReached(String cellId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nodeReached(String nodeId) {
		// TODO Auto-generated method stub
		
	}
}
