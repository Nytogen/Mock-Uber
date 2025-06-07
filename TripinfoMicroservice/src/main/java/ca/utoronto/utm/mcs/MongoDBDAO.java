package ca.utoronto.utm.mcs;


import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import io.github.cdimascio.dotenv.Dotenv;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

public class MongoDBDAO {
    private final MongoClient client;
    private final MongoDatabase database;
    private MongoCollection<Document> collection;

    private final String username = "root";
    private final String password = "123456";
    private final String dbName = "trip";
    private final String collectionName = "trips";

    public MongoDBDAO() {
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("MONGODB_ADDR");
        //String uriDb = String.format("mongodb://%s:%s@localhost:27017", username, password);
        String uriDb = String.format("mongodb://%s:%s@%s:27017", username, password, addr);

        this.client = MongoClients.create(uriDb);
        this.database = this.client.getDatabase(this.dbName);
        this.collection = this.database.getCollection(this.collectionName);

    }

    public Document createDocument(String driver, String passenger, int startTime){
        Document doc = new Document();
        doc.put("distance", null);
        doc.put("totalCost", null);
        doc.put("startTime", startTime);
        doc.put("endTime", null);
        doc.put("timeElapsed", null);
        doc.put("driver", driver);
        doc.put("driverPayout", null);
        doc.put("passenger", passenger);
        doc.put("discount", null);
        Bson filter = Filters.and(Filters.eq("driver", driver), Filters.eq("passenger", passenger), Filters.eq("startTime", startTime));
        FindIterable<Document> info = this.collection.find(filter);
        if(info.first() != null){
            return null;
        }
        this.collection.insertOne(doc);
        info = this.collection.find(filter);
        Document trip = info.first();
        return trip;
    }

    public boolean hasDocument(String id) {
        Bson filter = Filters.eq("_id", new ObjectId(id));
        FindIterable<Document> info = this.collection.find(filter);
        if(info.first() == null){
            return false;
        }
        return true;
    }

    public void updateDocument(String id, Bson newObject){
        BasicDBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(id));

        this.collection.findOneAndUpdate(query, newObject);
    }

    public Document retrieveDocument(String id){
        Bson filter = Filters.eq("_id", new ObjectId(id));
        FindIterable<Document> info = this.collection.find(filter);

        return info.first();
    }

    public FindIterable<Document> retrieveDocumentsPassenger(String id){
        Bson filter = Filters.eq("passenger", id);
        return this.collection.find(filter);
    }

    public FindIterable<Document> retrieveDocumentsDriver(String id){
        Bson filter = Filters.eq("driver", id);
        return this.collection.find(filter);
    }

    public void clear(){
        BasicDBObject document = new BasicDBObject();
        this.collection.deleteMany(document);
    }
}
