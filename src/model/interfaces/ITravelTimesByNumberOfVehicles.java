package model.interfaces;


/**
 * interface that defines a data structure that contains the expected travel times from a node to its
 * neighbors with a certain amount of vehicles in a prefixed time lapse of 10 minutes
 * @author BBC
 *
 */
public interface ITravelTimesByNumberOfVehicles {
	
	/**
	 * @param number of vehicles
	 * @param id of the node
	 * @return the expected travel time to the node with a certain amount of vehicles
	 */
	int getTravelTime(String nodeId, int numOfVehicles);
	
	/**
	 * sets the given travel time for a certain node with an amount of vehicles.
	 * @param nodeId
	 * @param numOfVehicles
	 * @param travelTime
	 */
	void setTravelTime(String nodeId, int numOfVehicles, int travelTime);
	
	/**
	 * initializes the values of the travel times with different numbers of vehicles to a default value.
	 * @param nodeId
	 * @param defaultValue
	 */
	void initTravelTimes(String nodeId, int defaultValue);
}
