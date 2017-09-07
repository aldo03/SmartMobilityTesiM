package model.interfaces;

/**
 * Interface that models the pending users that could send a ack to the infrastructure device
 * @author BBC
 *
 */

public interface IPendingUsers {
	/**
	 * adds a pending user to the pending users that could send a ack to the infrastructure device
	 * @param userId
	 * @param travelId
	 * @param time
	 */
	void addPendingUser(String userId, int travelId, int time);
	
	/**
	 * gets the time within which the user is going to reach the node and deletes every information about this user
	 * @param userId
	 * @param travelId
	 * @return the time within which the user is going to reach the node
	 */
	int getTravelTimeAndRemoveUser(String userId, int travelId);
}
