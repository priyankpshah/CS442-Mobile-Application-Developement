package edu.iit.cs442.team15.ehome.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

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
        final StringBuilder where = new StringBuilder();
        final ArrayList<String> args = new ArrayList<>();

        if (id != null) {
            where.append(Apartments.TABLE).append(".").append(Apartments.ID).append("=?");
            args.add(id);
        }
        if (minCost != null) {
            where.append(TOTAL_COST).append(">=CAST(? AS NUMERIC) AND "); // selectionArgs are bound as Strings, so CAST to NUMERIC
            args.add(minCost);
        }
        if (maxCost != null) {
            where.append(TOTAL_COST).append("<=CAST(? AS NUMERIC) AND ");
            args.add(maxCost);
        }
        if (hasGym != null) {
            where.append(Amenities.GYM).append("=? AND ");
            args.add(hasGym);
        }
        if (hasParking != null) {
            where.append(Amenities.PARKING).append("=? AND ");
            args.add(hasParking);
        }
        if (minBedrooms != null) {
            where.append(Apartments.BEDROOMS).append(">=? AND ");
            args.add(minBedrooms);
        }
        if (maxBedrooms != null) {
            where.append(Apartments.BEDROOMS).append("<=? AND ");
            args.add(maxBedrooms);
        }
        if (minBathrooms != null) {
            where.append(Apartments.BATHROOMS).append(">=? AND ");
            args.add(minBathrooms);
        }
        if (maxBathrooms != null) {
            where.append(Apartments.BATHROOMS).append("<=? AND ");
            args.add(maxBedrooms);
        }
        if (minArea != null) {
            where.append(Apartments.AREA).append("<=CAST(? AS NUMERIC) AND ");
            args.add(minArea);
        }
        if (maxArea != null) {
            where.append(Apartments.AREA).append(">=CAST(? AS NUMERIC) AND ");
            args.add(maxArea);
        }

        where.append("1=1"); // prevent trailing AND

        final String limit = "100";

        return db.query(table, columns, where.toString(), args.toArray(new String[args.size()]), null, null, null, limit);
    }

}
