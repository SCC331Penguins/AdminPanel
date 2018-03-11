package app.android.scc331.setadmin.REST;

import android.content.Context;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public abstract class RestOperation {

    public static final int POST = 0;
    public static final int GET = 1;

    protected HttpClient httpClient;

    protected Context context;

    public RestOperation(Context context){
        this.context = context;
        Log.i("REST","Attempting to login to the server: " + RestPaths.IP);
        HttpParams httpParams = new BasicHttpParams();
        int timeoutConnection = 10000;
        HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
        int timeoutSocket = 10000;
        HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
        httpClient = new DefaultHttpClient(httpParams);
    }
}
