package edu.iit.cs442.team15.ehome;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.iit.cs442.team15.ehome.model.User;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;
import edu.iit.cs442.team15.ehome.util.Validation;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_READ_PHONE_STATE = 1;

    private EditText rEmail;
    private EditText rPassword;
    private EditText rConfirmPassword;
    private EditText rName;
    private EditText rPhone; // TODO autofill with user's phone

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rEmail = (EditText) findViewById(R.id.registerEmail);
        rPassword = (EditText) findViewById(R.id.registerPassword);
        rConfirmPassword = (EditText) findViewById(R.id.registerConfirmPassword);
        rName = (EditText) findViewById(R.id.registerName);
        rPhone = (EditText) findViewById(R.id.registerPhone);

        rEmail.setHintTextColor(getResources().getColor(R.color.white));
        rPassword.setHintTextColor(getResources().getColor(R.color.white));
        rConfirmPassword.setHintTextColor(getResources().getColor(R.color.white));
        rName.setHintTextColor(getResources().getColor(R.color.white));
        rPhone.setHintTextColor(getResources().getColor(R.color.white));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE))
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        } else {
            // autofill user's phone number
            TelephonyManager tmgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            rPhone.setText(tmgr.getLine1Number());
        }

        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.getBackground().setColorFilter(0xFF40FFC9, PorterDuff.Mode.MULTIPLY);
        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitButton:
                // clear any existing errors
                rEmail.setError(null);
                rPassword.setError(null);
                rConfirmPassword.setError(null);
                rName.setError(null);
                rPhone.setError(null);

                // get user input
                User newUser = new User()
                        .setEmail(rEmail.getText().toString())
                        .setPassword(rPassword.getText().toString())
                        .setName(rName.getText().toString())
                        .setPhone(rPhone.getText().toString());

                // validate input
                boolean validInput = true;

                // check Phone
                if (!Validation.isPhoneNumber(newUser.phone)) {
                    if (newUser.phone.trim().isEmpty())
                        rPhone.setError(getString(R.string.error_missing_field));
                    else
                        rPhone.setError(getString(R.string.error_invalid_phone));
                    validInput = false;
                }

                // check Name
                if (!Validation.isName(newUser.name)) {
                    if (newUser.name.trim().isEmpty())
                        rName.setError(getString(R.string.error_missing_field));
                    else
                        rName.setError(getString(R.string.error_invalid_name));
                    validInput = false;
                }

                // check Password
                boolean isShort = !Validation.isPassword(newUser.password);
                boolean isMatch = newUser.password.equals(rConfirmPassword.getText().toString());

                if (isShort || !isMatch) {
                    validInput = false;

                    if (isShort && !isMatch)
                        rPassword.setError(getString(R.string.error_password_short_match, Validation.MIN_PASSWORD_LENGTH));
                    else if (isShort) {
                        if (newUser.password.isEmpty())
                            rPassword.setError(getString(R.string.error_missing_field));
                        else
                            rPassword.setError(getString(R.string.error_password_short, Validation.MIN_PASSWORD_LENGTH));
                    } else // if (!isMatch)
                        rPassword.setError(getString(R.string.error_password_match));
                }

                // check Email
                if (!Validation.isEmail(newUser.email)) {
                    if (newUser.email.trim().isEmpty())
                        rEmail.setError(getString(R.string.error_missing_field));
                    else
                        rEmail.setError(getString(R.string.error_invalid_email));
                    validInput = false;
                }

                if (validInput) {
                    if (ApartmentDatabaseHelper.getInstance().addUser(newUser) > 0) {
                        // success
                        Toast.makeText(this, R.string.toast_account_created, Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK, new Intent().putExtra(LoginActivity.EXTRA_EMAIL, newUser.email));
                        finish();
                    } else {
                        // email already in use, account not created
                        rEmail.setError(getString(R.string.error_email_not_unique));
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_PHONE_STATE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // autofill user's phone number
            TelephonyManager tmgr = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            rPhone.setText(tmgr.getLine1Number());
        }
    }

}
