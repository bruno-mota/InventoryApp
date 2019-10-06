package com.example.bruno.inventoryapp.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.bruno.inventoryapp.data.InventoryContract.InventoryEntry;
/**
 * Created by Bruno on 1/9/2018.
 */
public class InventoryDBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = InventoryDBHelper.class.getSimpleName();
    public static final String DATABASE_NAME = "warehouse.db";
    private static final int DATABASE_VERSION = 1;
    public InventoryDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_INVENTORY_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + " ("
                + InventoryEntry._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryEntry.COLUMN_NAME +" TEXT NOT NULL, "
                + InventoryEntry.COLUMN_PRICE+" INTEGER NOT NULL, "
                + InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + InventoryEntry.COLUMN_SUPPLIER + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL, "
                + InventoryEntry.COLUMN_IMAGE + " TEXT NOT NULL);";
        db.execSQL(SQL_CREATE_INVENTORY_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
