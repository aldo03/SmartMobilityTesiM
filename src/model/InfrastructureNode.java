package model;

import model.interfaces.ICoordinates;
import model.interfaces.IInfrastructureNode;
import utils.sbf.SBFUtils;

public class InfrastructureNode implements IInfrastructureNode {
	
	private String id;
	private ICoordinates coordinates;
	
	public InfrastructureNode(String id){
		this.id = id;
		this.coordinates = SBFUtils.getCenterCoordinatesFromCell(id);
	}
	
	@Override
	public String getNodeID() {
		return this.id;
	}

	@Override
	public ICoordinates getCoordinates() {
		return this.coordinates;
	}

	@Override
	public void setCoordinates(ICoordinates coordinates) {
		this.coordinates = coordinates;
		
	}



}
