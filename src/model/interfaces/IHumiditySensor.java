package model.interfaces;

/**
 * Interface that models a humidity sensor that perceives data from the environment
 * @author BBC
 *
 */
public interface IHumiditySensor {
	/**
	 * gets the current humidity
	 * @return the humidity
	 */
	double getCurrentHumidity();
}
