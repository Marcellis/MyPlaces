package com.example.marmm.myplaces;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by marmm on 5/24/16.
 */
public class LocationsProvider extends ContentProvider {


    private static final String AUTHORITY = "com.example.marmm.myplaces"; // Change to correct package
    private static final String BASE_PATH = "locations";

    // uniform resource identifier that identifies the content provider.
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    private static final int LOCATIONS = 1;
    private static final int LOCATION_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "Locations";

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, LOCATIONS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", LOCATION_ID);
    }


    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) == LOCATION_ID)
            selection = DBOpenHelper.LOCATION_ID + "=" + uri.getLastPathSegment();
        // Returns all columns.
        // The data is filtered in the UI so the 'selection' argument is passed with it
        return database.query(DBOpenHelper.TABLE_LOCATIONS, DBOpenHelper.ALL_COLUMNS, selection, null, null, null, null);
    }




    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(DBOpenHelper.TABLE_LOCATIONS, null, values);
        //Create the URI to pass back that includes the new primary key value.
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE_LOCATIONS, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_LOCATIONS, values, selection, selectionArgs);
    }
}
