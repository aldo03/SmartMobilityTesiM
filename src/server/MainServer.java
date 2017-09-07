package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.json.JSONException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import edu.asu.emit.qyan.alg.control.YenTopKShortestPathsAlg;
import edu.asu.emit.qyan.alg.model.Graph;
import edu.asu.emit.qyan.alg.model.Path;
import edu.asu.emit.qyan.alg.model.Vertex;
import edu.asu.emit.qyan.alg.model.abstracts.BaseVertex;
import model.InfrastructureNode;
import model.NodePath;
import model.interfaces.IInfrastructureNode;
import model.interfaces.IInfrastructureNodeImpl;
import model.interfaces.INodePath;
import model.interfaces.IPair;
import model.interfaces.msg.ICheckFilterMsg;
import model.interfaces.msg.ICreateMockUserMsg;
import model.interfaces.msg.IPathAckMsg;
import model.interfaces.msg.IRequestBestPathMsg;
import model.interfaces.msg.IRequestBestPathWithFilterMsg;
import model.interfaces.msg.IRequestPathMsg;
import model.interfaces.msg.IResponsePathMsg;
import model.interfaces.msg.ISendSBFMsg;
import model.msg.PathAckMsg;
import model.msg.ResponsePathMsg;
import user.MockPriorityUser;
import user.MockUserDevice;
import utils.json.JSONMessagingUtils;
import utils.messaging.MessagingUtils;
import utils.sbf.SBFUtils;

/**
 * class that model Server logic
 * 
 * @author BBC
 *
 */

public class MainServer {

	private final static String USER_ID = "User-Device-";
	private final static Integer K_SHORTEST_PATHS = 10;
	private Graph graph;
	private Set<IInfrastructureNodeImpl> nodesSet;
	private Map<String, IInfrastructureNodeImpl> nodeMapId;
	private int userSeed;
	private List<String> requestedPaths;
	private Semaphore s;

	public MainServer() throws Exception {
		this.graph = new Graph();
		this.nodesSet = new HashSet<>();
		this.nodeMapId = new HashMap<>();
		this.userSeed = 10000;
		this.requestedPaths = new ArrayList<>();
		this.s = new Semaphore(0);
		this.initHTTP();
	}

