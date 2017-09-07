package model.msg;

import model.interfaces.msg.IRequestBestPathMsg;

public class RequestBestPathMsg implements IRequestBestPathMsg {

	
	
	private String msgId;
	private String startingNode;
	private String endingNode;
	private String userId;
	private String timeDay;
	
	public RequestBestPathMsg(String msgId, String startingNode, String endingNode, String userId, String timeDay){
		this.msgId = msgId;
		this.startingNode = startingNode;
		this.endingNode = endingNode;
		this.userId = userId;
		this.timeDay = timeDay;
	}

	@Override
	public String getMsgID() {
		return this.msgId;
	}

	@Override
	public String getStartingNode() {
		return this.startingNode;
	}

	@Override
	public String getEndingNode() {
		return this.endingNode;
	}

	@Override
	public String getUserID() {
		return this.userId;
	}

	@Override
	public String getTimeAndDay() {
		return this.timeDay;
	}

}
