package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AppTest {
   @Test
   public void exampleTest() {
      assertTrue(true);
   }
   /*
   //Tutorial 1 Code to send API Requests neatly and not spagetti
   private static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {

      HttpClient client = HttpClient.newHttpClient();

      HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create("http://localhost:8000/location" + endpoint))
              .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
              .build();

      return client.send(request, HttpResponse.BodyHandlers.ofString());
   }


   @BeforeAll
   public static void RunServer() throws IOException {
      //Run the server
      App.main(null);
   }

   @BeforeEach
   public void clear() throws IOException {
      URL url = new URL("http://localhost:8000/location/clear");
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("DELETE");
      con.getResponseCode();
   }

   @Test
   public void getNearbyDriverPass() throws IOException, JSONException, InterruptedException {
      JSONObject confirmReq;

      confirmReq = new JSONObject().put("uid", "1234").put("is_driver", false);
      sendRequest("/user", "PUT", confirmReq.toString());
      confirmReq = new JSONObject().put("longitude", 56.235).put("latitude", 70.368).put("street", "Mississauga Rd.");
      sendRequest("/1234", "PATCH", confirmReq.toString());


      confirmReq = new JSONObject().put("uid", "1").put("is_driver", true);
      sendRequest("/user", "PUT", confirmReq.toString());
      confirmReq = new JSONObject().put("longitude", 79.3832).put("latitude", 43.6532).put("street", "Queens St. E");
      sendRequest("/1", "PATCH", confirmReq.toString());

      confirmReq = new JSONObject().put("uid", "2").put("is_driver", true);
      sendRequest("/user", "PUT", confirmReq.toString());
      confirmReq = new JSONObject().put("longitude", 56.255).put("latitude", 70.168).put("street", "Burnhamthorpe");
      sendRequest("/2", "PATCH", confirmReq.toString());

      HttpResponse<String> confirmRes = sendRequest("/nearbyDriver/1234?radius=1000", "GET", confirmReq.toString());

      int status = confirmRes.statusCode();
      assertTrue(status == 200);

      String result = "{\"data\":{\"2\":{\"street\":\"Burnhamthorpe\",\"latitude\":70.168,\"longitude\":56.255}},\"status\":\"OK\"}";
      assertTrue(result.equals(confirmRes.body()));
   }

   @Test
   public void getNearbyDriverFail() throws IOException, JSONException, InterruptedException {
      JSONObject confirmReq;

      confirmReq = new JSONObject().put("uid", "1234").put("is_driver", false);
      sendRequest("/user", "PUT", confirmReq.toString());
      confirmReq = new JSONObject().put("longitude", 56.235).put("latitude", 70.368).put("street", "Mississauga Rd.");
      sendRequest("/1234", "PATCH", confirmReq.toString());


      confirmReq = new JSONObject().put("uid", "1").put("is_driver", true);
      sendRequest("/user", "PUT", confirmReq.toString());
      confirmReq = new JSONObject().put("longitude", 79.3832).put("latitude", 43.6532).put("street", "Queens St. E");
      sendRequest("/1", "PATCH", confirmReq.toString());

      confirmReq = new JSONObject().put("uid", "2").put("is_driver", true);
      sendRequest("/user", "PUT", confirmReq.toString());
      confirmReq = new JSONObject().put("longitude", 56.255).put("latitude", 70.168).put("street", "Burnhamthorpe");
      sendRequest("/2", "PATCH", confirmReq.toString());

      HttpResponse<String> confirmRes = sendRequest("/nearbyDriver/1?radius=1000", "GET", confirmReq.toString());

      int status = confirmRes.statusCode();
      assertTrue(status == 404);

      String result = "{\"data\":{},\"status\":\"NOT FOUND\"}";
      assertTrue(result.equals(confirmRes.body()));
   }

   @Test
   public void getNavigationPass() throws IOException, JSONException, InterruptedException {
      JSONObject confirmReq;

      confirmReq = new JSONObject().put("uid", "1234").put("is_driver", false);
      sendRequest("/user", "PUT", confirmReq.toString());
      confirmReq = new JSONObject().put("longitude", 56.235).put("latitude", 70.368).put("street", "Mississauga Rd.");
      sendRequest("/1234", "PATCH", confirmReq.toString());


      confirmReq = new JSONObject().put("uid", "1").put("is_driver", true);
      sendRequest("/user", "PUT", confirmReq.toString());
      confirmReq = new JSONObject().put("longitude", 79.3832).put("latitude", 43.6532).put("street", "Queens St. E");
      sendRequest("/1", "PATCH", confirmReq.toString());

      confirmReq = new JSONObject().put("roadName", "Mississauga Rd.").put("hasTraffic", false);
      sendRequest("/road", "PUT", confirmReq.toString());
      confirmReq = new JSONObject().put("roadName", "Alta Street").put("hasTraffic", true);
      sendRequest("/road", "PUT", confirmReq.toString());
      confirmReq = new JSONObject().put("roadName", "Queens St. E").put("hasTraffic", false);
      sendRequest("/road", "PUT", confirmReq.toString());

      confirmReq = new JSONObject().put("roadName1", "Queens St. E").put("roadName2", "Mississauga Rd.").put("hasTraffic", true).put("time", 20);
      sendRequest("/hasRoute", "POST", confirmReq.toString());
      confirmReq = new JSONObject().put("roadName1", "Queens St. E").put("roadName2", "Alta Street").put("hasTraffic", false).put("time", 5);
      sendRequest("/hasRoute", "POST", confirmReq.toString());
      confirmReq = new JSONObject().put("roadName1", "Alta Street").put("roadName2", "Mississauga Rd.").put("hasTraffic", true).put("time", 5);
      sendRequest("/hasRoute", "POST", confirmReq.toString());

      HttpResponse<String> confirmRes = sendRequest("/navigation/1?passengerUid=1234", "GET", confirmReq.toString());

      int status = confirmRes.statusCode();
      assertTrue(status == 200);

      String result = "{\"data\":{\"route\":[{\"street\":\"Queens St. E\",\"is_traffic\":false,\"time\":0},{\"street\":\"Alta Street\",\"is_traffic\":true,\"time\":5},{\"street\":\"Mississauga Rd.\",\"is_traffic\":false,\"time\":5}],\"total_time\":10},\"status\":\"OK\"}";
      assertTrue(result.equals(confirmRes.body()));
   }

   @Test
   public void getNavigationFail() throws IOException, JSONException, InterruptedException {
      JSONObject confirmReq;

      confirmReq = new JSONObject().put("uid", "1234").put("is_driver", false);
      sendRequest("/user", "PUT", confirmReq.toString());
      confirmReq = new JSONObject().put("longitude", 56.235).put("latitude", 70.368).put("street", "Mississauga Rd.");
      sendRequest("/1234", "PATCH", confirmReq.toString());


      confirmReq = new JSONObject().put("uid", "1").put("is_driver", true);
      sendRequest("/user", "PUT", confirmReq.toString());
      confirmReq = new JSONObject().put("longitude", 79.3832).put("latitude", 43.6532).put("street", "Queens St. E");
      sendRequest("/1", "PATCH", confirmReq.toString());

      confirmReq = new JSONObject().put("roadName", "Mississauga Rd.").put("hasTraffic", false);
      sendRequest("/road", "PUT", confirmReq.toString());
      confirmReq = new JSONObject().put("roadName", "Alta Street").put("hasTraffic", true);
      sendRequest("/road", "PUT", confirmReq.toString());
      confirmReq = new JSONObject().put("roadName", "Queens St. E").put("hasTraffic", false);
      sendRequest("/road", "PUT", confirmReq.toString());

      confirmReq = new JSONObject().put("roadName1", "Queens St. E").put("roadName2", "Mississauga Rd.").put("hasTraffic", true).put("time", 20);
      sendRequest("/hasRoute", "POST", confirmReq.toString());
      confirmReq = new JSONObject().put("roadName1", "Queens St. E").put("roadName2", "Alta Street").put("hasTraffic", false).put("time", 5);
      sendRequest("/hasRoute", "POST", confirmReq.toString());
      confirmReq = new JSONObject().put("roadName1", "Alta Street").put("roadName2", "Mississauga Rd.").put("hasTraffic", true).put("time", 5);
      sendRequest("/hasRoute", "POST", confirmReq.toString());

      HttpResponse<String> confirmRes = sendRequest("/navigation/1?passengeruid=1234", "GET", confirmReq.toString());

      int status = confirmRes.statusCode();
      assertTrue(status == 400);

      String result = "{\"data\":{},\"status\":\"BAD REQUEST\"}";
      assertTrue(result.equals(confirmRes.body()));
   }*/

}
