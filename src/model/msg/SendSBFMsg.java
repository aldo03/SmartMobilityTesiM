package model.msg;

import model.interfaces.msg.ISendSBFMsg;

public class SendSBFMsg implements ISendSBFMsg {

	private String msgId;
	private String condensedPath;
	private String userId;
	private String startTime;
	
	public SendSBFMsg(String msgId, String condensedPath, String userId, String startTime){
		this.msgId = msgId;
		this.condensedPath = condensedPath;
		this.userId = userId;
		this.startTime = startTime;
	}
	
	@Override
	public String getMsgID() {
		return this.msgId;
	}

	@Override
	public String getCondensedPath() {
		return this.condensedPath;
	}

	@Override
	public String getUser() {
		return this.userId;
	}

	@Override
	public String getStartTime() {
		return this.startTime;
	}

}
