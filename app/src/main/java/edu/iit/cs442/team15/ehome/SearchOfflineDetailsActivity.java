package edu.iit.cs442.team15.ehome;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.iit.cs442.team15.ehome.model.Apartment;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;

public class SearchOfflineDetailsActivity extends AppCompatActivity {
    String value;
    FloatingActionButton call, text;
    TextView address, apartment, bdroom, bthroom, area, rent;
    CheckedTextView cable, electricity, gas, gym, intenet, pspace, heat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchoffline_details);

        Intent i = getIntent();
        value = i.getStringExtra("Position");

        ApartmentDatabaseHelper adh = ApartmentDatabaseHelper.getInstance();
        Apartment apartment = adh.getApartment(value);
        final ArrayList<String> amenities = adh.getAmenities(value);

        address = (TextView) findViewById(R.id.Address);
        this.apartment = (TextView) findViewById(R.id.Apartment_name);
        area = (TextView) findViewById(R.id.squarefoot);
        bthroom = (TextView) findViewById(R.id.Bathroom);
        bdroom = (TextView) findViewById(R.id.Bedroom);
        rent = (TextView) findViewById(R.id.rent);

        address.setText("Address: " + apartment.address);
        area.setText("Area: " + apartment.square_feet + "sq. ft.");
        bdroom.setText("Bedrooms: " + apartment.bedrooms);
        bthroom.setText("Bathrooms: " + apartment.bathrooms);
        rent.setText("Rent: $" + apartment.rent);

        pspace = (CheckedTextView) findViewById(R.id.parking);
        heat = (CheckedTextView) findViewById(R.id.thermostate);
        gas = (CheckedTextView) findViewById(R.id.gas);
        gym = (CheckedTextView) findViewById(R.id.gym);
        electricity = (CheckedTextView) findViewById(R.id.Electricity);
        //cable = (CheckedTextView) findViewById(R.id.cable); //0
        //internet = (CheckedTextView) findViewById(R.id.internet); //4

        //if (ammenitiesinfo.get(0) == "1")
        //    cable.setChecked(true);

        if (String.valueOf(amenities.get(1)) == "1")
            electricity.setChecked(true);

        if (amenities.get(2) == "1")
            gas.setChecked(true);

        if (amenities.get(3) == "TRUE")
            gym.setChecked(true);

        if (amenities.get(4) == "1")
            intenet.setChecked(true);

        if (amenities.get(5) == "TRUE")
            pspace.setChecked(true);

        //if (ammenitiesinfo.get(6) == "1")
        //    heat.setChecked(true);

        //Call Owner with the following method
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

        //Send Message to Owner
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

}
