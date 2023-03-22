package com.example.cruddypizza_assign3;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.util.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class DBAdapter {
    //Constants
    public static final String KEY_ROWID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_SIZE = "size";
    public static final String KEY_TOP1 = "top1";
    public static final String KEY_TOP2 = "top2";
    public static final String KEY_TOP3 = "top3";
    public static final String KEY_DATE = "date";
    public static final String TAG = "DBAdapter";
    public static final String DATABASE_NAME = "OrderDB";
    public static final String DATABASE_TABLE = "orders";
    public static final int DATABASE_VERSION = 3;

    //Create statement
    private static String DATABASE_CREATE = "create table orders(_id integer primary key autoincrement,"
            + "name text not null, size text not null, top1 text not null,"
            + "top2 text not null, top3 text not null, date text not null);";

    private Context context = null;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    //Constructor
    public DBAdapter(Context context) {
        this.context = context;
        DBHelper = new DatabaseHelper(context);
    }//End Constructor

    //Database Helper
    public static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        //OnCreate
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }//End onCreate

        //OnUpgrade
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrade database from version " + oldVersion + " to " + newVersion
                    + " which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS orders");
            onCreate(db);
        }//End onUpgrade
    }//End Helper

    //Database Opener
    public DBAdapter open()throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }//End DB Opener

    //Database Closer
    public void close() {
        DBHelper.close();
    }//End DB Closer

    //Insert Order
    public long insertOrder(String name, String size, String top1, String top2, String top3) {
        //Create ContentValues object
        ContentValues insertValues = new ContentValues();

        //Get Date
        //Code referenced at: https://stackoverflow.com/questions/8654990/how-can-i-get-current-date-in-android
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String currDate = df.format(c);

        //Input values
        insertValues.put(KEY_NAME, name);
        insertValues.put(KEY_SIZE, size);
        insertValues.put(KEY_TOP1, top1);
        insertValues.put(KEY_TOP2, top2);
        insertValues.put(KEY_TOP3, top3);
        insertValues.put(KEY_DATE, currDate);

        return db.insert(DATABASE_TABLE, null, insertValues);
    }//End insertOrder

    //Update Order
    public boolean updateOrder(long rowId, String name, String size, String top1, String top2, String top3) {
        //Create ContentValues object with new data
        ContentValues newData = new ContentValues();
        //Input values
        newData.put(KEY_NAME, name);
        newData.put(KEY_SIZE, size);
        newData.put(KEY_TOP1, top1);
        newData.put(KEY_TOP2, top2);
        newData.put(KEY_TOP3, top3);

        return db.update(DATABASE_TABLE, newData, KEY_ROWID+"="+rowId, null) > 0;
    }//End updateOrder

    //Delete Order
    public boolean deleteOrder(long rowId) {
        return db.delete(DATABASE_TABLE, KEY_ROWID+"="+rowId, null) > 0;
    }//End deleteOrder

    //Retrieve single order
    public Cursor getOrder(long rowId)throws SQLException {
        //Create cursor object and query data
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{KEY_ROWID,
                KEY_NAME, KEY_SIZE, KEY_TOP1, KEY_TOP2, KEY_TOP3}, KEY_ROWID+"="+rowId,
                null, null, null, null,
                null, null);

        //Move cursor to first if not null
        if(mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }//End getOrder

    //Retrieve all orders
    public Cursor getAllOrders()throws SQLException {
        //Return cursor with all orders
        return db.query( DATABASE_TABLE, new String[]{KEY_ROWID,
                        KEY_NAME, KEY_SIZE, KEY_TOP1, KEY_TOP2, KEY_TOP3, KEY_DATE},
                null, null, null, null,
                "_id DESC");
    }//End getAllOrders
}//End DBAdapter
