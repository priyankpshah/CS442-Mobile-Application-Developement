package edu.iit.cs442.tmolenho.ehome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                // TODO verify login info
                Intent login = new Intent(this, MainActivity.class);
                startActivity(login);
                break;
            case R.id.registerButton:
                Intent register = new Intent(this, CreateAccountActivity.class);
                startActivity(register);
                break;
        }
    }

}
