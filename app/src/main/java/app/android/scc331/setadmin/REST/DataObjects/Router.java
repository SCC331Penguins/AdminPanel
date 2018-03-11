package app.android.scc331.setadmin.REST.DataObjects;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Router {

    public String id;
    public String owner;
    public String lastHeard;
    public String sensors;
    public boolean online;

    public Router(String id, String owner, long lastHeard, int sensors, boolean online){
        if(id.contains("\n")) {
            Log.d("NEWLINE", id);
            id = id.substring(0, id.length() - 1);
        }
        this.id = id;
        this.owner = owner;
        if (lastHeard != 0) {
            Date currentTime = new Date(lastHeard * 1000L);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            this.lastHeard = formatter.format(currentTime);
        }else{
            this.lastHeard = "Never";
        }
        this.sensors = String.valueOf(sensors);
        this.online = online;
    }

}
