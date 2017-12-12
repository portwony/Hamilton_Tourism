package ca.mohawk.opendata.hamiltontourism;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class MyDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "OpenData.db";
    public static final String TABLE_LOCATIONS = "locations";
    public static final String SQL_CREATE =
            "CREATE TABLE "
                    + TABLE_LOCATIONS +
                    " (_id INTEGER PRIMARY KEY," +
                    "name TEXT," +
                    "longitude float NULL," +
                    "latitude float NULL," +
                    "location TEXT NULL," +
                    "type TEXT NULL," +
                    "community TEXT NULL," +
                    "optional INTEGER," +
                    "hours INTEGER )";

    public static final String SQL_DELETE =
            "DROP TABLE IF EXISTS " + TABLE_LOCATIONS;



    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE);

        // Create tables again
        onCreate(db);
    }




}
