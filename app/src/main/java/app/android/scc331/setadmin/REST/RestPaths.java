package app.android.scc331.setadmin.REST;

public class RestPaths {

    public static final String IP = "192.168.0.100";

    public static final String URL = "http://" + IP + ":5001";

    public static final String URL_PREFIX = URL + "/admin";

    public static final String LOGIN = URL + "/user/login";

    public static final String ADMIN_TEST = URL_PREFIX + "/test";

    public static final String HOME_DATA = URL_PREFIX + "/get_home_data";

    public static final String OPEN_LINK = URL_PREFIX + "/create_data_link";

    public static final String ADD_ROUTER = URL_PREFIX + "/add_router";

    public static final String ADD_SENSOR = URL_PREFIX + "/add_sensor";

    public static final String GET_USERS = URL_PREFIX + "/get_users";

    public static final String SET_USER_ADMIN = URL_PREFIX + "/set_user_admin";

    public static final String REMOVE_USER = URL_PREFIX + "/remove_user";

    public static final String REMOVE_SENSOR = URL_PREFIX + "/remove_sensor";

    public static final String REMOVE_ROUTER = URL_PREFIX + "/remove_router";

    public static final String GET_ROUTERS = URL_PREFIX + "/get_routers";

    public static final String GET_SENSORS = URL_PREFIX + "/get_sensors";

}
