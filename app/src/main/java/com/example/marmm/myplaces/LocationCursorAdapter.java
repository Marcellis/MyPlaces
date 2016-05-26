package com.example.marmm.myplaces;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by marmm on 5/24/16.
 */
public class LocationCursorAdapter extends CursorAdapter {


    public LocationCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(
                R.layout.layout_listview_item, parent, false
        );
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String locationName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.LOCATION_NAME));
        String city = cursor.getString(cursor.getColumnIndex(DBOpenHelper.LOCATION_CITY));
        double latitude = cursor.getDouble(cursor.getColumnIndex(DBOpenHelper.LOCATION_LATITUDE));
        double longitude = cursor.getDouble(cursor.getColumnIndex(DBOpenHelper.LOCATION_LONGITUDE));

        TextView textName = (TextView) view.findViewById(R.id.txtName);
        TextView textCity = (TextView) view.findViewById(R.id.txtCity);
        TextView textLatitude = (TextView) view.findViewById(R.id.txtLatitude);
        TextView textLongitude = (TextView) view.findViewById(R.id.txtLongitude);

        textName.setText(locationName);
        textCity.setText(city);
        textLatitude.setText(String.format("%.2f", latitude));
        textLongitude.setText(String.format("%.2f", longitude));
    }
}
