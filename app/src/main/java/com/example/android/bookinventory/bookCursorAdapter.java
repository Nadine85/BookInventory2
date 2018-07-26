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

public class bookCursorAdapter extends CursorAdapter{

    //constructs a new Adapter
    public bookCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
    }
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        // Inflate a list item view using the list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

        //method, which binds the book data to the given list item layout.
    public void bindView(View view, Context context, Cursor cursor){
        //find the TextViews which will be modified in the list_item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        Button saleButton = (Button) view.findViewById(R.id.saleButton);

        //find the Columns which should be displayed
        int nameColumnIndex = cursor.getColumnIndex(bookContract.BookEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(bookContract.BookEntry.COLUMN_PRICE);

        //read the Input from the Columns
        String name = cursor.getString(nameColumnIndex);
        float price = cursor.getFloat(priceColumnIndex);
        String mprice =Float.toString(price);

        //update TextViews
        nameTextView.setText(name);
        summaryTextView.setText(mprice);
        }
}




