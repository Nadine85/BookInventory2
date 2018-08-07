package com.example.android.bookinventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import static com.example.android.bookinventory.data.bookContract.BookEntry;

public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;
    bookCursorAdapter mCursorAdapter;
    //Content URI for the existing book (null if it's a new book)
    private Uri mCurrentBookUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);


        //Button to open EditorActivity
        Button add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                //launch editorActivity to display book
                startActivity(intent);

            }
        });
        //find the listview in order to fill it with the data  of the books table
        ListView listView = findViewById(R.id.listView_book);
        //find the emptyView in order to display it, when there is no data
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);


        //set up ItemClickListener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Create intent to navigate to EditorAvtivity
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                //create uri that indicates the book which was clicked on in the list
                Uri mCurrentBookUri = ContentUris.withAppendedId(BookEntry.Content_URI, id);
                //set uri on the data field
                intent.setData(mCurrentBookUri);
                //launch editorActivity to display book
                startActivity(intent);
            }
        });

        //set up CursorAdpater and attach it to the listivew
        mCursorAdapter = new bookCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);


        //initialize Loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }


    //Helper method to insert book data in the database (for debugging).
    private void insertBook() {
        //ContentValues object: columns are keys, and SuperBook Attributes= values
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, "SuperBook");
        values.put(BookEntry.COLUMN_ON_STOCK, BookEntry.ON_STOCK_TRUE);
        values.put(BookEntry.COLUMN_PRICE, 23.30);
        values.put(BookEntry.COLUMN_QUANTITY, 1000);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "SuperSupplier");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "+43/ 1764342302");
        //Insert new row for SuperBook into the provider by means of the ContentResolver
        Uri newUri = getContentResolver().insert(BookEntry.Content_URI, values);
    }

    //Helper method to delete all books in the database.

    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.Content_URI, null, null);
        Log.v("InventoryActivity", rowsDeleted + " rows deleted from book database");
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
                return true;
            case R.id.action_delete_all:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //define a projection that specifies the database columns which you will find in the query
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY};


        // Perform a query on the books table
        return new CursorLoader(this,
                BookEntry.Content_URI,   // The content_URI
                projection,            // The columns to return
                null,              // selection criteria
                null, //selection criteria
                null); //sortOrder
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);


    }


    //method for decreasing the quantity by 1, applied in the bookCursorAdapter
    public void saleProduct(long id, int quantity) {

        // Decrement item quantity
        if (quantity >= 1) {
            quantity--;
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
                Toast.makeText(this, R.string.sale_successful, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.sale_failed, Toast.LENGTH_SHORT).show();
            }

        } else {
            //  Out of stock
            Toast.makeText(this, R.string.sale_outOfStock, Toast.LENGTH_LONG).show();
        }
    }
}
