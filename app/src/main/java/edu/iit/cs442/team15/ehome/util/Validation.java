package edu.iit.cs442.team15.ehome.util;

import android.util.Patterns;

public final class Validation {
    private Validation() {
    }

    public static final int MIN_PASSWORD_LENGTH = 6;

    public static boolean isEmail(String email) {
        return (email != null) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isName(String name) {
        return (name != null) && !name.isEmpty() && !name.matches("\\s+");
    }

    public static boolean isPhoneNumber(String phone) {
        return (phone != null) && Patterns.PHONE.matcher(phone).matches();
    }

    public static boolean isPassword(String password) {
        return (password != null) && password.length() >= MIN_PASSWORD_LENGTH;
    }

}
