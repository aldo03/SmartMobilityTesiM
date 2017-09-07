package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.interfaces.ITravelTimesByNumberOfVehicles;
import utils.mongodb.MongoDBUtils;



public class TravelTimesByNumberOfVehicles implements ITravelTimesByNumberOfVehicles {
	private static final int RANGE = 5;
	private static final int ARRAY_LENGTH = 200;
	private static final int MAX_VARIATION_PERCENTAGE = 8;     //Variations over 25% of the old value are not allowed
	private String nodeId;
	
	private Map<String, Integer[]> travelTimes;
	
	public TravelTimesByNumberOfVehicles(String nodeId){
		this.travelTimes = new HashMap<>();
		this.nodeId = nodeId;
		//MongoDBUtils.initTimes(nodeId);
	}
	
	
	
	@Override
	public int getTravelTime(String nodeId, int numOfVehicles) {
		int time = 0;
		Integer[] l = this.travelTimes.get(nodeId);
		if(l.length<numOfVehicles/RANGE){
			time = l[l.length-1];
		} else{
			time = l[numOfVehicles/RANGE];
		}
		return time;
	}



	@Override
	public void initTravelTimes(String nodeId, int defaultValue) {
		Integer[] times = new Integer[ARRAY_LENGTH];
		MongoDBUtils.getTimeTravels(this.nodeId).get(nodeId).toArray(times);
		this.travelTimes.put(nodeId, times);
		/*List<Integer> list = new ArrayList<>();
		for(int i = 0; i<ARRAY_LENGTH; i++){
			list.add(defaultValue);
		}
		Integer[] times = new Integer[ARRAY_LENGTH];
		list.toArray(times);
		this.travelTimes.put(nodeId, times);
		MongoDBUtils.initTravelTimes(this.nodeId, nodeId, list);*/
	}


	//the travel time for a certain amount of vehicles is set. The near values are updated in order to make
	//them consistent with the new value
	@Override
	public void setTravelTime(String nodeId, int numOfVehicles, int time) {
		Integer[] times = this.travelTimes.get(nodeId);
		int range = numOfVehicles/RANGE;
		int oldVal= times[range];
		int travelTime = Math.floorDiv(oldVal+time, 2);
		if(Math.abs(travelTime-oldVal)>oldVal/MAX_VARIATION_PERCENTAGE){
			if(travelTime-oldVal<0){
				travelTime = oldVal - oldVal/MAX_VARIATION_PERCENTAGE;
			} else{
				travelTime = oldVal + oldVal/MAX_VARIATION_PERCENTAGE;
			}
		}
		if(range<times.length){
			if(times[range]>=travelTime){
				times[range] = travelTime;
				MongoDBUtils.setTravelTime(this.nodeId, nodeId, range, travelTime);
				for(int i=range-1; i>=0;i--){
					if(times[i]<=travelTime){
						break;
					}
					times[i]=travelTime;
					MongoDBUtils.setTravelTime(this.nodeId, nodeId, i, travelTime);
				}
			} else if(times[range]<travelTime){
				times[range] = travelTime;
				MongoDBUtils.setTravelTime(this.nodeId, nodeId, range, travelTime);
				for(int i=range+1; i<times.length;i++){
					if(times[i]>travelTime){
						break;
					}
					times[i]=travelTime;
					MongoDBUtils.setTravelTime(this.nodeId, nodeId, i, travelTime);
				}
			}
		}
		this.travelTimes.put(nodeId, times);
	}
}
