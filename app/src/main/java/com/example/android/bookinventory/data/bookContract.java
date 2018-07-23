package com.example.android.bookinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

//API class that defines the constants and column names of the database, which the BookInventory uses
public final class bookContract {

    //empty Constructor which prevents to accidentally start the Contract
    private bookContract() {
    }

    //Content Uri for managing the data
//Variable for Content_Authority
    public static final String CONTENT_AUTHORITY = "com.example.android.books";
    //Schema: definition & parsing
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //define path_table name
    public static final String PATH_BOOKS = "books";


    //Inner class: defines constant values for the books database table
    public static final class BookEntry implements BaseColumns {
        //whole Uri
        public static final Uri Content_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);
        //name of the database table for books
        public final static String TABLE_NAME = "books";
        //Unique ID number for the book, serves as identifier in the database
        public final static String _ID = BaseColumns._ID;
        //column for the book name
        public final static String COLUMN_BOOK_NAME = "Product_Name";
        //column for the price
        public final static String COLUMN_PRICE = "Price";
        //column for the Quantity
        public final static String COLUMN_QUANTITY = "Quantity";
        //column for the Supplier Name
        public final static String COLUMN_SUPPLIER_NAME = "Supplier_Name";
        //column for the Supplier Phone Number
        public final static String COLUMN_SUPPLIER_PHONE = "Supplier_Phone_Number";
        //column for product on stock at supplier, which indicates the availability of the product
        public final static String COLUMN_ON_STOCK = "On_Stock";
        //possible values for product on stock
        public final static int ON_STOCK_UNKNOWN = 0;
        public final static int ON_STOCK_TRUE = 1;
        public final static int ON_STOCK_FALSE = 2;


        //validate that there is an entry for onStock. otherwise return false
        public static boolean isValidOnStock(int onStock) {
            if (onStock == ON_STOCK_UNKNOWN || onStock == ON_STOCK_TRUE || onStock == ON_STOCK_FALSE) {
                return true;
            }
            return false;
        }

        // MIME type of the Content_URI for a list of books
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        //MIME type of the Content_URI for a single book
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

    }
}




