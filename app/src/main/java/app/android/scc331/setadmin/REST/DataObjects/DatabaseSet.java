package app.android.scc331.setadmin.REST.DataObjects;

import java.util.ArrayList;

public class DatabaseSet {

    private ArrayList databaseSet;

    public DatabaseSet(ArrayList set){
        this.databaseSet = set;
    }

    public ArrayList<Router> getRouterSet(){
        ArrayList<Router> routers = new ArrayList<>();
        for(Object o : databaseSet){
            routers.add((Router) o);
        }
        return routers;
    }

    public ArrayList<Sensor> getSensorSet(){
        ArrayList<Sensor> sensors = new ArrayList<>();
        for(Object o : databaseSet){
            sensors.add((Sensor) o);
        }
        return sensors;
    }

    public ArrayList<User> getUserSet(){
        ArrayList<User> users = new ArrayList<>();
        for(Object o : databaseSet){
            users.add((User) o);
        }
        return users;
    }
}
