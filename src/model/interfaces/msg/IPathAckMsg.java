package model.interfaces.msg;

import model.interfaces.IInfrastructureNode;

/**
 * Interface that models the message that acknowledges the intention of a user to move through a certain node
 * @author BBC
 *
 */
public interface IPathAckMsg extends IUserMobilityMsg {
	/**
	 * gets the travel time in which the vehicle is expected
	 * @return travel time
	 */
	int getTravelTime();
	
	/**
	 * gets the next node of the path
	 * @return next node
	 */
	IInfrastructureNode getNextNode();
}
