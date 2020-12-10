package com.example.hawker_customer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "hawker_db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_CUSTOMERS = "users";
    private static final String KEY_UID = "uid";
    private static final String KEY_STORE_ID = "store_id";
    private static final String KEY_STORE_NAME = "store_name";
    private static final String KEY_TABLE_NO = "table_no";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LONG = "long";

    public DatabaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE " + TABLE_CUSTOMERS + "(" +
                KEY_UID + " TEXT PRIMARY KEY," +
                KEY_STORE_ID + " TEXT," +
                KEY_STORE_NAME + " TEXT," +
                KEY_TABLE_NO + " TEXT," +
                KEY_LAT + " REAL," +
                KEY_LONG + " REAL" +
                ")";

        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS " + TABLE_CUSTOMERS;
        sqLiteDatabase.execSQL(sql);

        onCreate(sqLiteDatabase);
    }

    public void setCustomer(Customer customer) {
        SQLiteDatabase db = getWritableDatabase();

        deleteCustomers();

        ContentValues values = new ContentValues();
        values.put(KEY_UID, customer.getUid());
        values.put(KEY_STORE_ID, customer.getStoreId());
        values.put(KEY_STORE_NAME, customer.getStoreName());
        values.put(KEY_LAT, customer.getLatitude());
        values.put(KEY_LONG, customer.getLongitude());
        values.put(KEY_TABLE_NO, customer.getTableNo());

        db.insert(TABLE_CUSTOMERS, null, values);
    }

    public Customer getCustomer(String uid) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + TABLE_CUSTOMERS + " WHERE " + KEY_UID + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { uid });

        Customer customer = null;
        if (cursor.moveToFirst()) {
            customer = new Customer(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getDouble(4),
                    cursor.getDouble(5)
            );
        }

        return customer;
    }

    public boolean deleteCustomers() {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(TABLE_CUSTOMERS, null, null) > 0;
    }
}
