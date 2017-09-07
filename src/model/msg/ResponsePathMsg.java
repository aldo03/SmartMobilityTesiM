package model.msg;

import java.util.List;

import model.interfaces.INodePath;
import model.interfaces.msg.IResponsePathMsg;

public class ResponsePathMsg implements IResponsePathMsg {
	private String msgId;
	private String userId;
	private List<INodePath> paths;
	private String brokerAddress;
	
	public ResponsePathMsg(String msgId, String userId, List<INodePath> paths, String brokerAddress){
		this.msgId = msgId;
		this.userId = userId;
		this.paths = paths;
		this.brokerAddress = brokerAddress;
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
	public List<INodePath> getPaths() {
		return this.paths;
	}

	@Override
	public String getBrokerAddress() {
		return this.brokerAddress;
	}

}
