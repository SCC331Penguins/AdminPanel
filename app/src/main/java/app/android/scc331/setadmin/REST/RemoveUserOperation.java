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

public class RemoveUserOperation extends RestOperation {

    private static final String TAG = "REMUser";

    public RemoveUserOperation(Context context) {
        super(context);
    }

    public boolean start(String username){
        boolean object = false;
        try {
            new PostTask(username).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        return object;
    }

    public class PostTask extends AsyncTask<String, Integer, Boolean> {

        private String username;

        private boolean result;

        PostTask(String username){
            this.username = username;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                result = performPost(username);

            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (result){
                Toast.makeText(context,"User removed successfully",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context, "Error removing user", Toast.LENGTH_LONG).show();
            }

        }
    }

    public boolean performPost(String username) throws IOException, JSONException {

        HttpPost post = new HttpPost(RestPaths.REMOVE_USER);
        Log.d(TAG, RestPaths.REMOVE_USER);


        SharedPreferences sharedPreferences = context.getSharedPreferences("com.set.app",Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token","");

        StringBuilder s = new StringBuilder();
        s.append("{\"token\":\"" + token + "\", \"username\": \"" + username + "\"}");

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
