package model.interfaces;

import java.util.List;

/**
 * Interface that models GPS coordinates  
 * @author BBC
 *
 */
public interface ICoordinates {
	/**
	 * gets the latitude
	 * @return latitude
	 */
	double getLatitude();
	
	/**
	 * gets the longitude
	 * @return longitude
	 */
	double getLongitude();
	
	
	/**
	 * tells if the coordinates are close enough
	 * @param coordinates
	 * @return true if the coordinates are close enough, false otherwise
	 */
	boolean isCloseEnough(ICoordinates coordinates);
	
	/**
	 * get the distance between two points
	 * @param coordinates
	 * @return distance
	 */
	double getDistance(ICoordinates coordinates);
	
	/**
	 * get the coordinates between two coordinates separated from a certain range value
	 * @param coordinates, range (in meters)
	 * @return list of coordinates
	 */
	List<ICoordinates> getCoordinatesInBetween(ICoordinates coordinates, double binLong);
}
