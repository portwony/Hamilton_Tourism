package ca.mohawk.opendata.hamiltontourism;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LocationActivity extends AppCompatActivity {

    public MyDBHelper dbHelper = new MyDBHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);


        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int id = getIntent().getIntExtra("id", 0);


        String[] projection = {"_id, category"};
        String selection = " _id = ? ";
        String[] selectionargs = {Integer.toString(id)};
        Cursor infoCursor = db.query("categories",projection, selection, selectionargs, null, null,null,null);


        String category = "";

        if (infoCursor.moveToFirst()){
            while(!infoCursor.isAfterLast()){
                category = infoCursor.getString(infoCursor.getColumnIndex("category"));

                infoCursor.moveToNext();
            }
        }
        infoCursor.close();
    }
}
