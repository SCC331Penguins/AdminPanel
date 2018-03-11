package app.android.scc331.setadmin.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import app.android.scc331.setadmin.LoginActivity;
import app.android.scc331.setadmin.MainActivity;
import app.android.scc331.setadmin.R;
import app.android.scc331.setadmin.REST.DataObjects.HomeData;
import app.android.scc331.setadmin.REST.DataObjects.HomeDataListener;
import app.android.scc331.setadmin.REST.DataObjects.LiveData;
import app.android.scc331.setadmin.REST.HomeDataOperation;

public class MainFragment extends Fragment implements HomeDataListener, CompoundButton.OnCheckedChangeListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView router_total, router_claimed, router_unclaimed, router_online;
    private TextView sensor_total, sensor_claimed, sensor_unclaimed, sensor_online;
    private TextView users_total;

    private FrameLayout statusFrame;

    private static final int MAX_RECORDS = 20;

    private int test = 0;

    private ArrayList<LiveData> listItems = new ArrayList<>();

    private LinearLayout infoBoxRouter, infoBoxSensor, infoBoxUser;

    private ListView liveDataList;

    private LiveDataAdapter adapter;

    private CheckBox postcheck, getcheck, usercheck, apicheck;

    private ArrayList<String> filter = new ArrayList<>();

    public MainFragment() {}

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        Button logout = v.findViewById(R.id.logout_button);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getContext().getSharedPreferences("com.set.app", Context.MODE_PRIVATE);
                preferences.edit().putString("token",null).apply();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
            }
        });

        statusFrame = v.findViewById(R.id.status_frame);

        Button refresh = v.findViewById(R.id.refresh_button);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updateHomeData();
                addLiveData(new LiveData(String.valueOf(test++), "TYPE", "INFO"));
            }
        });

        infoBoxRouter = v.findViewById(R.id.router_info_box);
        infoBoxSensor = v.findViewById(R.id.sensor_info_box);
        infoBoxUser = v.findViewById(R.id.user_info_box);

        router_online = v.findViewById(R.id.online_router);
        router_claimed = infoBoxRouter.findViewById(R.id.claimed_router);
        router_unclaimed = infoBoxRouter.findViewById(R.id.unclaimed_router);
        router_total = infoBoxRouter.findViewById(R.id.total_router);

        sensor_online = infoBoxSensor.findViewById(R.id.online_sensor);
        sensor_total = infoBoxSensor.findViewById(R.id.total_sensor);
        sensor_claimed = infoBoxSensor.findViewById(R.id.claimed_sensor);
        sensor_unclaimed = infoBoxSensor.findViewById(R.id.unclaimed_sensor);

        users_total = infoBoxUser.findViewById(R.id.total_user);

        postcheck = v.findViewById(R.id.post_check);
        getcheck = v.findViewById(R.id.get_check);
        usercheck = v.findViewById(R.id.user_check);
        apicheck = v.findViewById(R.id.api_check);

        postcheck.setOnCheckedChangeListener(this);
        getcheck.setOnCheckedChangeListener(this);
        usercheck.setOnCheckedChangeListener(this);
        apicheck.setOnCheckedChangeListener(this);

        updateHomeData();

        liveDataList = v.findViewById(R.id.live_actions_list);
        adapter = new LiveDataAdapter(getActivity(), listItems);
        liveDataList.setAdapter(adapter);

        filter.add("POST");
        filter.add("GET");
        filter.add("API");
        filter.add("USER");

        return v;
    }

    private void updateHomeData(){
        statusFrame.setVisibility(View.VISIBLE);
        HomeDataOperation homeDataOperation = new HomeDataOperation(getActivity());
        homeDataOperation.start(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setHomeData(HomeData data){
        if(data != null){
            router_total.setText(data.routerTotal);
            router_claimed.setText(data.routerCliamed);
            router_unclaimed.setText(data.routerUnclaimed);
            router_online.setText(data.routerOnline);

            sensor_total.setText(data.sensorTotal);
            sensor_claimed.setText(data.sensorClaimed);
            sensor_unclaimed.setText(data.sensorUnclaimed);
            sensor_online.setText(data.sensorOnline);

            users_total.setText(data.userTotal);
            statusFrame.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()){

            case R.id.api_check:
                if(isChecked){
                    if(!filter.contains("API"))
                        filter.add("API");
                }else{
                    if(filter.contains("API"))
                        filter.remove("API");
                }
                break;

            case R.id.post_check:
                if(isChecked){
                    if(!filter.contains("POST"))
                        filter.add("POST");
                }else{
                    if(filter.contains("POST"))
                        filter.remove("POST");
                }
                break;

            case R.id.get_check:
                if(isChecked){
                    if(!filter.contains("GET"))
                        filter.add("GET");
                }else{
                    if(filter.contains("GET"))
                        filter.remove("GET");
                }
                break;

            case R.id.user_check:
                if(isChecked){
                    if(!filter.contains("USER"))
                        filter.add("USER");
                }else{
                    if(filter.contains("USER"))
                        filter.remove("USER");
                }
                break;

        }

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void addLiveData(LiveData liveData){
        for(String string : filter) {
            if (liveData.type.contains(string)) {
                Log.d("FILTER", "LD: " + liveData.type + " FIL: " + string);
                if(listItems.size() == MAX_RECORDS){
                    listItems.remove(listItems.get(listItems.size()-1));
                }
                listItems.add(0, liveData);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class LiveDataAdapter extends ArrayAdapter<LiveData> {

        private Context context;
        private ArrayList<LiveData> data;

        public LiveDataAdapter(@NonNull Context context, ArrayList<LiveData> data) {
            super(context, -1, data);
            this.context = context;
            this.data = data;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.admin_data_element, parent, false);

            TextView typeText = rowView.findViewById(R.id.live_type);
            TextView timeText = rowView.findViewById(R.id.live_timestamp);
            TextView infoText = rowView.findViewById(R.id.live_info);

            LiveData dataToDisplay = data.get(position);
            typeText.setText(dataToDisplay.type);
            timeText.setText(dataToDisplay.time);
            infoText.setText(dataToDisplay.info);

            return rowView;
        }
    }

}
