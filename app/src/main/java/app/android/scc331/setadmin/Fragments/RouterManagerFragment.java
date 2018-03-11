package app.android.scc331.setadmin.Fragments;

import android.app.AlertDialog;
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
import android.view.WindowManager;
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
import app.android.scc331.setadmin.REST.AddRouterOperation;
import app.android.scc331.setadmin.REST.DataObjects.DatabaseDataListener;
import app.android.scc331.setadmin.REST.DataObjects.DatabaseSet;
import app.android.scc331.setadmin.REST.DataObjects.Router;
import app.android.scc331.setadmin.REST.Interfaces.RestOperationListener;
import app.android.scc331.setadmin.REST.RemoveRouterOperation;
import app.android.scc331.setadmin.REST.RouterDatabaseOperation;

public class RouterManagerFragment extends Fragment implements DatabaseDataListener, RestOperationListener {

    private static final String ARG_PARAM1 = "param1";

    private Object mParam1;

    private ArrayList<Router> databaseItems = new ArrayList<>();

    private DatabaseAdapter adapter;

    private RestOperationListener restOperationListener = this;

    private Button addRouter;

    private OnFragmentInteractionListener mListener;

    public RouterManagerFragment() {
    }

    private ListView datbaseList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_router_manager, container, false);

        datbaseList = v.findViewById(R.id.database_list);

        addRouter = v.findViewById(R.id.add_router);
        addRouter.setOnClickListener(addRouterClick());

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

        RouterDatabaseOperation routerDatabaseOperation = new RouterDatabaseOperation(getActivity());
        routerDatabaseOperation.start(this);

        return v;
    }

    private View.OnClickListener addRouterClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                final View vw = layoutInflater.inflate(R.layout.dialog_add_router, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setCancelable(false);
                final EditText router_id_text = (EditText) vw.findViewById(R.id.router_id_text);
                final Button save = vw.findViewById(R.id.add_router_dialog);
                final Button cancel = vw.findViewById(R.id.cancel);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (router_id_text.getText().toString().equals("")) {
                            Toast.makeText(getActivity(), "Please fill out the field", Toast.LENGTH_LONG).show();
                        } else {
                            if (!isUnique(router_id_text.getText().toString())) {
                                Toast.makeText(getActivity(), "Not unique ID", Toast.LENGTH_LONG).show();
                            } else {
                                if (router_id_text.getText().toString().length() >= 4) {
                                    Toast.makeText(getActivity(), "Only 3 Digit Number", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                AddRouterOperation addRouterOperation = new AddRouterOperation(getActivity());
                                addRouterOperation.start("SCC33102_R" + router_id_text.getText().toString(), restOperationListener);
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

    private boolean isUnique(String id) {
        for (Router r : databaseItems) {
            if (r.id.equals(id))
                return false;
        }
        return true;
    }

    public static RouterManagerFragment newInstance(String param1, String param2) {
        RouterManagerFragment fragment = new RouterManagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
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
    public void onDatabaseData(DatabaseSet databaseSet) {
        databaseItems = databaseSet.getRouterSet();
        adapter = new DatabaseAdapter(getActivity(), databaseItems);
        datbaseList.setAdapter(adapter);
    }


    @Override
    public void onResult(boolean result, int type) {
        Log.d("RESULT", ""+result);
        switch (type){

            case RestOperationListener.ADD_ROUTER:
                break;
        }
    }

    public void addRouterData(Router router){
        databaseItems.add(router);
        adapter.notifyDataSetChanged();
    }

    public void removeRouterData(Router router){
        Router routerToRemove = null;
        for(Router r : databaseItems){
            if(r.id.equals(router.id))
                routerToRemove = r;
        }
        if(routerToRemove != null){
            databaseItems.remove(routerToRemove);
            adapter.notifyDataSetChanged();
        }

    }

    public interface OnFragmentInteractionListener {
    }

    public void editRouter(Router router) {

    }

    private View.OnClickListener editRouterClick(final Router router) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRouter(router);
            }
        };
    }

    private View.OnClickListener removeRouter(final Router router) {
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

                info.setText(router.id);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        databaseItems.remove(router);
                        adapter.notifyDataSetChanged();
                        RemoveRouterOperation removeRouterOperation = new RemoveRouterOperation(getActivity());
                        removeRouterOperation.start(router.id);
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

    private class DatabaseAdapter extends ArrayAdapter<Router> {

        private ArrayList<Router> routers;
        private Context context;

        public DatabaseAdapter(@NonNull Context context, ArrayList<Router> routers) {
            super(context, -1, routers);
            this.context = context;
            this.routers = routers;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.database_item_router, parent, false);

            TextView id = rowView.findViewById(R.id.database_item_id);
            TextView owner = rowView.findViewById(R.id.database_item_owner);
            TextView lastHeard = rowView.findViewById(R.id.database_item_last_heard);
            TextView sensors = rowView.findViewById(R.id.database_item_sensor_count);
            ImageView online = rowView.findViewById(R.id.database_item_online);

            Button edit = rowView.findViewById(R.id.edit_button);
            Button remove = rowView.findViewById(R.id.remove_button);

            Router router = routers.get(position);

            remove.setOnClickListener(removeRouter(router));

            edit.setOnClickListener(editRouterClick(router));

            id.setText(router.id);
            owner.setText(router.owner);
            lastHeard.setText(router.lastHeard);
            sensors.setText(router.sensors);

            if (router.online) {
                online.setImageDrawable(getResources().getDrawable(R.drawable.on_green));
            } else {
                online.setImageDrawable(getResources().getDrawable(R.drawable.off_red));
            }

            return rowView;
        }
    }
}
