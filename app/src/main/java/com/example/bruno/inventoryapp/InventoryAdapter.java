package com.example.bruno.inventoryapp;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.bruno.inventoryapp.data.InventoryContract;
import static com.example.bruno.inventoryapp.data.InventoryProvider.LOG_TAG;
/**
 * Created by Bruno on 1/9/2018.
 */
public class InventoryAdapter extends CursorAdapter {
    public InventoryAdapter(Context context, Cursor cursor){
        super(context, cursor, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = (TextView)view.findViewById(R.id.name);
        TextView priceTextView = (TextView)view.findViewById(R.id.price);
        TextView quantityTextView = (TextView)view.findViewById(R.id.quantity_sale);
        Button soldButton = (Button)view.findViewById(R.id.order);
        ImageView imageView = (ImageView)view.findViewById(R.id.item_image);
        int nameColumnIdex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_NAME);
        int priceColumnIdex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_PRICE);
        int quantityColumnIdex = cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_QUANTITY);
        String name = cursor.getString(nameColumnIdex);
        String price = "$"+ cursor.getInt(priceColumnIdex);
        final int quantity = cursor.getInt(quantityColumnIdex);
        nameTextView.setText(name);
        priceTextView.setText(price);
        quantityTextView.setText(Integer.toString(quantity));
        imageView.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(InventoryContract.InventoryEntry.COLUMN_IMAGE))));
        int id = cursor.getInt(cursor.getColumnIndex(InventoryContract.InventoryEntry._ID));
        final Uri currentUri = ContentUris.withAppendedId(InventoryContract.InventoryEntry.CONTENT_URI, id);
        soldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver contentResolver = view.getContext().getContentResolver();
                ContentValues values = new ContentValues();
                int sold = quantity;
                values.put(InventoryContract.InventoryEntry.COLUMN_QUANTITY, --sold);
                contentResolver.update(
                        currentUri,
                        values,
                        null,
                        null
                );
                context.getContentResolver().notifyChange(currentUri, null);
            }
        });
    }
}
