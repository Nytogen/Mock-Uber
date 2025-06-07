package ca.utoronto.utm.mcs;

import com.mongodb.util.JSON;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DriverTime extends Endpoint{
    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] apiURI = r.getRequestURI().toString().split("/");

        //Correct fixed formats
        if (apiURI.length != 4 || apiURI[3].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }

        //Running the actual method
        try {
            String id = apiURI[3];

            if(!ObjectId.isValid(id)){
                this.sendStatus(r, 400, true);
                return;
            }

            if(this.dao.hasDocument(id)){
                Document doc = this.dao.retrieveDocument(id);
                JSONObject info = new JSONObject(doc.toJson());


                String driver = info.getString("driver");
                String passenger = info.getString("passenger");

                String endpoint = String.format("/location/navigation/%s?passengerUid=%s", driver, passenger);

                HttpResponse<String> res =this.sendRequest(endpoint, "GET", "{}");

                if(res.statusCode() != 200){
                    this.sendStatus(r, res.statusCode(), true);
                    return;
                }

                JSONObject navRes = new JSONObject(res.body());
                JSONObject navData = navRes.getJSONObject("data");
                JSONObject resSend = new JSONObject();
                resSend.put("arrival_time", navData.get("total_time"));

                JSONObject dataSend = new JSONObject();
                dataSend.put("data", resSend);

                this.sendResponse(r, dataSend,200);

            } else{
                this.sendStatus(r, 404, true);
            }
            return;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            this.sendStatus(r, 500, true);
            return;
        }
    }
}
