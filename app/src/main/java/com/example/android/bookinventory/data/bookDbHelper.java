package com.example.android.bookinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookinventory.data.bookContract.BookEntry;

public class bookDbHelper extends SQLiteOpenHelper {
    public final String LOG_TAG = bookDbHelper.class.getSimpleName();
    //name of the database
    private static final String DATABASE_NAME = "inventory.db";

    //Database version
    private static final int DATABASE_VERSION = 1;

    //Constructor for new instance of bookDbHelper including the NAME & VERSION of the Database and factory null for creating default cursor,
    // set param: context of the app
    public bookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //called when database is created the 1st time
    @Override
    public void onCreate(SQLiteDatabase db) {
        //define String for the SQL statement, which creates the books table
        // for the class SQLiteDatabase, the SQLlite commands are used in combination with the contract constants. The SQlite database
        // knows how to create a SQLite dabase out of those combinations and creates a table with the constant name,
        // tell the database to create columns with the constant culumn names (defined in the contract previously) and define the datatype for its entries.
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + "("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_ON_STOCK + " INTEGER, "
                + BookEntry.COLUMN_PRICE + " DECIMAL NOT NULL, "
                + BookEntry.COLUMN_QUANTITY + " INTEGER DEFAULT 0, "
                + BookEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL);";


        //Execute SQL statement
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    //This method is called, when the database needs to be upgraded
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //The database is still at version1
    }
}





