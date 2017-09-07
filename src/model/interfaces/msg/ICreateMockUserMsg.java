package model.interfaces.msg;

/**
 * Interface that models a request from the web GUI of creating a mock user
 * @author Matteo
 *
 */
public interface ICreateMockUserMsg extends IUserMobilityMsg {
	
	/**
	 * gets the start node
	 * @return start node
	 */
	String getStartNode();
	
	/**
	 * gets the end node
	 * @return end node
	 */
	String getEndNode();
}
