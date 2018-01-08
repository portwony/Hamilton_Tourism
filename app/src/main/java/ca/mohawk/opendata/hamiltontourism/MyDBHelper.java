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
                    "categoryId INTEGER NOT NULL," +
                    "name TEXT NOT NULL," +
                    "longitude FLOAT NULL," +
                    "latitude FLOAT NULL," +
                    "address TEXT NULL" +
                    "location TEXT NULL," +
                    "type TEXT NULL," +
                    "community TEXT NULL," +
                    "additionalInfo TEXT NULL," +
                    "FOREIGN KEY(categoryId) REFERENCES categories(_id))";

    public static final String SQL_CREATE_CATEGORIES =
            "CREATE TABLE "
                    + TABLE_CATEGORIES +
                    "(_id INTEGER PRIMARY KEY," +
                    "name TEXT NOT NULL)";


    public static final String SQL_DELETE_LOCATIONS =
            "DROP TABLE IF EXISTS " + TABLE_LOCATIONS;

    public static final String SQL_DELETE_CATEGORIES =
            "DROP TABLE IF EXISTS " + TABLE_CATEGORIES;



    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CATEGORIES);
        db.execSQL(SQL_CREATE_LOCATIONS);
        //db.execSQL("INSERT INTO categories(_id, name) VALUES(1, 'Restaurants')");
        //db.execSQL("INSERT INTO categories(_id, name) VALUES(2, 'Waterfalls')");
        //db.execSQL("INSERT INTO locations(_id, name, category) VALUES(1, 'wilburs falls', 2)");
        //db.execSQL("INSERT INTO locations(_id, name, category) VALUES(2, 'barbers falls', 2)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_LOCATIONS);
        db.execSQL(SQL_DELETE_CATEGORIES);

        // Create tables again
        onCreate(db);
    }




}
