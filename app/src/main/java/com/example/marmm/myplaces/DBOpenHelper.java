package com.example.marmm.myplaces;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by marmm on 5/24/16.
 */
public class DBOpenHelper extends SQLiteOpenHelper {


    // Database info
    public static final String DATABASE_NAME = "locations.db";
    public static final int DATABASE_VERSION = 1; // The version number must be incremented each time a change to DB structure occurs.

    // Database columns
    public static final String TABLE_LOCATIONS = "locations";
    public static final String LOCATION_ID = "_id";
    public static final String LOCATION_NAME = "name";
    public static final String LOCATION_CITY= "city";
    public static final String LOCATION_LATITUDE = "latitude";
    public static final String LOCATION_LONGITUDE = "longitude";

    // All columns
    public static final String[] ALL_COLUMNS = new String[]{LOCATION_ID, LOCATION_NAME, LOCATION_CITY, LOCATION_LATITUDE, LOCATION_LONGITUDE};

    //SQL statement to create database
    private static final String DATABASE_CREATE_SQL =
            "CREATE TABLE " + TABLE_LOCATIONS
                    + " (" + LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + LOCATION_NAME + " TEXT, "
                    + LOCATION_CITY + " TEXT, "
                    + LOCATION_LATITUDE + " REAL, "
                    + LOCATION_LONGITUDE + " REAL "
                    + ");";


    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Destroy old database:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        // Recreate new database:
        onCreate(db);

    }
}
