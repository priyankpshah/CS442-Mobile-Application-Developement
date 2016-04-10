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
import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.iit.cs442.team15.ehome.model.User;

public final class ApartmentDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "offline_apartments";
    private static final int DB_VERSION = 1;

    private static ApartmentDatabaseHelper sInstance; // singleton instance

    private final Context context;

    // Priyank, this is private for a reason - Tom
    public ApartmentDatabaseHelper(Context context) {
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
            setupDatabase();
        }
    }

    public long addUser(User newUser) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Users.KEY_EMAIL, newUser.email);
        values.put(Users.KEY_PASSWORD, newUser.password);
        values.put(Users.KEY_NAME, newUser.name);
        values.put(Users.KEY_ADDRESS, newUser.address);
        values.put(Users.KEY_PHONE, newUser.phone);

        return db.insert(Users.TABLE_NAME, null, values);
    }

    @Nullable
    public User getUser(final String email) {
        SQLiteDatabase db = getReadableDatabase();

        final String sqlQuery = "SELECT * FROM " + Users.TABLE_NAME + " WHERE " + Users.KEY_EMAIL + "=?";
        Cursor result = db.rawQuery(sqlQuery, new String[]{email});

        User user = null;

        if (result.moveToFirst()) {
            // User exists
            user = new User()
                    .setId(result.getInt(result.getColumnIndex(Users.KEY_ID)))
                    .setEmail(email)
                    .setPassword(result.getString(result.getColumnIndex(Users.KEY_PASSWORD)))
                    .setName(result.getString(result.getColumnIndex(Users.KEY_NAME)))
                    .setAddress(result.getString(result.getColumnIndex(Users.KEY_ADDRESS)))
                    .setPhone(result.getString(result.getColumnIndex(Users.KEY_PHONE)));
        }

        result.close();
        db.close();

        return user;
    }
    
    public Cursor getApartments(final int zipcode, final int bedrooms, final int bathrooms, final int min_rent, final int max_rent){
        SQLiteDatabase db = getReadableDatabase();
        
        final String sqlQuery;
        if(zipcode==0)
            sqlQuery = "SELECT * FROM " + Aptinfo.TABLE_NAME + " WHERE " + Aptinfo.KEY_BEDROOMS + ">=" + bedrooms + " AND " + Aptinfo.KEY_BATHROOMS + ">=" + bathrooms + " AND " + Aptinfo.KEY_RENT + ">=" +min_rent + " AND " +Aptinfo.KEY_RENT + "<=" + max_rent + " ORDER BY " + Aptinfo.KEY_RENT + " ASC";
        
            else sqlQuery = "SELECT * FROM " + Aptinfo.TABLE_NAME + " WHERE " + Aptinfo.KEY_ZIPCODE + "=" + zipcode + " AND " + Aptinfo.KEY_BEDROOMS + ">=" + bedrooms + " AND " + Aptinfo.KEY_BATHROOMS + ">=" + bathrooms + " AND " + Aptinfo.KEY_RENT + ">=" +min_rent + " AND " +Aptinfo.KEY_RENT + "<=" + max_rent + " ORDER BY " + Aptinfo.KEY_RENT + " ASC";
        
        Cursor result = db.rawQuery(sqlQuery,null);

        return result;
    }

    public int updateUser(final String currentEmail, final User updatedUser) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Users.KEY_EMAIL, updatedUser.email);
        values.put(Users.KEY_PASSWORD, updatedUser.password);
        values.put(Users.KEY_NAME, updatedUser.name);
        values.put(Users.KEY_ADDRESS, updatedUser.address);
        values.put(Users.KEY_PHONE, updatedUser.phone);

        return db.update(Users.TABLE_NAME, values, Users.KEY_EMAIL + "=?", new String[]{currentEmail});
    }

    public ArrayList<String> getAptNames(){
        SQLiteDatabase db = getWritableDatabase();
        final String query = "SELECT "+Aptinfo.KEY_APTADDRESS+" FROM " + Aptinfo.TABLE_NAME;
        Cursor cur = db.rawQuery(query, null);
        ArrayList<String> aptinfo = new ArrayList<String>();
        while(cur.moveToNext())
        {
            aptinfo.add(cur.getString(0));
        }
        cur.close();
        return aptinfo;
    }
    @Nullable
    public ArrayList<String> getDetails(String id) {
        SQLiteDatabase db = getReadableDatabase();

        final String sqlQuery = "SELECT * FROM " + Aptinfo.TABLE_NAME + " WHERE " + Aptinfo.KEY_ID + "=?";
        Cursor result = db.rawQuery(sqlQuery, new String[]{id});
        ArrayList<String> aptinfo = new ArrayList<String>();
        while(result.moveToNext())
        {
            aptinfo.add(result.getString(result.getColumnIndex(Aptinfo.KEY_APTADDRESS)));
            aptinfo.add(result.getString(result.getColumnIndex(Aptinfo.KEY_AREA)));
            aptinfo.add(result.getString(result.getColumnIndex(Aptinfo.KEY_BATHROOMS)));
            aptinfo.add(result.getString(result.getColumnIndex(Aptinfo.KEY_BEDROOMS)));
            aptinfo.add(result.getString(result.getColumnIndex(Aptinfo.KEY_RENT)));
            aptinfo.add(result.getString(result.getColumnIndex(Aptinfo.KEY_ZIPCODE)));
            aptinfo.add(result.getString(result.getColumnIndex(Aptinfo.OWNERID)));
        }
        result.close();
        db.close();

        return aptinfo;
    }

    @Nullable
    public ArrayList<String> getAmenities(String id) {
        SQLiteDatabase db = getReadableDatabase();

        final String sqlQuery = "SELECT * FROM " + Ammenities.TABLE_NAME + " WHERE " + Ammenities.KEY_ID + "=?";
        Cursor result = db.rawQuery(sqlQuery, new String[]{id});
        ArrayList<String> ammenitiesinfo = new ArrayList<String>();
        while(result.moveToNext())
        {
            ammenitiesinfo.add(result.getString(result.getColumnIndex(Ammenities.KEY_CABLE)));
            ammenitiesinfo.add(result.getString(result.getColumnIndex(Ammenities.KEY_ELECTRICITY)));
            ammenitiesinfo.add(result.getString(result.getColumnIndex(Ammenities.KEY_GAS)));
            ammenitiesinfo.add(result.getString(result.getColumnIndex(Ammenities.KEY_GYM)));
            ammenitiesinfo.add(result.getString(result.getColumnIndex(Ammenities.KEY_INTERNET)));
            ammenitiesinfo.add(result.getString(result.getColumnIndex(Ammenities.KEY_PARKING)));
            ammenitiesinfo.add(result.getString(result.getColumnIndex(Ammenities.KEY_THERMOSTATE)));
        }
        result.close();
        db.close();

        return ammenitiesinfo;
    }

    public static final class Aptinfo {
        public static final String TABLE_NAME = "apartments";
        public static final String KEY_ID = "id";
        public static final String KEY_APTADDRESS = "address";
        public static final String KEY_ZIPCODE = "zipcode";
        public static final String KEY_BEDROOMS = "bedrooms";
        public static final String KEY_BATHROOMS = "bathrooms";
        public static final String KEY_AREA = "square_feet";
        public static final String KEY_RENT = "rent";
        public static final String OWNERID = "owner_id";

    }
    public static final class Ammenities {
        public static final String TABLE_NAME = "amenities";
        public static final String KEY_ID = "apartment_id";
        public static final String KEY_PARKING = "parking";
        public static final String KEY_GYM = "gym";
        public static final String KEY_GAS = "gas";
        public static final String KEY_ELECTRICITY = "electricity";
        public static final String KEY_INTERNET = "internet";
        public static final String KEY_CABLE = "cable";
        public static final String KEY_THERMOSTATE = "thermostate";

    }
    public static final class Users {
        public static final String TABLE_NAME = "users";
        public static final String KEY_ID = "id";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_PASSWORD = "password";
        public static final String KEY_NAME = "name";
        public static final String KEY_ADDRESS = "address";
        public static final String KEY_PHONE = "phone";
    }

}
