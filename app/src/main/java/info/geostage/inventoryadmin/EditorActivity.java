package info.geostage.inventoryadmin;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import info.geostage.inventoryadmin.data.ItemContract.ItemEntry;
import info.geostage.inventoryadmin.data.ItemDbHelper;
import info.geostage.inventoryadmin.data.ItemProvider;

public class EditorActivity extends AppCompatActivity {
    public static final String LOG_TAG = ItemDbHelper.class.getSimpleName();
    private ItemDbHelper dbHelper;
    Uri imageUri;
    TextView mPictureText;
    private ImageView mPictureView;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText supplierNameEditText;
    private EditText supplierPhoneEditText;
    private EditText supplierEmailEditText;
    private EditText orderEditText;
    ImageButton increaseQuantityBtn;
    ImageButton decreaseQuantityBtn;
    ImageButton increaseOrderBtn;
    ImageButton decreaseOrderBtn;
    Button orderByEmailBtn;
    Button orderByPhoneBtn;
    int quantityValue;
    int mOrder;
    long currentItemId;

    /**
     * Boolean flag that keeps track of whether the item has been edited (true) or not (false)
     */
    private boolean mItemHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mItemHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent mEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mPictureText = (TextView) findViewById(R.id.image_text);
        mPictureView = (ImageView) findViewById(R.id.image_view);
        mNameEditText = (EditText) findViewById(R.id.product_name_edit);
        mPriceEditText = (EditText) findViewById(R.id.price_edit);
        mQuantityEditText = (EditText) findViewById(R.id.quantity_edit);
        increaseQuantityBtn = (ImageButton) findViewById(R.id.increase_quantity);
        decreaseQuantityBtn = (ImageButton) findViewById(R.id.decrease_quantity);
        supplierNameEditText = (EditText) findViewById(R.id.supplier_name_edit);
        supplierPhoneEditText = (EditText) findViewById(R.id.supplier_phone_edit);
        supplierEmailEditText = (EditText) findViewById(R.id.supplier_email_edit);
        orderEditText = (EditText) findViewById(R.id.order_edit);
        increaseOrderBtn = (ImageButton) findViewById(R.id.increase_order);
        decreaseOrderBtn = (ImageButton) findViewById(R.id.decrease_order);
        orderByEmailBtn = (Button) findViewById(R.id.order_more_email);
        orderByPhoneBtn = (Button) findViewById(R.id.order_more_phone);

        dbHelper = new ItemDbHelper(this);
        currentItemId = getIntent().getLongExtra("itemId", 0);

        // If the intent DOES NOT contain an item content URI, then we know that we are
        // adding new item.
        if (currentItemId == 0) {
            // This is a new item, so change the app bar to say "Add new item"
            setTitle(getString(R.string.editor_activity_title_new_item));
            mPictureText.setText(getString(R.string.add_image_text));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete an item that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit Product"
            setTitle(getString(R.string.editor_activity_title_edit_item));
            mPictureText.setText(getString(R.string.change_image_text));
            addValuesToEditItem(currentItemId);
        }

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        supplierNameEditText.setOnTouchListener(mTouchListener);
        supplierPhoneEditText.setOnTouchListener(mTouchListener);
        supplierEmailEditText.setOnTouchListener(mTouchListener);
        orderEditText.setOnTouchListener(mTouchListener);
        increaseQuantityBtn.setOnTouchListener(mTouchListener);
        decreaseQuantityBtn.setOnTouchListener(mTouchListener);
        increaseOrderBtn.setOnTouchListener(mTouchListener);
        decreaseOrderBtn.setOnTouchListener(mTouchListener);
        orderByEmailBtn.setOnTouchListener(mTouchListener);
        orderByPhoneBtn.setOnTouchListener(mTouchListener);

        mPictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryOpenImgSelector();
                mItemHasChanged = true;
            }
        });

        orderByEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderByEmailFromSupplier();
            }
        });

        orderByPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderByPhoneFromSupplier();
            }
        });

        decreaseQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantityValue > 0) {
                    quantityValue--;
                    mQuantityEditText.setText(String.valueOf(quantityValue));
                    mItemHasChanged = true;
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.quantity_cant_be_below_zero), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        increaseQuantityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityValue++;
                mQuantityEditText.setText(String.valueOf(quantityValue));
                mItemHasChanged = true;
            }
        });

        increaseOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrder++;
                orderEditText.setText(String.valueOf(mOrder));
            }
        });

        decreaseOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOrder > 0) {
                    mOrder--;
                    orderEditText.setText(String.valueOf(mOrder));
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.order_cant_be_below_zero), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new item, hide the "Delete" menu item.
        if (currentItemId == 0) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save item to database
                if (!saveItem()) {
                    // saying to onOptionsItemSelected that user clicked button
                    return true;
                }
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog(currentItemId);
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the item hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get user input from editor and save item into database.
     */
    private boolean saveItem() {
        boolean isAllOk = true;
        if (!checkIfValueSet(mNameEditText, "name")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(mPriceEditText, "price")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(mQuantityEditText, "quantity")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(supplierNameEditText, "supplier name")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(supplierPhoneEditText, "supplier phone")) {
            isAllOk = false;
        }
        if (!checkIfValueSet(supplierEmailEditText, "supplier e-mail")) {
            isAllOk = false;
        }
        if (imageUri == null && currentItemId == 0) {
            isAllOk = false;
            mPictureText.setError("Missing image");
        }
        if (!isAllOk) {
            return false;
        }

        if (currentItemId == 0) {
            ItemProvider item = new ItemProvider(
                    mNameEditText.getText().toString().trim(),
                    Integer.parseInt(mPriceEditText.getText().toString().trim()),
                    Integer.parseInt(mQuantityEditText.getText().toString().trim()),
                    supplierNameEditText.getText().toString().trim(),
                    supplierPhoneEditText.getText().toString().trim(),
                    supplierEmailEditText.getText().toString().trim(),
                    imageUri.toString());
            dbHelper.insertItem(item);
        } else {
            int quantity = Integer.parseInt(mQuantityEditText.getText().toString().trim());
            dbHelper.updateItem(currentItemId, quantity);
        }
        return true;
    }

    private boolean checkIfValueSet(EditText text, String description) {
        if (TextUtils.isEmpty(text.getText())) {
            text.setError("Missing item " + description);
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }

    private void addValuesToEditItem(long itemId) {
        Cursor cursor = dbHelper.readItem(itemId);
        cursor.moveToFirst();
        mNameEditText.setText(cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME)));
        mPriceEditText.setText(cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE)));
        mQuantityEditText.setText(cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY)));
        supplierNameEditText.setText(cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPPLIER_NAME)));
        supplierPhoneEditText.setText(cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPPLIER_PHONE)));
        supplierEmailEditText.setText(cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_SUPPLIER_EMAIL)));
        mPictureView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PICTURE))));
        mNameEditText.setEnabled(false);
        mPriceEditText.setEnabled(false);
        mQuantityEditText.setEnabled(true);
        supplierNameEditText.setEnabled(false);
        supplierPhoneEditText.setEnabled(false);
        supplierEmailEditText.setEnabled(false);
        mPictureView.setEnabled(true);
    }

    /**
     * Perform the deletion of the item in the database.
     */
    private void deleteItem(long itemId) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = ItemEntry._ID + "=?";
        String[] selectionArgs = {String.valueOf(itemId)};
        int rowsDeleted = database.delete(
                ItemEntry.TABLE_NAME, selection, selectionArgs);

        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                    Toast.LENGTH_SHORT).show();
        }
        // Close the activity
        finish();
    }

    /**
     * Prompt the user to confirm that they want to delete this item.
     */
    private void showDeleteConfirmationDialog(final long itemId) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deleteItem(itemId);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void tryOpenImgSelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
        openSelector();
    }

    private void openSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType(getString(R.string.intentType));
        startActivityForResult(Intent.createChooser(intent, getString(R.string.selectPicture)), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openSelector();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                mPictureView.setImageURI(imageUri);
                mPictureView.invalidate();
            }
        }
    }

    /**
     * Called when the user taps the "Order by E-mail" button
     */
    private void orderByEmailFromSupplier() {

        String orderFromSupplier = orderEditText.getText().toString();
        String supplierName = supplierNameEditText.getText().toString();
        String itemName = mNameEditText.getText().toString();

        if (TextUtils.isEmpty(orderFromSupplier) || orderFromSupplier == "") {
            Toast.makeText(getApplicationContext(), getString(R.string.declare_items_number_to_order), Toast.LENGTH_SHORT).show();
            return;
        } else if (Integer.parseInt(orderFromSupplier) <= 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.declare_grater_number), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:" + supplierEmailEditText.getText().toString().trim())); // only email apps should handle this
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.new_order));
        String message = getString(R.string.hello) + " " + supplierName + getString(R.string.exclamation_mark) + " " + getString(R.string.request) + " " + orderFromSupplier + " " + getString(R.string.units_of) + " " + itemName + getString(R.string.point);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        startActivity(intent);
    }

    /**
     * Called when the user taps the "Order by phone" button
     */
    private void orderByPhoneFromSupplier() {

        String orderFromSupplier = orderEditText.getText().toString();

        if (TextUtils.isEmpty(orderFromSupplier) || orderFromSupplier == "") {
            Toast.makeText(getApplicationContext(), getString(R.string.declare_items_number_to_order), Toast.LENGTH_SHORT).show();
            return;
        } else if (Integer.parseInt(orderFromSupplier) <= 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.declare_grater_number), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + supplierPhoneEditText.getText().toString().trim()));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

}
