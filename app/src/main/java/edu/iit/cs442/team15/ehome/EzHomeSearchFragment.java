package edu.iit.cs442.team15.ehome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.iit.cs442.team15.ehome.model.Apartment;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;
import edu.iit.cs442.team15.ehome.util.ApartmentSearchFilter;
import edu.iit.cs442.team15.ehome.util.SavedLogin;

public class EzHomeSearchFragment extends Fragment {

    private static final int SEARCH_OPTIONS_REQUEST = 1;

    private List<Apartment> result = new ArrayList<>();
    private ListView lv_ehome_search;
    private Button temp_search;

    private int index_sort_rent=0;
    private int index_sort_area=0;
    private TextView tv_rent_title;
    private TextView tv_area_title;
    private MyAdapter adapter;
    private View v;
    public EzHomeSearchFragment() {

    }

    public static EzHomeSearchFragment newInstance() {
        return new EzHomeSearchFragment();
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

        initView(inflater,container);

        // TODO show user's last search for search history instead
        // TODO show SearchOptions if user has no search history
        result = ApartmentDatabaseHelper.getInstance().getApartments(new ApartmentSearchFilter());
        if(adapter==null)
            adapter = new MyAdapter();
        lv_ehome_search.setAdapter(adapter);

        temp_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchOptions = new Intent(getActivity(), EzHomeSearchOptionsActivity.class);
                startActivityForResult(searchOptions, 1);
            }
        });

        tv_rent_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index_sort_rent==0)
                {
                    Sort_by_rent1();
                    index_sort_rent=1;
                }else{
                    Sort_by_rent();
                    index_sort_rent=0;
                }

                adapter.notifyDataSetChanged();

            }
        });

        tv_area_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index_sort_area==0)
                {
                    Sort_by_area1();
                    index_sort_area=1;
                }else{
                    Sort_by_area();
                    index_sort_area=0;
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

    private void initView(LayoutInflater inflater,ViewGroup container){
        v = inflater.inflate(R.layout.fragment_ezhome_search, container, false);
        temp_search = (Button) v.findViewById(R.id.tempButtonOptions);
        lv_ehome_search = (ListView)v.findViewById(R.id.lv_ehomesearch);
        tv_rent_title = (TextView)v.findViewById(R.id.tv_rent_title);
        tv_area_title = (TextView)v.findViewById(R.id.tv_area_title);
        tv_rent_title.setClickable(true);
        tv_area_title.setClickable(true);
        tv_rent_title.setFocusable(true);
        tv_area_title.setFocusable(true);
    }

    public void Sort_by_rent1()
    {
        Collections.sort(result, new Comparator<Apartment>() {
            public int compare(Apartment arg0, Apartment arg1) {
                if (arg0.rent > arg1.rent)
                    return 1;
                else if(arg0.rent == arg1.rent)
                    return 0;
                else
                    return -1;
            }
        });
    }
    void Sort_by_area1()
    {
        Collections.sort(result, new Comparator<Apartment>() {
            public int compare(Apartment arg0, Apartment arg1) {
                if (arg0.squareFeet > arg1.squareFeet)
                    return 1;
                else if(arg0.squareFeet == arg1.squareFeet)
                    return 0;
                else
                    return -1;
            }
        });

    }

    void Sort_by_rent()
    {
        Collections.sort(result, new Comparator<Apartment>() {
            public int compare(Apartment arg0, Apartment arg1) {
                if (arg0.rent > arg1.rent)
                    return -1;
                else if(arg0.rent == arg1.rent)
                    return 0;
                else
                    return 1;
            }
        });

    }
    void Sort_by_area()
    {
        Collections.sort(result,new Comparator<Apartment>(){
            public int compare(Apartment arg0, Apartment arg1) {
                if (arg0.squareFeet > arg1.squareFeet)
                    return -1;
                else if(arg0.squareFeet == arg1.squareFeet)
                    return 0;
                else
                    return 1;
            }

        });
    }

    private class MyAdapter extends BaseAdapter{
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
            if(convertView == null){
                convertView = View.inflate(getActivity(), R.layout.item_list_ezhome_search, null);
                holder = new ViewHolder();
                holder.ItemId = (TextView)convertView.findViewById(R.id.tv_id);
                holder.Itemarea = (TextView)convertView.findViewById(R.id.tv_area);
                holder.Itemaddress = (TextView)convertView.findViewById(R.id.tv_address);
                holder.Itemrent = (TextView)convertView.findViewById(R.id.tv_rent);
                holder.Itemowner = (TextView)convertView.findViewById(R.id.tv_owner);
                holder.Itemphone = (TextView)convertView.findViewById(R.id.tv_phone);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            holder.ItemId.setText(Integer.toString(result.get(position).id));
            holder.Itemarea.setText(Double.toString(result.get(position).squareFeet));
            holder.Itemaddress.setText(result.get(position).address);
            holder.Itemrent.setText(Double.toString(result.get(position).getTotalCost()));
            holder.Itemowner.setText(result.get(position).owner.complexName);
            holder.Itemphone.setText(result.get(position).owner.ownerPhone);
            return convertView;
        }
    }

    public final class ViewHolder {
        public TextView ItemId;
        public TextView Itemaddress;
        public TextView Itemrent;
        public TextView Itemarea;
        public TextView Itemowner;
        public TextView Itemphone;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SEARCH_OPTIONS_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    ApartmentSearchFilter filter = (ApartmentSearchFilter) data.getSerializableExtra("filter");

                    result = ApartmentDatabaseHelper.getInstance().getApartments(filter);
                    ApartmentDatabaseHelper.getInstance().addSearchHistory(SavedLogin.getInstance().getId(),filter);

                    if (result != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
