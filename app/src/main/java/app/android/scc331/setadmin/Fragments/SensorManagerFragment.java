package app.android.scc331.setadmin.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app.android.scc331.setadmin.LoginActivity;
import app.android.scc331.setadmin.R;
import app.android.scc331.setadmin.REST.DataObjects.DatabaseDataListener;
import app.android.scc331.setadmin.REST.DataObjects.DatabaseSet;
import app.android.scc331.setadmin.REST.DataObjects.Sensor;
import app.android.scc331.setadmin.REST.Interfaces.RestOperationListener;
import app.android.scc331.setadmin.REST.SensorDatabaseOperation;
import app.android.scc331.setadmin.REST.TEST.RestOperationFactory;
import app.android.scc331.setadmin.REST.TEST.SetRESTOperation;

public class SensorManagerFragment extends Fragment implements DatabaseDataListener, RestOperationListener {

    private ArrayList<Sensor> databaseItems;

    private DatabaseAdapter adapter;

    private RestOperationListener restOperationListener;

    private ListView databaseList;

    public SensorManagerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_sensor_manager, container, false);

        restOperationListener = this;

        databaseList = v.findViewById(R.id.database_list);

        Button addSensor = v.findViewById(R.id.add_sensor_button);

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

        addSensor.setOnClickListener(addSensorClick());

        SensorDatabaseOperation sensorDatabaseOperation = new SensorDatabaseOperation(getActivity());
        sensorDatabaseOperation.start(this);

        return v;
    }

    @Override
    public void onDatabaseData(DatabaseSet databaseSet) {
        databaseItems = databaseSet.getSensorSet();
        adapter = new DatabaseAdapter(getActivity(), databaseItems);
        databaseList.setAdapter(adapter);
    }

    public void addSensorData(Sensor sensor){
        databaseItems.add(sensor);
        adapter.notifyDataSetChanged();
    }

    public void removeSensorData(Sensor sensor){
        Sensor sensorToRemove = null;
        for(Sensor s : databaseItems){
            if(s.getId().equals(sensor.getId()))
                sensorToRemove = s;
        }
        if(sensorToRemove != null){
            databaseItems.remove(sensorToRemove);
            adapter.notifyDataSetChanged();
        }
    }

    public View.OnClickListener removeSensorClick(final Sensor sensor){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                final View vw = layoutInflater.inflate(R.layout.confirm_removal, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setCancelable(false);
                final TextView info = vw.findViewById(R.id.info);
                final Button save = vw.findViewById(R.id.confirm);
                final Button cancel = vw.findViewById(R.id.cancel);

                info.setText(sensor.getId());

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SetRESTOperation removeOperation = RestOperationFactory.removeSensorOperation(getActivity(), sensor.getId());
                        removeOperation.run();

                        alertDialog.cancel();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });

                alertDialog.setView(vw, 0, 0, 0, 0);
                alertDialog.show();
            }
        };
    }

    public View.OnClickListener addSensorClick(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                final View vw = layoutInflater.inflate(R.layout.dialog_add_sensor, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setCancelable(false);
                final EditText sensor_id_text = (EditText) vw.findViewById(R.id.sensor_id_text);
                final Button save = vw.findViewById(R.id.add_sensor_dialog);
                final Button cancel = vw.findViewById(R.id.cancel);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (sensor_id_text.getText().toString().equals("")) {
                            Toast.makeText(getActivity(), "Please fill out the field", Toast.LENGTH_LONG).show();
                        } else {
                            if (!isUnique(sensor_id_text.getText().toString())) {
                                Toast.makeText(getActivity(), "Not unique ID", Toast.LENGTH_LONG).show();
                            } else {

                                SetRESTOperation addOperation = RestOperationFactory.addSensorOperation(getActivity(), sensor_id_text.getText().toString());
                                addOperation.run();

                                alertDialog.cancel();
                            }
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });

                alertDialog.setView(vw, 0, 0, 0, 0);
                alertDialog.show();
            }
        };
    }

    public boolean isUnique(String sensor_id){
        for (Sensor s : databaseItems) {
            if (s.getId().equals(sensor_id))
                return false;
        }
        return true;
    }

    @Override
    public void onResult(boolean result, int type) {

    }

    private class DatabaseAdapter extends ArrayAdapter<Sensor>{

        private Context context;
        private ArrayList<Sensor> sensors;

        public DatabaseAdapter(@NonNull Context context, ArrayList<Sensor> sensors) {
            super(context, -1, sensors);
            this.context = context;
            this.sensors = sensors;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.database_item_sensor, parent, false);

            TextView sensorId = v.findViewById(R.id.database_item_id);
            TextView owner = v.findViewById(R.id.database_item_owner);

            ImageView sensor_im_1,sensor_im_2,sensor_im_4,sensor_im_8,sensor_im_16,sensor_im_32,sensor_im_64,sensor_im_128;

            sensor_im_1 = v.findViewById(R.id.sensor_status_1);
            sensor_im_2 = v.findViewById(R.id.sensor_status_2);
            sensor_im_4 = v.findViewById(R.id.sensor_status_4);
            sensor_im_8 = v.findViewById(R.id.sensor_status_8);
            sensor_im_16 = v.findViewById(R.id.sensor_status_16);
            sensor_im_32 = v.findViewById(R.id.sensor_status_32);
            sensor_im_64 = v.findViewById(R.id.sensor_status_64);
            sensor_im_128 = v.findViewById(R.id.sensor_status_128);

            ImageView[] imageViews = new ImageView[]{sensor_im_1, sensor_im_2,sensor_im_4,sensor_im_8,sensor_im_16,sensor_im_32,sensor_im_64,sensor_im_128};

            Sensor sensor = sensors.get(position);

            Button remove = v.findViewById(R.id.remove_button);

            remove.setOnClickListener(removeSensorClick(sensor));

            sensorId.setText(sensor.getId());
            owner.setText(sensor.getRouterId());

            Boolean[] configBool = sensor.getConfig();
            int j = 0;
            for(Boolean b : configBool){
                if(b){
                    imageViews[j].setBackgroundColor(Color.parseColor("#00FF00"));
                }else{
                    imageViews[j].setBackgroundColor(Color.parseColor("#666666"));
                }
                j++;
            }

            return v;
        }
    }

}