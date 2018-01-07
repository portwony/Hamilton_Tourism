package ca.mohawk.opendata.hamiltontourism;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.R.attr.data;

public class MainActivity extends AppCompatActivity {

    public MyDBHelper dbHelper = new MyDBHelper(this);
    JSONObject data = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getJSON("Hamilton");

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


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_home:
                        Intent intentHome = new Intent (MainActivity.this, MainActivity.class);
                        startActivity(intentHome);
                        break;
                    case R.id.action_nearby:
                        Intent intentNearby = new Intent (MainActivity.this, NearbyActivity.class);
                        startActivity(intentNearby);
                        break;

                }
                return true;
            }
        });





    }

    private void populateListTwo() {

        final ListView lv2 = (ListView) findViewById(R.id.categoriesList2);
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        String[] projection2 = { "_id, name" };
        String sortOrder2 = null;

        String selection2;
        selection2 = "_id % 2 = 0";
        String[] selectionArgs2 = null;


        Cursor mycursor2 = db.query("categories", projection2, selection2, selectionArgs2, null, null, sortOrder2);


        String fromColumns2[] = {"_id", "name"};
        int toViews2[] = {R.id.id, R.id.categoryTextView};

        SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(this, R.layout.category_list_item, mycursor2, fromColumns2, toViews2, 0);


        lv2.setAdapter(adapter2);

        lv2.setClickable(true);
        lv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {

                //pass necessary values to intent. values in IF statement are only used to pass back to main
                Intent intent = new Intent(view.getContext(), LocationActivity.class);
                startActivity(intent);

            }


        });

    }

    private void populateListOne() {

        final ListView lv1 = (ListView) findViewById(R.id.categoriesList1);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = { "_id, name" };
        String sortOrder = null;

        String selection;
        selection = "_id % 2 = 1";
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

    public void getJSON(final String city) {

        new AsyncTask<Void, Void, Void>() {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=hamilton&APPID=54197d3351cf8597f0a8edc0e635a9ee");

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuffer json = new StringBuffer(1024);
                    String tmp = "";

                    while((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();

                    data = new JSONObject(json.toString());

                    if(data.getInt("cod") != 200) {
                        System.out.println("Cancelled");
                        return null;
                    }


                } catch (Exception e) {

                    System.out.println("Exception "+ e.getMessage());
                    return null;
                }

                return null;
            }

            TextView textView1 = (TextView)findViewById(R.id.weatherText);

            @Override
            protected void onPostExecute(Void Void) {
                if(data!=null){


                    try{

                        String jArray = data.getString("main");
                        JSONObject jObject = new JSONObject(jArray);
                        int t = (int)(jObject.getDouble("temp")-273.15);
                        textView1.setText(""+t+"° Celsius");

                    }catch(Exception e){}
                }

            }
        }.execute();

    }


}
