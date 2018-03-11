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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import app.android.scc331.setadmin.REST.Interfaces.AdminLinkInterface;


public class OpenAdminLinkOperation extends RestOperation {

    private final String TAG = "OpenLink";

    public OpenAdminLinkOperation(Context context) {
        super(context);
    }

    public boolean start(AdminLinkInterface adminLinkInterface){
        try {
            new PostTask(adminLinkInterface).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public class PostTask extends AsyncTask<String, Integer, String> {

        private AdminLinkInterface adminLinkInterface;

        private String topic_name;

        PostTask(AdminLinkInterface adminLinkInterface){
            this.adminLinkInterface = adminLinkInterface;
        }

        @Override
        protected String doInBackground(String... strings) {
            try{
                topic_name = (String) performPost();

            }catch (Exception e){
                e.printStackTrace();
            }
            return topic_name;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            Log.d(TAG, "String: " + string);

            if(topic_name != null)
                adminLinkInterface.openLink(topic_name);

        }
    }

    public Object performPost() throws IOException, JSONException {

        HttpPost post = new HttpPost(RestPaths.OPEN_LINK);
        Log.d(TAG, RestPaths.OPEN_LINK);


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
            JSONObject mainOb = new JSONObject((jsondatastring));

            Log.d(TAG,jsondatastring);

            return mainOb.getString("topic_name");

        }else{
            return null;
        }
    }
}
