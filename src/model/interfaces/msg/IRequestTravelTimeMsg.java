package model.interfaces.msg;

import model.interfaces.INodePath;

/**
 * Interface that models a request travel time msg from the user to the server
 * @author BBC
 *
 */
public interface IRequestTravelTimeMsg extends IUserMobilityMsg {
	
	/**
	 * gets (in seconds) the current travel time. This travel time is the sum between all previous travel times
	 * between two nodes
	 * @return current travel time
	 */
	int getCurrentTravelTime();
	
	/**
	 * gets the path of which the travel time is to be calculated
	 * @return path
	 */
	INodePath getPath();
	
	/**
	 * gets the ID of the travel
	 * @return travel ID
	 */
	int getTravelID();
	
	
	/**
	 * it returns the value of a boolean that indicates if through the path there may be some ice on the road
	 * @return
	 */
	boolean frozenDanger();
}
