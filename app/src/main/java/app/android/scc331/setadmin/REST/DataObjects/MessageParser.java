package app.android.scc331.setadmin.REST.DataObjects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alex Stout on 10/03/2018.
 */

public class MessageParser {

    public static final String LIVE_DATA = "ADMINDATA";
    public static final String ROUTER_DATA_ADD = "ROUTERUPDADD";
    public static final String ROUTER_DATA_REMOVE = "ROUTERUPDREM";

    public static final String SENSOR_DATA_ADD = "SENSORUPDADD";
    public static final String SENSOR_DATA_REMOVE = "SENSORUPDREM";

    public static final String USER_DATA_ADD = "USERUPDADD";
    public static final String USER_DATA_REMOVE = "USERUPDREM";

    public static final String USER_ADMIN = "USERUPDADM";

    private JSONObject message;

    private String type;

    public MessageParser(String json){
        try {
            this.message = new JSONObject(json);
            this.type = message.getString("type");
        } catch (JSONException e) {
            this.type = null;
            this.message = null;
        }
    }

    public String getType() {
        return type;
    }

    public JSONObject getMessage() {
        return message;
    }
}
