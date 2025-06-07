package ca.utoronto.utm.mcs;

import com.mongodb.util.JSON;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Request extends Endpoint {

    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("uid") && body.has("radius")) {
            String uid = body.getString("uid");
            int radius = body.getInt("radius");

            try {
                String endpoint = String.format("/location/nearbyDriver/%s?radius=%d", uid, radius);
                HttpResponse<String> res =this.sendRequest(endpoint, "GET", "{}");

                if(res.statusCode() != 200){
                    this.sendStatus(r, res.statusCode(), true);
                    return;
                }

                JSONObject resData = new JSONObject(res.body());
                JSONObject data = (JSONObject) resData.getJSONObject("data");

                Iterator<String> driverIter = data.keys();
                List<String> driverId = new ArrayList<>();

                while(driverIter.hasNext()){
                    driverId.add(driverIter.next());
                }

                JSONObject resp = new JSONObject();
                resp.put("data", driverId);

                this.sendResponse(r, resp ,200);
                return;

            } catch (Exception e) {
                e.printStackTrace();
                this.sendStatus(r, 500, true);
                return;
            }

        } else {
            this.sendStatus(r, 400, true);
        }
    }
}
