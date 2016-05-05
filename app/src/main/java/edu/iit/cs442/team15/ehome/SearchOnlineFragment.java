package edu.iit.cs442.team15.ehome;

import android.Manifest;
import android.app.Activity;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import edu.iit.cs442.team15.ehome.model.WebApartment;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;
import edu.iit.cs442.team15.ehome.util.ApartmentSearchFilter;
import edu.iit.cs442.team15.ehome.util.Chicago;
import edu.iit.cs442.team15.ehome.util.SavedLogin;

public class SearchOnlineFragment extends Fragment implements OnMapReadyCallback {

    private static final String ARG_SEARCH_HISTORY_ID = "search_history_id";
    private static final int SEARCH_OPTIONS_REQUEST_ONLINE = 1;

    private GoogleMap mMap;
    private Geocoder geocoder;

    private ApartmentSearchFilter filter;
    private Location filterLoc;
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

    public static SearchOnlineFragment newInstance(int searchHistoryId) {
        SearchOnlineFragment fragment = new SearchOnlineFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SEARCH_HISTORY_ID, searchHistoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        geocoder = new Geocoder(context);
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

        if (getArguments() != null)
            filter = ApartmentDatabaseHelper.getInstance().getSearchFilter(getArguments().getInt(ARG_SEARCH_HISTORY_ID));

        if (filter == null) {
            filter = ApartmentDatabaseHelper.getInstance().getLastSearchFilter(SavedLogin.getInstance().getId(), false);

            if (filter == null) // no search history
                filter = new ApartmentSearchFilter().setEzhomeSearch(false);
        }

        apartments = ApartmentDatabaseHelper.getInstance().getWebApartments(filter);
        filterApartmentsByLocation();

        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

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

            userMarker = map.addMarker(new MarkerOptions()
                    .position(lastLatLng)
                    .title("You are here")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.search_online_location_marker))
                    .snippet("Your last recorded location"));

            map.setMyLocationEnabled(true);
        } else {
            // get Chicago's location as fallback
            userMarker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(Chicago.LATITUDE, Chicago.LONGITUDE))
                    .title("Chicago")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.search_online_location_marker))
                    .snippet("You are here"));
        }

        // zoom in on user location or Chicago
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(), 12.5f));

        for (WebApartment apartment : apartments) {
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(apartment.latitude, apartment.longitude))
                    .title(apartment.name)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.search_online_marker_icon))
                    .snippet(apartment.getSnippet()));
        }

        // add filter marker if applicable
        if (filterLoc != null) {
            LatLng position = new LatLng(filterLoc.getLatitude(), filterLoc.getLongitude());

            userMarker = map.addMarker(new MarkerOptions()
                    .position(position)
                    .title(filter.location)
                    .snippet("Search radius"));

            map.addCircle(new CircleOptions()
                    .center(position)
                    .radius(filter.distance * Chicago.MILES_TO_METERS)
                    .strokeWidth(0f)
                    .fillColor(0x5590B6FD));

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(userMarker.getPosition(), 12.5f));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SEARCH_OPTIONS_REQUEST_ONLINE:
                if (resultCode == Activity.RESULT_OK) {
                    filter = (ApartmentSearchFilter) data.getSerializableExtra("filter");

                    ApartmentDatabaseHelper.getInstance().addSearchHistory(SavedLogin.getInstance().getId(), filter);
                    apartments = ApartmentDatabaseHelper.getInstance().getWebApartments(filter);
                    filterLoc = null; // clear

                    filterApartmentsByLocation();

                    mMap.clear(); // remove existing markers
                    onMapReady(mMap);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void filterApartmentsByLocation() {
        if (filter.location == null || filter.distance == null)
            return; // location filter not set by user

        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(filter.location, 1, Chicago.LOWER_LEFT_LAT, Chicago.LOWER_LEFT_LONG, Chicago.UPPER_RIGHT_LAT, Chicago.UPPER_RIGHT_LONG);
        } catch (IOException e) {
            return;
        }

        if (!addresses.isEmpty()) {
            filterLoc = new Location("");
            filterLoc.setLatitude(addresses.get(0).getLatitude());
            filterLoc.setLongitude(addresses.get(0).getLongitude());

            for (int i = 0; i < apartments.size(); i++) {
                Location apartLoc = new Location("");
                apartLoc.setLatitude(apartments.get(i).latitude);
                apartLoc.setLongitude(apartments.get(i).longitude);

                // remove item if it is out of bounds
                if (filterLoc.distanceTo(apartLoc) * Chicago.METERS_TO_MILES > filter.distance)
                    apartments.remove(i--); // decrement i because item is removed
            }
        }
    }

}
