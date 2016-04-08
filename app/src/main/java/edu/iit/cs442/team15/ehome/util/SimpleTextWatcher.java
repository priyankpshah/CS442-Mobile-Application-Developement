package edu.iit.cs442.team15.ehome.util;

import android.text.TextWatcher;

/**
 * Implementation of TextWatcher where beforeTextChanged and onTextChanged are already defined to do nothing.
 */
public abstract class SimpleTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}
