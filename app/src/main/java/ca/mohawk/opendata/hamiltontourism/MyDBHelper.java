package ca.mohawk.opendata.hamiltontourism;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class MyDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "OpenData.db";
    public static final String TABLE_LOCATIONS = "locations";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String SQL_CREATE_LOCATIONS =
            "CREATE TABLE "
                    + TABLE_LOCATIONS +
                    " (_id INTEGER PRIMARY KEY," +
                    "name TEXT," +
                    "FOREIGN KEY(category_id) REFERENCES categories(_id)," +
                    "longitude float NULL," +
                    "latitude float NULL," +
                    "location TEXT NULL," +
                    "type TEXT NULL," +
                    "community TEXT NULL," +
                    "additional_details TEXT)";

    public static final String SQL_CREATE_CATEGORIES =
            "CREATE TABLE "
                    + TABLE_CATEGORIES +
                    "(_id INTEGER PRIMARY KEY," +
                    "name TEXT)";


    public static final String SQL_DELETE_LOCATIONS =
            "DROP TABLE IF EXISTS " + TABLE_LOCATIONS;

    public static final String SQL_DELETE_CATEGORIES =
            "DROP TABLE IF EXISTS " + TABLE_LOCATIONS;



    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CATEGORIES);
        db.execSQL(SQL_CREATE_LOCATIONS);
        db.execSQL("INSERT INTO categories VALUES(1, 'Restaurants')");
        db.execSQL("INSERT INTO categories VALUES(2, 'Waterfalls')");
        db.execSQL("INSERT INTO locations VALUES(1, 'wilburs falls', 2, NULL, NULL, NULL, NULL, NULL, NULL)");
        db.execSQL("INSERT INTO locations VALUES(2, 'barbers falls', 2, NULL, NULL, NULL, NULL, NULL, NULL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_LOCATIONS);
        db.execSQL(SQL_DELETE_CATEGORIES);

        // Create tables again
        onCreate(db);
    }




}
