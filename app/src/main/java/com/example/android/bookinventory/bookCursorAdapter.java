package com.example.android.bookinventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookinventory.data.bookContract;
//Adapter for a list, that uses a Cursor of Book data as its data source. The adapter knwos how to create list items for each row

public class bookCursorAdapter extends CursorAdapter {


    //constructs a new Adapter
    public bookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }


    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Inflate a list item view using the list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    //method, which binds the book data to the given list item layout.
    public void bindView(final View view, final Context context, Cursor cursor) {
        //find the TextViews which will be modified in the list_item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.priceSummary);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantitySummary);


        //find the Columns which should be displayed
        int nameColumnIndex = cursor.getColumnIndex(bookContract.BookEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(bookContract.BookEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(bookContract.BookEntry.COLUMN_QUANTITY);


        //read the Input from the Columns
        final long id = cursor.getInt(cursor.getColumnIndexOrThrow(bookContract.BookEntry._ID));
        String name = cursor.getString(nameColumnIndex);
        final int quantitySummary = cursor.getInt(quantityColumnIndex);
        final String mquantitySummary = Integer.toString(quantitySummary);
        final float price = cursor.getFloat(priceColumnIndex);
        final String mprice = Float.toString(price);

        //method, which handles on click event on the SaleButton (list_item) and adds 1 (defined in the inventory Activity)

        final Button saleButton = (Button) view.findViewById(R.id.saleButton);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InventoryActivity mainActivity = (InventoryActivity) context;
                mainActivity.saleProduct(id, quantitySummary);

            }
        });

        //update TextViews
        nameTextView.setText(name);
        priceTextView.setText(mprice);
        quantityTextView.setText(mquantitySummary);
    }
}









