package ca.mohawk.opendata.hamiltontourism;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    JSONObject data = null;
    public MyDBHelper dbHelper = new MyDBHelper(this);
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    protected Location mLastLocation;

    //default variables
    public double userLatitude = 43.2562;
    public double userLongitude = -79.8681;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


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
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Intent intentHome = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intentHome);
                        break;
                    case R.id.action_nearby:
                        Intent intentNearby = new Intent(MainActivity.this, NearbyActivity.class);
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

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();

                            userLatitude = mLastLocation.getLatitude();
                            userLongitude = mLastLocation.getLongitude();
                    
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                        }
                    }
                });
    }



    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }

    /**
     * Shows a {@link Snackbar} using {@code text}.
     *
     * @param text The Snackbar text.
     */
    private void showSnackbar(final String text) {
        View container = findViewById(R.id.main_activity_layout);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a {@link Snackbar}.
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }


}
