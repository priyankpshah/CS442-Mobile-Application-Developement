package edu.iit.cs442.team15.ehome;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.iit.cs442.team15.ehome.model.Amenity;
import edu.iit.cs442.team15.ehome.model.Apartment;
import edu.iit.cs442.team15.ehome.model.User;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;
import edu.iit.cs442.team15.ehome.util.Chicago;
import edu.iit.cs442.team15.ehome.util.ImageAdapter;
import edu.iit.cs442.team15.ehome.util.SavedLogin;

public class EzHomeSearchDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_APARTMENT_ID = "apartment_id";

    private int apartmentId;
    private Button call, text,map;
    private TextView name, address, area, bedrooms, bathrooms, ezPrice, rent, thermostat, cable, internet, gas, electricity,ownerinfo;
    private CheckedTextView gym, parking;
    static final int NUM_ITEMS = 5;


    ViewPager viewPager;
    private MenuItem favorites;
    private boolean isFavorited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ezhome_search_details);

        apartmentId = getIntent().getIntExtra(EXTRA_APARTMENT_ID, -1);
        final Apartment apartment = ApartmentDatabaseHelper.getInstance().getApartment(apartmentId);

        Button details = (Button) findViewById(R.id.button_ezprice_details);
        details.setOnClickListener(this);

        address = (TextView) findViewById(R.id.ezhome_address);
        area = (TextView) findViewById(R.id.ezhome_area);
        bedrooms = (TextView) findViewById(R.id.ezhome_bedrooms);
        bathrooms = (TextView) findViewById(R.id.ezhome_bathrooms);
        parking = (CheckedTextView) findViewById(R.id.ezhome_parking);
        gym = (CheckedTextView) findViewById(R.id.ezhome_gym);
        ownerinfo = (TextView)findViewById(R.id.ownerinfo);

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
        ownerinfo.setText(getString(R.string.ezhome_owner,apartment.owner.complexName));

                // ezPrice and details
        ezPrice.setText(getString(R.string.ezhome_ezprice, apartment.getTotalCost()));
        rent.setText(getString(R.string.ezhome_rent, apartment.rent));
        thermostat.setText(getString(R.string.ezhome_thermostat, apartment.amenity.thermostat));
        cable.setText(getString(R.string.ezhome_cable, apartment.amenity.cable));
        internet.setText(getString(R.string.ezhome_internet, apartment.amenity.internet));
        gas.setText(getString(R.string.ezhome_gas, apartment.amenity.gas));
        electricity.setText(getString(R.string.ezhome_electricity, apartment.amenity.electricity));
        Boolean parkingval = apartment.amenity.parking;
        Boolean Gymval = apartment.amenity.gym;
        //Toast.makeText(getApplicationContext(),"Rent: "+apartment.rent,Toast.LENGTH_LONG).show();
        gym.setChecked(Gymval);
        parking.setChecked(parkingval);

        //Call owner with the following method
        call = (Button)findViewById(R.id.Call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent makeCall = new Intent(Intent.ACTION_CALL);
                String phNum = "tel:" + "3126473207";
                makeCall.setData(Uri.parse(phNum));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(makeCall);

                }
            }
        });


        //Send message to owner
        final User currentUser = ApartmentDatabaseHelper.getInstance().getUser(SavedLogin.getInstance().getEmail());
        text = (Button)findViewById(R.id.sms);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PhNO = "+13126473207";
                String message = "I am Interested in leasing you property, Contact me: "+currentUser.phone;
                //Toast.makeText(EzHomeSearchDetailsActivity.this,message,Toast.LENGTH_LONG).show();
                try {
                    SmsManager smsMan = SmsManager.getDefault();
                    ArrayList<String> parts = smsMan.divideMessage(message);
                    smsMan.sendMultipartTextMessage(PhNO, null, parts, null, null);
                }
                catch (Exception e)
                {
                    Toast.makeText(EzHomeSearchDetailsActivity.this,"Out of Network, Can't Send SMS",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        map = (Button)findViewById(R.id.button_search_map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String destaddress = apartment.address;
                //String curaddress = "";
                Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("geo:"+ Chicago.LATITUDE+","+Chicago.LONGITUDE+"?q="+destaddress));
                i.setPackage("com.google.android.apps.maps");
                //i.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
                startActivity(i);

            }
        });
        //Display multiple Images with swipe gesture, Adapter is located in ImageAdepter File.
        int[] imageIds = new int[]{
                R.drawable.img1, R.drawable.img2, R.drawable.img3,
                R.drawable.img4, R.drawable.img5, R.drawable.img6
        };

        ViewPager vp = (ViewPager)findViewById(R.id.imgpage);
        ImageAdapter ap = new ImageAdapter(this, imageIds, null);
        vp.setAdapter(ap);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_ezhome_details, menu);

        favorites = menu.findItem(R.id.addToFavorites);
        isFavorited = ApartmentDatabaseHelper.getInstance().isFavorited(SavedLogin.getInstance().getId(), apartmentId);
        favorites.setIcon(isFavorited ? R.drawable.favorite : R.drawable.add_favorite);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addToFavorites:
                if (isFavorited) {
                    if (ApartmentDatabaseHelper.getInstance().removeFavorite(SavedLogin.getInstance().getId(), apartmentId) > 0) {
                        favorites.setIcon(R.drawable.add_favorite);
                        isFavorited = false;
                    }
                } else {
                    if (ApartmentDatabaseHelper.getInstance().addFavorite(SavedLogin.getInstance().getId(), apartmentId) > 0) {
                        favorites.setIcon(R.drawable.favorite);
                        isFavorited = true;
                    }
                }
                return true;
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
