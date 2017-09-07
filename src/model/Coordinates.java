package model;

import java.util.ArrayList;
import java.util.List;

import model.interfaces.ICoordinates;
import utils.sbf.SBFUtils;

public class Coordinates implements ICoordinates {
	
	private double latitude;
	private double longitude;
	private static final int RANGE = 10 ;
	public static final double EARTH_CIRCUMFERENCE = 40075;
	public static final double EARTH_RADIUS = 6371000;
	
	public Coordinates(double lat, double lon){
		this.latitude = lat;
		this.longitude = lon;
	}

	@Override
	public double getLatitude() {
		return this.latitude;
	}

	@Override
	public double getLongitude() {
		return this.longitude;
	}

	@Override
	public boolean isCloseEnough(ICoordinates coordinates) {
		double dist = distFrom(this.latitude, this.longitude, coordinates.getLatitude(), coordinates.getLongitude()) ;
		return dist<=RANGE;
	}
	
	private double distFrom(double lat1, double lng1, double lat2, double lng2) {
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = EARTH_RADIUS * c;
	    return dist;
	}

	@Override
	public double getDistance(ICoordinates coordinates) {
		return this.distFrom(this.latitude, this.longitude, coordinates.getLatitude(), coordinates.getLongitude());
	}
	
	@Override
	public List<ICoordinates> getCoordinatesInBetween(ICoordinates coordinates, double range) {
		List<ICoordinates> list = new ArrayList<>();
		double dist = this.getDistance(coordinates);
		int numOfCoordinates = (int) Math.round(dist/range);
		double minLong;
		double maxLong;
		double minLat;
		double maxLat;
		double startLat;
		double startLong;
		int revLat = 1;
		int revLong = 1;
	    if(coordinates.getLongitude()>this.longitude){
	        minLong = this.longitude;
	        maxLong = coordinates.getLongitude();
	    }else {
	        minLong = coordinates.getLongitude();
	        maxLong = this.longitude;
	    }
	    if(coordinates.getLatitude()>this.latitude){
	        minLat = this.latitude;
	        maxLat = coordinates.getLatitude();
	        if(coordinates.getLongitude()>this.longitude){
	            startLat = minLat;
	            startLong = minLong;
	        } else {
	            startLat = minLat;
	            startLong = maxLong;
	            revLong = -1;
	        }
	    }else {
	        minLat = coordinates.getLatitude();
	        maxLat = this.latitude;
	        if(coordinates.getLongitude()>this.longitude){
	            startLat = maxLat;
	            startLong = minLong;
	            revLat = -1;
	        } else {
	            startLat = maxLat;
	            startLong = maxLong;
	            revLat = -1;
	            revLong = -1;
	        }
	    }
	    double stepLat = (maxLat-minLat)/numOfCoordinates*revLat;
	    double stepLong = (maxLong-minLong)/numOfCoordinates*revLong;
		for(int i = 0; i < numOfCoordinates; i++){
			list.add(new Coordinates(startLat + stepLat*i, startLong + stepLong*i));
		}
		return list;
	}
}
