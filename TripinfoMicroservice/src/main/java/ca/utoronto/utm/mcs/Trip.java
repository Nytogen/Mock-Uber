package ca.utoronto.utm.mcs;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Updates;
import com.sun.net.httpserver.HttpExchange;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Trip extends Endpoint{

    public void handlePatch(HttpExchange r) throws IOException, JSONException {
        //URI checking
        String[] params = r.getRequestURI().toString().split("/");

        if (params.length != 3 || params[2].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }
        try {
            if(!ObjectId.isValid(params[2])){
                this.sendStatus(r, 400);
                return;
            }

            if(!this.dao.hasDocument(params[2])){
                this.sendStatus(r, 404);
                return;
            }

            //Body Checking
            JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
            ArrayList<String> stringParmas = new ArrayList<>();
            ArrayList<String> intParams = new ArrayList<>();

            stringParmas.add("timeElapsed");
            intParams.add("distance");
            intParams.add("endTime");
            intParams.add("discount");

            if(!this.validateFields(body, stringParmas, intParams)){
                this.sendStatus(r, 400);
                return;
            }

            String time = body.getString("timeElapsed");
            String[] formatted = time.split(":");

            if(formatted.length != 3){
                this.sendStatus(r, 400);
                return;
            }

            Double cost;
            try {
                int test;
                for (int i = 0; i < 3; i++) {
                    test = Integer.parseInt(formatted[i]);
                    if(i != 0 &&(test < 0 || test >= 60)){
                        this.sendStatus(r, 400);
                        return;
                    }
                }
                cost = body.getDouble("totalCost");
            }catch(Exception e){
                this.sendStatus(r, 400);
                return;
            }

            String id = params[2];

            this.dao.updateDocument(id, Updates.set("timeElapsed",body.getString("timeElapsed")));
            this.dao.updateDocument(id, Updates.set("distance",body.getInt("distance")));
            this.dao.updateDocument(id, Updates.set("endTime",body.getInt("endTime")));
            this.dao.updateDocument(id, Updates.set("discount",body.getInt("discount")));
            this.dao.updateDocument(id, Updates.set("totalCost",cost));
            this.dao.updateDocument(id, Updates.set("driverPayout",body.getInt("driverPayout")));

            this.sendStatus(r, 200);
            return;

        }catch(Exception e){
            System.out.println(e.getMessage());
            this.sendStatus(r, 500);
            return;
        }
    }

}
