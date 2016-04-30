package edu.iit.cs442.team15.ehome.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import edu.iit.cs442.team15.ehome.model.Amenity;
import edu.iit.cs442.team15.ehome.model.Apartment;
import edu.iit.cs442.team15.ehome.model.Owner;
import edu.iit.cs442.team15.ehome.model.User;
import edu.iit.cs442.team15.ehome.model.WebApartment;

public final class ApartmentDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "offline_apartments";
    private static final int DB_VERSION = 4;

    private static ApartmentDatabaseHelper sInstance; // singleton instance

    private final Context context;

    private ApartmentDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;

        setupDatabase();
    }

    public static synchronized void initialize(Context context) {
        if (sInstance == null)
            sInstance = new ApartmentDatabaseHelper(context.getApplicationContext());
    }

    public static ApartmentDatabaseHelper getInstance() {
        if (sInstance == null)
            throw new RuntimeException("ApartmentDatabaseHelper not initialized");
        return sInstance;
    }

    private void setupDatabase() {
        // check if database already exists
        if (!context.getDatabasePath(DB_NAME).exists()) {
            getReadableDatabase(); // creates an empty database file

            try {
                InputStream input = context.getAssets().open(DB_NAME + ".db");
                OutputStream output = new FileOutputStream(context.getDatabasePath(DB_NAME));

                //copy bytes from the input file to the output file
                byte[] buffer = new byte[1024];
                int length;
                while ((length = input.read(buffer)) > 0)
                    output.write(buffer, 0, length);

                output.close();
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            context.deleteDatabase(DB_NAME);
        }
    }

    public boolean checkLogin(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cur = db.query(Users.TABLE, new String[]{Users.ID}, Users.EMAIL + "=? AND " + Users.PASSWORD + "=?", new String[]{email, password}, null, null, null);
        int count = cur.getCount();

        cur.close();
        db.close();

        return count > 0;
    }

    /* -------- Users -------- */

    public long addUser(User newUser) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Users.EMAIL, newUser.email);
        values.put(Users.PASSWORD, newUser.password);
        values.put(Users.NAME, newUser.name);
        values.put(Users.PHONE, newUser.phone);

        return db.insert(Users.TABLE, null, values);
    }

    public int updateUser(final String currentEmail, final User updatedUser) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Users.EMAIL, updatedUser.email);
        values.put(Users.PASSWORD, updatedUser.password);
        values.put(Users.NAME, updatedUser.name);
        values.put(Users.PHONE, updatedUser.phone);

        return db.update(Users.TABLE, values, Users.EMAIL + "=?", new String[]{currentEmail});
    }

    @Nullable
    public User getUser(final String email) {
        SQLiteDatabase db = getReadableDatabase();

        final String sqlQuery = "SELECT * FROM " + Users.TABLE + " WHERE " + Users.EMAIL + "=?";
        Cursor result = db.rawQuery(sqlQuery, new String[]{email});

        User user = null;

        if (result.moveToFirst()) {
            // User exists
            user = new User()
                    .setId(result.getInt(result.getColumnIndex(Users.ID)))
                    .setEmail(email)
                    .setName(result.getString(result.getColumnIndex(Users.NAME)))
                    .setPhone(result.getString(result.getColumnIndex(Users.PHONE)));
        }

        result.close();
        db.close();

        return user;
    }

    /* -------- Favorites -------- */

    // returns list of apartment ids of favorites
    public List<Integer> getFavorites(int userId) {
        SQLiteDatabase db = getReadableDatabase();

        final String selection = Favorites.USER_ID + "=?";
        Cursor cur = db.query(Favorites.TABLE, new String[]{Favorites.APARTMENT_ID}, selection, new String[]{Integer.toString(userId)}, null, null, null);

        List<Integer> result = new ArrayList<>();
        if (cur.moveToFirst()) {
            do {
                result.add(cur.getInt(cur.getColumnIndex(Favorites.APARTMENT_ID)));
            } while (cur.moveToNext());
        }

        cur.close();
        db.close();

        return result;
    }

    public long addFavorite(int userId, int apartmentId) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Favorites.USER_ID, userId);
        values.put(Favorites.APARTMENT_ID, apartmentId);

        return db.insert(Favorites.TABLE, null, values);
    }

    public int removeFavorite(int userId, int apartmentId) {
        SQLiteDatabase db = getWritableDatabase();

        final String whereClause = Favorites.USER_ID + "=? AND " + Favorites.APARTMENT_ID + "=?";
        return db.delete(Favorites.TABLE, whereClause, new String[]{Integer.toString(userId), Integer.toString(apartmentId)});
    }

    public boolean isFavorited(int userId, int apartmentId) {
        SQLiteDatabase db = getReadableDatabase();

        final String selection = Favorites.USER_ID + "=? AND " + Favorites.APARTMENT_ID + "=?";
        Cursor cur = db.query(Favorites.TABLE, null, selection, new String[]{Integer.toString(userId), Integer.toString(apartmentId)}, null, null, null, "1");

        boolean result = cur.getCount() > 0;

        cur.close();
        db.close();

        return result;
    }

    /* -------- Search History -------- */

    public long addSearchHistory(int userId, ApartmentSearchFilter filter) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SearchHistory.USER_ID, userId);
        values.put(SearchHistory.MIN_COST, filter.minCost);
        values.put(SearchHistory.MAX_COST, filter.maxCost);
        values.put(SearchHistory.HAS_GYM, filter.hasGym);
        values.put(SearchHistory.HAS_PARKING, filter.hasParking);
        values.put(SearchHistory.MIN_BEDS, filter.minBeds);
        values.put(SearchHistory.MAX_BEDS, filter.maxBeds);
        values.put(SearchHistory.MIN_BATHROOMS, filter.minBathrooms);
        values.put(SearchHistory.MAX_BATHROOMS, filter.maxBathrooms);
        values.put(SearchHistory.MIN_AREA, filter.minArea);
        values.put(SearchHistory.MAX_AREA, filter.maxArea);
        values.put(SearchHistory.DISTANCE, filter.distance);
        values.put(SearchHistory.LOCATION, filter.location);
        values.put(SearchHistory.IS_EZHOME_SEARCH, filter.isEzhomeSearch);

        return db.insert(SearchHistory.TABLE, null, values);
    }

    @Nullable
    public ApartmentSearchFilter getSearchFilter(int id) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(SearchHistory.TABLE, null, SearchHistory.ID + "=?", new String[]{Integer.toString(id)}, null, null, null, "1");
        List<ApartmentSearchFilter> filters = cursorToFilters(cursor);

        cursor.close();
        db.close();

        return filters.isEmpty() ? null : filters.get(0);
    }

    @Nullable
    public ApartmentSearchFilter getLastSearchFilter(int userId, boolean isEzhomeSearch) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                SearchHistory.TABLE,
                null,
                SearchHistory.USER_ID + "=? AND " + SearchHistory.IS_EZHOME_SEARCH + "=?",
                new String[]{Integer.toString(userId), isEzhomeSearch ? "TRUE" : "FALSE"},
                null,
                null,
                SearchHistory.ID + " DESC",
                "1");
        List<ApartmentSearchFilter> filters = cursorToFilters(cursor);

        cursor.close();
        db.close();

        return filters.isEmpty() ? null : filters.get(0);
    }

    public List<ApartmentSearchFilter> getSearchHistory(int userId) {
        SQLiteDatabase db = getReadableDatabase();

        final String orderBy = SearchHistory.ID + " DESC";
        Cursor cursor = db.query(SearchHistory.TABLE, null, SearchHistory.USER_ID + "=?", new String[]{Integer.toString(userId)}, null, null, orderBy);

        return cursorToFilters(cursor);
    }

    private List<ApartmentSearchFilter> cursorToFilters(final Cursor cur) {
        List<ApartmentSearchFilter> result = new ArrayList<>();
        if (cur.moveToFirst()) {
            do {
                ApartmentSearchFilter filter = new ApartmentSearchFilter();
                if (!cur.isNull(cur.getColumnIndex(SearchHistory.ID)))
                    filter.id = cur.getInt(cur.getColumnIndex(SearchHistory.ID));
                if (!cur.isNull(cur.getColumnIndex(SearchHistory.MIN_COST)))
                    filter.minCost = cur.getInt(cur.getColumnIndex(SearchHistory.MIN_COST));
                if (!cur.isNull(cur.getColumnIndex(SearchHistory.MAX_COST)))
                    filter.maxCost = cur.getInt(cur.getColumnIndex(SearchHistory.MAX_COST));
                if (!cur.isNull(cur.getColumnIndex(SearchHistory.HAS_GYM)))
                    filter.hasGym = cur.getInt(cur.getColumnIndex(SearchHistory.HAS_GYM)) == 1;
                if (!cur.isNull(cur.getColumnIndex(SearchHistory.HAS_PARKING)))
                    filter.hasParking = cur.getInt(cur.getColumnIndex(SearchHistory.HAS_PARKING)) == 1;
                if (!cur.isNull(cur.getColumnIndex(SearchHistory.MIN_BEDS)))
                    filter.minBeds = cur.getInt(cur.getColumnIndex(SearchHistory.MIN_BEDS));
                if (!cur.isNull(cur.getColumnIndex(SearchHistory.MAX_BEDS)))
                    filter.maxBeds = cur.getInt(cur.getColumnIndex(SearchHistory.MAX_BEDS));
                if (!cur.isNull(cur.getColumnIndex(SearchHistory.MIN_BATHROOMS)))
                    filter.minBathrooms = cur.getInt(cur.getColumnIndex(SearchHistory.MIN_BATHROOMS));
                if (!cur.isNull(cur.getColumnIndex(SearchHistory.MAX_BATHROOMS)))
                    filter.maxBathrooms = cur.getInt(cur.getColumnIndex(SearchHistory.MAX_BATHROOMS));
                if (!cur.isNull(cur.getColumnIndex(SearchHistory.MIN_AREA)))
                    filter.minArea = cur.getInt(cur.getColumnIndex(SearchHistory.MIN_AREA));
                if (!cur.isNull(cur.getColumnIndex(SearchHistory.MAX_AREA)))
                    filter.maxArea = cur.getInt(cur.getColumnIndex(SearchHistory.MAX_AREA));
                if (!cur.isNull(cur.getColumnIndex(SearchHistory.DISTANCE)))
                    filter.distance = cur.getDouble(cur.getColumnIndex(SearchHistory.DISTANCE));
                if (!cur.isNull(cur.getColumnIndex(SearchHistory.LOCATION)))
                    filter.location = cur.getString(cur.getColumnIndex(SearchHistory.LOCATION));

                filter.isEzhomeSearch = cur.getInt(cur.getColumnIndex(SearchHistory.IS_EZHOME_SEARCH)) == 1;

                result.add(filter);
            } while (cur.moveToNext());
        }

        return result;
    }

    public int clearSearchHistory(int userId) {
        return getWritableDatabase().delete(SearchHistory.TABLE, SearchHistory.USER_ID + "=?", new String[]{Integer.toString(userId)});
    }

    /* -------- Apartments -------- */

    public Cursor getApartmentsCursor(ApartmentSearchFilter filter) {
        if (!filter.isEzhomeSearch)
            throw new RuntimeException("filter is not an ezhome search filter");
        SQLiteDatabase db = getReadableDatabase();

        return filter.query(db);
    }

    public List<Apartment> getApartments(ApartmentSearchFilter filter) {
        Cursor r = getApartmentsCursor(filter);
        ArrayList<Apartment> apartments = new ArrayList<>();

        if (r.moveToFirst()) {
            do {
                apartments.add(new Apartment()
                        .setId(r.getInt(r.getColumnIndex(Amenities.ID))) // corresponds to apartment id
                        .setAddress(r.getString(r.getColumnIndex(Apartments.ADDRESS)))
                        .setZip(r.getInt(r.getColumnIndex(Apartments.ZIP)))
                        .setBedrooms(r.getInt(r.getColumnIndex(Apartments.BEDROOMS)))
                        .setBathrooms(r.getInt(r.getColumnIndex(Apartments.BATHROOMS)))
                        .setSquareFeet(r.getDouble(r.getColumnIndex(Apartments.AREA)))
                        .setRent(r.getInt(r.getColumnIndex(Apartments.RENT)))
                        .setOwner(new Owner()
                                .setId(r.getInt(r.getColumnIndex(Owners.ID)))
                                .setComplexName(r.getString(r.getColumnIndex(Owners.COMPLEX_NAME)))
                                .setOwnerPhone(r.getString(r.getColumnIndex(Owners.OWNER_PHONE)))
                                .setOwnerEmail(r.getString(r.getColumnIndex(Owners.OWNER_EMAIL))))
                        .setAmenity(new Amenity()
                                .setParking(r.getInt(r.getColumnIndex(Amenities.PARKING)) == 1)
                                .setGym(r.getInt(r.getColumnIndex(Amenities.GYM)) == 1)
                                .setGas(r.getDouble(r.getColumnIndex(Amenities.GAS)))
                                .setElectricity(r.getDouble(r.getColumnIndex(Amenities.ELECTRICITY)))
                                .setInternet(r.getDouble(r.getColumnIndex(Amenities.INTERNET)))
                                .setCable(r.getDouble(r.getColumnIndex(Amenities.CABLE)))
                                .setThermostat(r.getDouble(r.getColumnIndex(Amenities.THERMOSTAT)))));
            } while (r.moveToNext());
        }

        r.close();

        return apartments;
    }

    @Nullable
    public Apartment getApartment(int id) {
        List<Apartment> result = getApartments(new ApartmentSearchFilter().setApartmentId(id));
        return result.isEmpty() ? null : result.get(0);
    }

    /* Web Apartments */

    private Cursor getWebApartmentsCursor(ApartmentSearchFilter filter) {
        if (filter.isEzhomeSearch)
            throw new RuntimeException("filter is not a web filter");

        SQLiteDatabase db = getReadableDatabase();
        return filter.query(db);
    }

    public List<WebApartment> getWebApartments(ApartmentSearchFilter filter) {
        Cursor cur = getWebApartmentsCursor(filter);

        List<WebApartment> result = new ArrayList<>();
        if (cur.moveToFirst()) {
            do {
                result.add(new WebApartment()
                        .setName(cur.getString(cur.getColumnIndex(WebApartments.NAME)))
                        .setAddress(cur.getString(cur.getColumnIndex(WebApartments.ADDRESS)))
                        .setRent(cur.getDouble(cur.getColumnIndex(WebApartments.RENT)))
                        .setLatitude(cur.getDouble(cur.getColumnIndex(WebApartments.LATITUDE)))
                        .setLongitude(cur.getDouble(cur.getColumnIndex(WebApartments.LONGITUDE)))
                        .setOwnerEmail(cur.getString(cur.getColumnIndex(WebApartments.OWNER_EMAIL)))
                        .setOwnerPhone(cur.getString(cur.getColumnIndex(WebApartments.OWNER_PHONE)))
                        .setOwnerWebsite(cur.getString(cur.getColumnIndex(WebApartments.OWNER_WEBSITE))));
            } while (cur.moveToNext());
        }

        cur.close();

        return result;
    }

    public interface Users {
        String TABLE = "users";
        String ID = "id";
        String EMAIL = "email";
        String PASSWORD = "password";
        String NAME = "name";
        String PHONE = "phone";
    }

    public interface Favorites {
        String TABLE = "user_favorites";
        String USER_ID = "user_id";
        String APARTMENT_ID = "apartment_id";
    }

    public interface SearchHistory {
        String TABLE = "search_history";
        String ID = "_id";
        String USER_ID = "user_id";
        String MIN_COST = "min_cost";
        String MAX_COST = "max_cost";
        String HAS_GYM = "has_gym";
        String HAS_PARKING = "has_parking";
        String MIN_BEDS = "min_beds";
        String MAX_BEDS = "max_beds";
        String MIN_BATHROOMS = "min_bathrooms";
        String MAX_BATHROOMS = "max_bathrooms";
        String MIN_AREA = "min_area";
        String MAX_AREA = "max_area";
        String DISTANCE = "distance";
        String LOCATION = "location";
        String IS_EZHOME_SEARCH = "is_ezhome_search";
    }

    public interface Apartments {
        String TABLE = "apartments";
        String ID = "id";
        String ADDRESS = "address";
        String ZIP = "zip";
        String BEDROOMS = "bedrooms";
        String BATHROOMS = "bathrooms";
        String AREA = "square_feet";
        String RENT = "rent";
        String OWNER_ID = "owner_id";
    }

    public interface Owners {
        String TABLE = "owners";
        String ID = "id";
        String COMPLEX_NAME = "complex_name";
        String OWNER_PHONE = "owner_phone";
        String OWNER_EMAIL = "owner_email";
    }

    public interface Amenities {
        String TABLE = "amenities";
        String ID = "apartment_id";
        String PARKING = "parking";
        String GYM = "gym";
        String GAS = "gas";
        String ELECTRICITY = "electricity";
        String INTERNET = "internet";
        String CABLE = "cable";
        String THERMOSTAT = "thermostat";
    }

    public interface WebApartments {
        String TABLE = "web_apartments";
        String NAME = "name";
        String ADDRESS = "address";
        String RENT = "rent";
        String LATITUDE = "latitude";
        String LONGITUDE = "longitude";
        String OWNER_EMAIL = "owner_email";
        String OWNER_PHONE = "owner_phone";
        String OWNER_WEBSITE = "owner_website";
    }

}
