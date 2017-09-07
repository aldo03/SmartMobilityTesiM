package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.interfaces.ICurrentTimes;
import utils.mongodb.MongoDBUtils;

public class CurrentTimes implements ICurrentTimes {

	private Map<String, List<Integer>> currentTimes;
	private String nodeId;
	
	public CurrentTimes(String nodeId){
		this.currentTimes = new HashMap<>();
		this.nodeId = nodeId;
		MongoDBUtils.initCurrentTime(nodeId);
	}
	
	@Override
	public void addTime(String nodeId, int time) {
		this.currentTimes.get(nodeId).add(time);
		MongoDBUtils.addCurrentTime(this.nodeId, nodeId, time);
	}

	
	@Override
	public int getAverageTime(String nodeId) {
		List<Integer> l = this.currentTimes.get(nodeId);
		int sum = 0;
		for(int i : l){
			sum+=i;
		}
		if(l.size()!=0){
			return sum/l.size();
		} else {
			return -1;
		}
	}



	@Override
	public void initTimes(String nodeId) {
		this.currentTimes.put(nodeId, new ArrayList<>());
		MongoDBUtils.initCurrentTime(this.nodeId, nodeId);
	}

	@Override
	public void removeData() {
		for(List<Integer> l : this.currentTimes.values()){
			l.clear();
		}
		MongoDBUtils.clearCurrentTimes(this.nodeId, this.currentTimes.keySet());
	}

	@Override
	public int getVehicleCount(String nodeId) {
		return this.currentTimes.get(nodeId).size();
	}

}
