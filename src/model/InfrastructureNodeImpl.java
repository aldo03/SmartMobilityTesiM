package model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.interfaces.ICoordinates;
import model.interfaces.IInfrastructureNodeImpl;
import model.interfaces.IPair;
import utils.sbf.SBFUtils;

public class InfrastructureNodeImpl implements IInfrastructureNodeImpl {

	private String nodeID;
	private ICoordinates coordinates;
	private Set<IPair<String,List<String>>> nearNodesWeighted;

	public InfrastructureNodeImpl(String nodeID) {
		super();
		this.nodeID = nodeID;
		this.coordinates = SBFUtils.getCenterCoordinatesFromCell(nodeID);
		this.nearNodesWeighted = new HashSet<>();
	}

	public InfrastructureNodeImpl(String nodeID, ICoordinates coordinates) {
		super();
		this.nodeID = nodeID;
		this.coordinates = coordinates;
		this.nearNodesWeighted = new HashSet<>();
	}
	
	@Override
	public List<String> getBetweenNodes(String id){
		for(IPair<String,List<String>> p : nearNodesWeighted){
			if(p.getFirst().equals(id)){
				return p.getSecond();
			}
		}
		return null;
	}
	@Override
	public String getNodeID() {
		return this.nodeID;
	}

	@Override
	public ICoordinates getCoordinates() {
		return this.coordinates;
	}


	@Override
	public void setCoordinates(ICoordinates coordinates) {
		this.coordinates = coordinates;
	}

	@Override
	public Set<IPair<String,List<String>>> getNearNodesWeighted() {
		return this.nearNodesWeighted;
	}

	@Override
	public int hashCode() {
		return nodeID.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		InfrastructureNodeImpl node = (InfrastructureNodeImpl) obj;
		return node.getNodeID().equals(obj);
	}

	@Override
	public void setNearNodesWeighted(Set<IPair<String, List<String>>> nodes) {
		for(IPair<String, List<String>> p : nodes){
			this.nearNodesWeighted.add(p);
		}
		
	}
}