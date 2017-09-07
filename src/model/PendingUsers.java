package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import model.interfaces.IPair;
import model.interfaces.IPendingUsers;

public class PendingUsers implements IPendingUsers {

	private Map<String,Set<IPair<Integer,Integer>>> pendingUsers;
	
	public PendingUsers(){
		this.pendingUsers = new HashMap<>();
	}
	
	@Override
	public void addPendingUser(String userId, int travelId, int time) {
		if(!this.pendingUsers.containsKey(userId)){
			Set<IPair<Integer,Integer>> set = new HashSet<>();
			set.add(new Pair<Integer, Integer>(travelId, time));
			this.pendingUsers.put(userId, set);
		} else {
			Set<IPair<Integer,Integer>> set = this.pendingUsers.get(userId);
			if(set!=null){
				set.add(new Pair<Integer, Integer>(travelId, time));
			} else {
				set = new HashSet<>();
				set.add(new Pair<Integer, Integer>(travelId, time));
				this.pendingUsers.put(userId, set);
			}
		}
		/*for(Set<IPair<Integer,Integer>> s : this.pendingUsers.values()){
			//delete "non fresh" values
		}*/
	}

	@Override
	public int getTravelTimeAndRemoveUser(String userId, int travelId) {
		Set<IPair<Integer,Integer>> set = this.pendingUsers.get(userId);
		int ttime = -1;
		if(set!=null){
			for(IPair<Integer, Integer> p : set){
				if(p.getFirst().equals(travelId)){
					ttime = p.getSecond();
					break;
				}
			}
			this.pendingUsers.remove(userId);
		}
		return ttime;
	}

}
