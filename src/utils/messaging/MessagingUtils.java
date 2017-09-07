package utils.messaging;

import org.json.JSONException;
import org.json.JSONObject;

public class MessagingUtils {
	public static final String CONGESTION_ALARM = "congestionalarm";
	public static final String PATH_ACK = "pathack";
	public static final String REQUEST_PATH = "requestpath";
	public static final String REQUEST_TRAVEL_TIME = "requesttraveltime";
	public static final String RESPONSE_PATH = "responsepath";
	public static final String RESPONSE_TRAVEL_TIME = "responsetraveltime";
	public static final String TRAVEL_TIME_ACK = "traveltimeack";
	public static final String SBF_MSG = "sbf";
	public static final String REQUEST_BEST_PATH = "requestbestpath";
	public static final String REQUEST_BEST_PATH_FILTER = "requestbestpathfilter";
	public static final String SERVLET_MSG = "servletmsg";
	public static final String SEND_SBF = "sendsbf";
	public static final String CREATE_USER = "createuser";
	public static final String FILTER_CHECK = "filtercheck";
	public static final String CELL_REACHED = "cellreached";
	private static final String MSG_ID = "msgid";
	
	
	public static int getIntId(String s) throws JSONException{
		JSONObject obj = new JSONObject(s);
		String id = obj.getString(MSG_ID);
		//System.out.println(id);
		if(id.equals(PATH_ACK)){
			return 1;
		} else if(id.equals(REQUEST_PATH)){
			return 2;
		} else if(id.equals(REQUEST_TRAVEL_TIME)){
			return 3;
		} else if(id.equals(RESPONSE_PATH)){
			return 4;
		} else if(id.equals(RESPONSE_TRAVEL_TIME)){
			return 5;
		} else if(id.equals(TRAVEL_TIME_ACK)){
			return 6;
		} else if(id.equals(SBF_MSG)){
			return 7;
		} else if(id.equals(REQUEST_BEST_PATH)){
			return 8;
		} else if(id.equals(SERVLET_MSG)){
			return 9;
		} else if(id.equals(SEND_SBF)){
			return 10;
		} else if(id.equals(CREATE_USER)){
			return 11;
		} else if(id.equals(FILTER_CHECK)){
			return 12;
		} else if(id.equals(CELL_REACHED)){
			return 13;
		} else if(id.equals(REQUEST_BEST_PATH_FILTER)){
			return 14;
		} 
		else return -1;
	}
}
