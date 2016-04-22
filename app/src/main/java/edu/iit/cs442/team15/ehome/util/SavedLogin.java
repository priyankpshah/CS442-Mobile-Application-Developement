package edu.iit.cs442.team15.ehome.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper.Users;

public final class SavedLogin {

    private static final String SAVED_LOGIN_PREFS = "saved_login_prefs";
    private static SavedLogin sInstance; // singleton instance

    private SharedPreferences savedLoginPrefs;

    private SavedLogin(Context context) {
        savedLoginPrefs = context.getSharedPreferences(SAVED_LOGIN_PREFS, Context.MODE_PRIVATE);
    }

    // called in LoginActivity.onCreate()
    public static synchronized void initialize(Context context) {
        if (sInstance == null)
            sInstance = new SavedLogin(context.getApplicationContext());
    }

    public static SavedLogin getInstance() {
        if (sInstance == null)
            throw new RuntimeException("SavedLogin not initialized.");
        return sInstance;
    }

    public int getId() {
        return savedLoginPrefs.getInt(Users.ID, -1);
    }

    @Nullable
    public String getEmail() {
        return savedLoginPrefs.getString(Users.EMAIL, null);
    }

    public boolean checkPassword(final String password) {
        return password != null && password.equals(savedLoginPrefs.getString(Users.PASSWORD, null));
    }

    @Nullable
    public String getName() {
        return savedLoginPrefs.getString(Users.NAME, null);
    }

    // checks stored login against database
    public boolean loginIsValid() {
        String email = savedLoginPrefs.getString(Users.EMAIL, null);
        String password = savedLoginPrefs.getString(Users.PASSWORD, null);
        return email != null && password != null && ApartmentDatabaseHelper.getInstance().checkLogin(email, password);
    }

    public void saveLogin(final int id, final String email, final String password, final String name) {
        savedLoginPrefs.edit()
                .putInt(Users.ID, id)
                .putString(Users.EMAIL, email)
                .putString(Users.PASSWORD, password)
                .putString(Users.NAME, name)
                .apply();
    }

    public void logout() {
        savedLoginPrefs.edit()
                .remove(Users.ID)
                .remove(Users.EMAIL)
                .remove(Users.PASSWORD)
                .remove(Users.NAME)
                .apply();
    }

}