	private void initHTTP() throws Exception{
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/", new HttpHandler(){
        	 @Override
             public void handle(HttpExchange t) throws IOException {
        		 BufferedReader reader = new BufferedReader(new InputStreamReader(t.getRequestBody()));
                 String msg = reader.readLine();
  				 System.out.println("data received: " + msg);
                 try {
                	Thread thread;
                	int n;
 					n = MessagingUtils.getIntId(msg);
 					switch (n) {
 					/*case 1:
 						thread = new Thread() {
 							@Override
 							public void run() {
 								try {
 									handlePathAckMsg(t, msg);
 								} catch (JSONException e) {
 									e.printStackTrace();
 								} catch (IOException e) {
									e.printStackTrace();
								}
 							}

 						};
 						thread.start();
 						break;*/
 					case 2:
 						thread = new Thread() {
 							@Override
 							public void run() {
 								try {
 									handleRequestPathMsg(t, msg);
 								} catch (JSONException e) {
 									e.printStackTrace();
 								} catch (IOException e) {
									e.printStackTrace();
								}
 							}

 						};
 						thread.start();
 						break;
 					case 8:
 						thread = new Thread() {
 							@Override
 							public void run() {
 								try {
 									handleRequestBestPathMsg(t, msg);
 								} catch (JSONException e) {
 									e.printStackTrace();
 								} catch (IOException e) {
									e.printStackTrace();
								}
 							}

 						};
 						thread.start();
 						break;
 					case 9:
 						thread = new Thread() {
 							@Override
 							public void run() {
 								try {
									s.acquire();
									getRequestedPathAndSendResponse(t);
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}
 							}
 						};
 						thread.start();
 						break;
 					case 10:
 						handleSendSBFMsg(t, msg);
 						break;
 					case 11:
 						handleCreateUserMsg(t, msg);
 						break;
 					case 12:
 						handleCheckFilterMsg(t, msg);
 						break;
 					case 13:
 						handleCellReachedMsg(t, msg);
 						break;
 					case 14:
 						handleRequestBestPathWithFilterMsg(t, msg);
 						break;
 					}
 				} catch (JSONException e) {
 					e.printStackTrace();
 				}
        	 }
        });
        server.setExecutor(null); // creates a default executor
        server.start();
	}
	
	private void getRequestedPathAndSendResponse(HttpExchange t) {
		String response = requestedPaths.get(0);
		requestedPaths.remove(0);
		try {
			t.sendResponseHeaders(200, response.length());
			OutputStreamWriter os = new OutputStreamWriter(t.getResponseBody());
			os.write(response);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * a Request Path msg is received
	 * @param t
	 * @param msg the message received
	 */
	private void handleRequestPathMsg(HttpExchange t, String msg) throws JSONException, IOException{
		IRequestPathMsg requestPathMsg = JSONMessagingUtils.getRequestPathMsgFromString(msg);
		List<INodePath> pathList = this.getShortestPaths(this.findNearNode(requestPathMsg.getStartingNode()),this.findNearNode(
				requestPathMsg.getEndingNode()));
		String brokerAddress = this.getBrokerAddress(requestPathMsg.getStartingNode(), requestPathMsg.getEndingNode());
		IResponsePathMsg responsePathMsg = new ResponsePathMsg(MessagingUtils.RESPONSE_PATH, this.generateUserID(requestPathMsg.getUserID()),
				pathList, brokerAddress);
		String response = JSONMessagingUtils.getStringfromResponsePathMsg(responsePathMsg);
		t.sendResponseHeaders(200, response.length());
		OutputStreamWriter os = new OutputStreamWriter(t.getResponseBody());
		os.write(response);
		os.close();
		
	}
	
	/**
	 * a Request Path msg is received
	 * @param t
	 * @param msg the message received
	 */
	private void handleRequestBestPathMsg(HttpExchange t, String msg) throws JSONException, IOException{
		IRequestBestPathMsg requestPathMsg = JSONMessagingUtils.getRequestBestPathMsgFromString(msg);
		IInfrastructureNode n1 = new InfrastructureNode(requestPathMsg.getStartingNode());
		IInfrastructureNode n2 = new InfrastructureNode(requestPathMsg.getEndingNode());
		List<INodePath> pathList = this.getShortestPath(this.findNearNode(n1),this.findNearNode(n2));
		String pathInfo = JSONMessagingUtils.getStringFromRequestInfo(pathList.get(0).getCondensedPath(), requestPathMsg.getUserID(), requestPathMsg.getTimeAndDay());
		this.requestedPaths.add(pathInfo);
		s.release();
		String brokerAddress = this.getBrokerAddress(n1, n2);
		IResponsePathMsg responsePathMsg = new ResponsePathMsg(MessagingUtils.RESPONSE_PATH, requestPathMsg.getUserID(),
				pathList, brokerAddress);
		String response = JSONMessagingUtils.getStringfromResponsePathMsg(responsePathMsg);
		t.sendResponseHeaders(200, response.length());
		OutputStreamWriter os = new OutputStreamWriter(t.getResponseBody());
		os.write(response);
		os.close();
		
	}
	
	private void handleRequestBestPathWithFilterMsg(HttpExchange t, String msg) throws JSONException, IOException{
		IRequestBestPathWithFilterMsg requestPathMsg = JSONMessagingUtils.getRequestBestPathWithFilterMsgFromString(msg);
		IInfrastructureNode n1 = new InfrastructureNode(requestPathMsg.getStartingNode());
		IInfrastructureNode n2 = new InfrastructureNode(requestPathMsg.getEndingNode());
		List<String> filterNodes = requestPathMsg.getFilterNodes();
		List<Integer> times = requestPathMsg.getFilterTimes();
		System.out.println("Getting shortest path");
		List<INodePath> pathList = this.getShortestPathWithFilter(this.findNearNode(n1),this.findNearNode(n2), filterNodes);
		String pathInfo = JSONMessagingUtils.getStringFromRequestInfo(pathList.get(0).getCondensedPath(), requestPathMsg.getUserID(), requestPathMsg.getTimeAndDay());
		System.out.println("Path info: "+pathInfo);
		this.requestedPaths.add(pathInfo);
		s.release();
		String brokerAddress = this.getBrokerAddress(n1, n2);
		IResponsePathMsg responsePathMsg = new ResponsePathMsg(MessagingUtils.RESPONSE_PATH, requestPathMsg.getUserID(),
				pathList, brokerAddress);
		String response = JSONMessagingUtils.getStringfromResponsePathMsg(responsePathMsg);
		t.sendResponseHeaders(200, response.length());
		OutputStreamWriter os = new OutputStreamWriter(t.getResponseBody());
		os.write(response);
		os.close();
	}

	/**
	 * a Path Ack msg is received
	 * @param msg the message received
	 */
	/*private void handlePathAckMsg(HttpExchange t, String msg) throws JSONException, IOException {
		IPathAckMsg pathAckMsg = JSONMessagingUtils.getPathAckMsgFromString(msg);
		List<IInfrastructureNode> pathWithCoordinates = new ArrayList<>();
		INodePath pathFromMsg = pathAckMsg.getPath();
		for (IInfrastructureNode node : pathFromMsg.getPathNodes()) {
			for (IInfrastructureNode n : this.nodesSet) {
				if (node.getNodeID().equals(n.getNodeID()))
					pathWithCoordinates.add(n);
			}
		}
		INodePath path = new NodePath(pathWithCoordinates);
		IPathAckMsg coordinatesMsg = new PathAckMsg(pathAckMsg.getUserID(), MessagingUtils.PATH_ACK, path,
				pathAckMsg.getTravelID());
		String response = JSONMessagingUtils.getStringfromPathAckMsg(coordinatesMsg);
		t.sendResponseHeaders(200, response.length());
		OutputStreamWriter os = new OutputStreamWriter(t.getResponseBody());
		os.write(response);
		os.close();
	}*/
	
	
	/**
	 * a Send SBF Msg is received
	 * @param msg the message received
	 * @throws IOException 
	 */
	private void handleSendSBFMsg(HttpExchange t, String msg) throws JSONException, IOException{
		ISendSBFMsg sendSBFMsg = JSONMessagingUtils.getSendSBFMsgFromString(msg);
		String user = sendSBFMsg.getUser();
		String startTime = sendSBFMsg.getStartTime();
		String consensedPath = sendSBFMsg.getCondensedPath();
		MockPriorityUser userp = new MockPriorityUser("localhost");
		List<List<String>> p = new ArrayList<>();
		String[] roads = consensedPath.split("/");
		for(String s : roads){
			List<String> nodes = new ArrayList<>();
			for(String s1 : s.split(",")){
				nodes.add(s1);
			}
			p.add(nodes);
		}
		userp.setReceiver(user);
		userp.setStartTime(startTime);
		userp.setPath(p);
		userp.start();
		String response = "SBF sent";
		t.sendResponseHeaders(200, response.length());
		OutputStreamWriter os = new OutputStreamWriter(t.getResponseBody());
		os.write(response);
		os.close();
	}
	
	/**
	 * a Create User Msg is received
	 * @param msg the message received
	 * @throws JSONException 
	 * @throws IOException 
	 */
	private void handleCreateUserMsg(HttpExchange t, String msg) throws JSONException, IOException {
		ICreateMockUserMsg createMockUserMsg = JSONMessagingUtils.getCreateMockUserMsgFromString(msg);
		String userId = createMockUserMsg.getUserID();
		String startNode = createMockUserMsg.getStartNode();
		String endNode = createMockUserMsg.getEndNode();
		MockUserDevice user = new MockUserDevice(userId);
		user.setStartNode(startNode);
		user.setEndNode(endNode);
		user.start();
		String response = "User created";
		t.sendResponseHeaders(200, response.length());
		OutputStreamWriter os = new OutputStreamWriter(t.getResponseBody());
		os.write(response);
		os.close();
	}
	
	/**
	 * A check filter msg is received
	 * @param t
	 * @param msg
	 * @throws JSONException
	 * @throws IOException
	 */
	private void handleCheckFilterMsg(HttpExchange t, String msg) throws JSONException, IOException {
		this.requestedPaths.add(msg);
		s.release();
		String response = "Check received";
		t.sendResponseHeaders(200, response.length());
		OutputStreamWriter os = new OutputStreamWriter(t.getResponseBody());
		os.write(response);
		os.close();
	}
	
	/**
	 * A cell reached msg is received
	 * @param t
	 * @param msg
	 * @throws IOException 
	 */
	private void handleCellReachedMsg(HttpExchange t, String msg) throws IOException{
		this.requestedPaths.add(msg);
		s.release();
		String response = "Cell reached received";
		t.sendResponseHeaders(200, response.length());
		OutputStreamWriter os = new OutputStreamWriter(t.getResponseBody());
		os.write(response);
		os.close();
	}

	/**
	 * gets the MOM broker address for a certain path
	 * @param startingNode the first node of the path
	 * @param endingNode the last node of the path
	 * @return the MOM broker address for the given path
	 */
	private String getBrokerAddress(IInfrastructureNode startingNode, IInfrastructureNode endingNode) {
		return "localhost";
	}

	private String generateUserID(String string) {
		if(string.equals("newuser")){
			return USER_ID + this.userSeed++;
		} else{
			return string;
		}
	}
	
	private List<INodePath> getShortestPathWithFilter(IInfrastructureNode start, IInfrastructureNode finish, List<String> filterNodes){
		Graph newGraph = new Graph(this.graph);
		System.out.println("FILTER NODES");
		for(int i = 0; i < filterNodes.size()-1; i++){
			int sourceId = SBFUtils.getIntId(filterNodes.get(i));
			int sinkId = SBFUtils.getIntId(filterNodes.get(i+1));
			System.out.println(filterNodes.get(i));
			if(this.graph.get_edge_weight(this.graph.get_vertex(sourceId), this.graph.get_vertex(sinkId))!=Graph.DISCONNECTED)
				newGraph.add_edge(sourceId, sinkId, Double.MAX_VALUE);
			if(this.graph.get_edge_weight(this.graph.get_vertex(sinkId), this.graph.get_vertex(sourceId))!=Graph.DISCONNECTED)
				newGraph.add_edge(sinkId, sourceId, Double.MAX_VALUE);
		}
		YenTopKShortestPathsAlg algorithm = new YenTopKShortestPathsAlg(newGraph);
		BaseVertex startNode = newGraph.get_vertex(SBFUtils.getIntId(start.getNodeID()));
		BaseVertex endNode = newGraph.get_vertex(SBFUtils.getIntId(finish.getNodeID()));
		List<Path> paths = algorithm.get_shortest_paths(startNode, endNode, 1);
		return this.getNodePathFromPath(paths);
	}

	/**
	 * method invoked to obtain a list of shortest path
	 * 
	 * @param start
	 * @param finish
	 * @return list of shortest path
	 */
	private List<INodePath> getShortestPaths(IInfrastructureNode start, IInfrastructureNode finish) {
		YenTopKShortestPathsAlg algorithm = new YenTopKShortestPathsAlg(this.graph);
		BaseVertex startNode = this.graph.get_vertex(SBFUtils.getIntId(start.getNodeID()));
		BaseVertex endNode = this.graph.get_vertex(SBFUtils.getIntId(finish.getNodeID()));
		List<Path> paths = algorithm.get_shortest_paths(startNode, endNode, K_SHORTEST_PATHS);
		return this.getNodePathFromPath(paths);
	}
	
	/**
	 * method invoked to obtain a the shortest path
	 * 
	 * @param start
	 * @param finish
	 * @return shortest path
	 */
	private List<INodePath> getShortestPath(IInfrastructureNode start, IInfrastructureNode finish) {
		YenTopKShortestPathsAlg algorithm = new YenTopKShortestPathsAlg(this.graph);
		BaseVertex startNode = this.graph.get_vertex(SBFUtils.getIntId(start.getNodeID()));
		BaseVertex endNode = this.graph.get_vertex(SBFUtils.getIntId(finish.getNodeID()));
		List<Path> paths = algorithm.get_shortest_paths(startNode, endNode, 1);
		return this.getNodePathFromPath(paths);
	}

	/**
	 * gets Node Paths from Paths
	 * @param paths
	 * @return a list of INodePath
	 */
	private List<INodePath> getNodePathFromPath(List<Path> paths){
		List<INodePath> pathList = new ArrayList<>();
		for(Path path: paths){
			System.out.println(path);
			INodePath nodePath = new NodePath(new ArrayList<>());
			List<IInfrastructureNode> nodeList = new ArrayList<>();
			List<List<String>> betweenNodes = new ArrayList<>();
			List<Integer> travelTimes = new ArrayList<>();
			
			IInfrastructureNodeImpl prec = null;
			BaseVertex precV = null;
			for(BaseVertex vertex: path.get_vertices()){
				String id = SBFUtils.getStringId(vertex.get_id());
				nodeList.add(this.nodeMapId.get(id));
				if(prec!=null && prec.getBetweenNodes(id)!=null){
					betweenNodes.add(prec.getBetweenNodes(id));
					travelTimes.add((int) this.graph.get_edge_weight(precV, vertex));
				}
				prec = this.nodeMapId.get(id);
				precV = vertex;
			}
			nodePath.setPath(nodeList);
			nodePath.setBetweenNodes(betweenNodes);
			nodePath.setTravelTimes(travelTimes);
			pathList.add(nodePath);
		}
		return pathList;
	}
	
	/**
	 * method invoked to set the graph and nodeSet
	 * 
	 * @param nodesSet
	 */
	public void setNodes(Set<IInfrastructureNodeImpl> nodesSet) {
		this.nodesSet = nodesSet;
		for (IInfrastructureNodeImpl node : this.nodesSet) {
			BaseVertex v = new Vertex();
			v.set_id(SBFUtils.getIntId(node.getNodeID()));
			this.graph.add_vertex(v);
			this.nodeMapId.put(node.getNodeID(), node);
		}
		for (IInfrastructureNodeImpl start : this.nodesSet) {
			Integer idStart = SBFUtils.getIntId(start.getNodeID());
			for (IPair<String, List<String>> pair : start.getNearNodesWeighted()) {
				Integer idEnd = SBFUtils.getIntId(pair.getFirst());
				this.graph.add_edge(idStart, idEnd, pair.getSecond().size());
			}
		}
	}
	
	public void setGraph(){
		for (IInfrastructureNodeImpl start : this.nodesSet) {
			//System.out.println("NODE IN SERVER: "+start.getNodeID());
			for(IPair<String, List<String>> p : start.getNearNodesWeighted()){
				//System.out.println("NIEGHBOR: "+p.getFirst());
			}
		}
		for (IInfrastructureNodeImpl start : this.nodesSet) {
			Integer idStart = SBFUtils.getIntId(start.getNodeID());
			//System.out.println("FROM: "+idStart);
			for (IPair<String, List<String>> pair : start.getNearNodesWeighted()) {
				Integer idEnd = SBFUtils.getIntId(pair.getFirst());
				//System.out.println("TO: "+idEnd);
				this.graph.add_edge(idStart, idEnd, pair.getSecond().size());
			}
		}
	}

	/**
	 * method invoked to set a new node in the system
	 * 
	 * @param node
	 */
	public void setNewNode(IInfrastructureNodeImpl node) {
		this.nodesSet.add(node);
		//System.out.println("ADDED NODE: "+node.getNodeID());
		this.nodeMapId.put(node.getNodeID(), node);
		BaseVertex v = new Vertex();
		v.set_id(SBFUtils.getIntId(node.getNodeID()));
		this.graph.add_vertex(v);
	}
	
	public void printNodes(){
		for(IInfrastructureNodeImpl node : this.nodesSet){
			System.out.println();
			System.out.println("ID: "+node.getNodeID());
			System.out.println("COORDS: "+node.getCoordinates().getLatitude()+"-"+node.getCoordinates().getLongitude());
			System.out.println("NEAR NODES:");
			for(IPair<String, List<String>> p : node.getNearNodesWeighted()){
				System.out.println("NEIGHBOR: "+p.getFirst());
				System.out.println("NODES IN BETWEEN:");
				for(String s : p.getSecond()){
					System.out.println(s);
				}
			}
		}
	}
	
	/**
	 * method invoked in order to find the nearest node from the point selected by the user
	 * @param node the node selected from the user
	 * @return the nearest node of nodesSet
	 */
	private IInfrastructureNode findNearNode(IInfrastructureNode node){
		double distance = Double.MAX_VALUE;
		IInfrastructureNode near = node;
		for(IInfrastructureNode n: this.nodesSet){
			double temp = n.getCoordinates().getDistance(node.getCoordinates());
			if(temp<distance){
				distance = temp;
				near = n;
			}
		}
		return near;
	}
	
}
