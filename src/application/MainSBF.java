package application;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import model.Coordinates;
import model.InfrastructureNode;
import model.NodePath;
import model.interfaces.IInfrastructureNode;
import model.interfaces.INodePath;
import model.interfaces.SpatialBloomFilter;
import utils.sbf.SBFUtils;

public class MainSBF {

	public static void main(String[] args) {
		
		int numCells = 50;
		SpatialBloomFilter sbf = SpatialBloomFilter.INSTANCE;
		sbf.CreateSBF(SBFUtils.getSBFParams(numCells).getFirst(), 1, SBFUtils.getSBFParams(numCells).getSecond(), 5, "salt.txt");
    	
		for(int i = 0; i <50; i++){
			sbf.Insert("id"+i, ("id"+i).length(), i/10);
		}
		long millis = System.currentTimeMillis();
    	for(int i = 0; i < 100000; i++){
    		sbf.Check("id"+i, ("id"+i).length());
    	}
    	//sbf.PrintFilter(0);
    	long timeElapsed = System.currentTimeMillis()-millis;
    	System.out.println(timeElapsed);
	}
}
