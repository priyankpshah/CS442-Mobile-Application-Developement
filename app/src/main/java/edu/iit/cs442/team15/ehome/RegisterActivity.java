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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText rEmail;
    private EditText rPassword;
    private EditText rConfirm;
    private EditText rName;
    private EditText rAddress;
    private EditText rPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rEmail = (EditText) findViewById(R.id.registerEmail);
        rPassword = (EditText) findViewById(R.id.registerPassword);
        rConfirm = (EditText) findViewById(R.id.registerConfirmPassword);
        rName = (EditText) findViewById(R.id.registerName);
        rAddress = (EditText) findViewById(R.id.registerAddress);
        rPhone = (EditText) findViewById(R.id.registerPhone);

        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitButton:
                // TODO verify input
                User newUser = new User()
                        .setEmail(rEmail.getText().toString())
                        .setPassword(rPassword.getText().toString())
                        .setName(rName.getText().toString())
                        .setAddress(rAddress.getText().toString())
                        .setPhone(rPhone.getText().toString());

                if (ApartmentDatabaseHelper.getInstance(this).addUser(newUser) > 0) {
                    // success
                    Toast.makeText(this, "Account successfully created", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK, new Intent().putExtra(LoginActivity.EXTRA_EMAIL, newUser.email));
                    finish();
                } else {
                    // failure
                    Toast.makeText(this, "Failed to create account.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

}
