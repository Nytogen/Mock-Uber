package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;

import java.io.IOException;

public class Reset extends Endpoint{

    @Override
    public void handleDelete(HttpExchange r) throws JSONException, IOException {
        try{
            this.dao.clear();
            this.sendStatus(r, 200, true);
        } catch(Exception e){
            this.sendStatus(r, 500, true);
        }
        return;
    }
}
