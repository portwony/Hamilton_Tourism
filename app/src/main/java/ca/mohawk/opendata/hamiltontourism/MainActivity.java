package ca.mohawk.opendata.hamiltontourism;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public MyDBHelper dbHelper = new MyDBHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        if (pref.getBoolean("hasDatabase", false) == false) {

            boolean hasDatabase = downloadDatabase();

            if (hasDatabase) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("hasDatabase", true);
                editor.apply();
            } else {
                Toast.makeText(this, "There was a problem downloading the database. Please try again.", Toast.LENGTH_LONG).show();
            }
        }


        final ListView lv1 = (ListView) findViewById(R.id.categoriesList1);
        //final ListView lv2 = (ListView) findViewById(R.id.categoriesList2);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = { "_id, name" };
        String sortOrder = null;

        String selection;
        selection = null;
        String[] selectionArgs = null;

        Cursor mycursor = db.query("categories", projection, selection, selectionArgs, null, null, sortOrder);


        String fromColumns[] = {"_id", "name"};
        int toViews[] = {R.id.id, R.id.categoryTextView};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.category_list_item, mycursor, fromColumns, toViews, 0);


        lv1.setAdapter(adapter);

        lv1.setClickable(true);
       lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {

                //pass necessary values to intent. values in IF statement are only used to pass back to main
                Intent intent = new Intent(view.getContext(), LocationActivity.class);
                startActivity(intent);

            }


        });




    }



    private boolean downloadDatabase() {


        //TODO: set up checks for internet/connection to web service
        //categories
        DataAsyncTaskCategories dlCategories = new DataAsyncTaskCategories(this);

        String uri = "https://hamiltontourismapi.azurewebsites.net/categories";
        dlCategories.execute(uri);

        //locations
        DataAsyncTask dlLocations = new DataAsyncTask(this);

        String uriCat = "https://hamiltontourismapi.azurewebsites.net/locations";
        dlLocations.execute(uriCat);

        return true;

    }


}
