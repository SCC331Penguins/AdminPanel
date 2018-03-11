package app.android.scc331.setadmin;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.android.scc331.setadmin.Fragments.MainFragment;
import app.android.scc331.setadmin.Fragments.RouterManagerFragment;
import app.android.scc331.setadmin.Fragments.SensorManagerFragment;
import app.android.scc331.setadmin.Fragments.UserManagerFragment;
import app.android.scc331.setadmin.REST.DataObjects.LiveData;
import app.android.scc331.setadmin.REST.DataObjects.MessageParser;
import app.android.scc331.setadmin.REST.DataObjects.Router;
import app.android.scc331.setadmin.REST.DataObjects.Sensor;
import app.android.scc331.setadmin.REST.DataObjects.User;
import app.android.scc331.setadmin.REST.Interfaces.AdminLinkInterface;
import app.android.scc331.setadmin.REST.OpenAdminLinkOperation;
import app.android.scc331.setadmin.Services.MQTTConnection;
import devlight.io.library.ntb.NavigationTabBar;

public class MainActivity extends Activity implements MainFragment.OnFragmentInteractionListener, AdminLinkInterface,
        MQTTConnection.Callbacks, RouterManagerFragment.OnFragmentInteractionListener{

    public static MQTTConnection mqttConnection;

    private boolean mBounded;

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(getApplicationContext(), "Service is disconnected", Toast.LENGTH_SHORT).show();
            mBounded = false;
            mqttConnection = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(getApplicationContext(), "Service is connected", Toast.LENGTH_SHORT).show();
            mBounded = true;
            MQTTConnection.LocalBinder mLocalBinder = (MQTTConnection.LocalBinder) service;
            mqttConnection = mLocalBinder.getServerInstance();
            mqttConnection.registerClient(MainActivity.this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.fade_in_fragment, R.animator.fade_out_fragment);

        MainFragment fragment = new MainFragment();
        fragmentTransaction.replace(R.id.content_pane, fragment, "fragment_main").commit();
    }

    private void initUI() {

        final String[] colors = getResources().getStringArray(R.array.vertical_ntb);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_vertical);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.home),
                        Color.parseColor(colors[0]))
                        .title("ic_first")
                        .selectedIcon(getResources().getDrawable(R.drawable.home))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.router),
                        Color.parseColor(colors[1]))
                        .selectedIcon(getResources().getDrawable(R.drawable.router))
                        .title("ic_second")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.sensor_icon),
                        Color.parseColor(colors[2]))
                        .selectedIcon(getResources().getDrawable(R.drawable.sensor_icon))
                        .title("ic_third")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.user),
                        Color.parseColor(colors[3]))
                        .selectedIcon(getResources().getDrawable(R.drawable.user))
                        .title("ic_third")
                        .build()
        );


        navigationTabBar.setModels(models);
        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(NavigationTabBar.Model model, int index) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.fade_in_fragment, R.animator.fade_out_fragment);

                switch (index){
                    case 0:
                        MainFragment fragment = new MainFragment();
                        fragmentTransaction.replace(R.id.content_pane, fragment, "fragment_main").commit();
                        break;
                    case 1:
                        RouterManagerFragment fragment1 = new RouterManagerFragment();
                        fragmentTransaction.replace(R.id.content_pane, fragment1, "fragment_router_manage").commit();
                        break;
                    case 2:
                        SensorManagerFragment fragment2 = new SensorManagerFragment();
                        fragmentTransaction.replace(R.id.content_pane, fragment2, "fragment_sensor_manage").commit();
                        break;
                    case 3:
                        UserManagerFragment fragment3 = new UserManagerFragment();
                        fragmentTransaction.replace(R.id.content_pane, fragment3, "fragment_user_manage").commit();
                }
            }

            @Override
            public void onEndTabSelected(NavigationTabBar.Model model, int index) {

            }
        });

    }

    @Override
    protected void onStart() {
        Intent mIntent = new Intent(this, MQTTConnection.class);
        startService(mIntent);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        mqttConnection.disconnect();
        super.onDestroy();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void openLink(String topic_name) {
        if(topic_name != null);
            mqttConnection.subscribe(topic_name);
        Log.d("OpenLink", topic_name);
    }

    @Override
    public void onConnected() {
        OpenAdminLinkOperation openAdminLinkOperation = new OpenAdminLinkOperation(this);
        openAdminLinkOperation.start(this);
    }

    @Override
    public void onLiveData(LiveData liveData) {
        MainFragment f = (MainFragment) getFragmentManager().findFragmentByTag("fragment_main");
        f.addLiveData(liveData);
    }

    @Override
    public void onRouterUpdate(Router router, String type) {
        RouterManagerFragment f = (RouterManagerFragment) getFragmentManager().findFragmentByTag("fragment_router_manage");
        if(type.equals(MessageParser.ROUTER_DATA_ADD)) {
            f.addRouterData(router);
        }else if(type.equals(MessageParser.ROUTER_DATA_REMOVE)){
            f.removeRouterData(router);
        }
    }

    @Override
    public void onSensorUpdate(Sensor sensor, String type) {
        SensorManagerFragment f = (SensorManagerFragment) getFragmentManager().findFragmentByTag("fragment_sensor_manage");
        if(type.equals(MessageParser.SENSOR_DATA_ADD)){
            f.addSensorData(sensor);
        }else if(type.equals(MessageParser.SENSOR_DATA_REMOVE)){
            f.removeSensorData(sensor);
        }

    }

    @Override
    public void onUserUpdate(User user, String type) {
        UserManagerFragment f = (UserManagerFragment) getFragmentManager().findFragmentByTag("fragment_user_manage");
        if(type.equals(MessageParser.USER_DATA_ADD)){
            f.addUserData(user);
        }else if(type.equals(MessageParser.USER_DATA_REMOVE)){
            f.removeUserData(user);
        }else if(type.equals(MessageParser.USER_ADMIN)){
            f.updateUserAdmin(user);
        }
    }
}