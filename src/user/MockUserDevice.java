package user;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import model.interfaces.ICoordinates;
import model.interfaces.IGPSObserver;
import model.interfaces.INodePath;
import model.interfaces.SpatialBloomFilter;
import model.interfaces.msg.ICellReachedMsg;
import model.interfaces.msg.ICheckFilterMsg;
import model.interfaces.msg.IPathAckMsg;
import model.interfaces.msg.IRequestBestPathMsg;
import model.interfaces.msg.IRequestBestPathWithFilterMsg;
import model.interfaces.msg.IResponsePathMsg;
import model.interfaces.msg.ISBFMsg;
import model.interfaces.msg.ITravelTimeAckMsg;
import model.msg.CellReachedMsg;
import model.msg.CheckFilterMsg;
import model.msg.PathAckMsg;
import model.msg.RequestBestPathMsg;
import model.msg.RequestBestPathWithFilterMsg;
import model.msg.TravelTimeAckMsg;
import utils.gps.GpsMock;
import utils.http.HttpUtils;
import utils.json.JSONMessagingUtils;
import utils.messaging.MessagingUtils;
import utils.mom.MomUtils;
import utils.sbf.SBFUtils;
import view.MockUserFrame;

public class MockUserDevice extends Thread implements IGPSObserver {
	private ConnectionFactory factory;
	private String brokerAddress;
	private String userID;
	private INodePath chosenPath;
	private String startNode;
	private String endNode;
	private String currentNode;
	private MockUserFrame view;
	private int currentIndex;
	private long timerValue;
	private GpsMock gps;
	private List<String> filterNodes;
	private List<Integer> filterTimes;

	public MockUserDevice(String userID){
		this.userID = userID;
		this.filterNodes = new ArrayList<>();
		this.filterTimes = new ArrayList<>();
		/*this.view = new MockUserFrame(this);
		this.view.setVisible(true);*/
	}
	
	public void setStartNode(String startNode) {
		this.startNode = startNode;
		this.currentNode = startNode;
	}


	public void setEndNode(String endNode) {
		this.endNode = endNode;
	}

	@Override
	public void run() {
		this.currentIndex = 0;
		this.requestPaths(this.startNode, this.endNode);
	}



