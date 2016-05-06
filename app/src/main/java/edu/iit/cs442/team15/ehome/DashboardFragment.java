package edu.iit.cs442.team15.ehome;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import edu.iit.cs442.team15.ehome.model.Apartment;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;
import edu.iit.cs442.team15.ehome.util.ImageAdapter;
import edu.iit.cs442.team15.ehome.util.SavedLogin;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DashboardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private List<Apartment> favorites;
    private TextView noFavorites;
    private FavoritesAdapter favoritesAdapter;

    ViewPager recommendations;
    ImageAdapter recommendationsAdapter;

    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DashboardFragment.
     */
    public static DashboardFragment newInstance() {
        return new DashboardFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Favorites
        favorites = ApartmentDatabaseHelper.getInstance().getFavorites(SavedLogin.getInstance().getId());

        noFavorites = (TextView) v.findViewById(R.id.noFavorites);
        noFavorites.setVisibility(favorites.isEmpty() ? View.VISIBLE : View.GONE);

        if (favoritesAdapter == null)
            favoritesAdapter = new FavoritesAdapter();

        ListView favoritesListView = (ListView) v.findViewById(R.id.favoritesListView);
        favoritesListView.setAdapter(favoritesAdapter);
        favoritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onApartmentClicked(favorites.get(position).id); // open search details page
            }
        });

        // Recommendations
        int[] imageIds = {
                R.drawable.apartment_one, R.drawable.apartment_two, R.drawable.apartment_three,
                R.drawable.apartment_four, R.drawable.apartment_five, R.drawable.apartment_six
        };

        final int[] apartmentIds = {8, 7, 11, 10, 5, 6};

        recommendationsAdapter = new ImageAdapter(getActivity(), imageIds, new ImageAdapter.OnImageClickedListener() {
            @Override
            public void onImageClicked(int position) {
                mListener.onApartmentClicked(apartmentIds[position]);
            }
        });
        recommendations = (ViewPager) v.findViewById(R.id.pager);
        recommendations.setAdapter(recommendationsAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        favorites = ApartmentDatabaseHelper.getInstance().getFavorites(SavedLogin.getInstance().getId());
        favoritesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onApartmentClicked(int apartmentId);
    }

    // copied from EzHomeSearchFragment
    private class FavoritesAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return favorites.size();
        }

        @Override
        public Object getItem(int position) {
            return favorites.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_list_favorites, null);
                holder = new ViewHolder();
                holder.address = (TextView) convertView.findViewById(R.id.tv_address);
                holder.rent = (TextView) convertView.findViewById(R.id.tv_rent);
                holder.owner = (TextView) convertView.findViewById(R.id.tv_owner);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.address.setText(favorites.get(position).address);
            holder.rent.setText(getString(R.string.format_rent, favorites.get(position).getTotalCost()));
            holder.owner.setText(favorites.get(position).owner.complexName);
            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            noFavorites.setVisibility(favorites.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private static final class ViewHolder {
        public TextView address;
        public TextView rent;
        public TextView owner;
    }

}
