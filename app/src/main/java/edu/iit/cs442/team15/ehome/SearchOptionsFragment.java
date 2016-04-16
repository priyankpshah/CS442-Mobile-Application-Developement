package edu.iit.cs442.team15.ehome;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import edu.iit.cs442.team15.ehome.model.Apartment;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper.Apartments;

public class SearchOptionsFragment extends Fragment implements OnClickListener {

    private int zipcode;
    private int beds = 0;
    private int baths = 0;
    private int min_rent = 0;
    private int max_rent = Integer.MAX_VALUE;

    ArrayList<Apartment> search_result = new ArrayList<>();

    private Spinner bedspinner;
    private Spinner bathspinner;
    private EditText zip;
    private EditText minRent;
    private EditText maxRent;
    private Button apply;
    private Button save;

    public SearchOptionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchOptionsFragment.
     */
    public static SearchOptionsFragment newInstance() {
        return new SearchOptionsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_options, container, false);

        bedspinner = (Spinner) v.findViewById(R.id.bedSpinner);
        ArrayAdapter<CharSequence> bedsadapter = ArrayAdapter.createFromResource(getActivity(), R.array.select_beds, android.R.layout.simple_spinner_item);
        bedsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bedspinner.setAdapter(bedsadapter);

        bedspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String beds_selected = bedspinner.getSelectedItem().toString();
                try {
                    beds = Integer.parseInt(beds_selected.substring(0, 1));
                } catch (Exception e) {
                    beds = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        bathspinner = (Spinner) v.findViewById(R.id.bathSpinner);
        ArrayAdapter<CharSequence> bathsadapter = ArrayAdapter.createFromResource(getActivity(), R.array.select_baths, android.R.layout.simple_spinner_item);
        bathsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bathspinner.setAdapter(bathsadapter);

        bathspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String baths_selected = bathspinner.getSelectedItem().toString();
                try {
                    baths = Integer.parseInt(baths_selected.substring(0, 1));
                } catch (Exception e) {
                    baths = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        apply = (Button) v.findViewById(R.id.Apply);
        apply.setOnClickListener(this);
        save = (Button) v.findViewById(R.id.SaveFilter);
        apply.setOnClickListener(this);

        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Apply:
                zip = (EditText) getView().findViewById(R.id.zipCode);
                minRent = (EditText) getView().findViewById(R.id.min_rent);
                maxRent = (EditText) getView().findViewById(R.id.max_rent);
                String zipcode_s = zip.getText().toString();
                String min_rent_s = minRent.getText().toString();
                String max_rent_s = maxRent.getText().toString();
                try {
                    if (zipcode_s.length() == 0) {
                        zipcode = 0;
                    } else zipcode = Integer.parseInt(zipcode_s);

                    if (min_rent_s.length() == 0) {
                        min_rent = 0;
                    } else min_rent = Integer.parseInt(min_rent_s);

                    if (max_rent_s.length() == 0) {
                        max_rent = Integer.MAX_VALUE;
                    } else max_rent = Integer.parseInt(max_rent_s);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Zip Code and Rent must be Integer", Toast.LENGTH_LONG).show();
                }
                if (min_rent > max_rent) {
                    Toast.makeText(getActivity(), "Maximum rent must be greater than minimum rent!", Toast.LENGTH_LONG).show();
                    break;
                }
                Cursor result = ApartmentDatabaseHelper.getInstance().getApartments(zipcode, beds, baths, min_rent, max_rent);
                if (result.getCount() == 0)
                    Toast.makeText(getActivity(), "No apartments found!", Toast.LENGTH_LONG).show();
                else {
                    if (result.moveToFirst()) {
                        do {
                            search_result.add(new Apartment(
                                    result.getInt(result.getColumnIndex(Apartments.KEY_ID)),
                                    result.getString(result.getColumnIndex(Apartments.KEY_ADDRESS)),
                                    result.getInt(result.getColumnIndex(Apartments.KEY_ZIP)),
                                    result.getInt(result.getColumnIndex(Apartments.KEY_BEDROOMS)),
                                    result.getInt(result.getColumnIndex(Apartments.KEY_BATHROOMS)),
                                    result.getDouble(result.getColumnIndex(Apartments.KEY_AREA)),
                                    result.getInt(result.getColumnIndex(Apartments.KEY_RENT)),
                                    result.getInt(result.getColumnIndex(Apartments.KEY_OWNER_ID))));
                        } while (result.moveToNext());
                    }
                    Intent intent = new Intent(getActivity(), SearchResultActivity.class);
                    intent.putExtra("Searching Result", search_result);
                    startActivityForResult(intent, 1);
                }
                break;

            case R.id.SaveFilter:
                //TODO Save current filter in user's profile
                break;

            default:
                break;
        }
    }
}

