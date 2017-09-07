package model.interfaces;

/**
 * Interface that models an observer for a Temperature and Humidity source
 * @author BBC
 *
 */
public interface ITemperatureHumidityObserver {

	/**
	 * temperature passed to the observer
	 * @param temperature
	 */
	void setTemperature(double temperature);
	
	/**
	 * humidity passed to the observer 
	 * @param humidity
	 */
	void setHumidity(double humidity);
	
}
