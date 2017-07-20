package info.geostage.inventoryadmin;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.ImageView;

import info.geostage.inventoryadmin.data.ItemContract.ItemEntry;

/*
 * Created by Dimitar on 19.6.2017 г..
 */

/**
 * {@link ItemCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of items data as its data source. This adapter knows
 * how to create list items for each row of items data in the {@link Cursor}.
 */
public class ItemCursorAdapter extends CursorAdapter {
    private ItemActivity activity = new ItemActivity();

    protected ImageView itemImage;
    protected TextView nameTextView;
    protected TextView priceTextView;
    protected TextView quantityTextView;
    protected ImageButton sellBtn;

    /**
     * Constructs a new {@link ItemCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public ItemCursorAdapter(ItemActivity context, Cursor c) {
        super(context, c, 0);
        this.activity = context;
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
    }

    /**
     * This method binds the item data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current item can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        itemImage = (ImageView) view.findViewById(R.id.image);
        nameTextView = (TextView) view.findViewById(R.id.item_name);
        priceTextView = (TextView) view.findViewById(R.id.price);
        quantityTextView = (TextView) view.findViewById(R.id.quantity);
        sellBtn = (ImageButton) view.findViewById(R.id.sell_button);

        // Find the columns of item attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);

        // Read the product attributes from the Cursor for the current item
        String itemName = cursor.getString(nameColumnIndex);
        int itemPrice = cursor.getInt(priceColumnIndex);
        final int itemQuantity = cursor.getInt(quantityColumnIndex);

        itemImage.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PICTURE))));

        // Update the TextViews with the attributes for the current item
        nameTextView.setText(itemName);
        priceTextView.setText(String.valueOf(itemPrice));
        quantityTextView.setText(String.valueOf(itemQuantity));

        final long itemId = cursor.getLong(cursor.getColumnIndex(ItemEntry._ID));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.clickOnViewItem(itemId);
            }
        });

        sellBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.clickOnSale(itemId, itemQuantity);
            }
        });
    }
}

