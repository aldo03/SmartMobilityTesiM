package utils.mongodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import model.Pair;
import model.interfaces.IPair;

public class MongoDBUtils {
	private static final String DB_TIME_TRAVELS = "timetravelsdb";
	private static final String DB_EXPECTED_VEHICLES = "expectedvehiclesdb";
	private static final String DB_CURRENT_TIMES = "currenttimesdb";
	private static final String DB_TEMPHUM = "temphum";
	private static final String DB_ADDR = "localhost";
	
	public static void initDb(){
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_CURRENT_TIMES);
		db.drop();
		/*db = mongoClient.getDatabase(DB_TIME_TRAVELS);
		db.drop();*/
		db = mongoClient.getDatabase(DB_EXPECTED_VEHICLES);
		db.drop();
		db = mongoClient.getDatabase(DB_TEMPHUM);
		db.drop();
		mongoClient.close();
	}
	
	public static void initCurrentTime(String nodeId){
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_CURRENT_TIMES);
		db.getCollection(nodeId).dropIndexes();
		db.getCollection(nodeId).drop();
		mongoClient.close();
	}
	
	public static void initTimes(String nodeId){
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_TIME_TRAVELS);
		db.getCollection(nodeId).dropIndexes();
		db.getCollection(nodeId).drop();
		mongoClient.close();
	}
	
	public static void initExpectedVehicles(String nodeId) {
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_EXPECTED_VEHICLES);
		db.getCollection(nodeId).dropIndexes();
		db.getCollection(nodeId).drop();
		mongoClient.close();
	}
	
	public static void initTempHum(String nodeId, double temp, double hum){
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_TEMPHUM);
		MongoCollection<Document> collection = db.getCollection(nodeId);
		collection.dropIndexes();
		collection.drop();
		Document doc = new Document("temp", temp)
                .append("hum", hum);	
		collection.insertOne(doc);
		mongoClient.close();
	}
	
	public static void initTravelTimes(String nodeId, String nodeId2, List<Integer> list){
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_TIME_TRAVELS);
		MongoCollection<Document> collection = db.getCollection(nodeId);
		Document doc = new Document("_id", nodeId2)
                .append("times", list);
		collection.insertOne(doc);
		mongoClient.close();
	}

	public static void setTravelTime(String nodeId, String nodeId2, int index, int travelTime){
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_TIME_TRAVELS);
		MongoCollection<Document> collection = db.getCollection(nodeId);
		BsonDocument updateQuery  = new BsonDocument().append("_id", new BsonString(nodeId2));
		BsonDocument updateCommand = new BsonDocument("$set", new BsonDocument("times."+index, new BsonInt32(travelTime)));
		collection.updateOne(updateQuery, updateCommand);
		mongoClient.close();
	}
	
	public static void setTemp(String nodeId, double temp){
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_TEMPHUM);
		MongoCollection<Document> collection = db.getCollection(nodeId);
		BsonDocument updateQuery  = new BsonDocument();
		BsonDocument updateCommand = new BsonDocument("$set", new BsonDocument("temp", new BsonDouble(temp)));
		collection.updateOne(updateQuery, updateCommand);
		mongoClient.close();
	}
	
	public static void setHum(String nodeId, double hum){
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_TEMPHUM);
		MongoCollection<Document> collection = db.getCollection(nodeId);
		BsonDocument updateQuery  = new BsonDocument();
		BsonDocument updateCommand = new BsonDocument("$set", new BsonDocument("hum", new BsonDouble(hum)));
		collection.updateOne(updateQuery, updateCommand);
		mongoClient.close();
	}
	
	public static void initCurrentTime(String nodeId, String nodeId2){
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_CURRENT_TIMES);
		MongoCollection<Document> collection = db.getCollection(nodeId);
		Document doc = new Document("_id", nodeId2)
                .append("times", new ArrayList<>());
		collection.insertOne(doc);
		mongoClient.close();
	}
	
	public static void addCurrentTime(String nodeId, String nodeId2, int time){
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_CURRENT_TIMES);
		MongoCollection<Document> collection = db.getCollection(nodeId);
		BsonDocument updateQuery  = new BsonDocument().append("_id", new BsonString(nodeId2));
		BsonDocument updateCommand = new BsonDocument("$push", new BsonDocument("times", new BsonInt32(time)));
		collection.updateOne(updateQuery, updateCommand);
		mongoClient.close();
	}
	
	public static void clearCurrentTimes(String nodeId, Set<String> ids){
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_CURRENT_TIMES);
		MongoCollection<Document> collection = db.getCollection(nodeId);
		for(String s : ids){
			BsonDocument updateQuery  = new BsonDocument().append("_id", new BsonString(s));
			BsonDocument updateCommand = new BsonDocument("$set", new BsonDocument("times", new BsonArray()));
			collection.updateOne(updateQuery, updateCommand);
		}
		mongoClient.close();
	}
	
	public static void initExpectedVehicles(String nodeId, String nodeId2){
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_EXPECTED_VEHICLES);
		MongoCollection<Document> collection = db.getCollection(nodeId);
		Document doc = new Document("_id", nodeId2)
                .append("vehicles", new ArrayList<>());
		collection.insertOne(doc);
		mongoClient.close();
	}
	
	public static void addExpectedVehicle(String nodeId, String nodeId2, int time){
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_EXPECTED_VEHICLES);
		MongoCollection<Document> collection = db.getCollection(nodeId);
		BsonDocument updateQuery  = new BsonDocument().append("_id", new BsonString(nodeId2));
		BsonDocument updateCommand = new BsonDocument("$push", new BsonDocument("vehicles", new BsonInt32(time)));
		collection.updateOne(updateQuery, updateCommand);
		mongoClient.close();
	}
	
	public static void removeExpectedVehicles(String nodeId, String nodeId2, int freshTime){
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_EXPECTED_VEHICLES);
		MongoCollection<Document> collection = db.getCollection(nodeId);
		BsonDocument updateQuery  = new BsonDocument().append("_id", new BsonString(nodeId2));
		BsonDocument updateCommand = new BsonDocument("$pull", new BsonDocument("vehicles", new BsonDocument("$lt", new BsonInt32(freshTime))));
		collection.updateOne(updateQuery, updateCommand);
		mongoClient.close();
	}
	
	public static Map<String, List<Integer>> getCurrentTimes(String nodeId){
		Map<String, List<Integer>> curTimes = new HashMap<>();
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_CURRENT_TIMES);
		MongoCollection<Document> collection = db.getCollection(nodeId);
		FindIterable<Document> iterable = collection.find();
		MongoCursor<Document> cursor = iterable.iterator();
		while(cursor.hasNext()){
			Document d = cursor.next();
			String id = d.getString("_id");
			//System.out.println(id);
			List<Integer> list = (List<Integer>) d.get("times");
			curTimes.put(id, list);
		}
		mongoClient.close();
		return curTimes;
	}
	
	public static Map<String, List<Integer>> getExpectedVehicles(String nodeId){
		Map<String, List<Integer>> curTimes = new HashMap<>();
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_EXPECTED_VEHICLES);
		MongoCollection<Document> collection = db.getCollection(nodeId);
		FindIterable<Document> iterable = collection.find();
		MongoCursor<Document> cursor = iterable.iterator();
		while(cursor.hasNext()){
			Document d = cursor.next();
			String id = d.getString("_id");
			//System.out.println(id);
			List<Integer> list = (List<Integer>) d.get("vehicles");
			curTimes.put(id, list);
		}
		mongoClient.close();
		return curTimes;
	}
	
	public static Map<String, List<Integer>> getTimeTravels(String nodeId){
		Map<String, List<Integer>> curTimes = new HashMap<>();
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_TIME_TRAVELS);
		MongoCollection<Document> collection = db.getCollection(nodeId);
		FindIterable<Document> iterable = collection.find();
		MongoCursor<Document> cursor = iterable.iterator();
		while(cursor.hasNext()){
			Document d = cursor.next();
			String id = d.getString("_id");
			//System.out.println(id);
			List<Integer> list = (List<Integer>) d.get("times");
			curTimes.put(id, list);
		}
		mongoClient.close();
		return curTimes;
	}
	
	public static IPair<Double, Double> getTempHum(String nodeId){
		MongoClient mongoClient = new MongoClient( DB_ADDR );
		MongoDatabase db = mongoClient.getDatabase(DB_TEMPHUM);
		MongoCollection<Document> collection = db.getCollection(nodeId);
		FindIterable<Document> iterable = collection.find();
		Document d = iterable.first();
		double temp = d.getDouble("temp");
		double hum = d.getDouble("hum");
		mongoClient.close();
		return new Pair<Double, Double>(temp, hum);
	}
}
