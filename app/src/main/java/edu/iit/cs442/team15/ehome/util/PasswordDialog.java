package edu.iit.cs442.team15.ehome.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import edu.iit.cs442.team15.ehome.R;

public final class PasswordDialog {

    private PasswordDialog() {
    }

    public static void showDialog(final Context context, final OnAuthenticationListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.password_dialog_title)
                .setMessage(R.string.password_dialog_message)
                .setView(R.layout.dialog_authenticate)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText currentPassword = (EditText) ((Dialog) dialog).findViewById(R.id.currentPassword);
                        if (SavedLogin.getInstance().checkPassword(currentPassword.getText().toString()))
                            listener.onAuthentication();
                        else
                            retryDialog(context, listener);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }

    private static void retryDialog(final Context context, final OnAuthenticationListener listener) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.password_dialog_retry_title)
                .setMessage(R.string.password_dialog_retry_message)
                .setPositiveButton(R.string.password_dialog_retry_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog(context, listener);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
                .show();
    }

    public interface OnAuthenticationListener {
        public void onAuthentication();
    }

}
