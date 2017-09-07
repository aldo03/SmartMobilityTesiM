package model.msg;

import model.interfaces.msg.IResponseTravelTimeMsg;

public class ResponseTravelTimeMsg implements IResponseTravelTimeMsg {
	
	private String msgId;
	private int travelTime;
	private int travelId;
	private boolean isFrozen;
	
	public ResponseTravelTimeMsg(String msgId, int travelTime, int travelId, boolean isFrozen){
		this.msgId = msgId;
		this.travelTime = travelTime;
		this.travelId = travelId;
		this.isFrozen = isFrozen;
	}

	@Override
	public String getMsgID() {
		return this.msgId;
	}

	@Override
	public int getTravelTime() {
		return this.travelTime;
	}

	@Override
	public int getTravelID() {
		return this.travelId;
	}

	@Override
	public boolean frozenDanger() {
		return this.isFrozen;
	}

}
