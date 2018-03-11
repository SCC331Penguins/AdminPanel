package app.android.scc331.setadmin.Services;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import app.android.scc331.setadmin.REST.DataObjects.LiveData;
import app.android.scc331.setadmin.REST.DataObjects.MessageParser;
import app.android.scc331.setadmin.REST.DataObjects.Router;
import app.android.scc331.setadmin.REST.DataObjects.Sensor;
import app.android.scc331.setadmin.REST.DataObjects.User;
import app.android.scc331.setadmin.REST.RestPaths;

public class MQTTConnection extends Service implements MqttCallback {

    private static final String TAG = "MQTT";

    Callbacks activity;

    private boolean connected = false;

    IBinder mBinder = new LocalBinder();

    private MqttAndroidClient client;

    private String topic;

    private String connectedRouter;

    public MQTTConnection() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(getApplicationContext(), "tcp://" + RestPaths.IP + ":1883", clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "Connected to MQTT Server at: " + RestPaths.IP + ":1883");
                    activity.onConnected();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "Connection Failed.");
                    Log.d(TAG, "Exception: " + exception.getMessage());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        client.setCallback(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void connectionLost(Throwable cause) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        Log.d(TAG, topic + " : " + mqttMessage.toString());
        MessageParser message = new MessageParser(mqttMessage.toString());

        if(message.getType() != null){

            switch (message.getType()){
                case MessageParser.LIVE_DATA:
                    int time = message.getMessage().getInt("timestamp");
                    String type = message.getMessage().getString("action");
                    String info = message.getMessage().getString("info");

                    Log.d("TIME", ""+time);
                    Date currentTime = new Date(time * 1000L);
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    String dateString = formatter.format(currentTime);

                    activity.onLiveData(new LiveData(dateString, type, info));
                    break;

                case MessageParser.ROUTER_DATA_ADD:
                    JSONObject jo = message.getMessage();
                    activity.onRouterUpdate(new Router(jo.getString("router_id"), String.valueOf(jo.getString("owner")), jo.getLong("last_heard"), jo.getInt("sensors"), jo.getBoolean("online")), message.getType());
                    break;
                case MessageParser.ROUTER_DATA_REMOVE:
                    activity.onRouterUpdate(new Router(message.getMessage().getString("router_id"), "", 0, 0, false), message.getType());
                    break;
                case MessageParser.SENSOR_DATA_ADD:
                    activity.onSensorUpdate(new Sensor(message.getMessage().getString("sensor_id"), message.getMessage().getInt("config"), message.getMessage().getString("router")), message.getType());
                    break;
                case MessageParser.SENSOR_DATA_REMOVE:
                    activity.onSensorUpdate(new Sensor(message.getMessage().getString("sensor_id"), 0 , null), message.getType());
                    break;
                case MessageParser.USER_ADMIN:
                    activity.onUserUpdate(new User(0, message.getMessage().getString("username"), 0, message.getMessage().getInt("admin")), message.getType());
                    break;
                case MessageParser.USER_DATA_REMOVE:
                    activity.onUserUpdate(new User(0, message.getMessage().getString("username"), 0 ,0), message.getType());
                    break;
                case MessageParser.USER_DATA_ADD:
                    activity.onUserUpdate(new User(message.getMessage().getInt("id"), message.getMessage().getString("username"), 0, 0), message.getType());
                    break;
            }

        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public MQTTConnection getServerInstance() {
            return MQTTConnection.this;
        }
    }

    public void requestLiveData(String router_id, String sensor_id) {
        StringBuilder s = new StringBuilder();
        s.append("");
    }

    public void disconnect(){
        if(client != null)
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void unsubscribe(){
        try {
            client.unsubscribe(topic);
            Log.d(TAG,"Unsubscribed from: " + topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        this.connectedRouter = null;
        this.topic = null;
    }

    public void subscribe(String topic) {
        if(this.topic != null) unsubscribe();
        this.topic = topic;
        Log.d(TAG, "Attempting subscription: " + topic);
        if (client == null) return;
        try {
            client.subscribe(topic, 0);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Subscribed to: " + topic);
    }

    public void publish(String topic, String message) {
        try {
            client.publish(topic, new MqttMessage(message.getBytes()));
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void registerClient(Activity activity) {
        this.activity = (Callbacks) activity;
    }

    public interface Callbacks {
        void onConnected();
        void onLiveData(LiveData liveData);
        void onRouterUpdate(Router router, String type);
        void onSensorUpdate(Sensor sensor, String type);
        void onUserUpdate(User user, String type);
    }

}
