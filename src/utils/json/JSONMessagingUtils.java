package utils.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.interfaces.IInfrastructureNode;
import model.interfaces.INodePath;
import model.interfaces.msg.ICellReachedMsg;
import model.interfaces.msg.ICheckFilterMsg;
import model.interfaces.msg.ICongestionAlarmMsg;
import model.interfaces.msg.ICreateMockUserMsg;
import model.interfaces.msg.IPathAckMsg;
import model.interfaces.msg.IRequestBestPathMsg;
import model.interfaces.msg.IRequestBestPathWithFilterMsg;
import model.interfaces.msg.IResponsePathMsg;
import model.interfaces.msg.IRequestPathMsg;
import model.interfaces.msg.IRequestTravelTimeMsg;
import model.interfaces.msg.IResponseTravelTimeMsg;
import model.interfaces.msg.ISBFMsg;
import model.interfaces.msg.ISendSBFMsg;
import model.interfaces.msg.ITravelTimeAckMsg;
import model.msg.CheckFilterMsg;
import model.msg.CongestionAlarmMsg;
import model.msg.CreateMockUserMsg;
import model.msg.PathAckMsg;
import model.msg.RequestBestPathMsg;
import model.msg.RequestBestPathWithFilterMsg;
import model.msg.RequestPathMsg;
import model.msg.RequestTravelTimeMsg;
import model.msg.ResponsePathMsg;
import model.msg.ResponseTravelTimeMsg;
import model.msg.SBFMsg;
import model.msg.SendSBFMsg;
import model.msg.TravelTimeAckMsg;

public class JSONMessagingUtils {
	private static final String MSG_ID = "msgid";
	private static final String USER_ID = "usrid";
	private static final String PATH = "path";
	private static final String BETWEEN_NODES = "betweennodes";
	private static final String FIRST_NODE = "firstnode";
	private static final String SECOND_NODE = "secondnode";
	private static final String NEXT_NODE = "nextnode";
	private static final String TIME_DAY = "timeday";
	private static final String USER_TYPOLOGY = "usertypology";
	private static final String PATH_LIST = "pathlist";
	private static final String TRAVEL_TIME = "traveltime";
	private static final String TRAVEL_TIMES = "traveltimes";
	private static final String TRAVEL_ID = "travelid";
	private static final String BROKER_ADDR = "brokeraddress";
	private static final String IS_FROZEN = "isfrozen";
	private static final String BIT_MAPPING = "bitmapping";
	private static final String HASH_FAMILY = "hashfamily";
	private static final String HASH_NUMBER = "hashnumber";
	private static final String AREA_NUMBER = "areanumber";
	private static final String HASH_SALT = "hashsalt";
	private static final String SBF = "sbf";
	private static final String CONDENSED_PATH = "condensedpath"; 
	private static final String POSITIVE_CHECKS = "positivechecks"; 
	private static final String CELL_ID = "cellid"; 
	private static final String START_TIME = "starttime"; 
	private static final String FILTER_NODES = "filternodes"; 
	private static final String FILTER_TIMES = "filtertimes"; 


