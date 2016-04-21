package edu.iit.cs442.team15.ehome.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper.Amenities;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper.Apartments;
import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper.Owners;

/**
 * Provides a generic way to generate an SQL query for an apartment search
 * that will ignore unset fields
 */
public class ApartmentSearchFilter {

    public static final String TOTAL_COST = "ezprice";

    private final Set<Filter> filters;

    public ApartmentSearchFilter() {
        filters = new HashSet<>();
    }

    public ApartmentSearchFilter setId(int id) {
        filters.add(new Filter(Integer.toString(id), Apartments.TABLE + "." + Apartments.ID + "=?"));
        return this;
    }

    public ApartmentSearchFilter setMinCost(double minCost) {
        filters.add(new Filter(Double.toString(minCost), TOTAL_COST + ">=CAST(? AS NUMERIC)"));
        return this;
    }

    public ApartmentSearchFilter setMaxCost(double maxCost) {
        filters.add(new Filter(Double.toString(maxCost), TOTAL_COST + "<=CAST(? AS NUMERIC)"));
        return this;
    }

    public ApartmentSearchFilter setHasGym(boolean hasGym) {
        filters.add(new Filter(hasGym ? "1" : "0", Amenities.GYM + "=?"));
        return this;
    }

    public ApartmentSearchFilter setHasParking(boolean hasParking) {
        filters.add(new Filter(hasParking ? "1" : "0", Amenities.PARKING + "=?"));
        return this;
    }

    public ApartmentSearchFilter setMinBedrooms(int minBedrooms) {
        filters.add(new Filter(Integer.toString(minBedrooms), Apartments.BEDROOMS + ">=?"));
        return this;
    }

    public ApartmentSearchFilter setMaxBedrooms(int maxBedrooms) {
        filters.add(new Filter(Integer.toString(maxBedrooms), Apartments.BEDROOMS + "<=?"));
        return this;
    }

    public ApartmentSearchFilter setMinBathrooms(int minBathrooms) {
        filters.add(new Filter(Integer.toString(minBathrooms), Apartments.BATHROOMS + ">=?"));
        return this;
    }

    public ApartmentSearchFilter setMaxBathrooms(int maxBathrooms) {
        filters.add(new Filter(Integer.toString(maxBathrooms), Apartments.BATHROOMS + "<=?"));
        return this;
    }

    public ApartmentSearchFilter setMinArea(double minArea) {
        filters.add(new Filter(Double.toString(minArea), Apartments.AREA + "<=CAST(? AS NUMERIC)"));
        return this;
    }

    public ApartmentSearchFilter setMaxArea(double maxArea) {
        filters.add(new Filter(Double.toString(maxArea), Apartments.AREA + ">=CAST(? AS NUMERIC)"));
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

        for (Filter filter : filters) {
            selection.append(filter.where).append(" AND ");
            selectionArgs.add(filter.value);
        }
        selection.append("1=1"); // prevent trailing AND

        return db.query(table, columns, selection.toString(), selectionArgs.toArray(new String[selectionArgs.size()]), null, null, null, "100");
    }

    private static final class Filter {
        private final String value; // value for ? in where clause
        private final String where; // SQL where clause

        private Filter(String value, String where) {
            this.value = value;
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
