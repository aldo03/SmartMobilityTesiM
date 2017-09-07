package model.msg;

import model.interfaces.IInfrastructureNode;
import model.interfaces.msg.IRequestPathMsg;

public class RequestPathMsg implements IRequestPathMsg {
	
	private String msgId;
	private IInfrastructureNode startingNode;
	private IInfrastructureNode endingNode;
	private String userId;
	
	public RequestPathMsg(String msgId, IInfrastructureNode startingNode, IInfrastructureNode endingNode, String userId){
		this.msgId = msgId;
		this.startingNode = startingNode;
		this.endingNode = endingNode;
		this.userId = userId;
	}

	@Override
	public String getMsgID() {
		return this.msgId;
	}

	@Override
	public IInfrastructureNode getStartingNode() {
		return this.startingNode;
	}

	@Override
	public IInfrastructureNode getEndingNode() {
		return this.endingNode;
	}

	@Override
	public String getUserID() {
		return this.userId;
	}

}
