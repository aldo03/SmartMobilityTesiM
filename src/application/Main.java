package application;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import infrastructure.InfrastructureDevice;
import model.interfaces.IPair;
import server.MainServer;
import user.MockPriorityUser;
import user.MockUserDevice;
import utils.mongodb.MongoDBUtils;
import model.InfrastructureNodeImpl;
import model.Pair;
import model.interfaces.IInfrastructureNode;
import model.interfaces.IInfrastructureNodeImpl;
import view.MainUserView;

public class Main {
 
	public static void main(String[] args) throws Exception {
		/*Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
		mongoLogger.setLevel(Level.SEVERE); */
		MainServer server = new MainServer();	
		//MongoDBUtils.initDb();		
		List<IInfrastructureNode> nodes = new ArrayList<>();
		try {
			Scanner s = new Scanner(new FileReader("infdata (17).txt"));
			Set<IPair<String, List<String>>> neighborsSet = new HashSet<>();
			List<String> betweenNodes = new ArrayList<>();
			String curNode = "";
			String curNeighbor = "";
			while(s.hasNextLine()){
				String line = s.nextLine();
				String node = line.substring(3, line.length());
				if(line.startsWith("I")){
					//System.out.println("Inf Node: "+node);
					if(!curNode.equals("")){
						IInfrastructureNodeImpl infNode = new InfrastructureNodeImpl(curNode);
						neighborsSet.add(new Pair<String, List<String>>(curNeighbor, betweenNodes));
						infNode.setNearNodesWeighted(neighborsSet);
						System.out.println(neighborsSet);
						nodes.add(infNode);
						/*InfrastructureDevice infDev = new InfrastructureDevice(infNode,"localhost", true);
						infDev.start();*/
						server.setNewNode(infNode);
						neighborsSet = new HashSet<>();
						betweenNodes = new ArrayList<>();
						curNeighbor="";
					}
					curNode = node;
				} else if(line.startsWith("N")){
					//System.out.println("Near Node: "+node);
					if(!curNeighbor.equals("")){
						neighborsSet.add(new Pair<String, List<String>>(curNeighbor, betweenNodes));
					}
					betweenNodes = new ArrayList<>();
					curNeighbor = node;
				} else if(line.startsWith("B")){
					//System.out.println("Bet Node: "+node);
					betweenNodes.add(node);
				}
			}
			IInfrastructureNodeImpl infNode = new InfrastructureNodeImpl(curNode);
			neighborsSet.add(new Pair<String, List<String>>(curNeighbor, betweenNodes));
			infNode.setNearNodesWeighted(neighborsSet);
			//System.out.println(neighborsSet);
			nodes.add(infNode);
			/*InfrastructureDevice infDev = new InfrastructureDevice(infNode,"localhost", true);
			infDev.start();*/
			server.setNewNode(infNode);
			s.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		server.setGraph();
		server.printNodes();
		//MockUserDevice user = new MockUserDevice("plutino");
		//user.start();
		//MockPriorityUser userp = new MockPriorityUser("localhost");
		/*MainUserView muv = new MainUserView();
		muv.setVisible(true);*/
		/*System.out.println(SBFUtils.getIntId("2-2"));
		System.out.println(SBFUtils.getStringId(102));*/
		
		//List<UserDevice> users = new ArrayList<>();
		
		//MainView view = new MainView(nodes, users);
		//view.setVisible(true);
	}

}
