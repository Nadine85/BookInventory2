package com.example.android.bookinventory;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.bookinventory.data.bookContract;

import static android.widget.Toast.LENGTH_LONG;
import static com.example.android.bookinventory.data.bookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the book data loader */
    private static final int EXISTING_BOOK_LOADER = 0;

    //TextViews to enter Book Name, OnStock, Price, Quantity, Supplier Name, Supplier Phone Number
    private EditText mNameEditText;
    private Spinner mOnStockSpinner;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;
    //Content URI for the existing book (null if it's a new book)
    private Uri mCurrentBookUri;
    private int quantity;


    private int mOnStock = BookEntry.ON_STOCK_UNKNOWN;
    private boolean mBookHasChanged = false;

    //OnTouchListener which listens to UserInputs on the UI, under the implication, that the user is modifying the views and mBookHasChanged is set tu true
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        //Examine the intent in order to display the correct data
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.add));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_book));
            // Initialize a loader to read the book data from the database and display the current values in the editor
            getSupportLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }


        // find the Input Views in the editor.xml in order to read the user input
        mNameEditText = (EditText) findViewById(R.id.edit_book_name);
        mOnStockSpinner = (Spinner) findViewById(R.id.spinner_onStock);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplierName);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.edit_supplierPhone);
        mNameEditText.setOnTouchListener(mOnTouchListener);
        mOnStockSpinner.setOnTouchListener(mOnTouchListener);
        mPriceEditText.setOnTouchListener(mOnTouchListener);
        mQuantityEditText.setOnTouchListener(mOnTouchListener);
        mSupplierNameEditText.setOnTouchListener(mOnTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mOnTouchListener);
        setupSpinner();

    }

    //set up the Spinner for "availability", so that the user can choose between different availability options
    private void setupSpinner() {
        //Create the adapter for the spinner
        ArrayAdapter onStockSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_onStock_options, android.R.layout.simple_spinner_item);
        //Specify drop-down layout
        onStockSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mOnStockSpinner.setAdapter(onStockSpinnerAdapter);
        mOnStockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.onStock_false))) {
                        mOnStock = BookEntry.ON_STOCK_FALSE;
                    } else if (selection.equals(getString(R.string.onStock_true))) {
                        mOnStock = BookEntry.ON_STOCK_TRUE;
                    } else {
                        mOnStock = BookEntry.ON_STOCK_UNKNOWN;
                    }
                }
            }

            // define onNothingSelected for the abstract AdapterClass
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mOnStock = BookEntry.ON_STOCK_UNKNOWN;
            }
        });
    }

    //get UserInput and save Book in the dataBase
    private void saveBook() {
        //Read Input fields and trim white space
        String nameString = mNameEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();


        //return if all fields are empty and throw a hint to enter book details for saving
        if (mCurrentBookUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(getApplicationContext(), getString(R.string.add_empty_fields_hint), Toast.LENGTH_LONG).show();
            return;
        }



        //data validation for a new item (when clicking the add button):when user inputs wrong or empty product information, instead of erroring out,
        //add a Toast that prompts the user to fill all empty fields before saving the item.

        if (mCurrentBookUri == null && TextUtils.isEmpty((nameString))) {
          Toast.makeText(getApplicationContext(), getString(R.string.add_empty_fields_hint), Toast.LENGTH_LONG).show();
          return;
       }
        if (mCurrentBookUri == null && TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(getApplicationContext(), getString(R.string.add_empty_fields_hint), Toast.LENGTH_LONG).show();
            return;
        }
        if (mCurrentBookUri == null && TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(getApplicationContext(), getString(R.string.add_empty_fields_hint), Toast.LENGTH_LONG).show();
            return;
        }
        if (mCurrentBookUri == null && TextUtils.isEmpty(priceString)) {
            Toast.makeText(getApplicationContext(), getString(R.string.add_empty_fields_hint), Toast.LENGTH_LONG).show();
            return;
        }

        //data validation for an already existing item in the database: when user inputs wrong or empty product information, instead of erroring out,
        //add a Toast that prompts the user to input the correct information before saving the item.

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(getApplicationContext(), getString(R.string.update_empty_name_hint), Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(getApplicationContext(), getString(R.string.update_empty_price_hint), Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(getApplicationContext(),getString(R.string.update_empty_supplier_hint), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(getApplicationContext(), getString(R.string.update_empty_supplier_phone_hint), Toast.LENGTH_LONG).show();
            return;
        }

        /*if (TextUtils.isEmpty(nameString) && TextUtils.isEmpty(supplierPhoneString) && TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(priceString) ) {
            Toast.makeText(getApplicationContext(), getString(R.string.update_empty_name_price_suppliername_phone_hint), Toast.LENGTH_LONG).show();
            return;
        }*/
        /*if (TextUtils.isEmpty(nameString)&& TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(getApplicationContext(), getString(R.string.update_empty_name_supplierphone_hint), Toast.LENGTH_LONG).show();
            return;
        }

        /*if (TextUtils.isEmpty(nameString)&& TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(getApplicationContext(), getString(R.string.update_empty_name_suppliername_hint), Toast.LENGTH_LONG).show();
            return;
        }
       /* if (TextUtils.isEmpty(nameString)&& TextUtils.isEmpty(priceString)) {
            Toast.makeText(getApplicationContext(), "unable to update book: please enter a valid book name, price", Toast.LENGTH_LONG).show();
            return;
        }

        /*if (TextUtils.isEmpty(nameString)&& TextUtils.isEmpty(supplierPhoneString) && TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(getApplicationContext(), "unable to update book: please enter a valid book name, supplier phone, supplier name", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(nameString)&& TextUtils.isEmpty(supplierPhoneString)&& TextUtils.isEmpty(priceString)) {
            Toast.makeText(getApplicationContext(),"unable to update book: please enter a valid book name, supplier phone, price", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(nameString)&& TextUtils.isEmpty(supplierNameString)&& TextUtils.isEmpty(priceString)) {
            Toast.makeText(getApplicationContext(), "unable to update book: please enter a valid book name, supplier name, price", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(supplierNameString)&& TextUtils.isEmpty(supplierPhoneString) ) {
            Toast.makeText(getApplicationContext(), "unable to update book: please enter a valid supplier name, supplier phone", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierPhoneString)) {
            Toast.makeText(getApplicationContext(), "unable to update book: please enter a valid supplier name, supplier phone", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(priceString)) {
            Toast.makeText(getApplicationContext(), "unable to update book: please enter a valid supplier name, price", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(supplierNameString) && TextUtils.isEmpty(supplierPhoneString) && TextUtils.isEmpty(priceString)) {
            Toast.makeText(getApplicationContext(), "unable to update book: please enter a valid supplier name, supplier phone, price", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(supplierPhoneString) && TextUtils.isEmpty(priceString)) {
            Toast.makeText(getApplicationContext(), "unable to update book: please enter a valid price, supplier phone", Toast.LENGTH_LONG).show();
            return;
        }*/



        //set price == 0, if there is an entry, parse to Float and put it into the price Column for the given Uri


        float price =0;
        if (!TextUtils.isEmpty(priceString)) {
            //just parse Float, if the number is valid

            try {
                price = Float.parseFloat(priceString);
                //catch invalid numbers and show a toast if the user entered an invalid number
            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.invalid_price_number), Toast.LENGTH_LONG).show();
                return;
            }
        }

        //set quantity == 0, if there is an entry, parse to Integer and put it into the quantity Column for the given Uri
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            //just parse int, if the number is valid
            try {
                quantity = Integer.parseInt(quantityString);
                //catch invalid numbers and show a toast if the user entered an invalid number
            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.invalid_quantity_number), Toast.LENGTH_LONG).show();
                return;
            }
        }

