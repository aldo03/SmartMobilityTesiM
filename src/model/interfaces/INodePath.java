package model.interfaces;

import java.util.List;

/**
 * Interface that models a path of nodes inside the system
 * @author BBC
 *
 */
public interface INodePath {
	/**
	 * gets the nodes of the path
	 * @return nodes of the path
	 */
	List<IInfrastructureNode> getPathNodes();
	
	/**
	 * removes the head of the list
	 */
	void removeFirstNode();
	
	/**
	 * set the path
	 * @param path
	 */
	void setPath(List<IInfrastructureNode> path);
	
	/**
	 * set the between nodes
	 * @param betweenNodes
	 */
	void setBetweenNodes(List<List<String>> betweenNodes);
	
	List<Integer> getTravelTimes();
	
	void setTravelTimes(List<Integer> times);
	
	/**
	 * prints the path
	 */
	void printPath();
	
	/**
	 * gets the path as a condensed string
	 * @return the condensed string
	 */
	String getCondensedPath();

	List<List<String>> getBetweenNodes();
}
