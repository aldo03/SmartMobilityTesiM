package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.interfaces.IExpectedNumberOfVehicles;
import utils.mongodb.MongoDBUtils;

public class ExpectedNumberOfVehicles implements IExpectedNumberOfVehicles {

	private Map<String, List<Integer>> vehicles;  //every element of the list is the absolute time (in seconds)
												  //in which a single vehicle is expected to perform a certain path from a node to another
	private static final int RANGE = 300;        //range (in seconds) within which the flow of vehicles must be checked
	private String nodeId;
	public ExpectedNumberOfVehicles(String nodeId){
		this.vehicles = new HashMap<>();
		this.nodeId = nodeId;
		MongoDBUtils.initExpectedVehicles(nodeId);
	}
	
	
	
	@Override
	public int getVehicles(String nodeId, int time) {
		List<Integer> listOfVehicles = this.vehicles.get(nodeId);
		int currentTimeSeconds = (int) (System.currentTimeMillis()/1000);
		int futureTime = currentTimeSeconds+time;
		int rangeMin = futureTime-RANGE/2;
		int rangeMax = futureTime+RANGE/2;
		int vehiclesCount = 0;
		if(listOfVehicles!=null){
		for(Integer i : listOfVehicles){       
			if(i>=rangeMin&&i<=rangeMax){         //if a vehicle is within the range the counter is incremented
				vehiclesCount++;
			}else if(i>rangeMax){
				break;
			}
		}
		}
		//System.out.println("VEHICLES COUNT:      "+vehiclesCount);
		return vehiclesCount;
	}

	@Override
	public void addVehicle(String nodeId, int time) {
		List<Integer> listOfVehicles = this.vehicles.get(nodeId);
		int currentTimeSeconds = (int) (System.currentTimeMillis()/1000);
		listOfVehicles.add((int) (currentTimeSeconds+time));
		MongoDBUtils.addExpectedVehicle(this.nodeId, nodeId, (int) (currentTimeSeconds+time));
		Collections.sort(listOfVehicles);
		for(int i = listOfVehicles.size()-1; i>=0; i--){       //every "non fresh" information is removed
			if(listOfVehicles.get(i)<currentTimeSeconds){
				listOfVehicles.remove(i);
			}
		}
		MongoDBUtils.removeExpectedVehicles(this.nodeId, nodeId, currentTimeSeconds);
	}



	@Override
	public void initVehicles(String nodeId) {
		this.vehicles.put(nodeId, new ArrayList<>());
		MongoDBUtils.initExpectedVehicles(this.nodeId, nodeId);
	}
}
