package edu.iit.cs442.team15.ehome.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.iit.cs442.team15.ehome.util.ApartmentDatabaseHelper.WebApartments;

public class WebApartmentSearchFilter {

    public Integer minRent, maxRent;
    public Integer distance; // easier to do in code
    public String location;

    public WebApartmentSearchFilter setMinRent(int minRent) {
        this.minRent = minRent;
        return this;
    }

    public WebApartmentSearchFilter setMaxRent(int maxRent) {
        this.maxRent = maxRent;
        return this;
    }

    public WebApartmentSearchFilter setDistance(int distance) {
        this.distance = distance;
        return this;
    }

    public WebApartmentSearchFilter setLocation(String location) {
        this.location = location;
        return this;
    }

    public Cursor query(SQLiteDatabase db) {
        final String table = WebApartments.TABLE;

        final String[] columns = {"*"};

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
                new Filter(minRent, WebApartments.RENT + "<=CAST(? AS INTEGER)"),
                new Filter(maxRent, WebApartments.RENT + ">=CAST(? AS INTEGER)"),
        };
    }

    private static final class Filter {
        private final String value; // class value
        private final String where; // SQL where clause

        private Filter(Integer var, String where) {
            this.value = var == null ? null : var.toString();
            this.where = where;
        }
    }
}
