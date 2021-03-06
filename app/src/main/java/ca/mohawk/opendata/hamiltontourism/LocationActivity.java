package ca.mohawk.opendata.hamiltontourism;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class LocationActivity extends AppCompatActivity {

    public MyDBHelper dbHelper = new MyDBHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        final ListView lvLocations = (ListView) findViewById(R.id.locationsList);


        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        final int categoryId = getIntent().getIntExtra("categoryId", 0);
        final double userLongitude = getIntent().getDoubleExtra("userLongitude", 0);
        final double userLatitude = getIntent().getDoubleExtra("userLatitude", 0);


        Button button = (Button)findViewById(R.id.btnTitle);

        Cursor c=db.rawQuery("SELECT name FROM categories WHERE _id  = " + categoryId,null);
        c.moveToFirst();
        button.setText(c.getString(c.getColumnIndex("name")));

        String[] projection = {"_id, name, address, type, latitude, longitude, additionalInfo, (abs(latitude - " + userLatitude +  ") + abs(longitude - " + userLongitude + ")) as closeness"};
        String selection = " categoryId =  " + categoryId;
        String[] selectionargs = null;
        String sortBy = "closeness ASC";
        String limit = String.valueOf(pref.getInt("limit", 20));
        Cursor myCursor = db.query("locations", projection, selection, selectionargs, null, null, sortBy, limit);

        String fromColumns2[] = {"_id", "name", "address", "type", "latitude", "longitude", "additionalInfo"};
        int toViews2[] = {R.id.idTextView, R.id.nameTextView, R.id.addressTextView, R.id.typeTextView, R.id.latitudeTextView, R.id.longitudeTextView, R.id.additionalTextView};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.location_list_item, myCursor, fromColumns2, toViews2, 0);


        lvLocations.setAdapter(adapter);

        lvLocations.setClickable(true);
        lvLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {


                SQLiteDatabase db2 = dbHelper.getReadableDatabase();

                Cursor c = db2.rawQuery("SELECT name, latitude, longitude, (abs(latitude - " + userLatitude +  ") + abs(longitude - " + userLongitude + ")) as closeness FROM Locations WHERE categoryID = "+categoryId+" ORDER BY closeness ASC LIMIT " + pref.getInt("limit",20), null);
                c.moveToPosition(position);

                String name = c.getString(0);
                double latitude = c.getDouble(1);
                double longitude = c.getDouble(2);
                /*
                TextView latitudeText = (TextView)findViewById(R.id.latitudeTextView);
                double latitude = Double.valueOf((String) latitudeText.getText());

                TextView longitudeText = (TextView)findViewById(R.id.longitudeTextView);
                double longitude = Double.valueOf((String) longitudeText.getText());

                TextView nameText = (TextView)findViewById(R.id.nameTextView);
                String name = String.valueOf(nameText.getText());
                name = name.replaceAll("\\s+","+");
                */

                String uri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(" + name +")";
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);



            }

        });

    }
}

