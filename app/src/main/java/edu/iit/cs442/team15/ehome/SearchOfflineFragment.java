package edu.iit.cs442.team15.ehome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;

public class SearchOfflineFragment extends Fragment {

    List<String> Aptname = null;
    ListView lv = null;

    public SearchOfflineFragment() {

    }

    public static SearchOfflineFragment newInstance() {
        return new SearchOfflineFragment();
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
        View v = inflater.inflate(R.layout.fragment_search_offline, container, false);
        lv = (ListView) v.findViewById(R.id.offline_list);
        Aptname = ApartmentDatabaseHelper.getInstance().getAptNames();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, Aptname) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                String entry = Aptname.get(position);
                TextView t1 = (TextView) view.findViewById(android.R.id.text1);
                t1.setText(entry);
                return view;
            }

        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), SearchOfflineDetailsActivity.class);
                int pos = position;
                i.putExtra("Position", String.valueOf(pos + 1));
                startActivity(i);

            }
        });
        return v;
    }

}
