package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class UserEndPoint extends Endpoint{
    //Tutorial 1 Code to send API Requests neatly and not spagetti usermicroservice
    public HttpResponse<String> sendRequest(String endpoint, String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://usermicroservice:8000" + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .timeout(Duration.ofSeconds(30))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }


    public void handlePost(HttpExchange r) throws IOException, JSONException, InterruptedException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        HttpResponse<String> resp;
        try {
            resp = sendRequest(r.getRequestURI().toString(), "POST", body.toString());
            this.sendStatus(r, resp.statusCode());
            return;
        }catch(Exception e){

            this.sendStatus(r, 400);
            return;
        }
    }

    public void handleGet(HttpExchange r) throws IOException, JSONException, InterruptedException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        HttpResponse<String> resp;
        try {
            resp = sendRequest(r.getRequestURI().toString(), "GET", body.toString());
            JSONObject test = new JSONObject(resp.body());
            this.sendResponse(r, test ,resp.statusCode());
            return;
        }catch(Exception e){
            this.sendStatus(r, 400, true);
            return;
        }
    }

    public void handlePatch(HttpExchange r) throws IOException, JSONException, InterruptedException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        HttpResponse<String> resp;
        try {
            resp = sendRequest(r.getRequestURI().toString(), "PATCH", body.toString());
            this.sendStatus(r, resp.statusCode());
            return;
        }catch(Exception e){
            this.sendStatus(r, 400);
            return;
        }

    }

    public void handleDelete(HttpExchange r) throws IOException, JSONException, InterruptedException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        HttpResponse<String> resp;
        try {
            resp = sendRequest(r.getRequestURI().toString(), "DELETE", body.toString());
            this.sendStatus(r, resp.statusCode());
            return;
        }catch(Exception e){
            this.sendStatus(r, 400);
            return;
        }

    }

    public void handlePut(HttpExchange r) throws IOException, JSONException, InterruptedException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        HttpResponse<String> resp;
        try {
            resp = sendRequest(r.getRequestURI().toString(), "PUT", body.toString());
            this.sendStatus(r, resp.statusCode());
            return;
        }catch(Exception e){
            this.sendStatus(r, 400);
            return;
        }

    }
}
