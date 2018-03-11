package app.android.scc331.setadmin.REST;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginOperation extends RestOperation {

    private final String TAG = "REST Login";

    public LoginOperation(Context context) {
        super(context);
    }

    public boolean start(String username, String password){
        boolean object = false;
        try {
            object = new PostTask(username, password).execute().get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return object;
    }

    public class PostTask extends AsyncTask<String, Integer, Boolean> {

        private String username,password;

        PostTask(String username, String password){
            this.username = username;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                String token = (String) performPost(username, password);

                if(token==null){
                    SharedPreferences preferences = context.getSharedPreferences("com.set.app", Context.MODE_PRIVATE);
                    preferences.edit().putString("token",null).apply();
                    preferences.edit().putString("username", null).apply();
                    return false;
                }else{
                    SharedPreferences preferences = context.getSharedPreferences("com.set.app", Context.MODE_PRIVATE);
                    preferences.edit().putString("token",token).apply();
                    preferences.edit().putString("username", null).apply();
                    return true;
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }
    }

    public Object performPost(String username, String password) throws IOException, JSONException {

        HttpPost post = new HttpPost(RestPaths.LOGIN);
        Log.d(TAG, RestPaths.LOGIN);

        post.setEntity(new StringEntity("{\"username\":\"" + username + "\", \"password\":\"" + password + "\"}"));
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
            String logged_in = mainOb.getString("logged_in");
            if(logged_in.equals("true")){
                String token = mainOb.getString("token");
                Log.i(TAG, "Logged in! Token: " + token);
                return token;
            }
            return null;
        }else{
            return null;
        }
    }
}
