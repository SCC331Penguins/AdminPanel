package app.android.scc331.setadmin.REST;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import app.android.scc331.setadmin.REST.DataObjects.DatabaseDataListener;
import app.android.scc331.setadmin.REST.DataObjects.DatabaseSet;
import app.android.scc331.setadmin.REST.DataObjects.Router;

public class RouterDatabaseOperation extends RestOperation {

    private final String TAG = "GET_ROUTER";

    public RouterDatabaseOperation(Context context) {
        super(context);
    }

    public boolean start(DatabaseDataListener databaseDataListener){
        boolean object = false;
        try {
            new PostTask(databaseDataListener).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        return object;
    }

    public class PostTask extends AsyncTask<String, Integer, Boolean> {

        private DatabaseDataListener databaseDataListener;

        private DatabaseSet databaseSet;

        PostTask(DatabaseDataListener databaseDataListener){
            this.databaseDataListener = databaseDataListener;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                databaseSet = (DatabaseSet) performPost();

            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            databaseDataListener.onDatabaseData(databaseSet);

        }
    }

    public Object performPost() throws IOException, JSONException {

        HttpPost post = new HttpPost(RestPaths.GET_ROUTERS);
        Log.d(TAG, RestPaths.GET_ROUTERS);


        SharedPreferences sharedPreferences = context.getSharedPreferences("com.set.app",Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token","");

        StringBuilder s = new StringBuilder();
        s.append("{\"token\":\"" + token + "\"}");

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
            JSONArray mainOb = new JSONArray((jsondatastring));

            ArrayList<Router> routers = new ArrayList<>();

            for(int i = 0; i < mainOb.length(); i++){
                JSONObject jo = (JSONObject) mainOb.get(i);
                Log.d(TAG, jo.toString());
                routers.add(new Router(jo.getString("router_id"), String.valueOf(jo.getString("owner")), jo.getLong("last_heard"), jo.getInt("sensors"), jo.getBoolean("online")));
            }

            return new DatabaseSet(routers);
        }else{
            return null;
        }
    }
}