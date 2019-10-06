package com.example.bruno.inventoryapp;
import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.bruno.inventoryapp.data.InventoryContract.InventoryEntry;
import android.widget.ImageView;
import android.widget.Toast;
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private static final int INVENTORY_LOADER= 0;
    private boolean mInventoryChange = false;
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQuantityText;
    private Button mSubtractOne;
    private Button mAddOne;
    private EditText mSupplier;
    private EditText mSupplierPhone;
    private EditText mSupplierEmail;
    private Button mImageAddButton;
    private ImageView mImageview;
    private Uri mPhotoUri;
    private Button mOrderMore;
    private Uri mCurrentUri;
    public static final int PHOTO_REQUEST = 1;
    public static final int REQUEST_READ_STORAGE = 2;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInventoryChange = true;
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
        mCurrentUri = intent.getData();
        mNameEditText = (EditText) findViewById(R.id.editor_name);
        mPriceEditText = (EditText) findViewById(R.id.editor_price);
        mQuantityText = (EditText) findViewById(R.id.quantity_counter);
        mSubtractOne = (Button) findViewById(R.id.quantity_lower);
        mAddOne = (Button) findViewById(R.id.quantity_add);
        mSupplier = (EditText)findViewById(R.id.supplier_name);;
        mSupplierPhone = (EditText)findViewById(R.id.supplier_phone);;
        mSupplierEmail = (EditText)findViewById(R.id.supplier_email);;
        mImageAddButton = (Button)findViewById(R.id.image_add);
        mOrderMore = (Button)findViewById(R.id.order_more);
        mImageview = (ImageView)findViewById(R.id.image_view);
        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityText.setOnTouchListener(mTouchListener);
        mSubtractOne.setOnTouchListener(mTouchListener);
        mAddOne.setOnTouchListener(mTouchListener);
        mSupplier.setOnTouchListener(mTouchListener);
        mSupplierPhone.setOnTouchListener(mTouchListener);
        mSupplierEmail.setOnTouchListener(mTouchListener);
        mImageAddButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                tryPhotoSelect();
            }
        });
        if(mCurrentUri== null){
            setTitle(getString(R.string.editor_title_add));
            invalidateOptionsMenu();
            mOrderMore.setVisibility(View.GONE);
        }else{
            setTitle(getString(R.string.editor_title_edit));
            mOrderMore.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
        }
        mSubtractOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtractOneToQuantity();
            }
        });
        mAddOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOneToQuantity();
            }
        });
        mOrderMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderMore();
            }
        });
    }
    private void orderMore(){
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ mSupplierPhone.getText().toString()));
        startActivity(phoneIntent);
        }
    private void subtractOneToQuantity(){
        String oldQuantityString = mQuantityText.getText().toString();
        int oldQuantity;
        if (oldQuantityString.isEmpty()) {
            return;
        } else if (oldQuantityString.equals("0")) {
            return;
        }else{
            oldQuantity = Integer.parseInt(oldQuantityString);

            mQuantityText.setText(String.valueOf(--oldQuantity));
        }
    }
    private void addOneToQuantity(){
        String oldQuantityString = mQuantityText.getText().toString();
        int oldQuantity;
        if (oldQuantityString.isEmpty()) {
            return;
        }else{
            oldQuantity = Integer.parseInt(oldQuantityString);
            mQuantityText.setText(String.valueOf(++oldQuantity));
        }
    }
    public void tryPhotoSelect(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);
        }
        getPhoto();
    }
    private void getPhoto(){
        Intent pickPhoto = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pickPhoto.addCategory(Intent.CATEGORY_OPENABLE);
        pickPhoto.setType("image/*");
        startActivityForResult(Intent.createChooser(pickPhoto, "Select Product"), PHOTO_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode== PHOTO_REQUEST && resultCode == RESULT_OK){
            if(data!=null){
                mPhotoUri = data.getData();
                mImageview.setImageURI(mPhotoUri);
                mImageview.invalidate();
            }
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection= {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY,
                InventoryEntry.COLUMN_SUPPLIER,
                InventoryEntry.COLUMN_SUPPLIER_PHONE,
                InventoryEntry.COLUMN_SUPPLIER_EMAIL,
                InventoryEntry.COLUMN_IMAGE};
        return new CursorLoader(this,
                mCurrentUri,
                projection,
                null,
                null,
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor.moveToFirst()){
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_PHONE);
            int supplierEmailColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_SUPPLIER_EMAIL);
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_IMAGE);
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);
            String supplierEmail = cursor.getString(supplierEmailColumnIndex);
            String currentPhoto = cursor.getString(imageColumnIndex);
            Uri imageUri = Uri.parse(currentPhoto);
            mImageview.setImageURI(imageUri);
            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            Log.e(LOG_TAG,"Send Help");
            mQuantityText.setText(Integer.toString(quantity));
            Log.e(LOG_TAG,"Send Help");
            mSupplier.setText(supplier);
            mSupplierPhone.setText(supplierPhone);
            mSupplierEmail.setText(supplierEmail);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityText.setText("");
        mSupplier.setText("");
        mSupplierPhone.setText("");
        mSupplierEmail.setText("");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_save:
                saveInventory();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.home:
                if(!mInventoryChange){
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };
                showUnsavedChangedDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void saveInventory(){
        String name = mNameEditText.getText().toString().trim();
        String price = mPriceEditText.getText().toString().trim();
        String quantity = mQuantityText.getText().toString().trim();
        String supplier = mSupplier.getText().toString().trim();
        String supplierPhone = mSupplierPhone.getText().toString().trim();
        String supplierEmail = mSupplierEmail.getText().toString().trim();
        if(TextUtils.isEmpty(name)
                ||TextUtils.isEmpty(price)
                ||TextUtils.isEmpty(quantity)
                ||TextUtils.isEmpty(supplier)
                ||TextUtils.isEmpty(supplierPhone)
                ||TextUtils.isEmpty(supplierEmail)
                ||mPhotoUri == null){
            Toast.makeText(this, "Missing fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String photoUri = mPhotoUri.toString();
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_NAME,name);
        values.put(InventoryEntry.COLUMN_PRICE,price);
        values.put(InventoryEntry.COLUMN_QUANTITY,quantity);
        values.put(InventoryEntry.COLUMN_SUPPLIER,supplier);
        values.put(InventoryEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);
        values.put(InventoryEntry.COLUMN_SUPPLIER_EMAIL,supplierEmail);
        values.put(InventoryEntry.COLUMN_IMAGE, photoUri);

        if(mCurrentUri == null){
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if(newUri== null){
                Toast.makeText(this, "Error saving inventory", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Inventory saved", Toast.LENGTH_SHORT).show();
            }
        }else{
            int rowsAffected = getContentResolver().update(mCurrentUri, values, null, null);
            if(rowsAffected == 0){
                Toast.makeText(this,"Error with updating inventory", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Inventory updated", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (!mInventoryChange) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangedDialog(discardButtonClickListener);
    }
    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog);
        builder.setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteInventory();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteInventory(){
        if(mCurrentUri != null){
            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);
            if(rowsDeleted == 0){
                Toast.makeText(this, getString(R.string.delete_inventory_failed), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, getString(R.string.delete_inventory_success), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
    private void showUnsavedChangedDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });
    }
}
