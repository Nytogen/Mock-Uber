package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/*
Please write your tests for the TripInfo Microservice in this class. 
*/

public class TripInfoTests {
    //Tutorial 1 Code to send API Requests neatly and not spagetti
    private static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        //Expecting these unit tests to run after everything has be dockerized and launched
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004" + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @BeforeAll
    public static void RunServer() throws IOException {
        try {
            App.main(null);
        }catch(Exception e){
            System.out.println("Server already running on same port");
        }
    }

    @BeforeEach
    public void clear() throws IOException, InterruptedException {
        sendRequest("/location/clear", "DELETE", "{}");
        sendRequest("/trip/clear", "DELETE", "{}");
    }

    @Test
    public void tripRequestPass() throws IOException, JSONException, InterruptedException {
        JSONObject confirmReq;

        confirmReq = new JSONObject().put("uid", "1234").put("is_driver", false);
        sendRequest("/location/user", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("longitude", 56.235).put("latitude", 70.368).put("street", "Mississauga Rd.");
        sendRequest("/location/1234", "PATCH", confirmReq.toString());


        confirmReq = new JSONObject().put("uid", "1").put("is_driver", true);
        sendRequest("/location/user", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("longitude", 79.3832).put("latitude", 43.6532).put("street", "Queens St. E");
        sendRequest("/location/1", "PATCH", confirmReq.toString());

        confirmReq = new JSONObject().put("uid", "2").put("is_driver", true);
        sendRequest("/location/user", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("longitude", 56.255).put("latitude", 70.168).put("street", "Burnhamthorpe");
        sendRequest("/location/2", "PATCH", confirmReq.toString());

        confirmReq = new JSONObject().put("radius", 1000).put("uid", "1234");
        HttpResponse<String> confirmRes = sendRequest("/trip/request", "POST", confirmReq.toString());

        int status = confirmRes.statusCode();
        assertTrue(status == 200);

        String result = "{\"data\":[\"2\"],\"status\":\"OK\"}";
        assertTrue(result.equals(confirmRes.body()));
    }

    @Test
    public void tripRequestFail() throws IOException, JSONException, InterruptedException {
        JSONObject confirmReq;

        confirmReq = new JSONObject().put("uid", "1234").put("is_driver", false);
        sendRequest("/location/user", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("longitude", 56.235).put("latitude", 70.368).put("street", "Mississauga Rd.");
        sendRequest("/location/1234", "PATCH", confirmReq.toString());


        confirmReq = new JSONObject().put("uid", "1").put("is_driver", true);
        sendRequest("/location/user", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("longitude", 79.3832).put("latitude", 43.6532).put("street", "Queens St. E");
        sendRequest("/location/1", "PATCH", confirmReq.toString());

        confirmReq = new JSONObject().put("uid", "2").put("is_driver", true);
        sendRequest("/location/user", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("longitude", 56.255).put("latitude", 70.168).put("street", "Burnhamthorpe");
        sendRequest("/location/2", "PATCH", confirmReq.toString());

        confirmReq = new JSONObject().put("radius", 1000).put("uid", "1");
        HttpResponse<String> confirmRes = sendRequest("/trip/request", "POST", confirmReq.toString());

        int status = confirmRes.statusCode();
        assertTrue(status == 404);

        String result = "{\"data\":{},\"status\":\"NOT FOUND\"}";
        assertTrue(result.equals(confirmRes.body()));
    }

    @Test
    public void tripConfirmPass() throws IOException, JSONException, InterruptedException {
        JSONObject confirmReq;

        confirmReq = new JSONObject().put("uid", "1234").put("is_driver", false);
        sendRequest("/location/user", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("longitude", 56.235).put("latitude", 70.368).put("street", "Mississauga Rd.");
        sendRequest("/location/1234", "PATCH", confirmReq.toString());


        confirmReq = new JSONObject().put("uid", "1").put("is_driver", true);
        sendRequest("/location/user", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("longitude", 79.3832).put("latitude", 43.6532).put("street", "Queens St. E");
        sendRequest("/location/1", "PATCH", confirmReq.toString());

        confirmReq = new JSONObject().put("roadName", "Mississauga Rd.").put("hasTraffic", false);
        sendRequest("/location/road", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("roadName", "Alta Street").put("hasTraffic", true);
        sendRequest("/location/road", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("roadName", "Queens St. E").put("hasTraffic", false);
        sendRequest("/location/road", "PUT", confirmReq.toString());

        confirmReq = new JSONObject().put("roadName1", "Queens St. E").put("roadName2", "Mississauga Rd.").put("hasTraffic", true).put("time", 20);
        sendRequest("/location/hasRoute", "POST", confirmReq.toString());
        confirmReq = new JSONObject().put("roadName1", "Queens St. E").put("roadName2", "Alta Street").put("hasTraffic", false).put("time", 5);
        sendRequest("/location/hasRoute", "POST", confirmReq.toString());
        confirmReq = new JSONObject().put("roadName1", "Alta Street").put("roadName2", "Mississauga Rd.").put("hasTraffic", true).put("time", 5);
        sendRequest("/location/hasRoute", "POST", confirmReq.toString());

        confirmReq = new JSONObject().put("driver", "1").put("passenger", "1234").put("startTime", 1000);
        HttpResponse<String> confirmRes = sendRequest("/trip/confirm", "POST", confirmReq.toString());

        int status = confirmRes.statusCode();
        assertTrue(status == 200);

    }

    @Test
    public void tripConfirmFail() throws IOException, JSONException, InterruptedException {
        JSONObject confirmReq;

        confirmReq = new JSONObject().put("driver", "1").put("passenger", "1234").put("startTime", 1000);
        HttpResponse<String> confirmRes = sendRequest("/trip/confirm", "POST", confirmReq.toString());

        int status = confirmRes.statusCode();
        assertTrue(status == 404);
    }

    @Test
    public void patchTripPass() throws IOException, JSONException, InterruptedException {
        JSONObject confirmReq;

        confirmReq = new JSONObject().put("uid", "1234").put("is_driver", false);
        sendRequest("/location/user", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("longitude", 56.235).put("latitude", 70.368).put("street", "Mississauga Rd.");
        sendRequest("/location/1234", "PATCH", confirmReq.toString());


        confirmReq = new JSONObject().put("uid", "1").put("is_driver", true);
        sendRequest("/location/user", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("longitude", 79.3832).put("latitude", 43.6532).put("street", "Queens St. E");
        sendRequest("/location/1", "PATCH", confirmReq.toString());

        confirmReq = new JSONObject().put("roadName", "Mississauga Rd.").put("hasTraffic", false);
        sendRequest("/location/road", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("roadName", "Alta Street").put("hasTraffic", true);
        sendRequest("/location/road", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("roadName", "Queens St. E").put("hasTraffic", false);
        sendRequest("/location/road", "PUT", confirmReq.toString());

        confirmReq = new JSONObject().put("roadName1", "Queens St. E").put("roadName2", "Mississauga Rd.").put("hasTraffic", true).put("time", 20);
        sendRequest("/location/hasRoute", "POST", confirmReq.toString());
        confirmReq = new JSONObject().put("roadName1", "Queens St. E").put("roadName2", "Alta Street").put("hasTraffic", false).put("time", 5);
        sendRequest("/location/hasRoute", "POST", confirmReq.toString());
        confirmReq = new JSONObject().put("roadName1", "Alta Street").put("roadName2", "Mississauga Rd.").put("hasTraffic", true).put("time", 5);
        sendRequest("/location/hasRoute", "POST", confirmReq.toString());

        confirmReq = new JSONObject().put("driver", "1").put("passenger", "1234").put("startTime", 1000);
        HttpResponse<String> confirmRes = sendRequest("/trip/confirm", "POST", confirmReq.toString());

        JSONObject test = new JSONObject(confirmRes.body());
        JSONObject id = test.getJSONObject("data");
        id = id.getJSONObject("_id");

        confirmReq = new JSONObject().put("distance", 100).put("totalCost", 400).put("endTime", 1249).put("timeElapsed", "00:15:00").put("driverPayout", 123).put("discount", 0);
        confirmRes = sendRequest("/trip/" + id.getString("$oid"), "PATCH", confirmReq.toString());

        int status = confirmRes.statusCode();
        System.out.println(status);
        assertTrue(status == 200);

        String result = "{\"status\":\"OK\"}";
        assertTrue(result.equals(confirmRes.body()));
    }

    @Test
    public void patchTripFail() throws IOException, JSONException, InterruptedException {
        JSONObject confirmReq;

        confirmReq = new JSONObject().put("driver", "2").put("passenger", "1234").put("startTime", 1000);
        HttpResponse<String> confirmRes = sendRequest("/trip/61a291e73a17e01985980968", "PATCH", confirmReq.toString());

        int status = confirmRes.statusCode();
        assertTrue(status == 404);

    }

    @Test
    public void tripsForPassengerFail() throws IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject();
        HttpResponse<String> confirmRes = sendRequest("/trip/passenger/test/one", "GET", confirmReq.toString());

        int status = confirmRes.statusCode();
        System.out.println(status);
        assertTrue(status == 400);
    }

    @Test
    public void tripsForPassengerPass() throws IOException, InterruptedException, JSONException {
        JSONObject confirmReq;

        confirmReq = new JSONObject().put("driver", "1").put("passenger", "1234").put("startTime", 1000);
        sendRequest("/trip/confirm", "POST", confirmReq.toString());

        confirmReq = new JSONObject().put("driver", "1").put("passenger", "1234").put("startTime", 1000);
        HttpResponse<String> id = sendRequest("/trip/passenger/1234", "GET", confirmReq.toString());

        int status = id.statusCode();
        System.out.println(status);
        assertTrue(status == 200);

        String result = "{\"data\":{\"arrival_time\":10},\"status\":\"OK\"}";
    }

    @Test
    public void tripsForDriverFail() throws IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject();
        HttpResponse<String> confirmRes = sendRequest("/trip/driver/test/one", "GET", confirmReq.toString());

        int status = confirmRes.statusCode();
        System.out.println(status);
        assertTrue(status == 400);
    }

    @Test
    public void tripsForDriverPass() throws IOException, InterruptedException, JSONException {
        JSONObject confirmReq;

        confirmReq = new JSONObject().put("driver", "1").put("passenger", "1234").put("startTime", 1000);
        sendRequest("/trip/confirm", "POST", confirmReq.toString());

        confirmReq = new JSONObject().put("driver", "1").put("passenger", "1234").put("startTime", 1000);
        HttpResponse<String> id = sendRequest("/trip/driver/1", "GET", confirmReq.toString());

        int status = id.statusCode();
        System.out.println(status);
        assertTrue(status == 200);

        String result = "{\"data\":{\"arrival_time\":10},\"status\":\"OK\"}";
    }

    @Test
    public void driverTimeFail() throws IOException, InterruptedException {
        JSONObject confirmReq = new JSONObject();
        HttpResponse<String> confirmRes = sendRequest("/driverTime/61a291e73a17e01985980968", "GET", confirmReq.toString());

        int status = confirmRes.statusCode();
        assertTrue(status == 404);
    }

    @Test
    public void driverTimePass() throws IOException, InterruptedException, JSONException {
        JSONObject confirmReq;

        confirmReq = new JSONObject().put("uid", "1234").put("is_driver", false);
        sendRequest("/location/user", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("longitude", 56.235).put("latitude", 70.368).put("street", "Mississauga Rd.");
        sendRequest("/location/1234", "PATCH", confirmReq.toString());


        confirmReq = new JSONObject().put("uid", "1").put("is_driver", true);
        sendRequest("/location/user", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("longitude", 79.3832).put("latitude", 43.6532).put("street", "Queens St. E");
        sendRequest("/location/1", "PATCH", confirmReq.toString());

        confirmReq = new JSONObject().put("roadName", "Mississauga Rd.").put("hasTraffic", false);
        sendRequest("/location/road", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("roadName", "Alta Street").put("hasTraffic", true);
        sendRequest("/location/road", "PUT", confirmReq.toString());
        confirmReq = new JSONObject().put("roadName", "Queens St. E").put("hasTraffic", false);
        sendRequest("/location/road", "PUT", confirmReq.toString());

        confirmReq = new JSONObject().put("roadName1", "Queens St. E").put("roadName2", "Mississauga Rd.").put("hasTraffic", true).put("time", 20);
        sendRequest("/location/hasRoute", "POST", confirmReq.toString());
        confirmReq = new JSONObject().put("roadName1", "Queens St. E").put("roadName2", "Alta Street").put("hasTraffic", false).put("time", 5);
        sendRequest("/location/hasRoute", "POST", confirmReq.toString());
        confirmReq = new JSONObject().put("roadName1", "Alta Street").put("roadName2", "Mississauga Rd.").put("hasTraffic", true).put("time", 5);
        sendRequest("/location/hasRoute", "POST", confirmReq.toString());

        confirmReq = new JSONObject().put("driver", "1").put("passenger", "1234").put("startTime", 1000);
        HttpResponse<String> id = sendRequest("/trip/confirm", "POST", confirmReq.toString());

        JSONObject oid = new JSONObject(id.body());

        oid = oid.getJSONObject("data");
        oid = oid.getJSONObject("_id");

        HttpResponse<String> confirmRes = sendRequest("/trip/driverTime/" + oid.get("$oid"), "GET", confirmReq.toString());
        int status = confirmRes.statusCode();
        System.out.println(status);
        assertTrue(status == 200);

        String result = "{\"data\":{\"arrival_time\":10},\"status\":\"OK\"}";
        System.out.println(confirmRes.body());
        assertTrue(result.equals(confirmRes.body()));
    }
}
