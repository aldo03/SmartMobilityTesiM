package model.interfaces;
/**
 * Interface that models a temperature sensor that perceives data from the environment
 * @author BBC
 *
 */

public interface ITemperatureSensor {
	/**
	 * gets the current temperature 
	 * @return the temperature
	 */
	double getCurrentTemperature();
}
