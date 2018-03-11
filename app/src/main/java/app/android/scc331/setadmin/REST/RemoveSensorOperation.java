package app.android.scc331.setadmin.REST;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Alex Stout on 11/03/2018.
 */

public class RemoveSensorOperation extends RestOperation {

    private final String TAG = "REMSensor";

    public RemoveSensorOperation(Context context) {
        super(context);
    }
    public boolean start(String sensor_id){
        boolean object = false;
        try {
            new PostTask(sensor_id).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        return object;
    }

    public class PostTask extends AsyncTask<String, Integer, Boolean> {

        private String sensor_id;

        private boolean result;

        PostTask(String sensor_id){
            this.sensor_id = sensor_id;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                result = performPost(sensor_id);

            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (result){
                Toast.makeText(context,"Sensor removed successfully",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context, "Error removing sensor", Toast.LENGTH_LONG).show();
            }

        }
    }

    public boolean performPost(String sensor_id) throws IOException, JSONException {

        HttpPost post = new HttpPost(RestPaths.REMOVE_SENSOR);
        Log.d(TAG, RestPaths.REMOVE_SENSOR);


        SharedPreferences sharedPreferences = context.getSharedPreferences("com.set.app",Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token","");

        StringBuilder s = new StringBuilder();
        s.append("{\"token\":\"" + token + "\", \"sensor_id\": \"" + sensor_id + "\"}");

        post.setEntity(new StringEntity(s.toString()));
        post.setHeader("Accept", "application/json");
        post.setHeader("content-type", "application/json");
        ;
        Log.i(TAG, "Executing post...");

        HttpResponse r = httpClient.execute(post);

        int status = r.getStatusLine().getStatusCode();

        if(status == 200)//200 0k
        {
            HttpEntity e = r.getEntity();
            String jsondatastring = EntityUtils.toString(e);
            JSONObject mainOb = new JSONObject((jsondatastring));

            boolean data = mainOb.getBoolean("result");

            return data;
        }
        return false;
    }
}