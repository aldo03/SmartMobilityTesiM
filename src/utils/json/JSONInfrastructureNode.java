package utils.json;

import org.json.JSONException;
import org.json.JSONObject;

import model.InfrastructureNode;
import model.interfaces.IInfrastructureNode;

public class JSONInfrastructureNode extends JSONObject {
	private static final String COORDINATES = "coordinates";
	private static final String ID = "id";
	public JSONInfrastructureNode(IInfrastructureNode node){
		try {
			this.put(ID, node.getNodeID());
			if(node.getCoordinates()!=null){
				this.put(COORDINATES, new JSONCoordinates(node.getCoordinates()));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static IInfrastructureNode getInfrastructureNodeFromJSONObject(JSONObject obj) throws JSONException{
		IInfrastructureNode node = new InfrastructureNode(obj.getString(ID));
		return node;
	}
	
	public static IInfrastructureNode getInfrastructureNodeWithCoordinatesFromJSONObject(JSONObject obj) throws JSONException{
		IInfrastructureNode node = new InfrastructureNode(obj.getString(ID));
		node.setCoordinates(JSONCoordinates.getCoordinatesFromJSONObject((obj.getJSONObject(COORDINATES))));
		return node;
	}
}
