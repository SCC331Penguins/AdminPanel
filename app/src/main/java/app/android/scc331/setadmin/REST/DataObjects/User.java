package app.android.scc331.setadmin.REST.DataObjects;

public class User {

    private String username;
    private int id;
    private int routers;
    private boolean is_admin;

    public User(int id, String username, int routers, int is_admin){
        this.id = id;
        this.username = username;
        this.routers = routers;
        this.is_admin = is_admin != 0;
    }

    public boolean is_admin() {
        return is_admin;
    }

    public String getId() {
        return String.valueOf(id);
    }

    public String getRouters() {
        return String.valueOf(routers);
    }

    public String getUsername() {
        return username;
    }

    public void setAdmin(boolean admin){
        this.is_admin = admin;
    }
}
