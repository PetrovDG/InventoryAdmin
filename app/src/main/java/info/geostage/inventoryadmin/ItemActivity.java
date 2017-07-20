package info.geostage.inventoryadmin;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import info.geostage.inventoryadmin.data.ItemContract.ItemEntry;
import info.geostage.inventoryadmin.data.ItemDbHelper;
import info.geostage.inventoryadmin.data.ItemProvider;

public class ItemActivity extends AppCompatActivity {

    public static final String LOG_TAG = ItemActivity.class.getSimpleName();

    /**
     * Adapter for the ListView
     */
    ItemCursorAdapter mCursorAdapter;

    View emptyView;
    ItemDbHelper dbHelper;
    Cursor cursor;
    int lastVisibleItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        dbHelper = new ItemDbHelper(this);

        // Setup FAB to open EditorActivity
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the item data
        ListView itemListView = (ListView) findViewById(R.id.list_view);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        emptyView = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyView);

        cursor = dbHelper.readProduct();

        // Setup an Adapter to create a list item for each row of items data in the Cursor.
        // There is no items data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new ItemCursorAdapter(this, cursor);
        itemListView.setAdapter(mCursorAdapter);

        // Setup the item scroll listener
        itemListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) return;
                final int currentFirstVisibleItem = view.getFirstVisiblePosition();
                if (currentFirstVisibleItem > lastVisibleItem) {
                    fab.show();
                } else if (currentFirstVisibleItem < lastVisibleItem) {
                    fab.hide();
                }
                lastVisibleItem = currentFirstVisibleItem;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCursorAdapter.swapCursor(dbHelper.readProduct());
    }

    public void clickOnViewItem(long id) {
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra("itemId", id);
        startActivity(intent);
    }

    public void clickOnSale(long itemId, int itemQuantity) {
        dbHelper.sellOneItem(itemId, itemQuantity);
        mCursorAdapter.swapCursor(dbHelper.readProduct());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_item.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllItems();
                finish();
                startActivity(getIntent());
                return true;
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                addDummyItem();
                mCursorAdapter.swapCursor(dbHelper.readProduct());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to insert hardcoded item data into the database. For debugging purposes only.
     */
    private void addDummyItem() {
        ItemProvider pixel = new ItemProvider(
                "Google Pixel Phone",
                750,
                7,
                "Dimitar Petrov",
                "+359 2 22 22 22",
                "petrovdg@gmail.com",
                "android.resource://info.geostage.inventoryadmin/drawable/pixel");

        dbHelper.insertItem(pixel);

    }

    /**
     * Helper method to delete all item in the database.
     */
    private int deleteAllItems() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(ItemEntry.TABLE_NAME, null, null);
    }
}