	private void requestPaths(String startNode, String endNode) {
		IRequestBestPathMsg requestMsg = new RequestBestPathMsg(MessagingUtils.REQUEST_BEST_PATH, startNode, endNode, this.userID, "");
		try {
			String requestPathString = JSONMessagingUtils.getStringfromRequestBestPathMsg(requestMsg);
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
	 * The RabbitMQ channel is initialized
	 */
	private Channel initChannel() throws IOException, TimeoutException {
		this.factory = new ConnectionFactory();
		this.factory.setHost(brokerAddress);
		Connection connection = this.factory.newConnection();
		//System.out.println("Connected");
		Channel channel = connection.createChannel();
		//System.out.println("Created");
		channel.queueDeclare(this.userID, false, false, false, null);
		//System.out.println("DECLARED");
		return channel;
	}
	
	/**
	 * a response path msg is received.
	 * @param msg the message received
	 */
	private void handleResponsePathMsg(String msg)
			throws JSONException, UnsupportedEncodingException, IOException, TimeoutException {
		if(this.chosenPath!=null){
			this.gps.stopGPS();
		}
		System.out.println("[User "+this.userID+" received: "+msg);
		IResponsePathMsg message = JSONMessagingUtils.getResponsePathMsgFromString(msg);
		List<INodePath> paths;
		paths = message.getPaths();
		//this.userID = message.getUserID();
		this.brokerAddress = message.getBrokerAddress();
		this.chosenPath = paths.get(0);
		System.out.println("CHOSEN PATH: ");
		this.chosenPath.printPath();
		System.out.println(this.chosenPath.getCondensedPath());
		//this.view.setResponsePath(this.chosenPath);
		try {
			this.timerValue = System.currentTimeMillis();
			this.startReceiving();
			//this.sendAckToNodes();
			this.gps = new GpsMock(this.chosenPath); //the GPS signal is Mock
			this.gps.attachObserver(this);
			this.gps.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * an ack is sent to the nodes of the chosen path.
	 */
	private void sendAckToNodes() throws JSONException, UnsupportedEncodingException, IOException, TimeoutException{
		int travelTime = 0;
		this.chosenPath.printPath();
		for(int i = 0; i < this.chosenPath.getPathNodes().size()-1; i++){
			IPathAckMsg ackMsgToNode = new PathAckMsg(this.userID, MessagingUtils.PATH_ACK, travelTime, this.chosenPath.getPathNodes().get(i+1));
			String ackToSend = JSONMessagingUtils.getStringfromPathAckMsg(ackMsgToNode);
			MomUtils.sendMsg(this.factory, this.chosenPath.getPathNodes().get(i).getNodeID(), ackToSend);
			travelTime += this.chosenPath.getTravelTimes().get(i);
		}
	}
	
	
	/**
	 * a sbf msg is received.
	 * @param msg the message received
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws TimeoutException 
	 */
	private void handleSBFMsg(String msg) throws JSONException, IOException, TimeoutException {
		System.out.println("SBF MSG");
		ISBFMsg message = JSONMessagingUtils.getSBFMsgFromString(msg);
		SpatialBloomFilter sbf = SpatialBloomFilter.INSTANCE;
		try {
			this.saveHashSalt(message.getHashSalt(), "saltsbf.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		sbf.CreateSBF(message.getBitMapping(), message.getHashFamily(), message.getHashNumber(), message.getAreaNumber(), "saltsbf.txt");
    	int i = 0;
		for(int elem : message.getSBF()){
    		sbf.InsertArea(i, elem);
    		i++;
    	}
		this.checkSBF(sbf);
	}
	
	private void saveHashSalt(String salt, String path) throws FileNotFoundException{
		PrintWriter out = new PrintWriter(path);
		out.print(salt);
		out.close();
	}
	
	/**
	 * checks if the current path of this user crosses the path of the sbf
	 * @param sbf
	 * @throws JSONException 
	 * @throws IOException 
	 * @throws TimeoutException 
	 */
	private void checkSBF(SpatialBloomFilter sbf) throws IOException, JSONException, TimeoutException{
		System.out.println("CHECKING SBF");
		int i = 0;
		int check;
		String checks = "";
		check = sbf.Check(this.chosenPath.getPathNodes().get(i).getNodeID(), this.chosenPath.getPathNodes().get(i).getNodeID().length());
		if(check!=0){
			System.out.println("POSITIVE SBF CHECK: "+this.chosenPath.getPathNodes().get(i).getNodeID());
			checks += this.chosenPath.getPathNodes().get(i).getNodeID()+"/";
			filterNodes.add(this.chosenPath.getPathNodes().get(i).getNodeID());
			filterTimes.add(check);
		}
		for(List<String> l : this.chosenPath.getBetweenNodes()){
			for(String s : l){
				check = sbf.Check(s, s.length());
				if(check!=0){
					System.out.println("POSITIVE SBF CHECK: "+s);
					checks += s+"/";
				}
			}
			i++;
			check = sbf.Check(this.chosenPath.getPathNodes().get(i).getNodeID(), this.chosenPath.getPathNodes().get(i).getNodeID().length());
			if(check!=0){
				System.out.println("POSITIVE SBF CHECK: "+this.chosenPath.getPathNodes().get(i).getNodeID());
				checks += this.chosenPath.getPathNodes().get(i).getNodeID()+"/";
				filterNodes.add(this.chosenPath.getPathNodes().get(i).getNodeID());
				filterTimes.add(check);
			}
		}
		if(checks.length()>0){
			String chk = checks.substring(0, checks.length()-1);
			IRequestBestPathWithFilterMsg rbpmsg = new RequestBestPathWithFilterMsg(MessagingUtils.REQUEST_BEST_PATH_FILTER, this.currentNode, this.endNode, this.userID, "time", filterNodes, filterTimes);
			this.handleResponsePathMsg(HttpUtils.POST(JSONMessagingUtils.getStringfromRequestBestPathWithFilterMsg(rbpmsg)));
			ICheckFilterMsg msg = new CheckFilterMsg(MessagingUtils.FILTER_CHECK, chk);
			HttpUtils.POST(JSONMessagingUtils.getStringFromCheckFilterMsg(msg));
		}
	}
	
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
	
	@Override
	public void cellReached(String cellId) {
		ICellReachedMsg msg = new CellReachedMsg(MessagingUtils.CELL_REACHED, cellId, this.userID);
		try {
			String toSend = JSONMessagingUtils.getStringFromCellReachedMsg(msg);
			HttpUtils.POST(toSend);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void nodeReached(String nodeId) {
		this.currentNode = nodeId;
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
	
	private void switchArrivedMsg(String msg) throws UnsupportedEncodingException, IOException, TimeoutException {
		try {
			int n = MessagingUtils.getIntId(msg);
			switch (n) {
			case 4:
				handleResponsePathMsg(msg);
				break;
			case 7:
				handleSBFMsg(msg);
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
