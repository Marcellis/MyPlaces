package com.example.marmm.myplaces;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PlaceListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PLACE_DETAIL_REQUEST_CODE = 1234;
    private PlaceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_list);

        setToolbar();

        adapter = new PlaceAdapter(null);
        RecyclerView placeRecycler = (RecyclerView) findViewById(R.id.recycler_view_places);
        placeRecycler.setLayoutManager(new LinearLayoutManager(this));
        placeRecycler.setHasFixedSize(true);
        placeRecycler.setAdapter(adapter);
        placeRecycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_place);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaceListActivity.this, PlaceDetailActivity.class);
                startActivityForResult(intent, PLACE_DETAIL_REQUEST_CODE);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_place_list);
        setSupportActionBar(toolbar);
        toolbar.setTitle("My Places");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_DETAIL_REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader();
        }
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, PlacesProvider.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Take the data represented by the cursor object, and pass it to the cursor adaptor
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_place_list, menu);
        return true;
    }

    private void deleteAllPlaces() {
        getContentResolver().delete(PlacesProvider.CONTENT_URI, null, null);
        restartLoader();

        // Notifying user that all items are deleted
        Toast.makeText(PlaceListActivity.this, "All items deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete_all) {
            showDeleteAllMessage();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteAllMessage() {
        new AlertDialog.Builder(this)
                .setTitle("Delete all places")
                .setMessage("Are you sure you want to delete all of your places?")
                .setIcon(R.drawable.ic_warn)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllPlaces();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder> {

        // The cursor which will provide our data.
        private PlaceCursorWrapper mCursor;

        // A reference to the column, used to get the ID of an item.
        private int mIdColumn;

        public PlaceAdapter(Cursor cursor) {
            swapCursor(cursor);
        }

        @Override
        public PlaceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View placeItemView = inflater.inflate(R.layout.list_item_place, parent, false);
            return new PlaceHolder(placeItemView);
        }

        @Override
        public void onBindViewHolder(PlaceHolder holder, int position) {
            // When the cursor can't move to given position, it will crash and burn.
            if (!mCursor.moveToPosition(position)) {
                throw new IllegalStateException();
            }

            // This is where our CursorWrapper comes into play, fetching our Place.
            Place place = mCursor.getPlace();
            holder.mNameTextView.setText(place.getName());
            holder.mCityTextView.setText(place.getCity());
            holder.mLatitudeTextView.setText(String.valueOf(place.getLatitude()));
            holder.mLongitudeTextView.setText(String.valueOf(place.getLongitude()));

            // Getting the item id to pass it to PlaceDetailActivity.
            // final modifier is necessary for the variable to be used
            // in the anonymous class implementation of the OnClickListener.
            final long itemId = getItemId(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PlaceListActivity.this, PlaceDetailActivity.class);
                    Uri uri = Uri.parse(PlacesProvider.CONTENT_URI + "/" + itemId);
                    intent.putExtra(PlacesProvider.CONTENT_ITEM_TYPE, uri);
                    startActivityForResult(intent, PLACE_DETAIL_REQUEST_CODE);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (mCursor == null) {
                return 0;
            }

            return mCursor.getCount();
        }

        @Override
        public long getItemId(int position) {
            if (mCursor == null || !mCursor.moveToPosition(position)) {
                return 0;
            }

            return mCursor.getLong(mIdColumn);
        }

        // This method is derived from CursorAdapter's implementation.
        public void swapCursor(Cursor cursor) {
            // If the given cursor isn't null, we can use it to fetch Place objects.
            // Otherwise we have to notify the adapter that there is no cursor and thus no data.
            if (cursor != null) {
                mCursor = new PlaceCursorWrapper(cursor);
                mIdColumn = cursor.getColumnIndexOrThrow(PlacesOpenHelper.ID);
                notifyDataSetChanged();
            } else {
                mCursor = null;
                mIdColumn = -1;
                notifyItemRangeRemoved(0, getItemCount());
            }
        }

        class PlaceHolder extends RecyclerView.ViewHolder {

            TextView mNameTextView;
            TextView mCityTextView;
            TextView mLatitudeTextView;
            TextView mLongitudeTextView;

            public PlaceHolder(View itemView) {
                super(itemView);
                mNameTextView = (TextView) itemView.findViewById(R.id.text_view_name);
                mCityTextView = (TextView) itemView.findViewById(R.id.text_view_city);
                mLatitudeTextView = (TextView) itemView.findViewById(R.id.text_view_latitude);
                mLongitudeTextView = (TextView) itemView.findViewById(R.id.text_view_longitude);
            }
        }
    }
}