//Create ContentValues object: column names are the keys, book attributes (editor)= values
        //if (!TextUtils.isEmpty(nameString) && !TextUtils.isEmpty(supplierNameString) && !TextUtils.isEmpty(supplierPhoneString)
               // && !TextUtils.isEmpty(priceString)) {
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
            values.put(BookEntry.COLUMN_ON_STOCK, mOnStock);
            values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
            values.put(BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);
            values.put(BookEntry.COLUMN_PRICE, price);
            values.put(BookEntry.COLUMN_QUANTITY, quantity);

            // determine whether it is a new or an existing uri
            if (mCurrentBookUri == null) {
                Uri newUri = getContentResolver().insert(BookEntry.Content_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.toast_editorInsertBookFailed), Toast.LENGTH_SHORT).show();
                } else {
                    //otherwise insertion = successful, show toast with the rowID
                    Toast.makeText(this, getString(R.string.toast_editorInsertBookSuccessful), Toast.LENGTH_SHORT).show();
                }
            } else {
                // Otherwise, it is an existing book, so update the book with content URI: mCurrentBookUri and pass in the new ContentValues.
                // //Pass in null for the selection and selection args (because mCurrentBookUri identifies the correct to be modified row in the database
                int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

                // toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, getString(R.string.editor_update_book_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful; display a toast.
                    Toast.makeText(this, getString(R.string.editor_update_book_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }

       // }

        //do nothing
        return;
    }


    // Create on Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu options from the res/menu/menu_editor.xml
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new book, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveBook();
                finish();
                return true;
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            //Hook up up button, set dialog, if user leaves without saving changes, asking the user to discard or keep editing
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                    // if there are unsaved changes, setup a dialog to warn the user.
                    // Create a click listener to handle the user confirming that
                    // changes should be discarded.
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);

                            }
                        };// Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }
        // if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_ON_STOCK,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri, // Query the content URI for the current book
                projection, //Columns to include
                null, //No selection clause
                null, //No selection arguments
                null); //Default sort order
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Fail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (=only row in the cursor)
        if (cursor.moveToFirst()) {

            // Find the columns of book attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int onStockColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_ON_STOCK);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            final long id = cursor.getInt(cursor.getColumnIndexOrThrow(bookContract.BookEntry._ID));
            String name = cursor.getString(nameColumnIndex);
            int onStock = cursor.getInt(onStockColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            final String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mPriceEditText.setText(Float.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(supplierPhone);
            // onStock is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 2 is false, 1 is True.
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (onStock) {
                case BookEntry.ON_STOCK_FALSE:
                    mOnStockSpinner.setSelection(2);
                    break;
                case BookEntry.ON_STOCK_TRUE:
                    mOnStockSpinner.setSelection(1);
                    break;
                default:
                    mOnStockSpinner.setSelection(0);
                    break;
            }

            final Button decrementButton = (Button) findViewById(R.id.decrement_button);
            decrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    decrement(id, quantity);

                }

            });

            final Button incrementButton = (Button) findViewById(R.id.increment_button);
            incrementButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    increment(id, quantity);

                }

            });
            final Button callButton = (Button) findViewById(R.id.call_button);
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callSupplier(supplierPhone);

                }

            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
        mSupplierPhoneEditText.setText("");
        mSupplierNameEditText.setText("");
        mOnStockSpinner.setSelection(0); // Select "Unknown" onStock

    }


    //Show a dialog that warns the user there are unsaved changes that will be lost
    //if they continue leaving the editor.
    //discardButtonClickListener = click listener for what to do when
    // the user confirms they want to discard their changes

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Prompt the user a confirm to delete this book.

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked "Cancel": dismiss the dialog
                // and continue editing the book.
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
     * Delete the book in the database.
     */
    private void deleteBook() {
        // Only delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            // Show a toast message depending on whether or not deleting was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, deleting was successful,and a toast is displayed.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }


    //method for decreasing the quantity by 1, applied in the editorActivity
    public void decrement(long id, int quantity) {
        if (quantity >= 1) {
            quantity--;
            Uri updateUri = ContentUris.withAppendedId(BookEntry.Content_URI, id);
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_QUANTITY, quantity);
            int rowsUpdated = getContentResolver().update(
                    updateUri,
                    values,
                    null,
                    null);
            if (rowsUpdated == 1) {
                Toast.makeText(this, R.string.decrement_successful, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.decrement_failed, Toast.LENGTH_SHORT).show();
            }
        } else {
            //  Out of stock
            Toast.makeText(this, R.string.sale_outOfStock, Toast.LENGTH_LONG).show();
        }

    }

    //method for calling the supplier, applied in the
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    public void callSupplier(String supplierPhone) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + supplierPhone));
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, R.string.call_dialog_msg, LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        } else {
            // Permission has already been granted
            startActivity(intent);
        }
    }


    //method for increasing the quantity by 1, applied in the editorActivity
    public void increment(long id, int quantity) {
        quantity++;
        // Construct new uri and content values
        Uri updateUri = ContentUris.withAppendedId(BookEntry.Content_URI, id);
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_QUANTITY, quantity);
        int rowsUpdated = getContentResolver().update(
                updateUri,
                values,
                null,
                null);
        if (rowsUpdated == 1) {
            Toast.makeText(this, R.string.increment_successful, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.increment_failed, Toast.LENGTH_SHORT).show();
        }
    }
}


