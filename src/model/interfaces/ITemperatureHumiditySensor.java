package model.interfaces;

/**
 * Interface that models temperature and humidity sensor
 * @author BBC
 *
 */
public interface ITemperatureHumiditySensor {

	/**
	 * method invoked to sense and obtain data from the sensor
	 */
	void senseTemperatureAndHumidity();
	
	/**
	 * method invoked to get the actual value of temperature
	 * @return temperature in C°
	 */
	double getTemperature();
	
	/**
	 * method invoked to get the actual value of humidity
	 * @return humidity
	 */
	double getHumidity();
	
}
