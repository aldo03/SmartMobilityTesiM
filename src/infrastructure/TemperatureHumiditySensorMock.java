package infrastructure;

import model.interfaces.ITemperatureHumiditySensor;

public class TemperatureHumiditySensorMock implements ITemperatureHumiditySensor{

	public static final double TEMPERATURE_RANGE = 4;
	public static final double HUMIDITY_RANGE = 2;
	
	private double temperature;
	private double minTemperature;
	private double maxTemperature;
	private boolean increaseTemp;
	private double humidity;
	private double minHumidity;
	private double maxHumidity;
	private boolean increaseHumidity;
	
	
	public TemperatureHumiditySensorMock(double temperature, double humidity) {
		this.temperature = temperature;
		this.minTemperature = temperature - TEMPERATURE_RANGE;
		this.maxTemperature = temperature + TEMPERATURE_RANGE;
		this.increaseTemp = true;
		this.humidity = humidity;
		this.minHumidity = humidity - HUMIDITY_RANGE;
		this.maxHumidity = humidity + HUMIDITY_RANGE;
		this.increaseHumidity = true;
	}

	@Override
	public void senseTemperatureAndHumidity() {
		if(this.increaseTemp)
			this.temperature++;
		else if(!this.increaseTemp)
			this.temperature--;
		if(this.increaseHumidity)
			this.humidity++;
		else if(!this.increaseHumidity)
			this.humidity--;
		
		if(this.temperature == this.maxTemperature)
			this.increaseTemp = false;
		if(this.temperature == this.minTemperature)
			this.increaseTemp = true;
		if(this.humidity == this.maxHumidity)
			this.increaseHumidity = false;
		if(this.humidity == this.minHumidity)
			this.increaseHumidity = true;
	}

	@Override
	public double getTemperature() {
		return this.temperature;
	}

	@Override
	public double getHumidity() {
		return this.humidity;
	}

}
