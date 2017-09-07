package utils.gps;

import java.util.List;

import model.interfaces.IGPSObserver;
import model.interfaces.INodePath;

public class GpsMock extends Thread {
	private INodePath path;
	//private List<Integer> prefixedTimes;
	//private int currentIndex;
	private IGPSObserver observer;
	private boolean stopGPS;
	
	public GpsMock(INodePath path/*, List<Integer> prefixedTimes*/){
		this.path = path;
		this.stopGPS = false;
		//this.path.removeFirstNode();    //first node is removed. We assume that the user starts from that node
		//this.prefixedTimes = prefixedTimes;
		//this.currentIndex = 0;
	}
	
	public void attachObserver(IGPSObserver observer){
		this.observer = observer;
	}
	
	public void stopGPS(){
		this.stopGPS = true;
	}
	
	@Override
	public void run() {
		/*while(this.currentIndex<this.prefixedTimes.size()){
			try {
				Thread.sleep(this.prefixedTimes.get(this.currentIndex)*1000);
				this.observer.notifyGps(this.path.getPathNodes().get(this.currentIndex).getCoordinates());
				this.currentIndex++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/
		int i = 0;
		this.observer.cellReached(this.path.getPathNodes().get(i).getNodeID());
		this.observer.nodeReached((this.path.getPathNodes().get(i+1).getNodeID()));
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		for(List<String> list : this.path.getBetweenNodes()){
			if(this.stopGPS) break;
			for(String s : list){
				if(this.stopGPS) break;
				System.out.println("reached");
				this.observer.cellReached(s);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			i++;
			if(this.stopGPS) break;
			this.observer.cellReached(this.path.getPathNodes().get(i).getNodeID());
			if(i+1<this.path.getPathNodes().size()){
				this.observer.nodeReached((this.path.getPathNodes().get(i+1).getNodeID()));
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}	
}
