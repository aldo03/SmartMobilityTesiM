package model.msg;

import model.interfaces.IInfrastructureNode;
import model.interfaces.INodePath;
import model.interfaces.msg.IPathAckMsg;

public class PathAckMsg implements IPathAckMsg {
	
	private String userId;
	private String msgId;
	private int travelTime;
	private IInfrastructureNode nextNode;
	
	public PathAckMsg(String userId, String msgId, int travelTime, IInfrastructureNode nextNode){
		this.userId = userId;
		this.msgId = msgId;
		this.travelTime = travelTime;
		this.nextNode = nextNode;
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
	public int getTravelTime() {
		return this.travelTime;
	}

	@Override
	public IInfrastructureNode getNextNode() {
		return this.nextNode;
	}

}
