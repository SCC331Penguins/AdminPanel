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

import app.android.scc331.setadmin.REST.Interfaces.RestOperationListener;

/**
 * Created by Alex Stout on 11/03/2018.
 */

public class SetUserAdminOperation extends RestOperation {

    private final String TAG = "SETAdmin";

    public SetUserAdminOperation(Context context) {
        super(context);
    }
    public boolean start(String username, int admin){
        boolean object = false;
        try {
            new PostTask(username, admin).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        return object;
    }

    public class PostTask extends AsyncTask<String, Integer, Boolean> {

        private String username;
        private int admin;

        private boolean result;

        PostTask(String username, int admin){
            this.username = username;
            this.admin = admin;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                result = performPost(username, admin);

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
        }
    }

    public boolean performPost(String username, int admin) throws IOException, JSONException {

        HttpPost post = new HttpPost(RestPaths.SET_USER_ADMIN);
        Log.d(TAG, RestPaths.SET_USER_ADMIN);


        SharedPreferences sharedPreferences = context.getSharedPreferences("com.set.app",Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token","");

        StringBuilder s = new StringBuilder();
        s.append("{\"token\":\"" + token + "\", \"username\": \"" + username + "\", \"admin\": \""+admin+"\"}");

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
