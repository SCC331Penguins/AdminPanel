package app.android.scc331.setadmin;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import app.android.scc331.setadmin.REST.LoginOperation;

public class LoginActivity extends Activity {

    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username_text);
        password = findViewById(R.id.password_text);

        username.setText("Stouty");
        password.setText("dredd");

        Button login = findViewById(R.id.login_button);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(username.getText().toString(), password.getText().toString());
            }
        });
    }

    public void login(String username, String password){

        LoginOperation loginOperation = new LoginOperation(this);
        boolean result = loginOperation.start(username, password);

        if(result) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }else{
            Toast.makeText(this, "Invalid admin user", Toast.LENGTH_LONG).show();
        }
    }
}
