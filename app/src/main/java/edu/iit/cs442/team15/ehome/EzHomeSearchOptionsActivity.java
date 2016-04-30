package edu.iit.cs442.team15.ehome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;
import edu.iit.cs442.team15.ehome.util.ApartmentSearchFilter;
import edu.iit.cs442.team15.ehome.util.SavedLogin;

public class EzHomeSearchOptionsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private EditText minRent;
    private EditText maxRent;
    private Spinner minBedsSpinner;
    private Spinner maxBedsSpinner;
    private Spinner minBathroomsSpinner;
    private Spinner maxBathroomsSpinner;
    private EditText minAreaEditText;
    private EditText maxAreaEditText;
    private CheckBox hasGym;
    private CheckBox hasParking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ezhome_search_options);

        // ezPrice
        minRent = (EditText) findViewById(R.id.filterMinRent);
        maxRent = (EditText) findViewById(R.id.filterMaxRent);

        ArrayAdapter<CharSequence> minRoomAdapter = ArrayAdapter.createFromResource(this, R.array.select_min_rooms, android.R.layout.simple_spinner_item);
        minRoomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> maxRoomAdapter = ArrayAdapter.createFromResource(this, R.array.select_max_rooms, android.R.layout.simple_spinner_item);
        maxRoomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // bed spinners
        minBedsSpinner = (Spinner) findViewById(R.id.filterMinBeds);
        minBedsSpinner.setAdapter(minRoomAdapter);
        minBedsSpinner.setOnItemSelectedListener(this);

        maxBedsSpinner = (Spinner) findViewById(R.id.filterMaxBeds);
        maxBedsSpinner.setAdapter(maxRoomAdapter);
        maxBedsSpinner.setOnItemSelectedListener(this);

        // bathrooms spinners
        minBathroomsSpinner = (Spinner) findViewById(R.id.filterMinBaths);
        minBathroomsSpinner.setAdapter(minRoomAdapter);
        minBathroomsSpinner.setOnItemSelectedListener(this);

        maxBathroomsSpinner = (Spinner) findViewById(R.id.filterMaxBaths);
        maxBathroomsSpinner.setAdapter(maxRoomAdapter);
        maxBathroomsSpinner.setOnItemSelectedListener(this);

        // area
        minAreaEditText = (EditText) findViewById(R.id.filterMinArea);
        maxAreaEditText = (EditText) findViewById(R.id.filterMaxArea);

        // options
        hasGym = (CheckBox) findViewById(R.id.filterHasGym);
        hasParking = (CheckBox) findViewById(R.id.filterHasParking);

        Button apply = (Button) findViewById(R.id.filterSearchButton);
        apply.setOnClickListener(this);

        // set form to user's last search settings
        ApartmentSearchFilter lastSearch = ApartmentDatabaseHelper.getInstance().getLastSearchFilter(SavedLogin.getInstance().getId(), true);
        if (lastSearch != null) {
            // cost
            if (lastSearch.minCost != null)
                minRent.setText(lastSearch.minCost.toString());
            if (lastSearch.maxCost != null)
                maxRent.setText(lastSearch.maxCost.toString());

            // beds
            if (lastSearch.minBeds != null)
                minBedsSpinner.setSelection(lastSearch.minBeds);
            if (lastSearch.maxBeds != null)
                maxBedsSpinner.setSelection(lastSearch.maxBeds);

            // bathrooms
            if (lastSearch.minBathrooms != null)
                minBathroomsSpinner.setSelection(lastSearch.minBathrooms);
            if (lastSearch.maxBathrooms != null)
                maxBathroomsSpinner.setSelection(lastSearch.maxBathrooms);

            // sq ft
            if (lastSearch.minArea != null)
                minAreaEditText.setText(lastSearch.minArea.toString());
            if (lastSearch.maxArea != null)
                maxAreaEditText.setText(lastSearch.maxArea.toString());

            // options
            if (lastSearch.hasGym != null)
                hasGym.setChecked(lastSearch.hasGym);
            if (lastSearch.hasParking != null)
                hasParking.setChecked(lastSearch.hasParking);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO ask user if they are sure they want to leave
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filterSearchButton:
                boolean validInput = true;
                ApartmentSearchFilter filter = new ApartmentSearchFilter();

                // options
                if (hasParking.isChecked())
                    filter.setHasParking(true);

                if (hasGym.isChecked())
                    filter.setHasGym(true);

                // min area
                int minArea = 0;
                if (!minAreaEditText.getText().toString().isEmpty())
                    try {
                        minArea = Integer.parseInt(minAreaEditText.getText().toString());
                        filter.setMinArea(minArea);
                    } catch (NumberFormatException e) {
                        minAreaEditText.setError(getText(R.string.error_invalid_input));
                        validInput = false;
                    }

                // max area
                if (!maxAreaEditText.getText().toString().isEmpty())
                    try {
                        int maxArea = Integer.parseInt(maxAreaEditText.getText().toString());
                        if (maxArea < minArea) {
                            maxAreaEditText.setError(getText(R.string.error_max_area_too_low));
                            validInput = false;
                        } else
                            filter.setMaxArea(maxArea);
                    } catch (NumberFormatException e) {
                        maxAreaEditText.setError(getText(R.string.error_invalid_input));
                        validInput = false;
                    }

                // min baths
                if (minBathroomsSpinner.getSelectedItemPosition() != 0)
                    filter.setMinBathrooms(minBathroomsSpinner.getSelectedItemPosition());

                // max baths
                if (maxBathroomsSpinner.getSelectedItemPosition() != 0) {
                    if (maxBathroomsSpinner.getSelectedItemPosition() < minBathroomsSpinner.getSelectedItemPosition()) {
                        Toast.makeText(this, R.string.toast_max_bathrooms, Toast.LENGTH_LONG).show();
                        validInput = false;
                    } else
                        filter.setMaxBathrooms(maxBathroomsSpinner.getSelectedItemPosition());
                }

                // min beds
                if (minBedsSpinner.getSelectedItemPosition() != 0)
                    filter.setMinBeds(minBedsSpinner.getSelectedItemPosition());

                // max beds
                if (maxBedsSpinner.getSelectedItemPosition() != 0) {
                    if (maxBedsSpinner.getSelectedItemPosition() < minBedsSpinner.getSelectedItemPosition()) {
                        Toast.makeText(this, R.string.toast_max_beds, Toast.LENGTH_LONG).show();
                        validInput = false;
                    } else
                        filter.setMaxBeds(maxBedsSpinner.getSelectedItemPosition());
                }

                // min Cost
                int minCost = 0;
                if (!minRent.getText().toString().isEmpty())
                    try {
                        minCost = Integer.parseInt(minRent.getText().toString());
                        filter.setMinCost(minCost);
                    } catch (NumberFormatException e) {
                        minRent.setError(getText(R.string.error_invalid_input));
                        validInput = false;
                    }

                // max Cost
                if (!maxRent.getText().toString().isEmpty())
                    try {
                        int maxCost = Integer.parseInt(maxRent.getText().toString());
                        if (maxCost < minCost) {
                            maxRent.setError(getText(R.string.error_max_rent_too_low));
                            validInput = false;
                        } else
                            filter.setMaxCost(maxCost);
                    } catch (NumberFormatException e) {
                        maxRent.setError(getText(R.string.error_invalid_input));
                        validInput = false;
                    }

                if (validInput) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("filter", filter); // pass search options

                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.filterMinBeds:
                int maxBeds = maxBedsSpinner.getSelectedItemPosition();
                if (maxBeds != 0 && position > maxBeds)
                    maxBedsSpinner.setSelection(position, true);
                break;
            case R.id.filterMaxBeds:
                if (position != 0 && position < minBedsSpinner.getSelectedItemPosition())
                    minBedsSpinner.setSelection(position, true);
                break;
            case R.id.filterMinBaths:
                int maxBathrooms = maxBathroomsSpinner.getSelectedItemPosition();
                if (maxBathrooms != 0 && position > maxBathrooms)
                    maxBathroomsSpinner.setSelection(position, true);
                break;
            case R.id.filterMaxBaths:
                if (position != 0 && position < minBathroomsSpinner.getSelectedItemPosition())
                    minBathroomsSpinner.setSelection(position, true);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

}
