package ca.mohawk.opendata.hamiltontourism;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class LocationActivity extends AppCompatActivity {

    public MyDBHelper dbHelper = new MyDBHelper(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);


        final ListView lvLocations = (ListView) findViewById(R.id.locationsList);


        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int categoryId = getIntent().getIntExtra("categoryId", 0);
        double userLongitude = getIntent().getDoubleExtra("userLongitude", 0);
        double userLatitude = getIntent().getDoubleExtra("userLatitude", 0);


        String[] projection = {"_id, name, address, type, latitude, longitude, additionalInfo, (abs(latitude - " + userLatitude +  ") + abs(longitude - " + userLongitude + ")) as closeness"};
        String selection = " categoryId =  " + categoryId;
        String[] selectionargs = null;
        String sortBy = "closeness ASC";
        String limit = "20";
        Cursor myCursor = db.query("locations", projection, selection, selectionargs, null, null, sortBy, limit);

        String fromColumns2[] = {"_id", "name", "address", "type", "latitude", "longitude", "additionalInfo"};
        int toViews2[] = {R.id.idTextView, R.id.nameTextView, R.id.addressTextView, R.id.typeTextView, R.id.latitudeTextView, R.id.longitudeTextView, R.id.additionalTextView};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.location_list_item, myCursor, fromColumns2, toViews2, 0);


        lvLocations.setAdapter(adapter);

        lvLocations.setClickable(true);
        lvLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {

                TextView latitudeText = (TextView)findViewById(R.id.latitudeTextView);
                double latitude = Double.valueOf((String) latitudeText.getText());

                TextView longitudeText = (TextView)findViewById(R.id.longitudeTextView);
                double longitude = Double.valueOf((String) longitudeText.getText());



            }

        });

    }
}

