package model.msg;

import model.interfaces.msg.ICreateMockUserMsg;

public class CreateMockUserMsg implements ICreateMockUserMsg {

	private String userId;
	private String msgId;
	private String startNode;
	private String endNode;	 
	
	public CreateMockUserMsg(String userId, String msgId, String startNode, String endNode) {
		this.userId = userId;
		this.msgId = msgId;
		this.startNode = startNode;
		this.endNode = endNode;
	}

	@Override
	public String getUserID() {
		return this.userId;
	}

	@Override
	public String getMsgID() {
		return this.msgId;
	}

	@Override
	public String getStartNode() {
		return this.startNode;
	}

	@Override
	public String getEndNode() {
		return this.endNode;
	}

}
