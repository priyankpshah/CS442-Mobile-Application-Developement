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

public final class ApartmentDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "offline_apartments";
    private static final int DB_VERSION = 3;

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

    public static synchronized ApartmentDatabaseHelper getInstance() {
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

        return db.insert(SearchHistory.TABLE, null, values);
    }

    public List<ApartmentSearchFilter> getSearchHistory(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.query(SearchHistory.TABLE, null, SearchHistory.USER_ID + "=?", new String[]{Integer.toString(userId)}, null, null, null);

        List<ApartmentSearchFilter> result = new ArrayList<>();
        if (cur.moveToFirst()) {
            do {
                ApartmentSearchFilter filter = new ApartmentSearchFilter();
                filter.minCost = cur.getString(cur.getColumnIndex(SearchHistory.MIN_COST));
                filter.maxCost = cur.getString(cur.getColumnIndex(SearchHistory.MAX_COST));
                filter.hasGym = cur.getString(cur.getColumnIndex(SearchHistory.HAS_GYM));
                filter.hasParking = cur.getString(cur.getColumnIndex(SearchHistory.HAS_PARKING));
                filter.minBeds = cur.getString(cur.getColumnIndex(SearchHistory.MIN_BEDS));
                filter.maxBeds = cur.getString(cur.getColumnIndex(SearchHistory.MAX_BEDS));
                filter.minBathrooms = cur.getString(cur.getColumnIndex(SearchHistory.MIN_BATHROOMS));
                filter.maxBathrooms = cur.getString(cur.getColumnIndex(SearchHistory.MAX_BATHROOMS));
                filter.minArea = cur.getString(cur.getColumnIndex(SearchHistory.MIN_AREA));
                filter.maxArea = cur.getString(cur.getColumnIndex(SearchHistory.MAX_AREA));

                result.add(filter);
            } while (cur.moveToNext());
        }

        cur.close();
        db.close();

        return result;
    }

    public int clearSearchHistory(int userId) {
        return getWritableDatabase().delete(SearchHistory.TABLE, SearchHistory.USER_ID + "=?", new String[]{Integer.toString(userId)});
    }

    public Cursor getApartmentsCursor(ApartmentSearchFilter filter) {
        SQLiteDatabase db = getReadableDatabase();

        return filter.query(db);
    }

    public List<Apartment> getApartments(ApartmentSearchFilter filter) {
        Cursor r = getApartmentsCursor(filter);
        ArrayList<Apartment> apartments = new ArrayList<>();

        if (r.moveToFirst()) {
            do {
                apartments.add(new Apartment()
                        .setId(r.getInt(r.getColumnIndex(Apartments.ID)))
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

    @Nullable
    public Amenity getAmenity(int apartment_id) {
        List<Apartment> result = getApartments(new ApartmentSearchFilter().setApartmentId(apartment_id));
        return result.isEmpty() ? null : result.get(0).amenity;
    }

    public static final class Users {
        public static final String TABLE = "users";
        public static final String ID = "id";
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
        public static final String NAME = "name";
        public static final String PHONE = "phone";
    }

    public static final class Favorites {
        public static final String TABLE = "user_favorites";
        public static final String USER_ID = "user_id";
        public static final String APARTMENT_ID = "apartment_id";
    }

    public static final class SearchHistory {
        public static final String TABLE = "search_history";
        public static final String ID = "_id";
        public static final String USER_ID = "user_id";
        public static final String MIN_COST = "min_cost";
        public static final String MAX_COST = "max_cost";
        public static final String HAS_GYM = "has_gym";
        public static final String HAS_PARKING = "has_parking";
        public static final String MIN_BEDS = "min_beds";
        public static final String MAX_BEDS = "max_beds";
        public static final String MIN_BATHROOMS = "min_bathrooms";
        public static final String MAX_BATHROOMS = "max_bathrooms";
        public static final String MIN_AREA = "min_area";
        public static final String MAX_AREA = "max_area";
    }

    public static final class Apartments {
        public static final String TABLE = "apartments";
        public static final String ID = "id";
        public static final String ADDRESS = "address";
        public static final String ZIP = "zip";
        public static final String BEDROOMS = "bedrooms";
        public static final String BATHROOMS = "bathrooms";
        public static final String AREA = "square_feet";
        public static final String RENT = "rent";
        public static final String OWNER_ID = "owner_id";
    }

    public static final class Owners {
        public static final String TABLE = "owners";
        public static final String ID = "id";
        public static final String COMPLEX_NAME = "complex_name";
        public static final String OWNER_PHONE = "owner_phone";
        public static final String OWNER_EMAIL = "owner_email";
    }

    public static final class Amenities {
        public static final String TABLE = "amenities";
        public static final String ID = "apartment_id";
        public static final String PARKING = "parking";
        public static final String GYM = "gym";
        public static final String GAS = "gas";
        public static final String ELECTRICITY = "electricity";
        public static final String INTERNET = "internet";
        public static final String CABLE = "cable";
        public static final String THERMOSTAT = "thermostat";
    }

    public static final class WebApartments {
        public static final String TABLE = "web_apartments";
        public static final String NAME = "name";
        public static final String ADDRESS = "address";
        public static final String RENT = "rent";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String OWNER_EMAIL = "owner_email";
        public static final String OWNER_PHONE = "owner_phone";
        public static final String OWNER_WEBSITE = "owner_website";
    }

}
