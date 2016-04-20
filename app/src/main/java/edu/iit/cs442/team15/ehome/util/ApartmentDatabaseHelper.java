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
import edu.iit.cs442.team15.ehome.model.User;

public final class ApartmentDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "offline_apartments";
    private static final int DB_VERSION = 2;

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

    public long addUser(User newUser) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Users.EMAIL, newUser.email);
        values.put(Users.PASSWORD, newUser.password);
        values.put(Users.NAME, newUser.name);
        values.put(Users.PHONE, newUser.phone);

        return db.insert(Users.TABLE, null, values);
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
                    .setPassword(result.getString(result.getColumnIndex(Users.PASSWORD)))
                    .setName(result.getString(result.getColumnIndex(Users.NAME)))
                    .setPhone(result.getString(result.getColumnIndex(Users.PHONE)));
        }

        result.close();
        db.close();

        return user;
    }

    public List<Apartment> getApartments(final int zip, final int bedrooms, final int bathrooms, final int min_rent, final int max_rent) {
        SQLiteDatabase db = getReadableDatabase();

        final String sqlQuery;
        if (zip == 0)
            sqlQuery = "SELECT * FROM " + Apartments.TABLE + " WHERE " + Apartments.BEDROOMS + ">=" + bedrooms + " AND " + Apartments.BATHROOMS + ">=" + bathrooms + " AND " + Apartments.RENT + ">=" + min_rent + " AND " + Apartments.RENT + "<=" + max_rent + " ORDER BY " + Apartments.RENT + " ASC";
        else
            sqlQuery = "SELECT * FROM " + Apartments.TABLE + " WHERE " + Apartments.ZIP + "=" + zip + " AND " + Apartments.BEDROOMS + ">=" + bedrooms + " AND " + Apartments.BATHROOMS + ">=" + bathrooms + " AND " + Apartments.RENT + ">=" + min_rent + " AND " + Apartments.RENT + "<=" + max_rent + " ORDER BY " + Apartments.RENT + " ASC";

        Cursor cur = db.rawQuery(sqlQuery, null);

        ArrayList<Apartment> result = new ArrayList<>();

        if (cur.moveToFirst()) {
            do {
                result.add(new Apartment()
                        .setId(cur.getInt(cur.getColumnIndex(Apartments.ID)))
                        .setAddress(cur.getString(cur.getColumnIndex(Apartments.ADDRESS)))
                        .setZip(cur.getInt(cur.getColumnIndex(Apartments.ZIP)))
                        .setBedrooms(cur.getInt(cur.getColumnIndex(Apartments.BEDROOMS)))
                        .setBedrooms(cur.getInt(cur.getColumnIndex(Apartments.BATHROOMS)))
                        .setSquareFeet(cur.getDouble(cur.getColumnIndex(Apartments.AREA)))
                        .setRent(cur.getInt(cur.getColumnIndex(Apartments.RENT)))
                        .setOwnerId(cur.getInt(cur.getColumnIndex(Apartments.OWNER_ID))));
            } while (cur.moveToNext());
        }

        cur.close();
        db.close();

        return result;
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

    public List<String> getAptNames() {
        SQLiteDatabase db = getWritableDatabase();

        final String query = "SELECT " + Apartments.ADDRESS + " FROM " + Apartments.TABLE;
        Cursor cur = db.rawQuery(query, null);
        ArrayList<String> apartments = new ArrayList<>();
        while (cur.moveToNext()) {
            apartments.add(cur.getString(0));
        }
        cur.close();
        return apartments;
    }

    @Nullable
    public Apartment getApartment(int id) {
        SQLiteDatabase db = getReadableDatabase();

        final String sqlQuery = "SELECT * FROM " + Apartments.TABLE + " WHERE " + Apartments.ID + "=?";
        Cursor result = db.rawQuery(sqlQuery, new String[]{Integer.toString(id)});

        Apartment apt = null;
        if (result.moveToFirst()) {
            apt = new Apartment()
                    .setId(result.getInt(result.getColumnIndex(Apartments.ID)))
                    .setAddress(result.getString(result.getColumnIndex(Apartments.ADDRESS)))
                    .setZip(result.getInt(result.getColumnIndex(Apartments.ZIP)))
                    .setBedrooms(result.getInt(result.getColumnIndex(Apartments.BEDROOMS)))
                    .setBathrooms(result.getInt(result.getColumnIndex(Apartments.BATHROOMS)))
                    .setSquareFeet(result.getDouble(result.getColumnIndex(Apartments.AREA)))
                    .setRent(result.getInt(result.getColumnIndex(Apartments.RENT)))
                    .setOwnerId(result.getInt(result.getColumnIndex(Apartments.OWNER_ID)));
        }

        result.close();
        db.close();

        return apt;
    }

    @Nullable
    public Amenity getAmenity(int apartment_id) {
        SQLiteDatabase db = getReadableDatabase();

        final String sqlQuery = "SELECT * FROM " + Amenities.TABLE + " WHERE " + Amenities.ID + "=?";
        Cursor result = db.rawQuery(sqlQuery, new String[]{Integer.toString(apartment_id)});

        Amenity amenity = null;
        if (result.moveToFirst()) {
            amenity = new Amenity()
                    .setApartmentId(result.getInt(result.getColumnIndex(Amenities.ID)))
                    .setCable(result.getDouble(result.getColumnIndex(Amenities.CABLE)))
                    .setElectricity(result.getDouble(result.getColumnIndex(Amenities.ELECTRICITY)))
                    .setGas(result.getDouble(result.getColumnIndex(Amenities.GAS)))
                    .setGym(result.getInt(result.getColumnIndex(Amenities.GYM)) == 1)
                    .setInternet(result.getDouble(result.getColumnIndex(Amenities.INTERNET)))
                    .setParking(result.getInt(result.getColumnIndex(Amenities.PARKING)) == 1)
                    .setThermostat(result.getDouble(result.getColumnIndex(Amenities.THERMOSTAT)));
        }

        result.close();
        db.close();

        return amenity;
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
