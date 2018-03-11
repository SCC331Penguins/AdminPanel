package app.android.scc331.setadmin.REST.Interfaces;

public interface RestOperationListener {

    int ADD_ROUTER = 1;
    int REMOVE_ROUTER = 2;

    int ADD_SENSOR = 3;
    int REMOVE_SENSOR = 4;

    void onResult(boolean result, int type);

}
