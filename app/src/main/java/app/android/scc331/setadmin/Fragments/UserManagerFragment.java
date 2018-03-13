package app.android.scc331.setadmin.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import app.android.scc331.setadmin.LoginActivity;
import app.android.scc331.setadmin.R;
import app.android.scc331.setadmin.REST.DataObjects.DatabaseDataListener;
import app.android.scc331.setadmin.REST.DataObjects.DatabaseSet;
import app.android.scc331.setadmin.REST.DataObjects.User;
import app.android.scc331.setadmin.REST.Interfaces.RestOperationListener;
import app.android.scc331.setadmin.REST.TEST.RestOperationFactory;
import app.android.scc331.setadmin.REST.TEST.SetRESTOperation;
import app.android.scc331.setadmin.REST.UserDatabaseOperation;

public class UserManagerFragment extends Fragment implements DatabaseDataListener, RestOperationListener {

    private ArrayList<User> databaseItems;

    private DatabaseAdapter adapter;

    private RestOperationListener restOperationListener;

    private ListView databaseList;

    public UserManagerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_manager, container, false);

        restOperationListener = this;

        databaseList = v.findViewById(R.id.database_list);


        Button logout = v.findViewById(R.id.logout_button);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getContext().getSharedPreferences("com.set.app", Context.MODE_PRIVATE);
                preferences.edit().putString("token", null).apply();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
            }
        });

        UserDatabaseOperation userDatabaseOperation = new UserDatabaseOperation(getActivity());
        userDatabaseOperation.start(this);

        return v;
    }

    @Override
    public void onDatabaseData(DatabaseSet databaseSet) {
        databaseItems = databaseSet.getUserSet();
        adapter = new DatabaseAdapter(getActivity(), databaseItems);
        databaseList.setAdapter(adapter);
    }

    public void addUserData(User user){
        User userToRemove = null;
        for(User u : databaseItems){
            if(u.getUsername().equals(user.getUsername())){
                userToRemove = u;
            }
        }
        if(userToRemove == null) {
            databaseItems.add(user);
            adapter.notifyDataSetChanged();
        }
    }

    public void removeUserData(User user) {
        User userToRemove = null;
        for (User u : databaseItems) {
            if (u.getUsername().equals(user.getUsername()))
                userToRemove = u;
        }
        if (userToRemove != null) {
            databaseItems.remove(userToRemove);
            adapter.notifyDataSetChanged();
        }
    }

    public void updateUserAdmin(User user){
        User userToUpdate = null;
        for(User u : databaseItems){
            if(u.getUsername().equals(user.getUsername()))
                userToUpdate = u;
        }
        if(userToUpdate != null){
            userToUpdate.setAdmin(user.is_admin());
            adapter.notifyDataSetChanged();
        }
    }

    public View.OnClickListener removeUserClick(final User user) {
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

                info.setText(user.getUsername());

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SetRESTOperation removeOperation = RestOperationFactory.removeUserOperation(getActivity(), user.getUsername());
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

    public View.OnClickListener editUserClick(final User user){
        return new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                final View vw = layoutInflater.inflate(R.layout.dialog_edit_user, null);
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setCancelable(false);
                Button save = vw.findViewById(R.id.save_user);
                final Button cancel = vw.findViewById(R.id.cancel);
                final ToggleButton adminToggle = vw.findViewById(R.id.admin_toggle_button);

                adminToggle.setChecked(user.is_admin());

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(adminToggle.isChecked() == user.is_admin()){
                            alertDialog.cancel();
                            return;
                        }
                        int admin = 0;
                        if(adminToggle.isChecked()){
                            admin = 1;
                        }else{
                            admin = 0;
                        }

                        SetRESTOperation adminOperation = RestOperationFactory.adminOperation(getActivity(), user.getUsername(), adminToggle.isChecked());
                        adminOperation.run();

                        alertDialog.cancel();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });

                alertDialog.setView(vw, 0, 0, 0, 0);
                alertDialog.show();
            }
        };
    }

    @Override
    public void onResult(boolean result, int type) {

    }

    private class DatabaseAdapter extends ArrayAdapter<User> {

        private Context context;
        private ArrayList<User> users;

        public DatabaseAdapter(@NonNull Context context, ArrayList<User> users) {
            super(context, -1, users);
            this.context = context;
            this.users = users;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.database_item_user, parent, false);

            User user = users.get(position);

            TextView id = v.findViewById(R.id.database_item_id);
            TextView username = v.findViewById(R.id.database_item_username);
            TextView routers = v.findViewById(R.id.database_item_router_count);

            ImageView admin = v.findViewById(R.id.database_item_admin);

            id.setText(user.getId());
            username.setText(user.getUsername());
            routers.setText(user.getRouters());

            if (user.is_admin()) {
                admin.setImageDrawable(getResources().getDrawable(R.drawable.on_green));
            } else {
                admin.setImageDrawable(getResources().getDrawable(R.drawable.off_red));
            }

            Button remove = v.findViewById(R.id.remove_button);

            Button edit = v.findViewById(R.id.edit_button);

            remove.setOnClickListener(removeUserClick(user));

            edit.setOnClickListener(editUserClick(user));

            return v;
        }
    }

}
