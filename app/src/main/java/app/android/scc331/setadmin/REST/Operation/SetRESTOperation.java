package app.android.scc331.setadmin.REST.Operation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class SetRESTOperation {

    private HttpClient httpClient;

    private String url;
    private String router_id;
    private String sensor_id;
    private String username;
    private String is_admin;

    private Context context;

    SetRESTOperation(Context context, String url, String router_id,
                     String sensor_id, String username, Boolean is_admin){
        this.context = context;
        HttpParams httpParams = new BasicHttpParams();
        int timeoutConnection = 5000;
        HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
        int timeoutSocket = 5000;
        HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
        httpClient = new DefaultHttpClient(httpParams);
        this.url = url;
        this.router_id = router_id;
        this.sensor_id = sensor_id;
        this.username = username;

        if(is_admin != null){
            if(is_admin){
                this.is_admin = "1";
            }else{
                this.is_admin = "0";
            }
        }else{
            this.is_admin = null;
        }
    }

    public void run(){
        new POST().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class POST extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                return performPost();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context, "Failure", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);
        }
    }

    public Boolean performPost() throws IOException, JSONException {

        HttpPost post = new HttpPost(url);

        SharedPreferences sharedPreferences = context.getSharedPreferences("com.set.app",Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token","");


        JSONObject toSend = new JSONObject();
        toSend.put("token", token);

        if(router_id != null)
            toSend.put("router_id", router_id);

        if(sensor_id != null)
            toSend.put("sensor_id", sensor_id);

        if(username != null)
            toSend.put("username", username);

        if(is_admin != null)
            toSend.put("admin", is_admin);


        post.setEntity(new StringEntity(toSend.toString()));
        post.setHeader("Accept", "application/json");
        post.setHeader("content-type", "application/json");

        HttpResponse r = httpClient.execute(post);

        int status = r.getStatusLine().getStatusCode();

        if(status == 200)
        {
            HttpEntity e = r.getEntity();
            String jsondatastring = EntityUtils.toString(e);
            JSONObject mainOb = new JSONObject((jsondatastring));

            return mainOb.getBoolean("result");
        }

        return false;
    }
}
