package ca.mohawk.opendata.hamiltontourism;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.mohawk.opendata.hamiltontourism.Adapters.RestaurantListAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Daniel on 2018-01-07.
 */

public class RestaurantsActivity extends AppCompatActivity {
    public static final String TAG = RestaurantsActivity.class.getSimpleName();

    @Bind(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private RestaurantListAdapter mAdapter;

    public ArrayList<Restaurant> restaurants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String longitude = intent.getStringExtra("userLongitude");
        String latitude = intent.getStringExtra("userLatitude");

        getRestaurants(longitude, latitude);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Intent intentHome = new Intent(RestaurantsActivity.this, MainActivity.class);
                        startActivity(intentHome);
                        break;

                    case R.id.action_settings:
                        Intent intentSettings = new Intent (RestaurantsActivity.this, SettingsActivity.class);
                        startActivity(intentSettings);

                }
                return true;
            }
        });
    }

    private void getRestaurants(String longitude, String latitude) {
        final YelpService yelpService = new YelpService();
        yelpService.findRestaurants(longitude, latitude, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                restaurants = yelpService.processResults(response);

                RestaurantsActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mAdapter = new RestaurantListAdapter(getApplicationContext(), restaurants);
                        mRecyclerView.setAdapter(mAdapter);
                        RecyclerView.LayoutManager layoutManager =
                                new LinearLayoutManager(RestaurantsActivity.this);

                        mRecyclerView.setLayoutManager(layoutManager);

                        mRecyclerView.setHasFixedSize(true);
                    }
                });
            }
        });
    }
}