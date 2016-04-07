package edu.iit.cs442.team15.ehome;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.iit.cs442.team15.ehome.model.User;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;
import edu.iit.cs442.team15.ehome.util.Validation;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText rEmail;
    private EditText rPassword;
    private EditText rConfirm;
    private EditText rName;
    private EditText rAddress;
    private EditText rPhone; // TODO autofill with user's phone

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
                if (validateInput()) {
                    User newUser = new User()
                            .setEmail(rEmail.getText().toString())
                            .setPassword(rPassword.getText().toString())
                            .setName(rName.getText().toString())
                            .setAddress(rAddress.getText().toString())
                            .setPhone(rPhone.getText().toString());

                    if (ApartmentDatabaseHelper.getInstance().addUser(newUser) > 0) {
                        // success
                        Toast.makeText(this, "Account successfully created", Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK, new Intent().putExtra(LoginActivity.EXTRA_EMAIL, newUser.email));
                        finish();
                    } else {
                        Toast.makeText(this, "Error: Failed to create account.", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private boolean validateInput() {
        // check Email
        if (!Validation.isEmail(rEmail.getText().toString())) {
            getAlertDialog(R.string.alert_invalid_email, rEmail).show();
            return false;
        }

        // check Passwords
        if (!Validation.isPassword(rPassword.getText().toString())) {
            getAlertDialog(getString(R.string.alert_password_short, Validation.MIN_PASSWORD_LENGTH), rPassword).show();
            return false;
        }
        if (!rPassword.getText().toString().equals(rConfirm.getText().toString())) {
            getAlertDialog(R.string.alert_password_match, rConfirm).show();
            return false;
        }

        // check Name
        if (!Validation.isName(rName.getText().toString())) {
            getAlertDialog(R.string.alert_invalid_name, rName).show();
            return false;
        }

        // TODO check Address?

        // check Phone
        if (!Validation.isPhoneNumber(rPhone.getText().toString())) {
            getAlertDialog(R.string.alert_invalid_phone, rPhone).show();
            return false;
        }

        return true; // all tests passed
    }

    private AlertDialog getAlertDialog(int messageId, final EditText target) {
        return getAlertDialog(getString(messageId), target);
    }

    private AlertDialog getAlertDialog(CharSequence message, final EditText target) {
        return new AlertDialog.Builder(this)
                .setTitle(android.R.string.dialog_alert_title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (target != null)
                            target.requestFocus();
                    }
                })
                .create();
    }

}
