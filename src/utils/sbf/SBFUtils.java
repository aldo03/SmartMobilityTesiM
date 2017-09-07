package utils.sbf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Coordinates;
import model.Pair;
import model.interfaces.ICoordinates;
import model.interfaces.INodePath;
import model.interfaces.IPair;

public class SBFUtils {
	private static final double BIN_LONG = 7.5;				//in meters
	private static final double BIN_LAT = 0.00009;      //in radians
	private static final double BIN_LONG_RAD = 0.00012;
	//desired false positives probability (upper bound)
	private static final double MAX_FPP = 0.001;
	private static final double START_LAT = 41.902222;
	private static final double START_LONG = 12.471334;
	private static final int GRID_WIDTH = 100;
	
	public static String getCellFromCoordinates(ICoordinates coordinates){
		long latBin = -Math.round((coordinates.getLatitude()+BIN_LAT/2-START_LAT)/BIN_LAT);
		long lngBin = Math.round((coordinates.getLongitude()-BIN_LONG_RAD/2-START_LONG)/BIN_LONG_RAD);
		return latBin+"-"+lngBin;
	}
	
	public static ICoordinates getCenterCoordinatesFromCell(String cell){
		String[] bins = cell.split("-");
		double lat = START_LAT - Integer.parseInt(bins[0])*BIN_LAT - BIN_LAT/2;
		double ltd = START_LONG + Integer.parseInt(bins[1])*BIN_LONG_RAD + BIN_LONG_RAD/2;
		return new Coordinates(lat,ltd);
	}
	
	public static Pair<List<Set<String>>, Integer> getAreasWithCellsFromPath(INodePath path){
		List<Set<String>> areasWithCells = new ArrayList<>();
		int numCells = 0;
		for(int i = 0; i < path.getPathNodes().size(); i++){
			Set<String> cells = new HashSet<>();
			if(i!=path.getPathNodes().size()-1){
				ICoordinates c1 = path.getPathNodes().get(i).getCoordinates();
				ICoordinates c2 = path.getPathNodes().get(i+1).getCoordinates();
				List<ICoordinates> coords = c1.getCoordinatesInBetween(c2, BIN_LONG);
				for(ICoordinates c : coords){
					cells.add(getCellFromCoordinates(c));
				}
				areasWithCells.add(cells);
				numCells+=cells.size();
			}
		}
		return new Pair<>(areasWithCells, numCells);
	}
	
	public static IPair<Integer, Integer> getSBFParams(int cells){
		//determines the optimal bitMapping and hash number
		int numCells = (int)Math.ceil(((double)((double)-cells*Math.log(MAX_FPP)) / Math.pow(Math.log(2), 2)));
		int bitMapping = (int)Math.ceil(log2(numCells));
		int hn = (int)Math.ceil((double)(numCells / cells)*Math.log(2));
		return new Pair<>(bitMapping, hn);
	}
	
	private static double log2(int num){
		return Math.log(num)/Math.log(2);
	}
	
	public static int getIntId(String id){
		String[] bins = id.split("-");
		return Integer.parseInt(bins[1])+Integer.parseInt(bins[0])*GRID_WIDTH;
	}
	
	public static String getStringId(int id){
		int longBin = id%GRID_WIDTH;
		int latBin = id/GRID_WIDTH;
		return latBin+"-"+longBin;
	}
}
