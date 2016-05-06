package edu.iit.cs442.team15.ehome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.iit.cs442.team15.ehome.model.Apartment;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;
import edu.iit.cs442.team15.ehome.util.ApartmentSearchFilter;
import edu.iit.cs442.team15.ehome.util.Chicago;
import edu.iit.cs442.team15.ehome.util.SavedLogin;

public class EzHomeSearchFragment extends Fragment {

    private static final String ARG_SEARCH_HISTORY_ID = "search_history_id";
    private static final int SEARCH_OPTIONS_REQUEST = 1;

    private List<Apartment> result = new ArrayList<>();
    private ListView lv_ehome_search;
    private Geocoder geocoder;

    private int index_sort_rent = 0;
    private int index_sort_area = 0;
    private TextView tv_rent_title;
    private TextView tv_area_title;
    private MyAdapter adapter;
    private View v;

    private TextView noSearchResults;

    public EzHomeSearchFragment() {

    }

    public static EzHomeSearchFragment newInstance() {
        return new EzHomeSearchFragment();
    }

    public static EzHomeSearchFragment newInstance(int searchHistoryId) {
        EzHomeSearchFragment fragment = new EzHomeSearchFragment();
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

        ApartmentSearchFilter filter = null;

        if (getArguments() != null)
            filter = ApartmentDatabaseHelper.getInstance().getSearchFilter(getArguments().getInt(ARG_SEARCH_HISTORY_ID));

        // try to get user's last search setting
        if (filter == null) {
            filter = ApartmentDatabaseHelper.getInstance().getLastSearchFilter(SavedLogin.getInstance().getId(), true);

            if (filter == null)
                filter = new ApartmentSearchFilter();
        }

        result = ApartmentDatabaseHelper.getInstance().getApartments(filter);
        filterApartmentsByLocation(filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView(inflater, container);
        if (adapter == null)
            adapter = new MyAdapter();
        lv_ehome_search.setAdapter(adapter);

        tv_rent_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index_sort_rent == 0) {
                    Sort_by_rent1();
                    index_sort_rent = 1;
                } else {
                    Sort_by_rent();
                    index_sort_rent = 0;
                }

                adapter.notifyDataSetChanged();

            }
        });

        tv_area_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index_sort_area == 0) {
                    Sort_by_area1();
                    index_sort_area = 1;
                } else {
                    Sort_by_area();
                    index_sort_area = 0;
                }
                adapter.notifyDataSetChanged();
            }
        });


        lv_ehome_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailsIntent = new Intent(getActivity(), EzHomeSearchDetailsActivity.class);
                detailsIntent.putExtra(EzHomeSearchDetailsActivity.EXTRA_APARTMENT_ID, result.get(position).id);
                startActivity(detailsIntent);
            }
        });

        return v;
    }

    private void initView(LayoutInflater inflater, ViewGroup container) {
        v = inflater.inflate(R.layout.fragment_ezhome_search, container, false);
        lv_ehome_search = (ListView) v.findViewById(R.id.lv_ehomesearch);
        tv_rent_title = (TextView) v.findViewById(R.id.tv_rent_title);
        tv_area_title = (TextView) v.findViewById(R.id.tv_area_title);
        tv_rent_title.setClickable(true);
        tv_area_title.setClickable(true);
        tv_rent_title.setFocusable(true);
        tv_area_title.setFocusable(true);
        noSearchResults = (TextView) v.findViewById(R.id.noSearchResults);
        noSearchResults.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
    }

    public void Sort_by_rent1() {
        Collections.sort(result, new Comparator<Apartment>() {
            public int compare(Apartment arg0, Apartment arg1) {
                if (arg0.getTotalCost() > arg1.getTotalCost())
                    return 1;
                else if (arg0.getTotalCost() == arg1.getTotalCost())
                    return 0;
                else
                    return -1;
            }
        });
    }

    void Sort_by_area1() {
        Collections.sort(result, new Comparator<Apartment>() {
            public int compare(Apartment arg0, Apartment arg1) {
                if (arg0.squareFeet > arg1.squareFeet)
                    return 1;
                else if (arg0.squareFeet == arg1.squareFeet)
                    return 0;
                else
                    return -1;
            }
        });

    }

    void Sort_by_rent() {
        Collections.sort(result, new Comparator<Apartment>() {
            public int compare(Apartment arg0, Apartment arg1) {
                if (arg0.getTotalCost() > arg1.getTotalCost())
                    return -1;
                else if (arg0.getTotalCost() == arg1.getTotalCost())
                    return 0;
                else
                    return 1;
            }
        });

    }

    void Sort_by_area() {
        Collections.sort(result, new Comparator<Apartment>() {
            public int compare(Apartment arg0, Apartment arg1) {
                if (arg0.squareFeet > arg1.squareFeet)
                    return -1;
                else if (arg0.squareFeet == arg1.squareFeet)
                    return 0;
                else
                    return 1;
            }

        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_ezhome_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSearchOptions:
                Intent searchOptions = new Intent(getActivity(), EzHomeSearchOptionsActivity.class);
                startActivityForResult(searchOptions, SEARCH_OPTIONS_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SEARCH_OPTIONS_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    ApartmentSearchFilter filter = (ApartmentSearchFilter) data.getSerializableExtra("filter");

                    result = ApartmentDatabaseHelper.getInstance().getApartments(filter);
                    filterApartmentsByLocation(filter);
                    ApartmentDatabaseHelper.getInstance().addSearchHistory(SavedLogin.getInstance().getId(), filter);

                    if (result != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // NOTE: may need to copy changes to this class to DashboardFragment.FavoritesAdapter
    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return result.size();
        }

        @Override
        public Object getItem(int position) {
            return result.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_list_ezhome_search, null);
                holder = new ViewHolder();
                holder.area = (TextView) convertView.findViewById(R.id.tv_area);
                holder.address = (TextView) convertView.findViewById(R.id.tv_address);
                holder.rent = (TextView) convertView.findViewById(R.id.tv_rent);
                holder.owner = (TextView) convertView.findViewById(R.id.tv_owner);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.area.setText(getString(R.string.format_area, result.get(position).squareFeet));
            holder.address.setText(result.get(position).address);
            holder.rent.setText(getString(R.string.format_rent, result.get(position).getTotalCost()));
            holder.owner.setText(result.get(position).owner.complexName);
            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            noSearchResults.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private static final class ViewHolder {
        public TextView address;
        public TextView rent;
        public TextView area;
        public TextView owner;
    }

    private void filterApartmentsByLocation(ApartmentSearchFilter filter) {
        if (filter.location == null || filter.distance == null)
            return; // location filter not set by user

        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(filter.location, 1, Chicago.LOWER_LEFT_LAT, Chicago.LOWER_LEFT_LONG, Chicago.UPPER_RIGHT_LAT, Chicago.UPPER_RIGHT_LONG);
        } catch (IOException e) {
            return;
        }

        if (!addresses.isEmpty()) {
            Location filterLoc = new Location("");
            filterLoc.setLatitude(addresses.get(0).getLatitude());
            filterLoc.setLongitude(addresses.get(0).getLongitude());

            for (int i = 0; i < result.size(); i++) {
                try {
                    addresses = geocoder.getFromLocationName(result.get(i).address, 1, Chicago.LOWER_LEFT_LAT, Chicago.LOWER_LEFT_LONG, Chicago.UPPER_RIGHT_LAT, Chicago.UPPER_RIGHT_LONG);
                } catch (IOException e) {
                    return; // TODO figure out exactly what to do here
                }

                if (addresses.isEmpty()) {
                    result.remove(i--); // address not found
                } else {
                    Location apartLoc = new Location("");
                    apartLoc.setLatitude(addresses.get(0).getLatitude());
                    apartLoc.setLongitude(addresses.get(0).getLongitude());

                    // remove item if it is out of bounds
                    if (filterLoc.distanceTo(apartLoc) * Chicago.METERS_TO_MILES > filter.distance)
                        result.remove(i--); // decrement i because item is removed
                }
            } // end for
        }
    }

}