	public static String getStringfromPathAckMsg(IPathAckMsg msg) throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(MSG_ID, msg.getMsgID());
		obj.put(USER_ID, msg.getUserID());
		obj.put(TRAVEL_TIME, msg.getTravelTime());
		obj.put(NEXT_NODE, new JSONInfrastructureNode(msg.getNextNode()));
		return obj.toString();
	}
	
	public static String getStringfromCongestionAlarmMsg(ICongestionAlarmMsg msg) throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(MSG_ID, msg.getMsgID());
		obj.put(FIRST_NODE, new JSONInfrastructureNode(msg.getFirstNode()));
		obj.put(SECOND_NODE, new JSONInfrastructureNode(msg.getSecondNode()));
		return obj.toString();
	}
	
	public static String getStringfromResponsePathMsg(IResponsePathMsg msg) throws JSONException{
		msg.getPaths().get(0).printPath();
		JSONObject obj = new JSONObject();
		obj.put(MSG_ID, msg.getMsgID());
		obj.put(USER_ID, msg.getUserID());
		JSONArray array = new JSONArray();
		for(INodePath path : msg.getPaths()){
			JSONObject ob = new JSONObject();
			ob.put(PATH, new JSONNodePath(path));
			JSONArray ttimes = new JSONArray();
			for(Integer i : path.getTravelTimes()){
				ttimes.put(i);
			}
			ob.put(TRAVEL_TIMES, ttimes);
			JSONArray betweenArray = new JSONArray();
			for(List<String> list : path.getBetweenNodes()){
				JSONArray a = new JSONArray();
				for(String s : list){
					a.put(s);
				}
				betweenArray.put(a);
			}
			ob.put(BETWEEN_NODES, betweenArray);
			array.put(ob);
		}
		obj.put(PATH_LIST, array);
		
		obj.put(BROKER_ADDR, msg.getBrokerAddress());
		return obj.toString();	
	}
	
	public static String getStringfromRequestPathMsg(IRequestPathMsg msg) throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(MSG_ID, msg.getMsgID());
		obj.put(FIRST_NODE, new JSONInfrastructureNode(msg.getStartingNode()));
		obj.put(SECOND_NODE, new JSONInfrastructureNode(msg.getEndingNode()));
		obj.put(USER_ID, msg.getUserID());
		return obj.toString();	
	}
	
	public static String getStringfromRequestBestPathMsg(IRequestBestPathMsg msg) throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(MSG_ID, msg.getMsgID());
		obj.put(FIRST_NODE, msg.getStartingNode());
		obj.put(SECOND_NODE, msg.getEndingNode());
		obj.put(USER_ID, msg.getUserID());
		obj.put(TIME_DAY, msg.getTimeAndDay());
		return obj.toString();	
	}
	
	public static String getStringfromRequestBestPathWithFilterMsg(IRequestBestPathWithFilterMsg msg) throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(MSG_ID, msg.getMsgID());
		obj.put(FIRST_NODE, msg.getStartingNode());
		obj.put(SECOND_NODE, msg.getEndingNode());
		obj.put(USER_ID, msg.getUserID());
		obj.put(TIME_DAY, msg.getTimeAndDay());
		JSONArray nodes = new JSONArray();
		for(String node : msg.getFilterNodes()){
			nodes.put(node);
		}
		obj.put(FILTER_NODES, nodes);
		JSONArray times = new JSONArray();
		for(Integer time : msg.getFilterTimes()){
			times.put(time);
		}
		obj.put(FILTER_TIMES, times);
		return obj.toString();	
	}
	
	public static String getStringfromRequestTravelTimeMsg(IRequestTravelTimeMsg msg) throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(MSG_ID, msg.getMsgID());
		obj.put(USER_ID, msg.getUserID());
		obj.put(TRAVEL_ID, msg.getTravelID());
		obj.put(TRAVEL_TIME, msg.getCurrentTravelTime());
		obj.put(PATH, new JSONNodePath(msg.getPath()));
		obj.put(IS_FROZEN, msg.frozenDanger());
		return obj.toString();	
	}
	
	public static String getStringfromResponseTravelTimeMsg(IResponseTravelTimeMsg msg) throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(MSG_ID, msg.getMsgID());
		obj.put(TRAVEL_ID, msg.getTravelID());
		obj.put(TRAVEL_TIME, msg.getTravelTime());
		obj.put(IS_FROZEN, msg.frozenDanger());
		return obj.toString();	
	}
	
	public static String getStringfromTravelTimeAckMsg(ITravelTimeAckMsg msg) throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(MSG_ID, msg.getMsgID());
		obj.put(USER_ID, msg.getUserID());
		obj.put(FIRST_NODE, new JSONInfrastructureNode(msg.getFirstNode()));
		obj.put(SECOND_NODE, new JSONInfrastructureNode(msg.getSecondNode()));
		obj.put(TRAVEL_TIME, msg.getTravelTime());
		return obj.toString();	
	}
	
	public static String getStringfromSFBMsg(ISBFMsg msg) throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(MSG_ID, msg.getMsgID());
		obj.put(BIT_MAPPING, msg.getBitMapping());
		obj.put(HASH_FAMILY, msg.getHashFamily());
		obj.put(HASH_NUMBER, msg.getHashNumber());
		obj.put(AREA_NUMBER, msg.getAreaNumber());
		obj.put(HASH_SALT, msg.getHashSalt());
		JSONArray sbf = new JSONArray();
		for(int elem : msg.getSBF()){
			sbf.put(elem);
		}
		obj.put(SBF, sbf);
		return obj.toString();	
	}
	
	public static String getStringFromRequestInfo(String path, String userTypology, String timeDay) throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(PATH, path);
		obj.put(USER_TYPOLOGY, userTypology);
		obj.put(TIME_DAY, timeDay);
		return obj.toString();
	}
	
	public static String getStringFromCheckFilterMsg(ICheckFilterMsg msg) throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(MSG_ID, msg.getMsgID());
		obj.put(POSITIVE_CHECKS, msg.getPositiveChecks());
		return obj.toString();
	}
	
	public static String getStringFromCellReachedMsg(ICellReachedMsg msg) throws JSONException{
		JSONObject obj = new JSONObject();
		obj.put(MSG_ID, msg.getMsgID());
		obj.put(CELL_ID, msg.getCellId());
		obj.put(USER_ID, msg.getUserID());
		return obj.toString();
	}
	
	public static IPathAckMsg getPathAckMsgFromString(String s) throws JSONException{
		JSONObject obj = new JSONObject(s);
		IInfrastructureNode nextNode = JSONInfrastructureNode.getInfrastructureNodeFromJSONObject(obj.getJSONObject(NEXT_NODE));
		IPathAckMsg msg = new PathAckMsg(obj.getString(USER_ID), obj.getString(MSG_ID), obj.getInt(TRAVEL_TIME), nextNode);
		return msg;
	}
	
	/*public static IPathAckMsg getPathAckWithCoordinatesMsgFromString(String s) throws JSONException{
		JSONObject obj = new JSONObject(s);
		INodePath path = JSONNodePath.getNodePathfromJSONArray(obj.getJSONArray(PATH));
		IPathAckMsg msg = new PathAckMsg(obj.getString(USER_ID), obj.getString(MSG_ID), path, obj.getInt(TRAVEL_ID));
		return msg;
	}*/
	
	public static ICongestionAlarmMsg getCongestionAlarmMsgFromString(String s) throws JSONException{
		JSONObject obj = new JSONObject(s);
		IInfrastructureNode firstNode = JSONInfrastructureNode.getInfrastructureNodeFromJSONObject(obj.getJSONObject(FIRST_NODE));
		IInfrastructureNode secondNode = JSONInfrastructureNode.getInfrastructureNodeFromJSONObject(obj.getJSONObject(SECOND_NODE));
		ICongestionAlarmMsg msg = new CongestionAlarmMsg(obj.getString(MSG_ID),firstNode, secondNode);
		return msg;
	}
	
	public static IResponsePathMsg getResponsePathMsgFromString(String s) throws JSONException{
		JSONObject obj = new JSONObject(s);
		List<INodePath> list= new ArrayList<>();
		JSONArray array = obj.getJSONArray(PATH_LIST);
		for(int i=0; i<array.length(); i++){
			JSONObject obj2 = array.getJSONObject(i);
			INodePath path = JSONNodePath.getNodePathfromJSONArray(obj2.getJSONArray(PATH), obj2.getJSONArray(BETWEEN_NODES), obj2.getJSONArray(TRAVEL_TIMES));
			list.add(path);
		}
		IResponsePathMsg msg = new ResponsePathMsg(obj.getString(MSG_ID),obj.getString(USER_ID), list, obj.getString(BROKER_ADDR));
		return msg;
	}
	
	public static IRequestPathMsg getRequestPathMsgFromString(String s) throws JSONException{
		JSONObject obj = new JSONObject(s);
		IInfrastructureNode firstNode = JSONInfrastructureNode.getInfrastructureNodeWithCoordinatesFromJSONObject(obj.getJSONObject(FIRST_NODE));
		IInfrastructureNode secondNode = JSONInfrastructureNode.getInfrastructureNodeWithCoordinatesFromJSONObject(obj.getJSONObject(SECOND_NODE));
		IRequestPathMsg msg = new RequestPathMsg(obj.getString(MSG_ID),firstNode, secondNode,obj.getString(USER_ID));
		return msg;
	}
	
	public static IRequestBestPathMsg getRequestBestPathMsgFromString(String s) throws JSONException{
		JSONObject obj = new JSONObject(s);
		String timeDay = "";
		if(obj.has(TIME_DAY)){
			timeDay = obj.getString(TIME_DAY);
		}
		IRequestBestPathMsg msg = new RequestBestPathMsg(obj.getString(MSG_ID), obj.getString(FIRST_NODE), obj.getString(SECOND_NODE), obj.getString(USER_ID), timeDay);
		return msg;
	}
	
	public static IRequestBestPathWithFilterMsg getRequestBestPathWithFilterMsgFromString(String s) throws JSONException{
		JSONObject obj = new JSONObject(s);
		String timeDay = "";
		if(obj.has(TIME_DAY)){
			timeDay = obj.getString(TIME_DAY);
		}
		JSONArray nodes = obj.getJSONArray(FILTER_NODES);
		List<String> filterNodes = new ArrayList<>();
		for(int i = 0; i < nodes.length(); i++){
			filterNodes.add(nodes.getString(i));
		}
		
		JSONArray times = obj.getJSONArray(FILTER_TIMES);
		List<Integer> filterTimes = new ArrayList<>();
		for(int i = 0; i < times.length(); i++){
			filterTimes.add(times.getInt(i));
		}
		IRequestBestPathWithFilterMsg msg = new RequestBestPathWithFilterMsg(obj.getString(MSG_ID), obj.getString(FIRST_NODE), obj.getString(SECOND_NODE), obj.getString(USER_ID), timeDay, filterNodes, filterTimes);
		return msg;
	}
	
	public static IRequestTravelTimeMsg getRequestTravelTimeMsgFromString(String s) throws JSONException{
		JSONObject obj = new JSONObject(s);
		INodePath path = JSONNodePath.getNodePathfromJSONArray(obj.getJSONArray(PATH));
		IRequestTravelTimeMsg msg =new RequestTravelTimeMsg(obj.getString(USER_ID), obj.getString(MSG_ID),
				obj.getInt(TRAVEL_TIME),path, obj.getInt(TRAVEL_ID), obj.getBoolean(IS_FROZEN));
		return msg;
	}
	
	public static IResponseTravelTimeMsg getResponseTravelTimeMsgFromString(String s) throws JSONException{
		JSONObject obj = new JSONObject(s);
		IResponseTravelTimeMsg msg = new ResponseTravelTimeMsg(obj.getString(MSG_ID),obj.getInt(TRAVEL_TIME), obj.getInt(TRAVEL_ID),
											obj.getBoolean(IS_FROZEN));
		return msg;
	}
	
	public static ITravelTimeAckMsg getTravelTimeAckMsgFromString(String s) throws JSONException{
		JSONObject obj = new JSONObject(s);
		IInfrastructureNode firstNode = JSONInfrastructureNode.getInfrastructureNodeFromJSONObject(obj.getJSONObject(FIRST_NODE));
		IInfrastructureNode secondNode = JSONInfrastructureNode.getInfrastructureNodeFromJSONObject(obj.getJSONObject(SECOND_NODE));
		ITravelTimeAckMsg msg = new TravelTimeAckMsg(obj.getString(USER_ID), obj.getString(MSG_ID), firstNode, secondNode, obj.getInt(TRAVEL_TIME));
		return msg;
	}
	
	public static ISBFMsg getSBFMsgFromString(String s) throws JSONException{
		JSONObject obj = new JSONObject(s);
		List<Integer> elems = new ArrayList<>();
		JSONArray sbf = obj.getJSONArray(SBF);
		for(int i = 0; i < sbf.length(); i++){
			elems.add(sbf.getInt(i));
		}
		ISBFMsg msg = new SBFMsg(obj.getString(MSG_ID), obj.getInt(BIT_MAPPING), obj.getInt(HASH_FAMILY), obj.getInt(HASH_NUMBER), 
				obj.getInt(AREA_NUMBER), obj.getString(HASH_SALT), elems);
		return msg;
	}
	
	public static ISendSBFMsg getSendSBFMsgFromString(String s) throws JSONException{
		JSONObject obj = new JSONObject(s);
		return new SendSBFMsg(obj.getString(MSG_ID), obj.getString(CONDENSED_PATH),obj.getString(USER_ID), obj.getString(START_TIME));
	}
	
	public static ICreateMockUserMsg getCreateMockUserMsgFromString(String s) throws JSONException{
		JSONObject obj = new JSONObject(s);
		return new CreateMockUserMsg(obj.getString(USER_ID), obj.getString(MSG_ID), obj.getString(FIRST_NODE),obj.getString(SECOND_NODE));
	}
	
	public static ICheckFilterMsg getCheckFilterMsgFromString(String s) throws JSONException{
		JSONObject obj = new JSONObject(s);
		return new CheckFilterMsg(obj.getString(MSG_ID), obj.getString(POSITIVE_CHECKS));
	}
}
