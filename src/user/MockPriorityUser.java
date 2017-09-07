package user;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;

import com.rabbitmq.client.ConnectionFactory;

import model.interfaces.SpatialBloomFilter;
import model.interfaces.msg.ISBFMsg;
import model.msg.SBFMsg;
import utils.json.JSONMessagingUtils;
import utils.messaging.MessagingUtils;
import utils.mom.MomUtils;
import utils.sbf.SBFUtils;
import view.MockPriorityUserFrame;

public class MockPriorityUser extends Thread {
	private List<List<String>> path;
	private String receiver;
	private String startTime;
	private ConnectionFactory factory;
	
	public MockPriorityUser(String brokerAddress){
		this.factory = new ConnectionFactory();
		this.factory.setHost(brokerAddress);
		/*MockPriorityUserFrame view = new MockPriorityUserFrame(this);
		view.setVisible(true);*/
	}
	
	public void setPath(List<List<String>> path){
		this.path = path;
	}
	
	public void setReceiver(String receiver){
		this.receiver = receiver;
	}

	public void setStartTime(String startTime){
		this.startTime = startTime;
	}

	
	@Override
	public void run() {		
		System.out.println("REC: "+this.receiver);
		System.out.println("START TIME: "+this.startTime);
		for(List<String> l : this.path){
			System.out.println("ROAD:");
			for(String s : l){
				System.out.println(s);
			}
		}
		ISBFMsg msg = this.getSBFMsg();
		try {
			MomUtils.sendMsg(this.factory, receiver, JSONMessagingUtils.getStringfromSFBMsg(msg));
		} catch (IOException | TimeoutException | JSONException e) {
			e.printStackTrace();
		}
	}

	private ISBFMsg getSBFMsg() {
		ISBFMsg msg;
		int numCells = 0;
		for(List<String> l : this.path){
			numCells += l.size();
		}
		SpatialBloomFilter sbf = SpatialBloomFilter.INSTANCE;
		int bitMapping = SBFUtils.getSBFParams(numCells).getFirst();
		int hashNumber = SBFUtils.getSBFParams(numCells).getSecond();
		sbf.CreateSBF(SBFUtils.getSBFParams(numCells).getFirst(), 4, SBFUtils.getSBFParams(numCells).getSecond(), 255, "salt.txt");
    	for(int i = this.path.size()-1; i>=0; i--){
    		for(String s : this.path.get(i)){
    			sbf.Insert(s, s.length(), i+200);
    		}
		}
		sbf.SaveToDisk("sbf.csv", 0);
		List<Integer> sbfcontent = null;
		String salt = "";
		try {
			Scanner s = new Scanner(new FileReader("sbf.csv"));
			sbfcontent = new ArrayList<>();
			while (s.hasNextInt()) {
				sbfcontent.add(s.nextInt());
			}
			s.close();
			Scanner s1 = new Scanner(new FileReader("salt.txt"));
			while(s1.hasNextLine()){
				salt += s1.nextLine()+"\n";
			}
			s1.close();
			System.out.println(salt);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		msg = new SBFMsg(MessagingUtils.SBF_MSG, bitMapping, 4, hashNumber, 
				this.path.size(), salt, sbfcontent);
		return msg;
	}
}
