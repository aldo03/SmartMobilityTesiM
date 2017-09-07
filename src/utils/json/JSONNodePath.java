package utils.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import model.NodePath;
import model.interfaces.IInfrastructureNode;
import model.interfaces.INodePath;

public class JSONNodePath extends JSONArray {
	public JSONNodePath(INodePath path){
		for(IInfrastructureNode node : path.getPathNodes()){
			this.put(new JSONInfrastructureNode(node));
		}
	}
	
	public static INodePath getNodePathfromJSONArray(JSONArray array, JSONArray betweenNodes, JSONArray travelTimes) throws JSONException{
		INodePath path;
		List<IInfrastructureNode> list = new ArrayList<>();   
		for(int i = 0; i < array.length(); i++){
			IInfrastructureNode node = JSONInfrastructureNode.getInfrastructureNodeFromJSONObject(array.getJSONObject(i));
			list.add(node);
		}
		List<List<String>> listNodes = new ArrayList<>();
		for(int i = 0; i < betweenNodes.length(); i++){
			JSONArray a = betweenNodes.getJSONArray(i);
			List<String> l = new ArrayList<>();
			for(int j = 0; j < a.length(); j++){
				l.add(a.getString(j));
			}
			listNodes.add(l);
		}
		List<Integer> listTimes = new ArrayList<>();
		for(int i = 0; i < travelTimes.length(); i++){
			listTimes.add(travelTimes.getInt(i));
		}
		path = new NodePath(list);
		path.setBetweenNodes(listNodes);
		path.setTravelTimes(listTimes);
		return path;
	}
	
	public static INodePath getNodePathfromJSONArray(JSONArray array) throws JSONException{
		INodePath path;
		List<IInfrastructureNode> list = new ArrayList<>();
		for(int i = 0; i < array.length(); i++){
			IInfrastructureNode node = JSONInfrastructureNode.getInfrastructureNodeFromJSONObject((array.getJSONObject(i)));
			list.add(node);
		}
		path = new NodePath(list);
		return path;
	}
}
