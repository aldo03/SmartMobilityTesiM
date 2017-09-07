package model.interfaces.msg;

import model.interfaces.IInfrastructureNode;

/**
 * Interface that models the message that is sent from the infrastructure nodes in order to
 * notify the presence of a congestion between two nodes
 * @author Matteo
 *
 */
public interface ICongestionAlarmMsg extends IMobilityMsg {
	/**
	 * gets the first of the two nodes
	 * @return first node
	 */
	IInfrastructureNode getFirstNode();
	
	/**
	 * gets the second of the two nodes
	 * @return second node
	 */
	IInfrastructureNode getSecondNode();
}
