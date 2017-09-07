package model.msg;

import model.interfaces.IInfrastructureNode;
import model.interfaces.msg.ITravelTimeAckMsg;

public class TravelTimeAckMsg implements ITravelTimeAckMsg {
	
	private String userId;
	private String msgId;
	private IInfrastructureNode firstNode;
	private IInfrastructureNode secondNode;
	private int travelTime;

	public TravelTimeAckMsg(String userId, String msgId, IInfrastructureNode firstNode, 
			IInfrastructureNode secondNode, int travelTime){
		this.userId = userId;
		this.msgId = msgId;
		this.firstNode = firstNode;
		this.secondNode = secondNode;
		this.travelTime = travelTime;
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
	public IInfrastructureNode getFirstNode() {
		return this.firstNode;
	}

	@Override
	public IInfrastructureNode getSecondNode() {
		return this.secondNode;
	}

	@Override
	public int getTravelTime() {
		return this.travelTime;
	}

}
