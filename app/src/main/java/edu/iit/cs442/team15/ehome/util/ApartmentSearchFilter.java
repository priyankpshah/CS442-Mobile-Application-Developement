package edu.iit.cs442.team15.ehome.util;

import android.database.sqlite.SQLiteQueryBuilder;

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

    public String getSqlQuery() {
        // join apartments, owners, and amenities tables together
        final String table = Apartments.TABLE +
                " JOIN " + Owners.TABLE + " ON " + Apartments.TABLE + "." + Apartments.OWNER_ID + "=" + Owners.TABLE + "." + Owners.ID +
                " JOIN " + Amenities.TABLE + " ON " + Apartments.TABLE + "." + Apartments.ID + "=" + Amenities.TABLE + "." + Amenities.ID;

        final String[] columns = {
                "*",
                "(" + Apartments.RENT + " + " + Amenities.GAS + " + " + Amenities.ELECTRICITY + " + " + Amenities.INTERNET + " + " + Amenities.CABLE + " + " + Amenities.THERMOSTAT + ") AS " + TOTAL_COST
        };

        // build where clause, must be in same order as in getSelectionArgs()
        final StringBuilder where = new StringBuilder();
        if (id != null)
            where.append(Apartments.TABLE).append(".").append(Apartments.ID).append("=?");
        if (minCost != null)
            where.append(TOTAL_COST).append(">=CAST(? AS NUMERIC) AND "); // selectionArgs are bound as Strings, so CAST to NUMERIC
        if (maxCost != null)
            where.append(TOTAL_COST).append("<=CAST(? AS NUMERIC) AND ");
        if (hasGym != null)
            where.append(Amenities.GYM).append("=? AND ");
        if (hasParking != null)
            where.append(Amenities.PARKING).append("=? AND ");
        if (minBedrooms != null)
            where.append(Apartments.BEDROOMS).append(">=? AND ");
        if (maxBedrooms != null)
            where.append(Apartments.BEDROOMS).append("<=? AND ");
        if (minBathrooms != null)
            where.append(Apartments.BATHROOMS).append(">=? AND ");
        if (maxBathrooms != null)
            where.append(Apartments.BATHROOMS).append("<=? AND ");
        if (minArea != null)
            where.append(Apartments.AREA).append("<=CAST(? AS NUMERIC) AND ");
        if (maxArea != null)
            where.append(Apartments.AREA).append(">=CAST(? AS NUMERIC) AND ");

        where.append("1=1"); // prevent trailing AND

        final String limit = "100";

        return SQLiteQueryBuilder.buildQueryString(false, table, columns, where.toString(), null, null, null, limit);
    }

    public String[] getSelectionArgs() {
        ArrayList<String> args = new ArrayList<>();

        // MUST be in same order as where clause above
        if (id != null)
            args.add(id);
        if (minCost != null)
            args.add(minCost);
        if (maxCost != null)
            args.add(maxCost);
        if (hasGym != null)
            args.add(hasGym);
        if (hasParking != null)
            args.add(hasParking);
        if (minBedrooms != null)
            args.add(minBedrooms);
        if (maxBedrooms != null)
            args.add(maxBedrooms);
        if (minBathrooms != null)
            args.add(minBathrooms);
        if (maxBathrooms != null)
            args.add(maxBathrooms);
        if (minArea != null)
            args.add(minArea);
        if (maxArea != null)
            args.add(maxArea);

        return args.toArray(new String[args.size()]);
    }

}
