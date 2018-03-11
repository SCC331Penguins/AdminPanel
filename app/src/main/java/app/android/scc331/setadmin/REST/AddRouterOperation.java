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

import app.android.scc331.setadmin.REST.DataObjects.HomeData;
import app.android.scc331.setadmin.REST.DataObjects.HomeDataListener;
import app.android.scc331.setadmin.REST.Interfaces.RestOperationListener;

public class AddRouterOperation extends RestOperation {

    private final String TAG = "ADDRouter";

    public AddRouterOperation(Context context) {
        super(context);
    }

    public boolean start(String router_id, RestOperationListener restOperationListener){
        boolean object = false;
        try {
            new PostTask(router_id, restOperationListener).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        return object;
    }

    public class PostTask extends AsyncTask<String, Integer, Boolean> {

        private String router_id;
        private RestOperationListener listener;

        private boolean result;

        PostTask(String router_id, RestOperationListener restOperationListener){
            this.router_id = router_id;
            this.listener = restOperationListener;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                result = performPost(router_id);

            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (result){
                Toast.makeText(context,"Router added successfully",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context, "Error adding router", Toast.LENGTH_LONG).show();
            }
            listener.onResult(result, RestOperationListener.ADD_ROUTER);
        }
    }

    public boolean performPost(String router_id) throws IOException, JSONException {

        HttpPost post = new HttpPost(RestPaths.ADD_ROUTER);
        Log.d(TAG, RestPaths.ADD_ROUTER);


        SharedPreferences sharedPreferences = context.getSharedPreferences("com.set.app",Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token","");

        StringBuilder s = new StringBuilder();
        s.append("{\"token\":\"" + token + "\", \"router_id\": \"" + router_id + "\"}");

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
