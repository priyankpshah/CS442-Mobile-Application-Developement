package edu.iit.cs442.team15.ehome.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper.Amenities;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper.Apartments;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper.Owners;

/**
 * Provides a generic way to generate an SQL query for an apartment search
 * that will ignore unset fields
 */
public class ApartmentSearchFilter {

    public static final String TOTAL_COST = "ezprice";

    private String id;
    private String minCost, maxCost;
    //private String distance, location; // too hard to implement in SQL
    private String hasGym, hasParking;
    private String minBedrooms, maxBedrooms;
    private String minBathrooms, maxBathrooms;
    private String minArea, maxArea;

    public ApartmentSearchFilter setId(int id) {
        this.id = Integer.toString(id);
        return this;
    }

    public ApartmentSearchFilter setMinCost(double minCost) {
        this.minCost = Double.toString(minCost);
        return this;
    }

    public ApartmentSearchFilter setMaxCost(double maxCost) {
        this.maxCost = Double.toString(maxCost);
        return this;
    }

    /*public ApartmentSearchFilter setDistance(String distance) {
        this.distance = distance;
        return this;
    }

    public ApartmentSearchFilter setLocation(String location) {
        this.location = location;
        return this;
    }*/

    public ApartmentSearchFilter setHasGym(boolean hasGym) {
        this.hasGym = Boolean.toString(hasGym);
        return this;
    }

    public ApartmentSearchFilter setHasParking(boolean hasParking) {
        this.hasParking = Boolean.toString(hasParking);
        return this;
    }

    public ApartmentSearchFilter setMinBedrooms(int minBedrooms) {
        this.minBedrooms = Integer.toString(minBedrooms);
        return this;
    }

    public ApartmentSearchFilter setMaxBedrooms(int maxBedrooms) {
        this.maxBedrooms = Integer.toString(maxBedrooms);
        return this;
    }

    public ApartmentSearchFilter setMinBathrooms(int minBathrooms) {
        this.minBathrooms = Integer.toString(minBathrooms);
        return this;
    }

    public ApartmentSearchFilter setMaxBathrooms(int maxBathrooms) {
        this.maxBathrooms = Integer.toString(maxBathrooms);
        return this;
    }

    public ApartmentSearchFilter setMinArea(double minArea) {
        this.minArea = Double.toString(minArea);
        return this;
    }

    public ApartmentSearchFilter setMaxArea(double maxArea) {
        this.maxArea = Double.toString(maxArea);
        return this;
    }

    public Cursor query(SQLiteDatabase db) {
        // join apartments, owners, and amenities tables together
        final String table = Apartments.TABLE +
                " JOIN " + Owners.TABLE + " ON " + Apartments.TABLE + "." + Apartments.OWNER_ID + "=" + Owners.TABLE + "." + Owners.ID +
                " JOIN " + Amenities.TABLE + " ON " + Apartments.TABLE + "." + Apartments.ID + "=" + Amenities.TABLE + "." + Amenities.ID;

        final String[] columns = {
                "*",
                "(" + Apartments.RENT + " + " + Amenities.GAS + " + " + Amenities.ELECTRICITY + " + " + Amenities.INTERNET + " + " + Amenities.CABLE + " + " + Amenities.THERMOSTAT + ") AS " + TOTAL_COST
        };

        // build selection query and args
        final StringBuilder selection = new StringBuilder();
        final List<String> selectionArgs = new ArrayList<>();

        for (Filter filter : getFilters()) {
            if (filter.var != null) {
                selection.append(filter.where).append(" AND ");
                selectionArgs.add(filter.var);
            }
        }
        selection.append("1=1"); // prevent trailing AND

        return db.query(table, columns, selection.toString(), selectionArgs.toArray(new String[selectionArgs.size()]), null, null, null, "100");
    }

    private Filter[] getFilters() {
        return new Filter[]{
                new Filter(id, Apartments.TABLE + "." + Apartments.ID + "=?"),
                new Filter(minCost, TOTAL_COST + ">=CAST(? AS NUMERIC)"),
                new Filter(maxCost, TOTAL_COST + "<=CAST(? AS NUMERIC)"),
                new Filter(hasGym, Amenities.GYM + "=?"),
                new Filter(hasParking, Amenities.PARKING + "=?"),
                new Filter(minBedrooms, Apartments.BEDROOMS + ">=?"),
                new Filter(maxBedrooms, Apartments.BEDROOMS + "<=?"),
                new Filter(minBathrooms, Apartments.BATHROOMS + ">=?"),
                new Filter(maxBathrooms, Apartments.BATHROOMS + "<=?"),
                new Filter(minArea, Apartments.AREA + "<=CAST(? AS NUMERIC)"),
                new Filter(maxArea, Apartments.AREA + ">=CAST(? AS NUMERIC)"),
        };
    }

    private final class Filter {
        private final String var; // class var
        private final String where; // SQL where clause

        private Filter(String var, String where) {
            this.var = var;
            this.where = where;
        }
    }

}
