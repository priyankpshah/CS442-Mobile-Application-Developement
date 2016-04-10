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

import java.util.ArrayList;

import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;

public class searchoffline_details extends AppCompatActivity {
    String value;
    FloatingActionButton call, text;
    static ApartmentDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchoffline_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        value = i.getStringExtra("Position");
        db = ApartmentDatabaseHelper.getInstance();

        ArrayList<String> aptinfo = db.getDetails(value);
        ArrayList<String> ammenitiesinfo = db.getAmenities(value);

        System.out.println("APT SIZE:" + aptinfo.get(4));
        System.out.println("AMM SIZE:" + ammenitiesinfo.get(1));
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
                }
            }
        });
        //Send Message to Owner
        text = (FloatingActionButton) findViewById(R.id.sms);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendText = new Intent(Intent.ACTION_VIEW);
                sendText.putExtra("address", new String("3126473207"));
                sendText.putExtra("sms_body", "I am Interested in leasing you property, Contact me!!");
            }
        });

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
