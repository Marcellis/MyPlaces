package com.example.marmm.myplaces;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by marmm on 5/24/16.
 */
public class PlacesOpenHelper extends SQLiteOpenHelper {

    // Database info
    public static final String DATABASE_NAME = "locations.db";
    public static final int DATABASE_VERSION = 1; // The version number must be incremented each time a change to DB structure occurs.

    // Database columns
    public static final String TABLE_LOCATIONS = "locations";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String CITY = "city";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    // All columns
    public static final String[] ALL_COLUMNS = new String[]{ID, NAME, CITY, LATITUDE, LONGITUDE};

    //SQL statement to create database
    private static final String CREATE_STATEMENT =
            "CREATE TABLE " + TABLE_LOCATIONS + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME + " TEXT, "
            + CITY + " TEXT, "
            + LATITUDE + " REAL, "
            + LONGITUDE + " REAL);";

    public PlacesOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Destroy old database:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        // Recreate new database:
        onCreate(db);
    }
}
