package com.example.android.bookinventory;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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

public class EditorActivity extends AppCompatActivity {
    //TextViews to enter Book Name, OnStock, Price, Quantity, Supplier Name, Supplier Phone Number
    private EditText mNameEditText;
    private Spinner mOnStockSpinner;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;

    private int mOnStock = BookEntry.ON_STOCK_UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
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
        //Show a toast message, indiating the result of the insertion
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
}

