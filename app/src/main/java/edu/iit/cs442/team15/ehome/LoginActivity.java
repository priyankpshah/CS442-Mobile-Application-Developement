package edu.iit.cs442.team15.ehome;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import edu.iit.cs442.team15.ehome.ApartmentDatabaseHelper.Users;
import edu.iit.cs442.team15.ehome.model.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText emailEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);

        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                User user = login(email, password);

                // TODO error feedback, pw hint, pass user data to MainActivity
                if (user != null) {
                    Intent login = new Intent(this, MainActivity.class);
                    startActivity(login);
                }
                break;
            case R.id.registerButton:
                Intent register = new Intent(this, CreateAccountActivity.class);
                startActivity(register);
                break;
        }
    }

    private User login(String email, String password) {
        SQLiteDatabase db = ApartmentDatabaseHelper.getInstance(this).getReadableDatabase();

        final String sqlQuery = "SELECT * FROM " + Users.TABLE_NAME + " WHERE " + Users.KEY_EMAIL + "=? AND " + Users.KEY_PASSWORD + "=?";
        Cursor result = db.rawQuery(sqlQuery, new String[]{email, password});

        User user = null;

        if (result.moveToFirst()) {
            // user exists
            user = new User();
            user.id = result.getInt(result.getColumnIndex(Users.KEY_ID));
            user.passwordHint = result.getString(result.getColumnIndex(Users.KEY_HINT));
            user.name = result.getString(result.getColumnIndex(Users.KEY_NAME));
            user.address = result.getString(result.getColumnIndex(Users.KEY_ADDRESS));
            user.phone = result.getString(result.getColumnIndex(Users.KEY_PHONE));
        }
        result.close();

        return user;
    }

}
