package com.example.marmm.myplaces;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by marmm on 5/24/16.
 */
public class PlacesProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.marmm.myplaces";
    private static final String BASE_PATH = "locations";
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int LOCATIONS = 1;
    private static final int LOCATION_ID = 2;

    public static final String CONTENT_ITEM_TYPE = "Locations";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    static {
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH, LOCATIONS);
        URI_MATCHER.addURI(AUTHORITY, BASE_PATH + "/#", LOCATION_ID);
    }

    private SQLiteDatabase mDatabase;

    @Override
    public boolean onCreate() {
        PlacesOpenHelper helper = new PlacesOpenHelper(getContext());
        mDatabase = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (URI_MATCHER.match(uri) == LOCATION_ID) {
            selection = PlacesOpenHelper.ID + "=?" + uri.getLastPathSegment();
        }

        // The data is filtered in the UI so the 'selection' argument is passed with it
        return mDatabase.query(PlacesOpenHelper.TABLE_LOCATIONS, PlacesOpenHelper.ALL_COLUMNS, selection, null, null, null, null);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long id = mDatabase.insert(PlacesOpenHelper.TABLE_LOCATIONS, null, values);

        //Create the URI to pass back that includes the new primary key value.
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return mDatabase.delete(PlacesOpenHelper.TABLE_LOCATIONS, selection, selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return mDatabase.update(PlacesOpenHelper.TABLE_LOCATIONS, values, selection, selectionArgs);
    }
}
