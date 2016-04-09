package edu.iit.cs442.team15.ehome;


import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import android.content.*;

import java.util.ArrayList;
import java.util.Collections;

import edu.iit.cs442.team15.ehome.model.Apartment;

public class SearchResultActivity extends Activity {

    private ArrayList<Apartment> result = new ArrayList<>();
    private TableLayout table;
    private Spinner orderSpinner;
    private ArrayAdapter<CharSequence> orderadapter;
    private Button back;
    private int order_method = -1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        result = (ArrayList<Apartment>) getIntent().getSerializableExtra("Searching Result");

        table = (TableLayout)findViewById(R.id.table);
        addData();

        orderSpinner = (Spinner) findViewById(R.id.orderSpinner);
        orderadapter = ArrayAdapter.createFromResource(this, R.array.order_method, android.R.layout.simple_spinner_item);
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

    public void addData(){
        for(int i=0 ; i<result.size(); i++){
            Apartment apt = result.get(i);
            TableRow tr = new TableRow(this);
            tr.setId(i);
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
    }

    public void addTextView(TableRow tr,String text){
        TextView tv = new TextView(this);

        tv.setText(text);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv.setLines(2);
        tv.setSingleLine(false);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tr.addView(tv);
    }

    public void updateTable(){
        for(int i=0 ; i<result.size(); i++){
            View view = table.getChildAt(i+2);
            if(view instanceof TableRow){
                TableRow tr = (TableRow) view;
                TextView tv0 = (TextView)tr.getChildAt(0);
                tv0.setText(result.get(i).id);
                TextView tv1 = (TextView)tr.getChildAt(1);
                tv1.setText(result.get(i).zipcode);
                TextView tv2 = (TextView)tr.getChildAt(2);
                tv2.setText(result.get(i).address);
                TextView tv3 = (TextView)tr.getChildAt(3);
                tv3.setText(result.get(i).bedrooms);
                TextView tv4 = (TextView)tr.getChildAt(4);
                tv4.setText(result.get(i).bathrooms);
                TextView tv5 = (TextView)tr.getChildAt(5);
                tv5.setText(Double.toString(result.get(i).square_feet));
                TextView tv6 = (TextView)tr.getChildAt(6);
                tv6.setText(result.get(i).rent);
            }

        }

    }
}
