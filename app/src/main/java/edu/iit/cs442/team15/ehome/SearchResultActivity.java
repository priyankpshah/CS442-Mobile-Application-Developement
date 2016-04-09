package edu.iit.cs442.team15.ehome;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.content.*;

import java.util.ArrayList;
import java.util.Collections;

import edu.iit.cs442.team15.ehome.model.Apartment;

public class SearchResultActivity extends Activity {

    private ArrayList<Apartment> result = new ArrayList<>();
    private Spinner orderSpinner;
    private ArrayAdapter<CharSequence> orderadapter;
    private Button back;
    private int order_method = -1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        result = (ArrayList<Apartment>) getIntent().getSerializableExtra("Searching Result");

        TableLayout table = (TableLayout)findViewById(R.id.table);
        for(Apartment apt : result){
            TableRow tr = new TableRow(this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            addTextView(tr, Integer.toString(apt.id));
            addTextView(tr,Integer.toString(apt.zipcode));
            addTextView(tr,apt.address);
            addTextView(tr,Integer.toString(apt.bedrooms));
            addTextView(tr,Integer.toString(apt.bathrooms));
            addTextView(tr,Double.toString(apt.square_feet));
            addTextView(tr,Integer.toString(apt.rent));
            table.addView(tr);
        }

        orderSpinner = (Spinner) findViewById(R.id.orderSpinner);
        orderadapter = ArrayAdapter.createFromResource(this, R.array.order_method, android.R.layout.simple_spinner_item);
        orderadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderSpinner.setAdapter(orderadapter);

        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                order_method = orderSpinner.getSelectedItemPosition();
                sort();
                orderadapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 finish();
            }
        });
    }

    public void sort(){
        switch(order_method){
            case 0:
                Collections.sort(result,Apartment.rentComparator);
                break;
            case 1:
                Collections.sort(result,Apartment.areaComparator);
                break;
        }
    }

    public void addTextView(TableRow tr,String text){
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr.addView(tv);
    }
}
