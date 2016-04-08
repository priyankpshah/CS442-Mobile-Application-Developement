package edu.iit.cs442.team15.ehome;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import edu.iit.cs442.team15.ehome.model.User;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;
import edu.iit.cs442.team15.ehome.util.SavedLogin;
import edu.iit.cs442.team15.ehome.util.SimpleTextWatcher;
import edu.iit.cs442.team15.ehome.util.Validation;

public class AccountSettingsFragment extends Fragment implements View.OnClickListener {

    private User user;

    private EditText email;
    private EditText newPassword;
    private EditText confirmNewPassword;
    private EditText name;
    private EditText address;
    private EditText phone;
    private Button updateButton;

    public AccountSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AccountSettingsFragment.
     */
    public static AccountSettingsFragment newInstance() {
        return new AccountSettingsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = ApartmentDatabaseHelper.getInstance().getUser(SavedLogin.getInstance().getEmail());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_settings, container, false);

        email = (EditText) view.findViewById(R.id.accountEmail);
        email.setText(user.email);

        newPassword = (EditText) view.findViewById(R.id.accountNewPassword);
        newPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    confirmNewPassword.setVisibility(View.VISIBLE);
                } else {
                    confirmNewPassword.setVisibility(View.GONE);
                    confirmNewPassword.setText("");
                }
            }
        });

        confirmNewPassword = (EditText) view.findViewById(R.id.accountConfirmNewPassword);

        name = (EditText) view.findViewById(R.id.accountName);
        name.setText(user.name);

        address = (EditText) view.findViewById(R.id.accountAddress);
        address.setText(user.address);

        phone = (EditText) view.findViewById(R.id.accountPhone);
        phone.setText(user.phone);

        updateButton = (Button) view.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updateButton:
                boolean validInput = true;
                // check Phone
                if (!Validation.isPhoneNumber(phone.getText().toString())) {
                    if (phone.getText().toString().trim().isEmpty())
                        phone.setError(getString(R.string.error_missing_field));
                    else
                        phone.setError(getString(R.string.alert_invalid_phone));
                    validInput = false;
                }

                // TODO check Address?

                // check Name
                if (!Validation.isName(name.getText().toString())) {
                    if (name.getText().toString().trim().isEmpty())
                        name.setError(getString(R.string.error_missing_field));
                    else
                        name.setError(getString(R.string.alert_invalid_name));
                    validInput = false;
                }

                // check New Password if there is one
                if (newPassword.getText().toString().length() > 0) {
                    boolean isShort = !Validation.isPassword(newPassword.getText().toString());
                    boolean isMatch = newPassword.getText().toString().equals(confirmNewPassword.getText().toString());

                    if (isShort || !isMatch) {
                        validInput = false;

                        if (isShort && !isMatch)
                            newPassword.setError(getString(R.string.error_password_short_match, Validation.MIN_PASSWORD_LENGTH));
                        else if (isShort)
                            newPassword.setError(getString(R.string.alert_password_short, Validation.MIN_PASSWORD_LENGTH));
                        else
                            newPassword.setError(getString(R.string.alert_password_match));
                    }
                }

                // check Email
                if (!Validation.isEmail(email.getText().toString())) {
                    if (email.getText().toString().trim().isEmpty())
                        email.setError(getString(R.string.error_missing_field));
                    else
                        email.setError(getString(R.string.alert_invalid_email));
                    validInput = false;
                }

                if (validInput) {
                    // prompt user to enter current password
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Authentication")
                            .setMessage("Please enter your current password:")
                            .setView(R.layout.dialog_authenticate)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText currentPassword = (EditText) ((Dialog) dialog).findViewById(R.id.currentPassword);
                                    // TODO check entered password
                                    // TODO execute update
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .create()
                            .show();
                }
                break;
            default:
        }
    }

}
