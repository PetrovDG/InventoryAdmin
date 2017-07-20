package info.geostage.inventoryadmin.data;

/*
 * Created by Dimitar on 19.6.2017 г..
 */

import android.provider.BaseColumns;

/**
 * API Contract for the Inventory Admin app.
 */
public final class ItemContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ItemContract() {
    }

    /**
     * Inner class that defines constant values for the items database table.
     * Each entry in the table represents a single item.
     */
    public static final class ItemEntry implements BaseColumns {

        /**
         * Name of database table for items
         */
        public final static String TABLE_NAME = "items";

        /**
         * Unique ID number for the item (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Picture of the item.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_PICTURE = "imageURI";

        /**
         * Name of the item.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_NAME = "name";

        /**
         * Price of the item.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_ITEM_PRICE = "price";

        /**
         * Quantity of the items.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_ITEM_QUANTITY = "quantity";

        /**
         * Supplier name.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_SUPPLIER_NAME = "supp_name";

        /**
         * Supplier phone number.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_SUPPLIER_PHONE = "supp_phone";

        /**
         * Supplier e-mail.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_SUPPLIER_EMAIL = "supp_email";

    }
}
