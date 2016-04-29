package edu.iit.cs442.team15.ehome.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper.Amenities;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper.Apartments;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper.Owners;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper.WebApartments;

/**
 * Provides a generic way to generate an SQL query for an apartment search
 * that will ignore unset fields
 */
public class ApartmentSearchFilter implements Serializable {

    public static final String TOTAL_COST = "ezprice";

    public int id;
    public Integer apartmentId;
    public Integer minCost, maxCost;
    public Boolean hasGym, hasParking;
    public Integer minBeds, maxBeds;
    public Integer minBathrooms, maxBathrooms;
    public Integer minArea, maxArea;
    public Double distance; // filter this in code
    public String location;
    boolean isEzhomeSearch = true;

    public ApartmentSearchFilter setApartmentId(int apartmentId) {
        this.apartmentId = apartmentId;
        return this;
    }

    public ApartmentSearchFilter setMinCost(int minCost) {
        this.minCost = minCost;
        return this;
    }

    public ApartmentSearchFilter setMaxCost(int maxCost) {
        this.maxCost = maxCost;
        return this;
    }

    public ApartmentSearchFilter setHasGym(boolean hasGym) {
        this.hasGym = hasGym;
        return this;
    }

    public ApartmentSearchFilter setHasParking(boolean hasParking) {
        this.hasParking = hasParking;
        return this;
    }

    public ApartmentSearchFilter setMinBeds(int minBeds) {
        this.minBeds = minBeds;
        return this;
    }

    public ApartmentSearchFilter setMaxBeds(int maxBeds) {
        this.maxBeds = maxBeds;
        return this;
    }

    public ApartmentSearchFilter setMinBathrooms(int minBathrooms) {
        this.minBathrooms = minBathrooms;
        return this;
    }

    public ApartmentSearchFilter setMaxBathrooms(int maxBathrooms) {
        this.maxBathrooms = maxBathrooms;
        return this;
    }

    public ApartmentSearchFilter setMinArea(int minArea) {
        this.minArea = minArea;
        return this;
    }

    public ApartmentSearchFilter setMaxArea(int maxArea) {
        this.maxArea = maxArea;
        return this;
    }

    public ApartmentSearchFilter setDistance(double distance) {
        this.distance = distance;
        return this;
    }

    public ApartmentSearchFilter setLocation(String location) {
        this.location = location;
        return this;
    }

    public ApartmentSearchFilter setEzhomeSearch(boolean ezhomeSearch) {
        isEzhomeSearch = ezhomeSearch;
        return this;
    }

    public Cursor query(SQLiteDatabase db) {
        final String table;
        final String[] columns;

        if (isEzhomeSearch) {
            // join apartments, owners, and amenities tables together
            table = Apartments.TABLE +
                    " JOIN " + Owners.TABLE + " ON " + Apartments.TABLE + "." + Apartments.OWNER_ID + "=" + Owners.TABLE + "." + Owners.ID +
                    " JOIN " + Amenities.TABLE + " ON " + Apartments.TABLE + "." + Apartments.ID + "=" + Amenities.TABLE + "." + Amenities.ID;

            columns = new String[]{
                    "*",
                    "(" + Apartments.RENT + " + " + Amenities.GAS + " + " + Amenities.ELECTRICITY + " + " + Amenities.INTERNET + " + " + Amenities.CABLE + " + " + Amenities.THERMOSTAT + ") AS " + TOTAL_COST
            };
        } else {
            table = WebApartments.TABLE;
            columns = new String[]{"*"};
        }

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
        if (isEzhomeSearch) {
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
        } else {
            return new Filter[]{
                    new Filter(minCost, WebApartments.RENT + "<=CAST(? AS NUMERIC)"),
                    new Filter(maxCost, WebApartments.RENT + ">=CAST(? AS INTEGER)")
            };
        }
    }

    private static final class Filter {
        private final String value; // class value
        private final String where; // SQL where clause

        private Filter(Boolean var, String where) {
            this.value = var == null ? null : var ? "1" : "0";
            this.where = where;
        }

        private Filter(Integer var, String where) {
            this.value = var == null ? null : var.toString();
            this.where = where;
        }
    }

}
