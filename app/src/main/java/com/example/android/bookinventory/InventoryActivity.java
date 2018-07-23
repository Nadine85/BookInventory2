package com.example.android.bookinventory;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.example.android.bookinventory.data.bookContract.BookEntry;

public class InventoryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);


        //Button to open EditorActivity
        Button edit = (Button) findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }


    //Helper method to display information on the Screen about the state of the inventory database
    private void displayDatabaseInfo() {

        //define a projection that specifies the database columns which you will find in the query
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_ON_STOCK,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE};


        // Perform a query on the books table
        Cursor cursor = getContentResolver().query(
                BookEntry.Content_URI,   // The content_URI
                projection,            // The columns to return
                null,              // selection criteria
                null, //selection criteria
                null //sortOrder
                );
        TextView displayView = (TextView) findViewById(R.id.textView_book);

        try {
            //create the following header in the textView: number of rows in the cursor of the book table
            // id - name - on stock - price - quantity - supplier name - supplier phone
            // in the while loop, interate through the rows of the cursor, display info from each column
            displayView.setText("the books table contains" + cursor.getCount() + "books.\n\n");
            displayView.append(BookEntry._ID + "-" +
                    BookEntry.COLUMN_BOOK_NAME + "-" +
                    BookEntry.COLUMN_ON_STOCK + "-" +
                    BookEntry.COLUMN_PRICE + "-" +
                    BookEntry.COLUMN_QUANTITY + "-" +
                    BookEntry.COLUMN_SUPPLIER_NAME + "-" +
                    BookEntry.COLUMN_SUPPLIER_PHONE + "\n");
            //Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int onStockColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_ON_STOCK);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);
            //Iterate through all the cursor rows
            while (cursor.moveToNext()) {
                //use the index to extract String or Int or Decimal Value at the current row of the cursor
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentOnStock = cursor.getInt(onStockColumnIndex);
                float currentPrice = cursor.getFloat(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

                //display the column values of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + "-" +
                        currentName + "-" +
                        currentOnStock + "-" +
                        currentPrice + "-" +
                        currentQuantity + "-" +
                        currentSupplierName + "-" +
                        currentSupplierPhone + "-"));
            }
        } finally {
            //close the cursor when reading is done in order to make all resources of the cursor invalid.
            cursor.close();
        }
    }

    //Helper method to insert book data in the database (for debugging).
    private void insertBook() {
        //ContentValues object: columns are keys, and SuperBook Attributes= values
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, "SuperBook");
        values.put(BookEntry.COLUMN_ON_STOCK, BookEntry.ON_STOCK_TRUE);
        values.put(BookEntry.COLUMN_PRICE, 23.30);
        values.put(BookEntry.COLUMN_QUANTITY, 1000);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "SupeSupplier");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "+43/ 1764342302");
        //Insert new row for SuperBook into the provider by means of the ContentResolver
        Uri newUri= getContentResolver().insert(BookEntry.Content_URI, values);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate the menu options from the res/menu/menu_inventory.xml file
        getMenuInflater().inflate(R.menu.inventory_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertBook();
                displayDatabaseInfo();
                return true;
            case R.id.action_delete_all:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
