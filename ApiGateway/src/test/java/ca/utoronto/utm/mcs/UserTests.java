package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;

/*
Please write your tests for the User Microservice in this class. 
*/

public class UserTests {
    //Tutorial 1 Code to send API Requests neatly and not spagetti
    private static HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();

        //Expecting these unit tests to run after everything has be dockerized and launched
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8004/user" + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }


    @BeforeAll
    public static void RunServer() throws IOException, SQLException, ClassNotFoundException {
        //Run the server
        try {
            App.main(null);
        }catch(Exception e){
            System.out.println("Server already running on same port");
        }
    }

    @BeforeEach
    public void clear() throws IOException, InterruptedException {
        sendRequest("/clear", "DELETE", "{}");
    }

    @Test
    public void userRegisterPass() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq;

        confirmReq = new JSONObject().put("name", "Sol").put("email", "123@gmail.com").put("password", "1234");
        sendRequest("/register", "POST", confirmReq.toString());

        confirmReq = new JSONObject().put("name", "Sol").put("email", "321@gmail.com").put("password", "1234");
        HttpResponse<String> confirmRes = sendRequest("/register", "POST", confirmReq.toString());

        int status = confirmRes.statusCode();
        assertTrue(status == 200);

        String result = "{\"status\":\"OK\"}";
        assertTrue(result.equals(confirmRes.body()));
    }

    @Test
    public void userRegisterFail() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq;

        confirmReq = new JSONObject().put("name", "Sol").put("email", "123@gmail.com").put("password", "1234");
        sendRequest("/register", "POST", confirmReq.toString());

        confirmReq = new JSONObject().put("name", "Shi").put("email", "123@gmail.com").put("password", "Supercala");
        HttpResponse<String> confirmRes = sendRequest("/register", "POST", confirmReq.toString());

        int status = confirmRes.statusCode();
        assertTrue(status == 400);

        String result = "{\"status\":\"BAD REQUEST\"}";
        assertTrue(result.equals(confirmRes.body()));
    }

    @Test
    public void userLoginPass() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq;

        confirmReq = new JSONObject().put("name", "Sol").put("email", "123@gmail.com").put("password", "1234");
        sendRequest("/register", "POST", confirmReq.toString());

        confirmReq = new JSONObject().put("email", "123@gmail.com").put("password", "1234");
        HttpResponse<String> confirmRes = sendRequest("/login", "POST", confirmReq.toString());

        int status = confirmRes.statusCode();
        assertTrue(status == 200);

        String result = "{\"status\":\"OK\"}";
        assertTrue(result.equals(confirmRes.body()));
    }

    @Test
    public void userLoginFail() throws JSONException, IOException, InterruptedException {
        JSONObject confirmReq;

        confirmReq = new JSONObject().put("name", "Sol").put("email", "123@gmail.com").put("password", "1234");
        sendRequest("/register", "POST", confirmReq.toString());

        confirmReq = new JSONObject().put("email", "123@gmail.com").put("password", "73824928356");
        HttpResponse<String> confirmRes = sendRequest("/login", "POST", confirmReq.toString());

        int status = confirmRes.statusCode();
        assertTrue(status == 403);

        String result = "{\"status\":\"FORBIDDEN\"}";
        assertTrue(result.equals(confirmRes.body()));
    }
}
