package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

import java.io.IOException;

public class Nearby extends Endpoint{
    // TODO

    /**
     * GET /location/nearbyDriver/:uid?radius=
     * @param uid, radius
     * @return 200, 404, 400, 500
     * Get the current location for a certain user.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String[] apiURI = r.getRequestURI().toString().split("/");

        //Correct fixed formats
        if (apiURI.length != 4 || apiURI[3].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }

        //Correct variable format
        String[] params = apiURI[3].split("\\?");
        int radius;

        if(params.length != 2 ){
            this.sendStatus(r, 400, true);
            return;
        }

        try{
            String[] radiusString = params[1].split("=");

            if(!radiusString[0].equals("radius")){
                this.sendStatus(r, 400, true);
                return;
            }

            radius = Integer.parseInt(radiusString[1]);
        }catch(Exception e){
            this.sendStatus(r, 400, true);
            return;
        }

        //Running the actual method
        try {
            String uid = params[0];


            Result result = this.dao.getUserLocationByUid(uid);
            if (result.hasNext()) {
                //Get User Info
                JSONObject res = new JSONObject();

                Record user = result.next();
                Double userLong = user.get("n.longitude").asDouble();
                Double userLat = user.get("n.latitude").asDouble();

                //Get Neo4j to get nearby drivers
                Result nearbyDrivers = this.dao.getNearbyDrivers(userLat, userLong, radius, uid);

                JSONObject data = new JSONObject();

                JSONObject driverData = new JSONObject();
                int count = 1;

                //Check for at least 1 driver nearby
                if(nearbyDrivers.hasNext()) {
                    //Get Driver Data that are close by
                    while (nearbyDrivers.hasNext()) {
                        Record driver = nearbyDrivers.next();

                        driverData = new JSONObject();
                        driverData.put("latitude",driver.get("n.latitude").asDouble());
                        driverData.put("longitude",driver.get("n.longitude").asDouble());
                        driverData.put("street",driver.get("n.street").asString());

                        data.put(driver.get("n.uid").asString(), driverData);
                        count++;
                    };

                    res.put("data", data);
                    this.sendResponse(r, res, 200);
                } else {
                    this.sendStatus(r, 404, true);
                }
                return;

            } else {
                this.sendStatus(r, 404, true);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500, true);
            return;
        }
    }
}
