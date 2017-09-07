package model.interfaces.msg;

import java.util.List;

/**
 * Interface that models a request of the best path msg from the user to the server, with filter information
 * @author BBC
 *
 */
public interface IRequestBestPathWithFilterMsg extends IRequestBestPathMsg {
	/**
	 * gets the information about the nodes of the filter
	 * @return list of node ids
	 */
	List<String> getFilterNodes();
	
	/**
	 * gets the information about the times of the filter
	 * @return list of times
	 */
	List<Integer> getFilterTimes();
}
