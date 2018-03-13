package app.android.scc331.setadmin.REST.TEST;

import android.content.Context;

import app.android.scc331.setadmin.REST.RestPaths;

public class RestOperationFactory {

    public static SetRESTOperation adminOperation(Context context, String username, Boolean is_admin){
        return new RESTOperationBuilder().setUsername(username)
                .setIsAdmin(is_admin)
                .setUrl(RestPaths.SET_USER_ADMIN)
                .setContext(context)
                .create();
    }

    public static SetRESTOperation removeUserOperation(Context context, String username){
        return new RESTOperationBuilder().setContext(context)
                .setUrl(RestPaths.REMOVE_USER)
                .setUsername(username)
                .create();
    }

    public static SetRESTOperation addRouterOperation(Context context, String router_id){
        return routerOperation(context, router_id, RestPaths.ADD_ROUTER);
    }

    public static SetRESTOperation removeRouterOperation(Context context, String router_id){
        return routerOperation(context, router_id, RestPaths.REMOVE_ROUTER);
    }

    public static SetRESTOperation addSensorOperation(Context context, String sensor_id){
        return sensorOperation(context, sensor_id, RestPaths.ADD_SENSOR);
    }

    public static SetRESTOperation removeSensorOperation(Context context, String sensor_id){
        return sensorOperation(context, sensor_id, RestPaths.REMOVE_SENSOR);
    }

    private static SetRESTOperation sensorOperation(Context context, String sensor_id, String url){
        return new RESTOperationBuilder().setContext(context)
                .setUrl(url)
                .setSensorID(sensor_id)
                .create();
    }

    private static SetRESTOperation routerOperation(Context context, String router_id, String url){
        return new RESTOperationBuilder().setContext(context)
                .setUrl(url)
                .setRouterID(router_id)
                .create();
    }

    private static class RESTOperationBuilder {

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

        private RESTOperationBuilder setUrl(String url){
            this.url = url;
            return this;
        }

        private RESTOperationBuilder setRouterID(String router_id){
            this.router_id = router_id;
            return this;
        }

        private RESTOperationBuilder setSensorID(String sensor_id){
            this.sensor_id = sensor_id;
            return this;
        }

        private RESTOperationBuilder setUsername(String username){
            this.username = username;
            return this;
        }

        private RESTOperationBuilder setIsAdmin(Boolean is_admin){
            this.is_admin = is_admin;
            return this;
        }

        private SetRESTOperation create(){
            return new SetRESTOperation(context, url, router_id, sensor_id, username, is_admin);
        }
    }
}
