package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;

import java.io.IOException;

public class Clear extends Endpoint{

    public void handleDelete(HttpExchange r) throws IOException, JSONException {
        try {
            this.dao.clear();
            this.sendStatus(r, 200, true);
            return;
        }catch(Exception e){
            this.sendStatus(r, 500, true);
            return;
        }
    }
}
