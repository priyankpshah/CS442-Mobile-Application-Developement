package edu.iit.cs442.team15.ehome;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

public class SearchOnlineFragment extends Fragment implements OnMapReadyCallback {

    private int userIcon;
    private Marker userMarker;
    LocationManager locMan;
    Location lastLoc;

    public SearchOnlineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchOnlineFragment.
     */
    public static SearchOnlineFragment newInstance() {
        return new SearchOnlineFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_online, container, false);

        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

        userIcon = R.drawable.search_online_marker_icon;

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // destroy embedded fragment
        FragmentManager cfm = getChildFragmentManager();
        cfm.beginTransaction()
                .remove(cfm.findFragmentById(R.id.map))
                .commitAllowingStateLoss();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("SearchOnlineFragment", "Location permissions not available.");
            return;
        }

        locMan = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (lastLoc != null) {
            double lat = lastLoc.getLatitude();
            double lng = lastLoc.getLongitude();
            LatLng lastLatLng = new LatLng(lat, lng);

            //  if (userMarker != null) userMarker.remove();

            userMarker = map.addMarker(new MarkerOptions()
                    .position(lastLatLng)
                    .title("You are here")
                    .icon(BitmapDescriptorFactory.fromResource(userIcon))
                    .snippet("Your last recorded location"));

            map.setMyLocationEnabled(true);
        } else {
            // get Chicago's location
            Address chicago = null;
            try {
                chicago = new Geocoder(getActivity()).getFromLocationName("Chicago", 1).get(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

            userMarker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(chicago.getLatitude(), chicago.getLongitude()))
                    .title("Chicago")
                    .icon(BitmapDescriptorFactory.fromResource(userIcon))
                    .snippet("Chicago"));
        }

        // zoom in on user location or Chicago
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(), 12.5f), 100, null);

        map.addCircle(new CircleOptions()
                .center(userMarker.getPosition())
                .radius(200)
                .strokeWidth(0f)
                .fillColor(0x5590B6FD));

        userMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(41.8348969, -87.6142183))
                .title("Lake Meadows Apartments")
                .icon(BitmapDescriptorFactory.fromResource(userIcon))
                .snippet("500 East 33rd Street, Chicago, IL 60616\n" +
                        "Phn.: (312) 842-7333\n" + "Email ID: lakemeadowsleasing@dklivingapts.com\n" + "Cost: $1,865"));
        LatLng lastLatLng1 = new LatLng(41.8348969, -87.6142183);
        //map.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng1), 3000, null);

        map.addCircle(new CircleOptions()
                .center(userMarker.getPosition())
                .radius(200)
                .strokeWidth(0f)
                .fillColor(0x5590B6FD));


        userMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(41.8426694, -87.6162544))
                .title("Prairie Shores Apartments")
                .icon(BitmapDescriptorFactory.fromResource(userIcon))
                .snippet("2851 S King Dr., Chicago, IL 60616\n" +
                        "Phn.: (312) 842-7333\n" + "Email ID: prairieshoresleasing@dklivingapts.com\n" + "Cost: $1,865"));
        LatLng lastLatLng2 = new LatLng(41.8426694, -87.6162544);
        //map.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng2), 3000, null);

        map.addCircle(new CircleOptions()
                .center(userMarker.getPosition())
                .radius(200)
                .strokeWidth(0f)
                .fillColor(0x5590B6FD));


        userMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(41.835314, -87.62671))
                .title("Illinois Institute of Technology")
                .icon(BitmapDescriptorFactory.fromResource(userIcon))
                .snippet("3222-3262 S State St, Chicago, IL 60616\n" + "Phn.: (312) 842-7333\n" + "Email ID: vedu16@gmail.com\n" + "Cost: $1,865"));
        LatLng lastLatLng3 = new LatLng(41.835314, -87.62671);
        //map.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng3), 3000, null);

        map.addCircle(new CircleOptions()
                .center(userMarker.getPosition())
                .radius(200)
                .strokeWidth(0f)
                .fillColor(0x5590B6FD));


        userMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(41.836544, -87.649508))
                .title("Bridgeport")
                .icon(BitmapDescriptorFactory.fromResource(userIcon))
                .snippet("920 W 32nd St, Chicago, IL 60608\n" + "Phn.: (312) 842-7333\n" + "Email ID: vedu16@gmail.com\n" + "Cost: $1,865"));
        LatLng lastLatLng4 = new LatLng(41.836544, -87.649508);
        //map.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng4), 3000, null);

        map.addCircle(new CircleOptions()
                .center(userMarker.getPosition())
                .radius(200)
                .strokeWidth(0f)
                .fillColor(0x5590B6FD));

        userMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(41.8348975, -87.6142175))
                .title("Allure Apartments")
                .icon(BitmapDescriptorFactory.fromResource(userIcon))
                .snippet("1401 S State St Chicago, IL, 60605\n" + "Phn.: (314) 842-7333\n" + "Email ID: allureapartments@gmail.com"));
        LatLng lastLatLng5 = new LatLng(41.8348969, -87.6142183);
        //map.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng5), 3000, null);

        map.addCircle(new CircleOptions()
                .center(userMarker.getPosition())
                .radius(200)
                .strokeWidth(0f)
                .fillColor(0x5590B6FD));

        userMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(41.837550, -87.648515))
                .title("Catalyst Apartments")
                .icon(BitmapDescriptorFactory.fromResource(userIcon))
                .snippet("123 N Des Plaines St Chicago 60661\n" + "Phn.: (312) 942-7333\n" + "Email ID: catalystpartments@gmail.com"));
        LatLng lastLatLng6 = new LatLng(41.837550, -87.648515);
        //map.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng6), 3000, null);

        map.addCircle(new CircleOptions()
                .center(userMarker.getPosition())
                .radius(200)
                .strokeWidth(0f)
                .fillColor(0x5590B6FD));


        userMarker = map.addMarker(new MarkerOptions()
                .position(new LatLng(41.835565, -87.644525))
                .title("Lake Shore Drive")
                .icon(BitmapDescriptorFactory.fromResource(userIcon))
                .snippet("500 N Lake Shore Dr Chicago 60611\n" + "Phn.: (312) 842-7443\n" + "Email ID: lakeshoredrive@gmail.com"));
        LatLng lastLatLng7 = new LatLng(41.835565, -87.644525);
        //map.animateCamera(CameraUpdateFactory.newLatLng(lastLatLng7), 3000, null);

        map.addCircle(new CircleOptions()
                .center(userMarker.getPosition())
                .radius(200)
                .strokeWidth(0f)
                .fillColor(0x5590B6FD));


        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file info_window_layout
                View v = getActivity().getLayoutInflater().inflate(R.layout.search_online_custom_infowindow, null);

                // Getting the position from the marker
                LatLng latLng = userMarker.getPosition();

                // Getting reference to the TextView to set latitude
                TextView tv_name_desc = (TextView) v.findViewById(R.id.tv_name_desc);

                tv_name_desc.setTextSize(18);
                tv_name_desc.setTypeface(null, Typeface.BOLD);
                tv_name_desc.setText("Name:" + arg0.getTitle() + "\n" + "Address & Contact Details:" + "\n" + arg0.getSnippet());
                tv_name_desc.setShadowLayer(5, 5, 5, Color.WHITE);

                // Returning the view containing InfoWindow contents
                return v;

            }
        });
    }

}
