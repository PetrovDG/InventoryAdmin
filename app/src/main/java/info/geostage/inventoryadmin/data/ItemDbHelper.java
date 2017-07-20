package info.geostage.inventoryadmin.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import info.geostage.inventoryadmin.data.ItemContract.ItemEntry;

/*
 * Created by Dimitar on 19.6.2017 г..
 */

public class ItemDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ItemDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "inventoryadmin.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link ItemDbHelper}.
     *
     * @param context of the app
     */
    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        // Create a String that contains the SQL statement to create the items table
        String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE "
                + ItemEntry.TABLE_NAME + " ("
                + ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemEntry.COLUMN_ITEM_PICTURE + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ItemEntry.COLUMN_ITEM_SUPPLIER_NAME + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE + " TEXT NOT NULL, "
                + ItemEntry.COLUMN_ITEM_SUPPLIER_EMAIL + " TEXT NOT NULL);";

        // Execute the SQL statement
        database.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Insert a item into the database with the given content values.
     */
    public void insertItem(ItemProvider item) {
        // Get writable database
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_NAME, item.getItemName());
        values.put(ItemEntry.COLUMN_ITEM_PRICE, item.getItemPrice());
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, item.getItemQuantity());
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_NAME, item.getSupplierName());
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE, item.getSupplierPhone());
        values.put(ItemEntry.COLUMN_ITEM_SUPPLIER_EMAIL, item.getSupplierEmail());
        values.put(ItemEntry.COLUMN_ITEM_PICTURE, item.getPicture());
        // Insert the new item with the given values
        long id = database.insert(ItemEntry.TABLE_NAME, null, values);
    }

    public Cursor readProduct() {
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_PRICE,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_SUPPLIER_NAME,
                ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE,
                ItemEntry.COLUMN_ITEM_SUPPLIER_EMAIL,
                ItemEntry.COLUMN_ITEM_PICTURE
        };
        Cursor cursor = db.query(
                ItemEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    public Cursor readItem(long itemId) {
        SQLiteDatabase database = getReadableDatabase();
        String[] projection = {
                ItemEntry._ID,
                ItemEntry.COLUMN_ITEM_NAME,
                ItemEntry.COLUMN_ITEM_PRICE,
                ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemEntry.COLUMN_ITEM_SUPPLIER_NAME,
                ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE,
                ItemEntry.COLUMN_ITEM_SUPPLIER_EMAIL,
                ItemEntry.COLUMN_ITEM_PICTURE
        };

        String selection = ItemEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(itemId)};

        Cursor cursor = database.query(
                ItemEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        return cursor;
    }

    public void updateItem(long currentItemId, int itemQuantity) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, itemQuantity);
        String selection = ItemEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(currentItemId)};
        database.update(ItemEntry.TABLE_NAME,
                values, selection, selectionArgs);
    }

    public void sellOneItem(long itemId, int itemQuantity) {
        SQLiteDatabase database = getWritableDatabase();
        int newQuantity = 0;
        if (itemQuantity > 0) {
            newQuantity = itemQuantity - 1;
        }

        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_QUANTITY, newQuantity);
        String selection = ItemEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(itemId)};
        database.update(ItemEntry.TABLE_NAME,
                values, selection, selectionArgs);
    }

}

