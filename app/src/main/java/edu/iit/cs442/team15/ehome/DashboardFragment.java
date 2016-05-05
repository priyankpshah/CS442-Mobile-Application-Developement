package edu.iit.cs442.team15.ehome;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import edu.iit.cs442.team15.ehome.model.Apartment;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;
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
    private FavoritesAdapter adapter;

    private static final int NUM_ITEMS = 5;
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    public static final String[] IMAGE_NAME = {"apartment_one", "apartment_two", "apartment_three", "apartment_four", "apartment_five", "apartment_six"};

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

        if (adapter == null)
            adapter = new FavoritesAdapter();

        ListView favoritesListView = (ListView) v.findViewById(R.id.favoritesListView);
        favoritesListView.setAdapter(adapter);
        favoritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onApartmentClicked(favorites.get(position).id); // open search details page
            }
        });

        // Recommendations
        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getChildFragmentManager());
        viewPager = (ViewPager) v.findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        favorites = ApartmentDatabaseHelper.getInstance().getFavorites(SavedLogin.getInstance().getId());
        adapter.notifyDataSetChanged();
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
            holder.area.setText(Double.toString(favorites.get(position).squareFeet));
            holder.address.setText(favorites.get(position).address);
            holder.rent.setText(Double.toString(favorites.get(position).getTotalCost()));
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
        public TextView area;
        public TextView owner;
    }

    public static class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            return SwipeFragment.newInstance(position);
        }
    }

    public static class SwipeFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View swipeView = inflater.inflate(R.layout.swap_fragment, container, false);
            ImageView imageView = (ImageView) swipeView.findViewById(R.id.imageView);
            Bundle bundle = getArguments();
            int position = bundle.getInt("position");
            String imageFileName = IMAGE_NAME[position];
            int imgResId = getResources().getIdentifier(imageFileName, "drawable", "edu.iit.cs442.team15.ehome");
            imageView.setImageResource(imgResId);
            return swipeView;
        }

        public static SwipeFragment newInstance(int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position + 1);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }
    }

}
