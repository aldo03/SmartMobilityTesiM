package model.interfaces;
/**
 * Interface that models an infrastructure node
 * @author BBC
 *
 */
public interface IInfrastructureNode {
	/**
	 * gets the ID of the node
	 * @return the node ID
	 */
	String getNodeID();
	
	/**
	 * gets the coordinates of the node
	 * @return the node coordinates
	 */
	ICoordinates getCoordinates();
	
	/**
	 * sets the coordinates of the node
	 */
	void setCoordinates(ICoordinates coordinates);
}
