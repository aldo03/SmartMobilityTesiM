package model.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Pair;

/**
 * interface that models the node with references to other nodes
 * @author BBC
 *
 */
public interface IInfrastructureNodeImpl extends IInfrastructureNode {
	
	/**
	 * method invoked to get near nodes with time of travelling 
	 * @return weighted near nodes
	 */
	Set<IPair<String,List<String>>> getNearNodesWeighted();
	
	/**
	 * method invoked to set a new node in the near nodes weighted
	 * @param node
	 */
	void setNearNodesWeighted(Set<IPair<String, List<String>>> nodes);

	List<String> getBetweenNodes(String id);

	
	
}
