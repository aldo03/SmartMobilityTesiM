package model;

import java.util.List;

import model.interfaces.IInfrastructureNode;
import model.interfaces.INodePath;

public class NodePath implements INodePath {

	private List<IInfrastructureNode> pathNodes;
	private List<List<String>> betweenNodes;
	private List<Integer> travelTimes;

	public NodePath(List<IInfrastructureNode> pathNodes) {
		this.pathNodes = pathNodes;
	}

	@Override
	public List<IInfrastructureNode> getPathNodes() {
		return this.pathNodes;
	}

	@Override
	public List<List<String>> getBetweenNodes() {
		return this.betweenNodes;
	}
	
	@Override
	public List<Integer> getTravelTimes() {
		return this.travelTimes;
	}

	@Override
	public void removeFirstNode() {
		this.pathNodes.remove(0);
		this.betweenNodes.remove(0);
	}

	@Override
	public void setPath(List<IInfrastructureNode> path) {
		this.pathNodes = path;
	}

	@Override
	public void setBetweenNodes(List<List<String>> betweenNodes) {
		this.betweenNodes = betweenNodes;
	}
	

	@Override
	public void setTravelTimes(List<Integer> times) {
		this.travelTimes = times;
	}

	@Override
	public void printPath() {
		System.out.println("PATH:");
		int i = 0;
		for (IInfrastructureNode node : this.pathNodes) {
			System.out.println("Node: " + node.getNodeID());
			if (i < this.betweenNodes.size()) {
				System.out.println("Between Nodes: ");
				for (String s : this.betweenNodes.get(i)) {
					System.out.println(s);
				}
			}
			i++;
		}
	}
	
	@Override
	public String toString(){
		String s = "PATH:";
		int i = 0;
		for (IInfrastructureNode node : this.pathNodes) {
			s+="\n";
			s+="Node: " + node.getNodeID();
			if (i < this.betweenNodes.size()) {
				s+="\n";
				s+="Between Nodes: ";
				System.out.println("Between Nodes: ");
				for (String s1 : this.betweenNodes.get(i)) {
					s+="\n";
					s+=s1;
				}
			}
			i++;
		}
		return s;
	}

	@Override
	public String getCondensedPath() {
		String s="";
		int i = 0;
		for (IInfrastructureNode node : this.pathNodes) {
			s+=node.getNodeID();
			if(i+1<this.pathNodes.size()){
				s+=",";
			}
			if (i < this.betweenNodes.size() && this.betweenNodes.size()>0) {
				int j = 0;
				for (String s1 : this.betweenNodes.get(i)) {
					s+=s1;
					j++;
					if(j<this.betweenNodes.get(i).size()){
						s+=",";
					}
					
				}
			}
			i++;
			if(i<this.pathNodes.size()){
				s+="/";
			}
		}
		return s;
	}


	
}
