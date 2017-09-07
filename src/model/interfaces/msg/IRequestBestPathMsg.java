package model.interfaces.msg;

/**
 * Interface that models a request of the best path msg from the user to the server
 * @author BBC
 *
 */
public interface IRequestBestPathMsg extends IUserMobilityMsg {
	/**
	 * gets the starting node of the path
	 * @return starting node of the path
	 */
	String getStartingNode();
	
	/**
	 * gets the ending node of the path
	 * @return ending node of the path
	 */
	String getEndingNode();
	
	/**
	 * gets the time and day of the msg
	 * @return time and day
	 */
	String getTimeAndDay();
}
