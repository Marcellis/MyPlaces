package com.example.marmm.myplaces;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class EditorActivity extends AppCompatActivity implements OnMapReadyCallback {

     private GoogleMap mMap;

    private EditText locationName;
    private String action;
    private String locationFilter;
    private String oldName;
    private String oldCity;
    private double oldLatitude;
    private double oldLongitude;
    private Marker marker;
    private LocationModel location;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_acitvity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEditor);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        locationName = (EditText) findViewById(R.id.locationName);

        Intent intent = getIntent();
        uri = intent.getParcelableExtra(LocationsProvider.CONTENT_ITEM_TYPE);
        populateMap();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);
         mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.remove();
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
            }
        });
// add new marker on long click
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                addMarker(latLng);
                // set the LatLng values each time a new marker is created
                location.setLatitude(marker.getPosition().latitude);
                location.setLongitude(marker.getPosition().longitude);
                location.setCity(showCityname(location.getLatitude(), location.getLongitude()));
            }
        });

        if (oldLatitude != 0.0 && oldLongitude != 0.0){
            // add marker and animate camera
            LatLng latLng = new LatLng(oldLatitude,oldLongitude);
            addMarker(latLng);
            CameraUpdate markerLocation = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mMap.animateCamera(markerLocation);
        } else {
            LatLng latLng = new LatLng(52.092876,5.104480);
            CameraUpdate markerLocation = CameraUpdateFactory.newLatLngZoom(latLng, 6);
            mMap.animateCamera(markerLocation);
        }
    }

    private String showCityname(double latitude, double longitude){
        String cityName = "*No City*";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
            cityName = addresses.get(0).getLocality();
        } catch (Exception e) {
            Toast.makeText(EditorActivity.this, "No city could be found", Toast.LENGTH_SHORT).show();
        } finally {
            return cityName;
        }
    }

    private void addMarker(LatLng latLng){
        mMap.clear();
        marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(locationName.getText().toString())
                .snippet("city: " + showCityname(latLng.latitude, latLng.longitude))
                .draggable(false)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT))
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        if (action.equals(Intent.ACTION_INSERT))
            getMenuInflater().inflate(R.menu.menu_insert, menu);
        return true;
    }

    private void deleteLocation() {
        getContentResolver().delete(LocationsProvider.CONTENT_URI, locationFilter, null);
        Toast.makeText(EditorActivity.this, "location deleted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
    private void updateLocation(LocationModel location) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.LOCATION_NAME, location.getName());
        values.put(DBOpenHelper.LOCATION_CITY, location.getCity());
        values.put(DBOpenHelper.LOCATION_LATITUDE, location.getLatitude());
        values.put(DBOpenHelper.LOCATION_LONGITUDE, location.getLongitude());
        getContentResolver().update(LocationsProvider.CONTENT_URI, values, locationFilter, null);
        Toast.makeText(EditorActivity.this, "location updated", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertLocation(LocationModel location) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.LOCATION_NAME, location.getName());
        values.put(DBOpenHelper.LOCATION_CITY, location.getCity());
        values.put(DBOpenHelper.LOCATION_LATITUDE, location.getLatitude());
        values.put(DBOpenHelper.LOCATION_LONGITUDE, location.getLongitude());
        getContentResolver().insert(LocationsProvider.CONTENT_URI, values);
        Toast.makeText(EditorActivity.this, "location added", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void finishEditing() {
        // get the string from the editText and set it in the object
        String name = locationName.getText().toString().trim();
        location.setName(name);

        switch (action) {
            case Intent.ACTION_INSERT:
                if (name.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertLocation(location);
                }
                break;
            case Intent.ACTION_EDIT:
                if (name.length() == 0) {
                    deleteLocation();
                } else if (oldName.equals(location.getName()) && (oldLatitude == location.getLatitude() || oldLongitude == location.getLongitude() )) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateLocation(location);
                }
        }
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_delete:
                deleteLocation();
                break;
            case R.id.action_done:
                finishEditing();
                break;
        }
        return true;
    }

    private void populateMap(){
        location = new LocationModel();
        // If the uri is NULL, the activity is setup empty and seen as a new location
        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle("New location");
        } // If the uri is not NULL, the activity is loaded with the data from the database
        else {
            setTitle("Edit location");
            action = Intent.ACTION_EDIT;
            locationFilter = DBOpenHelper.LOCATION_ID + "=" + uri.getLastPathSegment();
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COLUMNS, locationFilter, null, null);
            cursor.moveToFirst();

            // get the old values from the database
            oldName = cursor.getString(cursor.getColumnIndex(DBOpenHelper.LOCATION_NAME));
            oldCity = cursor.getString(cursor.getColumnIndex(DBOpenHelper.LOCATION_CITY));
            oldLatitude = cursor.getDouble(cursor.getColumnIndex(DBOpenHelper.LOCATION_LATITUDE));
            oldLongitude = cursor.getDouble(cursor.getColumnIndex(DBOpenHelper.LOCATION_LONGITUDE));

            // Set the old values in the view
            locationName.setText(oldName);
            // if Latlng is not set again, take the old values.
            location.setLongitude(oldLongitude);
            location.setLatitude(oldLatitude);
            location.setCity(oldCity);
        }
    }

}
