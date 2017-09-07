package model.msg;

import model.interfaces.msg.ICheckFilterMsg;

public class CheckFilterMsg implements ICheckFilterMsg {

	private String msgId;
	private String positiveChecks;
	
	public CheckFilterMsg(String msgId, String positiveChecks) {
		this.msgId = msgId;
		this.positiveChecks = positiveChecks;
	}

	@Override
	public String getMsgID() {
		return this.msgId;
	}

	@Override
	public String getPositiveChecks() {
		return this.positiveChecks;
	}

}
