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

import app.android.scc331.setadmin.REST.DataObjects.HomeData;
import app.android.scc331.setadmin.REST.DataObjects.HomeDataListener;

public class HomeDataOperation extends RestOperation {

    private final String TAG = "HomeData";

    public HomeDataOperation(Context context) {
        super(context);
    }

    public boolean start(HomeDataListener homeDataListener){
        boolean object = false;
        try {
            object = new PostTask(homeDataListener).execute().get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return object;
    }

    public class PostTask extends AsyncTask<String, Integer, Boolean> {

        private HomeDataListener homeDataListener;

        private HomeData homeData;

        PostTask(HomeDataListener homeDataListener){
            this.homeDataListener = homeDataListener;
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                homeData = (HomeData) performPost();

            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            homeDataListener.setHomeData(homeData);

        }
    }

    public Object performPost() throws IOException, JSONException {

        HttpPost post = new HttpPost(RestPaths.HOME_DATA);
        Log.d(TAG, RestPaths.HOME_DATA);


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

            JSONObject router = mainOb.getJSONObject("routers");
            JSONObject sensor = mainOb.getJSONObject("sensors");
            JSONObject users = mainOb.getJSONObject("users");

            Log.d(TAG, router.toString());
            Log.d(TAG, sensor.toString());
            Log.d(TAG, users.toString());

            HomeData.HomeDataBuilder homeDataBuilder = new HomeData.HomeDataBuilder();

            HomeData data = homeDataBuilder
                    .setRouterInfo(router.getInt("total"), router.getInt("online"), router.getInt("claimed"), router.getInt("unclaimed"))
                    .setSensorInfo(sensor.getInt("total"), sensor.getInt("online"), sensor.getInt("claimed"), sensor.getInt("unclaimed"))
                    .setUserInto(users.getInt("total"))
                    .create();


            return data;
        }else{
            return null;
        }
    }
}
