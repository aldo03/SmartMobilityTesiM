package model.interfaces.msg;

/**
 * Interface that models the request from the Web App to send a specific SBF to a specific user
 * @author BBC
 *
 */

public interface ISendSBFMsg extends IMobilityMsg {
	/**
	 * gets the condensed path to be sent
	 * @return a string representing the condensed path
	 */
	String getCondensedPath();
	
	/**
	 * gets the receiver of the SBF
	 * @return the user
	 */
	String getUser();
	
	/**
	 * gets the start time to be sent
	 * @return a string representing start time
	 */
	String getStartTime();
}
