package edu.iit.cs442.team15.ehome;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.iit.cs442.team15.ehome.model.User;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;
import edu.iit.cs442.team15.ehome.util.PasswordDialog;
import edu.iit.cs442.team15.ehome.util.SavedLogin;
import edu.iit.cs442.team15.ehome.util.SimpleTextWatcher;
import edu.iit.cs442.team15.ehome.util.Validation;

public class AccountSettingsFragment extends Fragment implements View.OnClickListener {

    private OnAccountUpdatedListener mListener;

    private User user;

    private EditText aEmail;
    private EditText aNewPassword;
    private EditText aConfirmNewPassword;
    private EditText aName;
    private EditText aAddress;
    private EditText aPhone;
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
        if (context instanceof OnAccountUpdatedListener) {
            mListener = (OnAccountUpdatedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnAccountUpdatedListener");
        }
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

        aEmail = (EditText) view.findViewById(R.id.accountEmail);
        aEmail.setText(user.email);

        aNewPassword = (EditText) view.findViewById(R.id.accountNewPassword);
        aNewPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    aConfirmNewPassword.setVisibility(View.VISIBLE);
                } else {
                    aConfirmNewPassword.setVisibility(View.GONE);
                    aConfirmNewPassword.setText("");
                }
            }
        });

        aConfirmNewPassword = (EditText) view.findViewById(R.id.accountConfirmNewPassword);

        aName = (EditText) view.findViewById(R.id.accountName);
        aName.setText(user.name);

        aAddress = (EditText) view.findViewById(R.id.accountAddress);
        aAddress.setText(user.address);

        aPhone = (EditText) view.findViewById(R.id.accountPhone);
        aPhone.setText(user.phone);

        updateButton = (Button) view.findViewById(R.id.updateButton);
        updateButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.updateButton:
                // get current status of EditTexts
                final User newInfo = new User()
                        .setEmail(aEmail.getText().toString())
                        .setPassword(aNewPassword.getText().toString())
                        .setName(aName.getText().toString())
                        .setAddress(aAddress.getText().toString())
                        .setPhone(aPhone.getText().toString());

                // validate input
                boolean validInput = true;

                // check Phone
                if (!Validation.isPhoneNumber(newInfo.phone)) {
                    if (newInfo.phone.trim().isEmpty())
                        aPhone.setError(getString(R.string.error_missing_field));
                    else
                        aPhone.setError(getString(R.string.alert_invalid_phone));
                    validInput = false;
                }

                // TODO check Address?

                // check Name
                if (!Validation.isName(newInfo.name)) {
                    if (newInfo.name.trim().isEmpty())
                        aName.setError(getString(R.string.error_missing_field));
                    else
                        aName.setError(getString(R.string.alert_invalid_name));
                    validInput = false;
                }

                // check New Password if there is one
                if (newInfo.password.length() > 0) {
                    boolean isShort = !Validation.isPassword(newInfo.password);
                    boolean isMatch = newInfo.password.equals(aConfirmNewPassword.getText().toString());

                    if (isShort || !isMatch) {
                        validInput = false;

                        if (isShort && !isMatch)
                            aNewPassword.setError(getString(R.string.error_password_short_match, Validation.MIN_PASSWORD_LENGTH));
                        else if (isShort)
                            aNewPassword.setError(getString(R.string.alert_password_short, Validation.MIN_PASSWORD_LENGTH));
                        else // if (!isMatch)
                            aNewPassword.setError(getString(R.string.alert_password_match));
                    }
                } else {
                    newInfo.setPassword(user.password); // don't change password
                }

                // check Email
                if (!Validation.isEmail(newInfo.email)) {
                    if (newInfo.email.trim().isEmpty())
                        aEmail.setError(getString(R.string.error_missing_field));
                    else
                        aEmail.setError(getString(R.string.alert_invalid_email));
                    validInput = false;
                }

                if (validInput) {
                    // prompt user to enter current password
                    PasswordDialog.showDialog(getActivity(), new PasswordDialog.OnAuthenticationListener() {
                        @Override
                        public void onAuthentication() {
                            int result = ApartmentDatabaseHelper.getInstance().updateUser(user.email, newInfo);
                            if (result == 0)
                                Toast.makeText(getActivity(), R.string.toast_account_not_updated, Toast.LENGTH_SHORT).show();
                            else {
                                // clean up
                                user = newInfo; // update this fragment's cached user info
                                aNewPassword.setText(""); // clear new password field

                                SavedLogin.getInstance().saveLogin(newInfo.email, newInfo.password, newInfo.name); // update saved login info
                                mListener.onAccountUpdated(); // notify activity that account settings have changed

                                Toast.makeText(getActivity(), R.string.toast_account_updated, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
            default:
        }
    }

    public interface OnAccountUpdatedListener {
        public void onAccountUpdated();
    }

}
