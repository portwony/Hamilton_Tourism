package ca.mohawk.opendata.hamiltontourism;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public MyDBHelper dbHelper = new MyDBHelper(this);
//    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
 //   private String locationProvider = LocationManager.NETWORK_PROVIDER;

    //TODO: remove set variables
    public double userLatitude = 43.23987705;
    public double userLongitude = -79.87474567;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: figure out how to get users location properly



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

        populateListOne();
        populateListTwo();


    }



    private void populateListTwo() {

        final ListView lv2 = (ListView) findViewById(R.id.categoriesList2);
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        String[] projection2 = { "_id, name" };
        String sortOrder2 = null;

        String selection2;
        selection2 = "(_id % 2) = 0";
        String[] selectionArgs2 = null;


        Cursor mycursor2 = db.query("categories", projection2, selection2, selectionArgs2, null, null, sortOrder2);


        String fromColumns2[] = {"_id", "name"};
        int toViews2[] = {R.id.idTextView, R.id.categoryTextView};

        SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(this, R.layout.category_list_item, mycursor2, fromColumns2, toViews2, 0);


        lv2.setAdapter(adapter2);

        lv2.setClickable(true);
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {


                int categoryId = (position*2) + 2;

                //pass necessary values to intent. values in IF statement are only used to pass back to main
                Intent intent = new Intent(view.getContext(), LocationActivity.class);
                intent.putExtra("categoryId", categoryId);
                intent.putExtra("userLatitude", userLatitude);
                intent.putExtra("userLongitude", userLongitude);
                startActivity(intent);

            }


        });

    }

    private void populateListOne() {

        final ListView lv1 = (ListView) findViewById(R.id.categoriesList1);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = { "_id, name"};
        String sortOrder = null;

        String selection;
        selection = "(_id % 2) = 1";
        String[] selectionArgs = null;

        Cursor mycursor = db.query("categories", projection, selection, selectionArgs, null, null, sortOrder);


        String fromColumns[] = {"_id", "name"};
        int toViews[] = {R.id.idTextView, R.id.categoryTextView};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.category_list_item, mycursor, fromColumns, toViews, 0);


        lv1.setAdapter(adapter);

        lv1.setClickable(true);
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {

                //pass necessary values to intent. values in IF statement are only used to pass back to main
                int categoryId = (position*2)+1;


                //pass necessary values to intent. values in IF statement are only used to pass back to main
                Intent intent = new Intent(view.getContext(), LocationActivity.class);
                intent.putExtra("categoryId", categoryId);
                intent.putExtra("userLatitude", userLatitude);
                intent.putExtra("userLongitude", userLongitude);
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
