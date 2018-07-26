package com.example.android.bookinventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

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

    private int mOnStock = BookEntry.ON_STOCK_UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);


        //Examine the intent in order to display the correct data
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_book));
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_book));
        }

        // Initialize a loader to read the book data from the database and display the current values in the editor
        getSupportLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);


        // find the Input Views in the editor.xml in order to read the user input
        mNameEditText = (EditText) findViewById(R.id.edit_book_name);
        mOnStockSpinner = (Spinner) findViewById(R.id.spinner_onStock);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);
        mSupplierNameEditText = (EditText) findViewById(R.id.edit_supplierName);
        mSupplierPhoneEditText = (EditText) findViewById(R.id.edit_supplierPhone);
        setupSpinner();
    }

    //set up the Spinner for "availability", so that the user can choose between different availability options
    private void setupSpinner() {
        //Create the adapter for the spinner
        ArrayAdapter onStockSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_onStock_options, android.R.layout.simple_spinner_item);
        //Specify drop-down layout
        onStockSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        //Apply the adapter to the spinner

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

    //get UserInput and save new Book in the dataBase
    private void insertBook() {
        //Read Input fields and trim white space
        String nameString = mNameEditText.getText().toString().trim();
        String supplierNameString = mSupplierNameEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);
        int price = Integer.parseInt(priceString);


        //Create ContentValues object: column names are the keys, book attributes (editor)= values
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookEntry.COLUMN_PRICE, price);
        values.put(BookEntry.COLUMN_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_ON_STOCK, mOnStock);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);

        // Insert a new book in the provider, returning its Uri.
        Uri newUri = getContentResolver().insert(BookEntry.Content_URI, values);
        //Show a toast message, indicating the result of the insertion
        //If rowID=-1, there was an error
        if (newUri == null) {
            Toast.makeText(this, getString(R.string.toast_editorInsertBookFailed), Toast.LENGTH_SHORT).show();
        } else {
            //otherwise insertion = successful, show toast with the rowID
            Toast.makeText(this, getString(R.string.toast_editorInsertBookSuccessful), Toast.LENGTH_SHORT).show();
        }
    }

    //Create on Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu options from the res/menu/menu_editor.xml
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                insertBook();
                finish();
                return true;
            case R.id.action_delete:
                return true;
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int onStockColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_ON_STOCK);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int onStock = cursor.getInt(onStockColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

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
}

