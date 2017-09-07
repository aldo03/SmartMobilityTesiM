package model.msg;

import java.util.List;

import model.interfaces.msg.IRequestBestPathWithFilterMsg;

public class RequestBestPathWithFilterMsg implements IRequestBestPathWithFilterMsg {

	private String msgId;
	private String startingNode;
	private String endingNode;
	private String userId;
	private String timeDay;
	private List<String> filterNodes;
	private List<Integer> filterTimes;
	
	public RequestBestPathWithFilterMsg(String msgId, String startingNode, String endingNode, 
			String userId, String timeDay, List<String> filterNodes, List<Integer> filterTimes){
		this.msgId = msgId;
		this.startingNode = startingNode;
		this.endingNode = endingNode;
		this.userId = userId;
		this.timeDay = timeDay;
		this.filterNodes = filterNodes;
		this.filterTimes = filterTimes;
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

	@Override
	public List<String> getFilterNodes() {
		return this.filterNodes;
	}

	@Override
	public List<Integer> getFilterTimes() {
		return this.filterTimes;
	}

}
