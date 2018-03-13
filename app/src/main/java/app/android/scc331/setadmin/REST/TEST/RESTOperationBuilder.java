package app.android.scc331.setadmin.REST.TEST;

import android.content.Context;

import app.android.scc331.setadmin.REST.RestOperation;

public class RESTOperationBuilder {

    private Context context;

    private String url = null;
    private String router_id = null;
    private String sensor_id = null;
    private String username = null;
    private Boolean is_admin = null;

    public RESTOperationBuilder setContext(Context context){
        this.context = context;
        return this;
    }

    public RESTOperationBuilder setUrl(String url){
        this.url = url;
        return this;
    }

    public RESTOperationBuilder setRouterID(String router_id){
        this.router_id = router_id;
        return this;
    }

    public RESTOperationBuilder setSensorID(String sensor_id){
        this.sensor_id = sensor_id;
        return this;
    }

    public RESTOperationBuilder setUsername(String username){
        this.username = username;
        return this;
    }

    public RESTOperationBuilder setIsAdmin(Boolean is_admin){
        this.is_admin = is_admin;
        return this;
    }
}
