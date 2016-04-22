package edu.iit.cs442.team15.ehome.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper.Amenities;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper.Apartments;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper.Owners;

/**
 * Provides a generic way to generate an SQL query for an apartment search
 * that will ignore unset fields
 */
public class ApartmentSearchFilter implements Serializable {

    public static final String TOTAL_COST = "ezprice";

    protected String apartmentId;
    protected String minCost, maxCost;
    protected String hasGym, hasParking;
    protected String minBeds, maxBeds;
    protected String minBathrooms, maxBathrooms;
    protected String minArea, maxArea;

    public ApartmentSearchFilter setApartmentId(int apartmentId) {
        this.apartmentId = Integer.toString(apartmentId);
        return this;
    }

    public ApartmentSearchFilter setMinCost(int minCost) {
        this.minCost = Integer.toString(minCost);
        return this;
    }

    public ApartmentSearchFilter setMaxCost(int maxCost) {
        this.maxCost = Integer.toString(maxCost);
        return this;
    }

    public ApartmentSearchFilter setHasGym(boolean hasGym) {
        this.hasGym = hasGym ? "1" : "0";
        return this;
    }

    public ApartmentSearchFilter setHasParking(boolean hasParking) {
        this.hasParking = hasParking ? "1" : "0";
        return this;
    }

    public ApartmentSearchFilter setMinBeds(int minBeds) {
        this.minBeds = Integer.toString(minBeds);
        return this;
    }

    public ApartmentSearchFilter setMaxBeds(int maxBeds) {
        this.maxBeds = Integer.toString(maxBeds);
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

    public ApartmentSearchFilter setMinArea(int minArea) {
        this.minArea = Integer.toString(minArea);
        return this;
    }

    public ApartmentSearchFilter setMaxArea(int maxArea) {
        this.maxArea = Integer.toString(maxArea);
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
            if (filter.value != null) {
                selection.append(filter.where).append(" AND ");
                selectionArgs.add(filter.value);
            }
        }
        selection.append("1=1"); // prevent trailing AND

        return db.query(table, columns, selection.toString(), selectionArgs.toArray(new String[selectionArgs.size()]), null, null, null, "100");
    }

    private Filter[] getFilters() {
        return new Filter[]{
                new Filter(apartmentId, Apartments.TABLE + "." + Apartments.ID + "=?"),
                new Filter(minCost, TOTAL_COST + ">=CAST(? AS INTEGER)"),
                new Filter(maxCost, TOTAL_COST + "<=CAST(? AS INTEGER)"),
                new Filter(hasGym, Amenities.GYM + "=?"),
                new Filter(hasParking, Amenities.PARKING + "=?"),
                new Filter(minBeds, Apartments.BEDROOMS + ">=?"),
                new Filter(maxBeds, Apartments.BEDROOMS + "<=?"),
                new Filter(minBathrooms, Apartments.BATHROOMS + ">=?"),
                new Filter(maxBathrooms, Apartments.BATHROOMS + "<=?"),
                new Filter(minArea, Apartments.AREA + "<=CAST(? AS INTEGER)"),
                new Filter(maxArea, Apartments.AREA + ">=CAST(? AS INTEGER)"),
        };
    }

    private static final class Filter {
        private final String value; // class value
        private final String where; // SQL where clause

        private Filter(String var, String where) {
            this.value = var;
            this.where = where;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Filter filter = (Filter) o;

            return where != null ? where.equals(filter.where) : filter.where == null;

        }

        @Override
        public int hashCode() {
            return where != null ? where.hashCode() : 0;
        }
    }

}
