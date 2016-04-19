package edu.iit.cs442.team15.ehome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.iit.cs442.team15.ehome.model.Apartment;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;

public class EzHomeSearchOptionsActivity extends AppCompatActivity implements View.OnClickListener {

    private int zipcode;

    private int beds = 0;
    private int baths = 0;
    private int min_rent = 0;
    private int max_rent = Integer.MAX_VALUE;

    private Spinner bedspinner;

    private Spinner bathspinner;
    private EditText zip;
    private EditText minRent;
    private EditText maxRent;
    private Button apply;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ez_home_search_options);

        bedspinner = (Spinner) findViewById(R.id.bedSpinner);
        ArrayAdapter<CharSequence> bedsadapter = ArrayAdapter.createFromResource(this, R.array.select_beds, android.R.layout.simple_spinner_item);
        bedsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bedspinner.setAdapter(bedsadapter);

        bedspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String beds_selected = bedspinner.getSelectedItem().toString();
                try {
                    beds = Integer.parseInt(beds_selected.substring(0, 1));
                } catch (Exception e) {
                    beds = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        bathspinner = (Spinner) findViewById(R.id.bathSpinner);
        ArrayAdapter<CharSequence> bathsadapter = ArrayAdapter.createFromResource(this, R.array.select_baths, android.R.layout.simple_spinner_item);
        bathsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bathspinner.setAdapter(bathsadapter);

        bathspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String baths_selected = bathspinner.getSelectedItem().toString();
                try {
                    baths = Integer.parseInt(baths_selected.substring(0, 1));
                } catch (Exception e) {
                    baths = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        apply = (Button) findViewById(R.id.Apply);
        apply.setOnClickListener(this);
        save = (Button) findViewById(R.id.SaveFilter);
        apply.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Apply:
                zip = (EditText) findViewById(R.id.zipCode);
                minRent = (EditText) findViewById(R.id.min_rent);
                maxRent = (EditText) findViewById(R.id.max_rent);
                String zipcode_s = zip.getText().toString();
                String min_rent_s = minRent.getText().toString();
                String max_rent_s = maxRent.getText().toString();
                try {
                    if (zipcode_s.length() == 0) {
                        zipcode = 0;
                    } else zipcode = Integer.parseInt(zipcode_s);

                    if (min_rent_s.length() == 0) {
                        min_rent = 0;
                    } else min_rent = Integer.parseInt(min_rent_s);

                    if (max_rent_s.length() == 0) {
                        max_rent = Integer.MAX_VALUE;
                    } else max_rent = Integer.parseInt(max_rent_s);
                } catch (Exception e) {
                    Toast.makeText(this, "Zip Code and Rent must be Integer", Toast.LENGTH_LONG).show();
                }
                if (min_rent > max_rent) {
                    Toast.makeText(this, "Maximum rent must be greater than minimum rent!", Toast.LENGTH_LONG).show();
                    break;
                }

                List<Apartment> result = ApartmentDatabaseHelper.getInstance().getApartments(zipcode, beds, baths, min_rent, max_rent);

                if (result.isEmpty())
                    Toast.makeText(this, "No apartments found!", Toast.LENGTH_LONG).show();
                else {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("Searching Result", (ArrayList<Apartment>) result);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
                break;
            case R.id.SaveFilter:
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
