package app.android.scc331.setadmin.REST.DataObjects;

/**
 * Created by Alex Stout on 10/03/2018.
 */

public class LiveData {

    public String time;
    public String type;
    public String info;

    public LiveData(String time, String type, String info){
        this.time = time;
        this.type = type;
        this.info = info;
    }
}
