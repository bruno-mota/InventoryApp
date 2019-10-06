package com.example.bruno.inventoryapp.data;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import com.example.bruno.inventoryapp.data.InventoryContract.InventoryEntry;
/**
 * Created by Bruno on 1/9/2018.
 */
public class InventoryProvider extends ContentProvider {
    private static final int INVENTORY = 100;
    private static final int INVENTORY_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();
    private InventoryDBHelper mDBHelper;
    @Override
    public boolean onCreate() {
        mDBHelper = new InventoryDBHelper(getContext());
        return true;
    }
    @Override
    public Cursor query(Uri uri,  String[] projection,  String selection,  String[] selectionArgs,  String sortOrder) {
        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown Uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }
    @Override
    public String getType( Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + "with match" + match);
        }
    }
    @Override
    public Uri insert( Uri uri,  ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                return insertInventory(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    private Uri insertInventory(Uri uri, ContentValues values){
        String name = values.getAsString(InventoryEntry.COLUMN_NAME);
        Integer price = values.getAsInteger(InventoryEntry.COLUMN_PRICE);
        Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_QUANTITY);
        String supplier = values.getAsString(InventoryEntry.COLUMN_SUPPLIER);
        Long supplier_phone = values.getAsLong(InventoryEntry.COLUMN_SUPPLIER_PHONE);
        String supplier_email = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
        String image = values.getAsString(InventoryEntry.COLUMN_IMAGE);
        if(name == null){
            throw new IllegalArgumentException("Inventory item requires a name");
        }
        if(price == null){
            throw new IllegalArgumentException("Inventory item requires a price");
        }
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Quantity must be above 0");
        }
        if(supplier == null){
            throw new IllegalArgumentException("Supplier requires a name.");
        }
        Log.e(LOG_TAG, "get save method");
        if(supplier_phone == null){
            throw new IllegalArgumentException("Need a phone number for the supplier");
        }
        Log.e(LOG_TAG, "get save method");
        if(supplier_email == null){
            throw new IllegalArgumentException("Need an email for the supplier.");
        }
        if(image == null){
            throw new IllegalArgumentException("Need an image");
        }
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        long id = db.insert(InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }
    @Override
    public int delete( Uri uri,  String selection,  String[] selectionArgs) {
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        int rowsDeleted;
        final int match= sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case INVENTORY_ID:
                selection= InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }
    @Override
    public int update( Uri uri,  ContentValues contentValues,  String selection,  String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case INVENTORY:
                return updateInventory(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateInventory(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }
        if (values.containsKey(InventoryEntry.COLUMN_NAME)) {
            String name = values.getAsString(InventoryEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_PRICE)){
            Integer price = values.getAsInteger(InventoryEntry.COLUMN_PRICE);
            if(price == null){
                throw new IllegalArgumentException("Inventory item requires a price");
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_QUANTITY)) {
            Integer quantity = values.getAsInteger(InventoryEntry.COLUMN_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Quantity must be above 0");
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_SUPPLIER)) {
            String supplier = values.getAsString(InventoryEntry.COLUMN_SUPPLIER);
            if(supplier == null){
                throw new IllegalArgumentException("Supplier requires a name.");
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_SUPPLIER_PHONE)) {
            Integer supplier_phone = values.getAsInteger(InventoryEntry.COLUMN_SUPPLIER_PHONE);
            if(supplier_phone == null){
                throw new IllegalArgumentException("Need a phone number for the supplier");
            }
            if(supplier_phone < 10){
                throw new IllegalArgumentException("Need an appropriate number for the supplier");
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_SUPPLIER_EMAIL)) {
            String supplier_email = values.getAsString(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
            if(supplier_email == null){
                throw new IllegalArgumentException("Need an email for the supplier.");
            }
        }
        if (values.containsKey(InventoryEntry.COLUMN_IMAGE)) {
            String image = values.getAsString(InventoryEntry.COLUMN_IMAGE);
            if(image == null){
                throw new IllegalArgumentException("Need an image");
            }
        }
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        int rowsUpdated = database .update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}























