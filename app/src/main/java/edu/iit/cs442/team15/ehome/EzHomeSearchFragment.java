package edu.iit.cs442.team15.ehome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.iit.cs442.team15.ehome.model.Apartment;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;

public class EzHomeSearchFragment extends Fragment {

    private static final int SEARCH_OPTIONS_REQUEST = 1;

    private List<Apartment> result = new ArrayList<>();
    private TableLayout table;
    private Spinner orderSpinner;
    private ArrayAdapter<CharSequence> orderadapter;
    private int order_method = -1;

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
        View v = inflater.inflate(R.layout.fragment_ezhome_search, container, false);
        // TODO default search
        //result = (ArrayList<Apartment>) getIntent().getSerializableExtra("Searching Result");

        Button temp = (Button) v.findViewById(R.id.tempButtonOptions);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchOptions = new Intent(getActivity(), EzHomeSearchOptionsActivity.class);
                startActivityForResult(searchOptions, 1);
            }
        });

        table = (TableLayout) v.findViewById(R.id.table);
        addData();

        orderSpinner = (Spinner) v.findViewById(R.id.orderSpinner);
        orderadapter = ArrayAdapter.createFromResource(getActivity(), R.array.order_method, android.R.layout.simple_spinner_item);
        orderadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(orderadapter);

        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                order_method = orderSpinner.getSelectedItemPosition();
                sort();
                updateTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SEARCH_OPTIONS_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    result = ApartmentDatabaseHelper.getInstance().getApartments(
                            data.getIntExtra("zip", 0),
                            data.getIntExtra("beds", 0),
                            data.getIntExtra("baths", 0),
                            data.getIntExtra("min_rent", 0),
                            data.getIntExtra("max_rent", Integer.MAX_VALUE));
                    if (result != null) {
                        addData();
                        updateTable();
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void sort() {
        switch (order_method) {
            case 0:
                Collections.sort(result, Apartment.rentComparator);
                break;
            case 1:
                Collections.sort(result, Apartment.areaComparator);
                break;
        }
    }

    public void addData() {
        for (int i = 0; i < result.size(); i++) {
            Apartment apt = result.get(i);
            TableRow tr = new TableRow(getActivity());
            tr.setId(i);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            addTextView(tr, Integer.toString(apt.id));
            addTextView(tr, Integer.toString(apt.zip));
            addTextView(tr, apt.address);
            addTextView(tr, Integer.toString(apt.bedrooms));
            addTextView(tr, Integer.toString(apt.bathrooms));
            addTextView(tr, Double.toString(apt.squareFeet));
            addTextView(tr, Double.toString(apt.rent));
            table.addView(tr);
        }
    }

    public void addTextView(TableRow tr, String text) {
        TextView tv = new TextView(getActivity());

        tv.setText(text);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv.setLines(2);
        tv.setSingleLine(false);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tr.addView(tv);
    }

    public void updateTable() {
        for (int i = 0; i < result.size(); i++) {
            View view = table.getChildAt(i + 2);
            if (view instanceof TableRow) {
                TableRow tr = (TableRow) view;
                TextView tv0 = (TextView) tr.getChildAt(0);
                tv0.setText(Integer.toString(result.get(i).id));
                TextView tv1 = (TextView) tr.getChildAt(1);
                tv1.setText(Integer.toString(result.get(i).zip));
                TextView tv2 = (TextView) tr.getChildAt(2);
                tv2.setText(result.get(i).address);
                TextView tv3 = (TextView) tr.getChildAt(3);
                tv3.setText(Integer.toString(result.get(i).bedrooms));
                TextView tv4 = (TextView) tr.getChildAt(4);
                tv4.setText(Integer.toString(result.get(i).bathrooms));
                TextView tv5 = (TextView) tr.getChildAt(5);
                tv5.setText(Double.toString(result.get(i).squareFeet));
                TextView tv6 = (TextView) tr.getChildAt(6);
                tv6.setText(Double.toString(result.get(i).rent));
            }
        }

    }
}
