package ca.utoronto.utm.mcs;

import com.mongodb.client.FindIterable;
import com.mongodb.util.JSON;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class Passenger extends Endpoint{
    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] apiURI = r.getRequestURI().toString().split("/");

        //Correct fixed formats
        if (apiURI.length != 4 || apiURI[3].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }

        String uid = apiURI[3];

        //Running the actual method
        try {
            // Get all Trips
            FindIterable<Document> allTrips = this.dao.retrieveDocumentsPassenger(uid);
            ArrayList<JSONObject> fullTrips = new ArrayList<>();
            JSONObject current;

            for(Document doc: allTrips){
                current = new JSONObject();
                current.put("driver",doc.get("driver"));
                current.put("startTime", doc.get("startTime"));
                current.put("_id", doc.get("_id"));

                if(doc.get("endTime") != null){
                    current.put("distance", doc.get("distance"));
                    current.put("totalCost", doc.get("totalCost"));
                    current.put("discount", doc.get("discount"));
                    current.put("endTime", doc.get("endTime"));
                    current.put("timeElapsed", doc.get("timeElapsed"));
                }

                fullTrips.add(current);
            }

            JSONObject data = new JSONObject();
            data.put("trips", fullTrips);

            JSONObject dataSend = new JSONObject();
            dataSend.put("data", data);
            this.sendResponse(r, dataSend,200);
            return;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            this.sendStatus(r, 500, true);
            return;
        }
    }
}
