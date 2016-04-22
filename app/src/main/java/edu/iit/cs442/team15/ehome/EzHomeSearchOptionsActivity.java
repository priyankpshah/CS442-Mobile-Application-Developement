package edu.iit.cs442.team15.ehome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class EzHomeSearchOptionsActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText minRent;
    private EditText maxRent;
    private Spinner minBedsSpinner;
    private Spinner maxBedsSpinner;
    private Spinner minBathroomsSpinner;
    private Spinner maxBathroomsSpinner;
    private EditText minArea;
    private EditText maxArea;
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
        minRoomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // bed spinners
        minBedsSpinner = (Spinner) findViewById(R.id.filterMinBeds);
        minBedsSpinner.setAdapter(minRoomAdapter);
        maxBedsSpinner = (Spinner) findViewById(R.id.filterMaxBeds);
        maxBedsSpinner.setAdapter(maxRoomAdapter);

        // bathrooms spinners
        minBathroomsSpinner = (Spinner) findViewById(R.id.filterMinBaths);
        minBathroomsSpinner.setAdapter(minRoomAdapter);
        maxBathroomsSpinner = (Spinner) findViewById(R.id.filterMaxBaths);
        maxBathroomsSpinner.setAdapter(maxRoomAdapter);

        // area
        minArea = (EditText) findViewById(R.id.filterMinArea);
        maxArea = (EditText) findViewById(R.id.filterMaxArea);

        // options
        hasGym = (CheckBox) findViewById(R.id.filterHasGym);
        hasParking = (CheckBox) findViewById(R.id.filterHasParking);

        Button apply = (Button) findViewById(R.id.filterApplyButton);
        apply.setOnClickListener(this);

        Button save = (Button) findViewById(R.id.filterSaveButton);
        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filterApplyButton:
                String beds_selected = minBedsSpinner.getSelectedItem().toString();
                String baths_selected = minBathroomsSpinner.getSelectedItem().toString();
                String min_rent_s = minRent.getText().toString();
                String max_rent_s = maxRent.getText().toString();

                int beds;
                int baths;
                int min_rent;
                int max_rent;

                try {
                    beds = minBedsSpinner.getSelectedItemPosition() == 0 ? 0 : Integer.parseInt(beds_selected.substring(0, 1));
                    baths = minBathroomsSpinner.getSelectedItemPosition() == 0 ? 0 : Integer.parseInt(baths_selected.substring(0, 1));
                    min_rent = min_rent_s.isEmpty() ? 0 : Integer.parseInt(min_rent_s);
                    max_rent = max_rent_s.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(max_rent_s);
                } catch (Exception e) {
                    Toast.makeText(this, "Zip Code and Rent must be Integer", Toast.LENGTH_LONG).show();
                    break;
                }

                if (min_rent > max_rent) {
                    Toast.makeText(this, "Maximum rent must be greater than minimum rent!", Toast.LENGTH_LONG).show();
                    break;
                }

                // pass search options
                Intent resultIntent = new Intent();
                resultIntent.putExtra("beds", beds);
                resultIntent.putExtra("baths", baths);
                resultIntent.putExtra("min_rent", min_rent);
                resultIntent.putExtra("max_rent", max_rent);

                setResult(RESULT_OK, resultIntent);
                finish();
                break;
            case R.id.filterSaveButton:
                //TODO Save current filter in user's profile
                break;
        }
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

}
