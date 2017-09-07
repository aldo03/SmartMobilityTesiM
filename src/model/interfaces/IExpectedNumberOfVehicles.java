package model.interfaces;

/**
 * Models the expected number of vehicles for a certain connection between two nodes
 * @author BBC
 *
 */

public interface IExpectedNumberOfVehicles {
	
	/**
	 * initializes the data structures
	 * @param nodeId
	 */
	void initVehicles(String nodeId);
	
	/**
	 * returns the amount of vehicles that are expected to go to a certain node before "time" minutes
	 * @param nodeId
	 * @param time
	 * @return an amount of vehicles
	 */
	int getVehicles(String nodeId, int time);
	
	/**
	 * adds a vehicle expected to go to a certain node before "time" minutes
	 * @param nodeId
	 * @param time
	 */
	void addVehicle(String nodeId, int time);
}
