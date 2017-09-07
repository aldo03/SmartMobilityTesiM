package model.interfaces;

/**
 * Interface that models an observer for a GPS source
 * @author BBC
 *
 */
public interface IGPSObserver {
	
	/**
	 * coordinates are passed to the observer
	 * @param coordinates
	 */
	void notifyGps(ICoordinates coordinates);
	
	/**
	 * cell reached is passed to the observer
	 * @param cellId
	 */
	void cellReached(String cellId);
	
	/**
	 * node reached is passed to the observer
	 * @param nodeId
	 */
	void nodeReached(String nodeId);
}
