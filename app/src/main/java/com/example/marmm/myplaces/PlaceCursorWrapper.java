package com.example.marmm.myplaces;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * Created by boydhogerheijde on 01/12/2016.
 */

public class PlaceCursorWrapper extends CursorWrapper {

    public PlaceCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Place getPlace() {
        String name = getString(getColumnIndex(PlacesOpenHelper.NAME));
        String city = getString(getColumnIndex(PlacesOpenHelper.CITY));
        double latitude = getDouble(getColumnIndex(PlacesOpenHelper.LATITUDE));
        double longitude = getDouble(getColumnIndex(PlacesOpenHelper.LONGITUDE));

        Place place = new Place();
        place.setName(name);
        place.setCity(city);
        place.setLatitude(latitude);
        place.setLongitude(longitude);

        return place;
    }

}
