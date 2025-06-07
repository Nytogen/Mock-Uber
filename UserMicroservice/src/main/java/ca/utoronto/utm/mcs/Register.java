package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;

public class Register extends Endpoint{

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if (body.has("name") && body.has("email") && body.has("password")) {
            String email = body.getString("email");
            String name = body.getString("name");
            String password = body.getString("password");

            try {
                ResultSet dupUser = this.dao.getUsersFromEmail(email);

                if(!dupUser.next()) {
                    this.dao.registerUser(email, name, password);

                    this.sendStatus(r, 200);
                }else{
                    this.sendStatus(r, 400);
                }
                return;

            } catch (Exception e) {
                e.printStackTrace();
                this.sendStatus(r, 500);
                return;
            }

        } else {
            this.sendStatus(r, 400);
        }

    }
}
