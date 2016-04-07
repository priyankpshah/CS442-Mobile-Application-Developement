package edu.iit.cs442.team15.ehome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.iit.cs442.team15.ehome.model.User;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;
import edu.iit.cs442.team15.ehome.util.SavedLogin;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_EMAIL = "extra_email";
    public static final String EXTRA_LOGOUT = "extra_logout";

    public static final int CREATE_ACCOUNT_REQUEST = 10;

    private EditText loginEmail;
    private EditText loginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize singletons
        SavedLogin.initialize(this);

        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPassword = (EditText) findViewById(R.id.loginPassword);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);

        if (getIntent().getBooleanExtra(EXTRA_LOGOUT, false)) {
            // remove saved user login
            SavedLogin.getInstance().logout();
        } else {
            // check if user is already signed in, and sign them in if they are
            String savedEmail = SavedLogin.getInstance().getEmail();
            String savedPassword = SavedLogin.getInstance().getPassword();

            if (savedEmail != null && savedPassword != null)
                login(savedEmail, savedPassword);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CREATE_ACCOUNT_REQUEST:
                if (resultCode == RESULT_OK)
                    loginEmail.setText(data.getStringExtra(EXTRA_EMAIL));
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                login(email, password);
                break;
            case R.id.registerButton:
                Intent register = new Intent(this, RegisterActivity.class);
                startActivityForResult(register, CREATE_ACCOUNT_REQUEST);
                break;
        }
    }

    private void login(String email, String password) {
        User user = ApartmentDatabaseHelper.getInstance(this).getUser(email, password);

        // TODO better error feedback
        if (user != null) {
            // save login info
            SavedLogin.getInstance().saveLogin(user.email, user.password, user.name);

            Intent login = new Intent(this, MainActivity.class);
            startActivity(login);
            finish();
        } else {
            Toast.makeText(this, "Login failed.", Toast.LENGTH_LONG).show();
        }
    }

}
