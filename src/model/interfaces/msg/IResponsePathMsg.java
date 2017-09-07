package model.interfaces.msg;

import java.util.List;

import model.interfaces.INodePath;

/**
 * Interface that models the response msg of the server with the user ID, the paths and the broker address
 * @author BBC
 *
 */
public interface IResponsePathMsg extends IUserMobilityMsg {
	
	/**
	 * gets the paths from the message
	 * @return paths
	 */
	List<INodePath> getPaths();
	
	/**
	 * gets the IP address of the broker
	 * @return broker IP address
	 */
	String getBrokerAddress();
}
