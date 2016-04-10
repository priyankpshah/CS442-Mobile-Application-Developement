package edu.iit.cs442.team15.ehome;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;

public class searchoffline_details extends AppCompatActivity {
    String value;
    FloatingActionButton call, text;
    TextView address, Apartment,bdroom, bthroom,area,rent;
    CheckedTextView cable,electricity,gas,gym,intenet,pspace,heat;
    static ApartmentDatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchoffline_details);

        Intent i = getIntent();
        value = i.getStringExtra("Position");

        db = ApartmentDatabaseHelper.getInstance();
        ArrayList<String> aptinfo = db.getDetails(value);
        final ArrayList<String> ammenitiesinfo = db.getAmenities(value);

        address = (TextView)findViewById(R.id.Address);
        Apartment = (TextView)findViewById(R.id.Apartment_name);
        area= (TextView)findViewById(R.id.squarefoot);
        bthroom= (TextView)findViewById(R.id.Bathroom);
        bdroom= (TextView)findViewById(R.id.Bedroom);
        rent = (TextView)findViewById(R.id.rent);
        address.setText(aptinfo.get(0));
        area.setText(aptinfo.get(1));
        bdroom.setText(aptinfo.get(2));
        bthroom.setText(aptinfo.get(3));
        rent.setText(aptinfo.get(4));

        pspace= (CheckedTextView)findViewById(R.id.parking);
        heat= (CheckedTextView)findViewById(R.id.thermostate);
        gas= (CheckedTextView)findViewById(R.id.gas);
        gym= (CheckedTextView)findViewById(R.id.gym);
        //electricity = (TextView)findViewById(R.id.Electricity);1
        //cable = (TextView)findViewById(R.id.cable);0
        //internet = (TextView)findViewById(R.id.internet);4

//        if(ammenitiesinfo.get(0)=="1")
//        {
//            cable.setChecked(true);
//        }
//        if(ammenitiesinfo.get(1)=="1")
//        {
//            electricity.setChecked(true);
//        }
        if(ammenitiesinfo.get(2)=="1")
        {
            gas.setChecked(true);
        }
        if(ammenitiesinfo.get(3)=="TRUE")
        {
            gym.setChecked(true);
        }
//        if(ammenitiesinfo.get(4)=="1")
//        {
//            intenet.setChecked(true);
//        }

        if(ammenitiesinfo.get(5)=="TRUE")
        {
            pspace.setChecked(true);
        }
        if(ammenitiesinfo.get(6)=="1")
        {
            heat.setChecked(true);
        }

        //Call Owner with the following method
        call = (FloatingActionButton) findViewById(R.id.Call);
        call.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent makeCall = new Intent(Intent.ACTION_CALL);
                String phNum = "tel:" + "3126473207";
                makeCall.setData(Uri.parse(phNum));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    startActivity(makeCall);
                } }
        });
        //Send Message to Owner
        text = (FloatingActionButton) findViewById(R.id.sms);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendText = new Intent(Intent.ACTION_VIEW);
                sendText.putExtra("address", new String("3126473207"));
                sendText.putExtra("sms_body","I am Interested in leasing you property, Contact me!!");
            }
        });

    }

}
