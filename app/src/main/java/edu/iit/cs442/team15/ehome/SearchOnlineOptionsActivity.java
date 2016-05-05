package edu.iit.cs442.team15.ehome;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.List;

import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;
import edu.iit.cs442.team15.ehome.util.ApartmentSearchFilter;
import edu.iit.cs442.team15.ehome.util.Chicago;
import edu.iit.cs442.team15.ehome.util.SavedLogin;

public class SearchOnlineOptionsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText minRent, maxRent;
    private EditText distance;
    private EditText location;
    private Button search;

    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_online_options);

        minRent = (EditText) findViewById(R.id.filterMinRentOnline);
        maxRent = (EditText) findViewById(R.id.filterMaxRentOnline);

        distance = (EditText) findViewById(R.id.distanceOnline);
        location = (EditText) findViewById(R.id.locationOnline);

        search = (Button) findViewById(R.id.searchButtonOnline);
        search.setOnClickListener(this);

        // restore user's previous search settings
        ApartmentSearchFilter lastSearch = ApartmentDatabaseHelper.getInstance().getLastSearchFilter(SavedLogin.getInstance().getId(), false);
        if (lastSearch != null) {
            if (lastSearch.minCost != null)
                minRent.setText(lastSearch.minCost.toString());
            if (lastSearch.maxCost != null)
                maxRent.setText(lastSearch.maxCost.toString());

            if (lastSearch.distance != null && lastSearch.location != null) {
                distance.setText(lastSearch.distance.toString());
                location.setText(lastSearch.location);
            }
        }

        geocoder = new Geocoder(this);
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
            case R.id.searchButtonOnline:
                boolean validInput = true;
                ApartmentSearchFilter filter = new ApartmentSearchFilter().setEzhomeSearch(false);

                // location
                if (!location.getText().toString().isEmpty())
                    try {
                        List<Address> locations = geocoder.getFromLocationName(location.getText().toString(), 1, Chicago.LOWER_LEFT_LAT, Chicago.LOWER_LEFT_LONG, Chicago.UPPER_RIGHT_LAT, Chicago.UPPER_RIGHT_LONG);

                        if (!locations.isEmpty()) {
                            filter.setLocation(location.getText().toString());
                        } else {
                            location.setError(getText(R.string.error_invalid_location));
                            validInput = false;
                        }
                    } catch (IOException e) {
                        validInput = false;
                    }

                // distance
                if (filter.location != null) { // ignore distance if no location
                    if (distance.getText().toString().isEmpty()) {
                        distance.setError(getText(R.string.error_missing_distance));
                        validInput = false;
                    } else try {
                        double radius = Double.parseDouble(distance.getText().toString());
                        filter.setDistance(radius);
                    } catch (NumberFormatException e) {
                        distance.setError(getText(R.string.error_invalid_input));
                        validInput = false;
                    }
                }

                // min rent
                int minCost = 0; // needed for comparison to max rent
                if (!minRent.getText().toString().isEmpty())
                    try {
                        minCost = Integer.parseInt(minRent.getText().toString());
                        filter.setMinCost(minCost);
                    } catch (NumberFormatException e) {
                        minRent.setError(getText(R.string.error_invalid_input));
                        validInput = false;
                    }

                // max rent
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
                    resultIntent.putExtra("filter", filter);

                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                break;
        }
    }

}
