package edu.iit.cs442.team15.ehome;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.iit.cs442.team15.ehome.model.Amenity;
import edu.iit.cs442.team15.ehome.model.Apartment;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;

public class EzHomeSearchDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton call, text;
    TextView name, address, area, bedrooms, bathrooms, ezPrice, rent, thermostat, cable, internet, gas, electricity;
    CheckedTextView gym, parking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ezhome_search_details);

        ApartmentDatabaseHelper adh = ApartmentDatabaseHelper.getInstance();
        Apartment apartment = adh.getApartment(getIntent().getIntExtra("Position", -1));
        Amenity amenity = adh.getAmenity(getIntent().getIntExtra("Position", -1));

        Button details = (Button) findViewById(R.id.button_ezprice_details);
        details.setOnClickListener(this);

        // find views
        //apartment = (TextView) findViewById(R.apartmentId.Apartment_name);
        address = (TextView) findViewById(R.id.ezhome_address);
        area = (TextView) findViewById(R.id.ezhome_area);
        bedrooms = (TextView) findViewById(R.id.ezhome_bedrooms);
        bathrooms = (TextView) findViewById(R.id.ezhome_bathrooms);
        parking = (CheckedTextView) findViewById(R.id.ezhome_parking);
        gym = (CheckedTextView) findViewById(R.id.ezhome_gym);

        ezPrice = (TextView) findViewById(R.id.ezhome_ezprice);
        rent = (TextView) findViewById(R.id.ezhome_rent);
        thermostat = (TextView) findViewById(R.id.ezhome_thermostat);
        cable = (TextView) findViewById(R.id.ezhome_cable);
        internet = (TextView) findViewById(R.id.ezhome_internet);
        gas = (TextView) findViewById(R.id.ezhome_gas);
        electricity = (TextView) findViewById(R.id.ezhome_electricity);

        // show apartment info
        address.setText(getString(R.string.ezhome_address, apartment.address));
        area.setText(getString(R.string.ezhome_area, apartment.squareFeet));
        bedrooms.setText(getString(R.string.ezhome_bedrooms, apartment.bedrooms));
        bathrooms.setText(getString(R.string.ezhome_bathrooms, apartment.bathrooms));

        // ezPrice and details
        ezPrice.setText(getString(R.string.ezhome_ezprice, apartment.rent + amenity.getTotalCost()));
        rent.setText(getString(R.string.ezhome_rent, apartment.rent));
        thermostat.setText(getString(R.string.ezhome_thermostat, amenity.thermostat));
        cable.setText(getString(R.string.ezhome_cable, amenity.cable));
        internet.setText(getString(R.string.ezhome_internet, amenity.internet));
        gas.setText(getString(R.string.ezhome_gas, amenity.gas));
        electricity.setText(getString(R.string.ezhome_electricity, amenity.electricity));

        //Call owner with the following method
        call = (FloatingActionButton) findViewById(R.id.Call);
        call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent makeCall = new Intent(Intent.ACTION_CALL);
                String phNum = "tel:" + "3126473207";
                makeCall.setData(Uri.parse(phNum));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(makeCall);
                }
            }
        });

        //Send message to owner
        text = (FloatingActionButton) findViewById(R.id.sms);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendText = new Intent(Intent.ACTION_VIEW);
                sendText.putExtra("address", "3126473207");
                sendText.putExtra("sms_body", "I am Interested in leasing you property, Contact me!!");
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_ezprice_details:
                // toggle visibility of ezPrice details view
                LinearLayout ezPriceDetails = (LinearLayout) findViewById(R.id.ezprice_details);
                if (ezPriceDetails != null)
                    ezPriceDetails.setVisibility(ezPriceDetails.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
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
