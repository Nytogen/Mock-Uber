package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Navigation extends Endpoint{
    // TODO Implement when the two people are on the same road

    /**
     * GET /location/navigation/:driver?passengerUid=
     * @param uid, radius
     * @return 200, 400, 404, 500
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
        String passengerUid;

        if(params.length != 2 ){
            this.sendStatus(r, 400, true);
            return;
        }

        try{
            String[] radiusString = params[1].split("=");

            if(!radiusString[0].equals("passengerUid")) {
                this.sendStatus(r, 400, true);
                return;
            }

            passengerUid = radiusString[1];

        }catch(Exception e){
            this.sendStatus(r, 400, true);
            return;
        }

        //Running the actual method
        try {
            String driverId = params[0];

            Result driverInfo = this.dao.getUserLocationByUid(driverId);
            Result passengerInfo = this.dao.getUserLocationByUid(passengerUid);
            if (driverInfo.hasNext() && passengerInfo.hasNext()) {
                //Get driver and passenger streets
                String driverRes;
                String passengerRes;

                Record user = driverInfo.next();
                driverRes = user.get("n.street").asString();

                user = passengerInfo.next();
                passengerRes = user.get("n.street").asString();

                List<JSONObject> route = new ArrayList<JSONObject>();
                int totalTime = 0;

                //On the same road
                if(driverRes.equals(passengerRes)){
                    Result sameRoad = this.dao.getRoad(driverRes);

                    if(!sameRoad.hasNext()){
                        this.sendStatus(r, 404, true);
                        return;
                    }

                    Node node = sameRoad.next().get("n").asNode();

                    JSONObject current = new JSONObject();
                    current.put("street", passengerRes);
                    current.put("time", 0);
                    current.put("is_traffic", node.get("is_traffic").asBoolean());

                    route.add(current);
                }
                else{
                    //Not on the same road
                    Result navPath = this.dao.getShortestPath(driverRes, passengerRes);

                    if(navPath.hasNext()) {
                        Record path = navPath.next();
                        Iterator<Node> nodeIterator = path.get("p").asPath().nodes().iterator();
                        Iterator<Relationship> edgeIterator = path.get("p").asPath().relationships().iterator();


                        JSONObject current = new JSONObject();


                        Node currentStreet;
                        Relationship currentRelationship;

                        currentStreet = nodeIterator.next();

                        current.put("street", currentStreet.get("name").asString());
                        current.put("time", 0);
                        current.put("is_traffic", currentStreet.get("is_traffic").asBoolean());

                        route.add(current);

                        while (edgeIterator.hasNext()) {
                            current = new JSONObject();
                            currentStreet = nodeIterator.next();
                            currentRelationship = edgeIterator.next();

                            current.put("street", currentStreet.get("name").asString());
                            current.put("time", currentRelationship.get("travel_time").asInt());
                            current.put("is_traffic", currentStreet.get("is_traffic").asBoolean());

                            totalTime += current.getInt("time");

                            route.add(current);
                        }

                    }
                    else{
                        this.sendStatus(r, 404, true);
                        return;
                    }
                }
                JSONObject res = new JSONObject();
                JSONObject data = new JSONObject();

                data.put("total_time", totalTime);
                data.put("route", route);

                res.put("data", data);

                this.sendResponse(r, res, 200);
            } else {
                this.sendStatus(r, 404, true);
                return;
            }
            return;
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500, true);
            return;
        }
    }
}


