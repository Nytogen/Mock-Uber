package ca.utoronto.utm.mcs;

import com.mongodb.util.JSON;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Confirm extends Endpoint{
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("driver") && body.has("passenger") && body.has("startTime")) {
            String driver = body.getString("driver");
            String passenger = body.getString("passenger");
            int start = body.getInt("startTime");

            try {
                HttpResponse<String> nearByres = this.sendRequest("/location/navigation/" + driver + "?passengerUid=" + passenger, "GET",(new JSONObject()).toString());

                if(nearByres.statusCode() != 200){
                    this.sendStatus(r, 404, true);
                    return;
                }

                Document res = this.dao.createDocument(driver, passenger, start);
                if(res == null){
                    this.sendStatus(r, 400, true);
                    return;
                }

                JSONObject resJSON = new JSONObject(res.toJson());
                JSONObject id = resJSON.getJSONObject("_id");
                JSONObject returnJson = new JSONObject();
                returnJson.put("_id", id);

                JSONObject dataReturn = new JSONObject();
                dataReturn.put("data", returnJson);

                this.sendResponse(r, dataReturn, 200);
                return;
            } catch(Exception e){
                this.sendStatus(r, 500, true);
                return;
            }

        } else {
            this.sendStatus(r, 400, true);
            return;
        }
    }
}
