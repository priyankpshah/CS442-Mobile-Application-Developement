package edu.iit.cs442.team15.ehome;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import edu.iit.cs442.team15.ehome.model.WebApartment;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;
import edu.iit.cs442.team15.ehome.util.ApartmentSearchFilter;

public class SearchOnlineFragment extends Fragment implements OnMapReadyCallback {

    private static final int SEARCH_OPTIONS_REQUEST_ONLINE = 1;

    private GoogleMap mMap;
    private int userIcon;

    private List<WebApartment> apartments;

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
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_online, container, false);

        // TODO use user search options
        apartments = ApartmentDatabaseHelper.getInstance().getWebApartments(new ApartmentSearchFilter().setEzhomeSearch(false));

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
        this.mMap = map; // save for permission callback

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

            return;
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);

        LocationManager locMan = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location lastLoc = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        Marker userMarker;

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
            // get Chicago's location as fallback
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
                    .snippet(""));
        }

        // zoom in on user location or Chicago
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(), 12.5f));

        for (WebApartment apartment : apartments) {
            Marker locMarker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(apartment.latitude, apartment.longitude))
                    .title(apartment.name)
                    .icon(BitmapDescriptorFactory.fromResource(userIcon))
                    .snippet(apartment.getSnippet()));
        }

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
                //LatLng latLng = userMarker.getPosition();

                // Getting reference to the TextView to set latitude
                TextView tv_name_desc = (TextView) v.findViewById(R.id.tv_name_desc);

                tv_name_desc.setTextSize(18);
                tv_name_desc.setTypeface(null, Typeface.BOLD);
                tv_name_desc.setText(arg0.getTitle() + "\n" + arg0.getSnippet());
                tv_name_desc.setShadowLayer(4, 0, 0, Color.BLACK);

                // Returning the view containing InfoWindow contents
                return v;

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            onMapReady(mMap);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_ezhome_search_online, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSearchOptionsOnline:
                Intent searchOptions = new Intent(getActivity(), SearchOnlineOptionsActivity.class);
                startActivityForResult(searchOptions, SEARCH_OPTIONS_REQUEST_ONLINE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
