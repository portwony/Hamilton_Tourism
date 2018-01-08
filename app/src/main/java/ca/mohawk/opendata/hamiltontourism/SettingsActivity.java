package ca.mohawk.opendata.hamiltontourism;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    private boolean isConnected = isOnline();
    public MyDBHelper dbHelper = new MyDBHelper(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);



        RadioButton celsius = (RadioButton)findViewById(R.id.radioDegreesC);
        RadioButton fahrenheit = (RadioButton)findViewById(R.id.radioDegreesF);
        RadioButton limit20 = (RadioButton)findViewById(R.id.radio20Results);
        RadioButton limit50 = (RadioButton)findViewById(R.id.radio50Results);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        if(pref.getBoolean("isCelsius", true)){
            celsius.setChecked(true);
        } else {
            fahrenheit.setChecked(true);
        }

        if (pref.getInt("limit", 20) == 20){
            limit20.setChecked(true);
        } else {
            limit50.setChecked(true);
        }





        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Intent intentHome = new Intent(SettingsActivity.this, MainActivity.class);
                        startActivity(intentHome);
                        break;
                    case R.id.action_nearby:
                        double userLatitude = getIntent().getDoubleExtra("userLatitude", 0);
                        double userLongitude = getIntent().getDoubleExtra("userLongitude", 0);

                        Intent intentNearby = new Intent(SettingsActivity.this, NearbyActivity.class);
                        intentNearby.putExtra("userLatitude", userLatitude);
                        intentNearby.putExtra("userLongitude", userLongitude);
                        startActivity(intentNearby);
                        break;
                    case R.id.action_settings:
                        break;
                }
                return true;
            }
        });
    }

    public boolean isOnline() {

            Runtime runtime = Runtime.getRuntime();
            try {
                Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
                int     exitValue = ipProcess.waitFor();
                return (exitValue == 0);
            }
            catch (IOException | InterruptedException e)          { e.printStackTrace(); }

        return false;

    }

    public void updateDatabase(View view) {

        if (isConnected){

            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            int databaseVersion = pref.getInt("databaseVersion",1);
            int newDatabaseVersion = databaseVersion + 1;

            dbHelper.onUpgrade(dbHelper.getWritableDatabase(), databaseVersion, newDatabaseVersion);



            DataAsyncTaskCategories dlCategories = new DataAsyncTaskCategories(this);

            String uri = "https://hamiltontourismapi.azurewebsites.net/categories";
            dlCategories.execute(uri);

            //locations
            DataAsyncTask dlLocations = new DataAsyncTask(this);

            String uriCat = "https://hamiltontourismapi.azurewebsites.net/locations";
            dlLocations.execute(uriCat);

            Toast.makeText(this, "Updated Data", Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("databaseVersion", newDatabaseVersion);
            editor.apply();

        } else {
            Toast.makeText(this, "There was a problem downloading the database. Please try again.", Toast.LENGTH_SHORT).show();

        }


    }

    public void changeTemperatureUnits(View view) {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioDegreesC:
                if (checked) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isCelsius", true);
                    editor.apply();
                }
                    break;
            case R.id.radioDegreesF:
                if (checked){
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("isCelsius", false);
                    editor.apply();
                }
                    break;
        }
    }

    public void changeLimit(View view) {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio20Results:
                if (checked) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("limit", 20);
                    editor.apply();
                }
                break;
            case R.id.radio50Results:
                if (checked){
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putInt("limit", 50);
                    editor.apply();
                }
                break;
        }
    }
}
