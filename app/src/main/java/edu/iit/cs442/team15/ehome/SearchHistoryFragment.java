package edu.iit.cs442.team15.ehome;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;
import edu.iit.cs442.team15.ehome.util.ApartmentSearchFilter;
import edu.iit.cs442.team15.ehome.util.SavedLogin;

public class SearchHistoryFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private List<ApartmentSearchFilter> searchHistory;
    private SearchFilterAdapter adapter;
    private TextView noSearchHistory;

    public SearchHistoryFragment() {
        // Required empty public constructor
    }

    public static SearchHistoryFragment newInstance() {
        return new SearchHistoryFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_history, container, false);

        searchHistory = ApartmentDatabaseHelper.getInstance().getSearchHistory(SavedLogin.getInstance().getId());
        adapter = new SearchFilterAdapter(getActivity(), searchHistory);

        ListView lvSearchHistory = (ListView) v.findViewById(R.id.lvSearchHistory);
        lvSearchHistory.setAdapter(adapter);
        lvSearchHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onSearchHistorySelected(searchHistory.get(position).id);
            }
        });

        noSearchHistory = (TextView) v.findViewById(R.id.noSearchHistory);
        noSearchHistory.setVisibility(searchHistory.isEmpty() ? View.VISIBLE : View.GONE);
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_search_history, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuClearHistory:
                ApartmentDatabaseHelper.getInstance().clearSearchHistory(SavedLogin.getInstance().getId());
                adapter.clear();
                adapter.notifyDataSetChanged();
                noSearchHistory.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public interface OnFragmentInteractionListener {
        void onSearchHistorySelected(int searchHistoryId);
    }

    private class SearchFilterAdapter extends ArrayAdapter<ApartmentSearchFilter> {
        public SearchFilterAdapter(Context context, List<ApartmentSearchFilter> searchFilters) {
            super(context, 0, searchFilters);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ApartmentSearchFilter filter = getItem(position);
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getActivity().getLayoutInflater().inflate(R.layout.item_search_filter, parent, false);

                holder.type = (TextView) convertView.findViewById(R.id.historyType);
                holder.cost = (TextView) convertView.findViewById(R.id.historyCost);
                holder.beds = (TextView) convertView.findViewById(R.id.historyBeds);
                holder.bathrooms = (TextView) convertView.findViewById(R.id.historyBathrooms);
                holder.area = (TextView) convertView.findViewById(R.id.historyArea);
                holder.options = (TextView) convertView.findViewById(R.id.historyOptions);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // TODO support web search?
            holder.type.setText(R.string.nav_ezhome_search);

            String temp = getRangeString(filter.minCost, filter.maxCost);
            holder.cost.setText(temp == null ? "_" : getString(R.string.history_price, temp));

            temp = getRangeString(filter.minBeds, filter.maxBeds);
            holder.beds.setText(temp == null ? "_" : getResources().getQuantityString(R.plurals.history_beds, filter.maxBeds == null ? 2 : filter.maxBeds, temp));

            temp = getRangeString(filter.minBathrooms, filter.maxBathrooms);
            holder.bathrooms.setText(temp == null ? "_" : getResources().getQuantityString(R.plurals.history_barthrooms, filter.maxBathrooms == null ? 2 : filter.maxBathrooms, temp));

            temp = getRangeString(filter.minArea, filter.maxArea);
            holder.area.setText(temp == null ? "_" : getString(R.string.history_area, temp));

            int numOptions = 0;
            if (filter.hasParking != null && filter.hasParking)
                numOptions++;
            if (filter.hasGym != null && filter.hasGym)
                numOptions++;

            if (numOptions > 0)
                holder.options.setText(getResources().getQuantityString(R.plurals.history_options, numOptions, numOptions));

            return convertView;
        }

        @Nullable
        private String getRangeString(Integer n1, Integer n2) {
            if (n1 != null && n2 != null) {
                if (n1.equals(n2))
                    return getString(R.string.history_range_equal, n2); // 1 or +
                else
                    return getString(R.string.history_range_min_max, n1, n2); // +
            }
            if (n1 != null)
                return getString(R.string.history_range_min, n1); // +
            if (n2 != null) {
                if (n2 == 1)
                    return getString(R.string.history_range_equal, n2); // 1
                else
                    return getString(R.string.history_range_max, n2); // +
            }

            return null;
        }
    }

    private static class ViewHolder {
        TextView type;
        TextView cost;
        TextView beds;
        TextView bathrooms;
        TextView area;
        TextView options;
    }

}
