package com.yogi.AndService;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IT-SOFT
 * Date: 6/5/14
 * Time: 12:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "LocationDB";


    // Books table name
    private static final String TABLE_LOCATIONS = "Location";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_CITY = "city";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_TGL = "tgl";

    private static final String[] COLUMNS = {KEY_ID,KEY_TGL,KEY_CITY,KEY_LONGITUDE,KEY_LATITUDE};

    private int id;
    private String Tgl;
    private String City;
    private String Longtitude;
    private String Latitude;

    public MySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_LOCATION_TABLE = "CREATE TABLE Location ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, tgl TEXT," +
                "city TEXT, "+
                "longitude TEXT,latitude TEXT )";

        // create books table
        db.execSQL(CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS Location");

        // create fresh books table
        this.onCreate(db);
    }

    //---------------------------------------------------------------------

    /**
     * CRUD operations (create "add", read "get", update, delete) book + get all books + delete all books
     */


    public void addBook(LocationModel myLocation){
        Log.d("addBook", myLocation.toString());
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TGL, myLocation.getTgl()); // get title
        values.put(KEY_CITY, myLocation.getCity()); // get title
        values.put(KEY_LONGITUDE, myLocation.getLongtitude()); // get title
        values.put(KEY_LATITUDE, myLocation.getLatitude()); // get title

        // 3. insert
        db.insert(TABLE_LOCATIONS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
    }

    public LocationModel getLocations(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_LOCATIONS, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build book object
        LocationModel locationModel = new LocationModel();
        //locationModel.setId(Integer.parseInt(cursor.getString(0)));
        locationModel.setTgl(cursor.getString(1));
        locationModel.setCity(cursor.getString(2));
        locationModel.setLongtitude(cursor.getString(3));
        locationModel.setLatitude(cursor.getString(4));

        Log.d("getBook(" + id + ")", locationModel.toString());

        // 5. return book
        return locationModel;
    }

    // Get All Books
    public List<LocationModel> getAllLocations() {
        List<LocationModel> books = new LinkedList<LocationModel>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_LOCATIONS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build book and add it to list
        LocationModel locationModel = null;
        if (cursor.moveToFirst()) {
            do {
                locationModel = new LocationModel();
                locationModel.setId(Integer.parseInt(cursor.getString(0)));
                locationModel.setTgl(cursor.getString(1));
                locationModel.setCity(cursor.getString(2));
                locationModel.setLongtitude(cursor.getString(3));
                locationModel.setLatitude(cursor.getString(4));

                // Add book to books
                books.add(locationModel);
            } while (cursor.moveToNext());
        }

        Log.d("getAllBooks()", books.toString());

        // return books
        return books;
    }

    // Updating single book
    public int updateBook(LocationModel book) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("tgl", book.getTgl()); // get title
        values.put("city", book.getCity()); // get author
        values.put("longitude", book.getLongtitude()); // get author
        values.put("latitude", book.getLatitude()); // get author

        // 3. updating row
        int i = db.update(TABLE_LOCATIONS, //table
                values, // column/value
                KEY_ID+" = ?", // selections
                new String[] { String.valueOf(book.getId()) }); //selection args

        // 4. close
        db.close();

        return i;

    }

    // Deleting single book
    public void deleteBook(LocationModel book) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_LOCATIONS,
                KEY_ID+" = ?",
                new String[] { String.valueOf(book.getId()) });

        // 3. close
        db.close();

        Log.d("deleteBook", book.toString());

    }

}
