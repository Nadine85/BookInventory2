package com.example.android.bookinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static com.example.android.bookinventory.data.bookContract.BookEntry;
import static com.example.android.bookinventory.data.bookContract.CONTENT_AUTHORITY;
import static com.example.android.bookinventory.data.bookContract.PATH_BOOKS;

public class bookProvider extends ContentProvider {

    //LOG for LOGMessages
    public final static String LOG_TAG = bookProvider.class.getSimpleName();
    //DatabaseVersion

    //Helper Object for initializing the database
    private bookDbHelper mDbHelper;

    //Uri matcher for the books table
    private static final int BOOKS = 100;

    //Uri matcher for a single book in the table
    private static final int BOOK_ID = 101;

    //Uri matcher for Uri object; input "no match" is passed into the constructor = code to return for the root Uri
   private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //matcher that identifies the whole book table by means of the tables URI Path
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS,BOOKS);
        //matcher that identifies a single book by means of the book URI Path
        sUriMatcher.addURI(CONTENT_AUTHORITY,PATH_BOOKS + "/#", BOOK_ID);
    }

    //Initialize ContentProvider with Helper Object
    @Override
    public boolean onCreate() {
        mDbHelper = new bookDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // Cursor: holds the query result
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Query the books table directly for the Books code. The cursor
                // could contain multiple rows of the books table.
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI.
                               selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Query the books table with the given _ID and return a cursor containing that row of the table.
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;

    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case BOOKS:
                return insertBook(uri,contentValues);
               default:throw new IllegalArgumentException("Insert is not supported for "+ uri);
        }
    }
    //define method insertBook, in order to get the user input and insert it in the database
    private Uri insertBook(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(BookEntry.COLUMN_BOOK_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Book requires a name");
        }

        // Check that onStock is valid
        Integer onStock = values.getAsInteger(BookEntry.COLUMN_ON_STOCK);
        if (onStock == null || !BookEntry.isValidOnStock(onStock)) {
            throw new IllegalArgumentException("Book requires valid availability");
        }

        // Check that the price is provided (!= null) and is greater than 0
        float price = values.getAsFloat(BookEntry.COLUMN_PRICE);
        if (price <= 0) {
            throw new IllegalArgumentException("Book requires valid price");
        }
        // Check that the quantity is greater than or equal 0
        Integer quantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
        if (quantity < 0) {
            throw new IllegalArgumentException("Book requires valid quantity");
        }

       //Check that the Supplier name is provided
        String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
        if(supplierName == null) {
            throw new IllegalArgumentException("Book requires a supplier name");
        }

        //Check that the Supplier phone is provided
        String supplerPhone = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE);
        if(supplerPhone == null){
            throw new IllegalArgumentException("Book requires a supplier phone number");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new book with the given values
        long id = database.insert(BookEntry.TABLE_NAME, null, values);
        // If the ID is -1, the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        return ContentUris.withAppendedId(uri, id);
    }


    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                // For the Book_ID code, extract out the ID from the URI, in order to know, know which row to update.
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments
     * Return the number of rows that were successfully updated.
     */
    private int updateBook (Uri uri, ContentValues values, String selection, String[] selectionArgs) {
            // If the COLUMN_BOOK_NAME key is present, check that the name value is not null.
            if (values.containsKey(BookEntry.COLUMN_BOOK_NAME)) {
                String name = values.getAsString(BookEntry.COLUMN_BOOK_NAME);
                if (name == null) {
                    throw new IllegalArgumentException("Book requires a name");
                }
            }

            // If the onStock key is present,check that the onStock value is valid.
            if (values.containsKey(BookEntry.COLUMN_ON_STOCK)) {
                Integer onStock = values.getAsInteger(BookEntry.COLUMN_ON_STOCK);
                if (onStock == null || !BookEntry.isValidOnStock(onStock)) {
                    throw new IllegalArgumentException("Book requires valid availability");
                }
            }

            // If the COLUMN_Price key is present,check that the value is valid.
            if (values.containsKey(BookEntry.COLUMN_PRICE)) {
                // Check that the price is greater than or equal to 0
                Float price = values.getAsFloat(BookEntry.COLUMN_PRICE);
                if (price <= 0) {
                    throw new IllegalArgumentException("Book requires valid price");
                }
            }
            // If the COLUMN_Quantity key is present,check that the value is valid.
        if (values.containsKey(BookEntry.COLUMN_QUANTITY)) {
            // Check that the quantity is greater than or equal 0
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_QUANTITY);
            if (quantity < 0) {
                throw new IllegalArgumentException("Book requires valid quantity");
            }
        }
        // If the COLUMN_Supplier_NAME key is present, check that the value is not null.
        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Book requires a supplier name");
            }
        }

        // If the COLUMN_Supplier_PHONE key is present, check that the value is not null.
        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_PHONE)) {
            String supplierPhone = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE);
            if (supplierPhone == null) {
                throw new IllegalArgumentException("Book requires a supplier phone name");
            }
        }

            // If there are no values to update, don't update the database
            if (values.size() == 0) {
                return 0;
            }

            // Otherwise, get writeable database to update the data
            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            // Returns the number of database rows affected by the update statement
            return database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);
        }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                return database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
