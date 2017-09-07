package model.interfaces;

/**
 * interface that models the current times of a certain path between two nodes
 * @author BBC
 *
 */
public interface ICurrentTimes {
	
	/**
	 * initializes the lists of collections of data
	 * @param nodeId
	 */
	void initTimes(String nodeId);
	
	/**
	 * adds a time to a certain path
	 * @param nodeId
	 * @param time
	 */
	void addTime(String nodeId, int time);
	
	
	/**
	 * returns the average time between the current collection of data
	 * @param nodeId
	 * @return
	 */
	int getAverageTime(String nodeId);
	
	/**
	 * returns the amount of vehicles detected through a certain node
	 * @param nodeId
	 * @return
	 */
	int getVehicleCount(String nodeId);
	
	/**
	 * remove the current collections
	 */
	void removeData();
}
