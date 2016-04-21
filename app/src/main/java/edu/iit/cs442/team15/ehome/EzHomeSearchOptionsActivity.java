package edu.iit.cs442.team15.ehome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class EzHomeSearchOptionsActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner bedSpinner;
    private Spinner bathSpinner;
    private EditText zip;
    private EditText minRent;
    private EditText maxRent;
    private Button apply;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ezhome_search_options);

        // bed spinner
        ArrayAdapter<CharSequence> bedsAdapter = ArrayAdapter.createFromResource(this, R.array.select_beds, android.R.layout.simple_spinner_item);
        bedsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        bedSpinner = (Spinner) findViewById(R.id.bedSpinner);
        bedSpinner.setAdapter(bedsAdapter);

        // bath spinner
        ArrayAdapter<CharSequence> bathsAdapter = ArrayAdapter.createFromResource(this, R.array.select_baths, android.R.layout.simple_spinner_item);
        bathsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        bathSpinner = (Spinner) findViewById(R.id.bathSpinner);
        bathSpinner.setAdapter(bathsAdapter);

        zip = (EditText) findViewById(R.id.zipCode);
        minRent = (EditText) findViewById(R.id.min_rent);
        maxRent = (EditText) findViewById(R.id.max_rent);

        apply = (Button) findViewById(R.id.Apply);
        apply.setOnClickListener(this);

        save = (Button) findViewById(R.id.SaveFilter);
        apply.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Apply:
                String zip_s = zip.getText().toString();
                String beds_selected = bedSpinner.getSelectedItem().toString();
                String baths_selected = bathSpinner.getSelectedItem().toString();
                String min_rent_s = minRent.getText().toString();
                String max_rent_s = maxRent.getText().toString();

                int zip;
                int beds;
                int baths;
                int min_rent;
                int max_rent;

                try {
                    zip = zip_s.isEmpty() ? 0 : Integer.parseInt(zip_s);
                    beds = bedSpinner.getSelectedItemPosition() == 0 ? 0 : Integer.parseInt(beds_selected.substring(0, 1));
                    baths = bathSpinner.getSelectedItemPosition() == 0 ? 0 : Integer.parseInt(baths_selected.substring(0, 1));
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
                resultIntent.putExtra("zip", zip);
                resultIntent.putExtra("beds", beds);
                resultIntent.putExtra("baths", baths);
                resultIntent.putExtra("min_rent", min_rent);
                resultIntent.putExtra("max_rent", max_rent);

                setResult(RESULT_OK, resultIntent);
                finish();
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
