package ca.mohawk.opendata.hamiltontourism;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import ca.mohawk.opendata.hamiltontourism.Adapters.RestaurantListAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Daniel on 2018-01-08.
 */

public class RestaurantActivity extends AppCompatActivity {
    public static final String TAG = RestaurantsActivity.class.getSimpleName();
    public int index;
    public ArrayList<Restaurant> restaurants = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        final TextView textView = (TextView) findViewById(R.id.restaurantNameTextView);
        ButterKnife.bind(this);

        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        String longitude = sp.getString("longitude", "");
        String latitude = sp.getString("latitude", "");
        Intent intent = getIntent();
        index = intent.getIntExtra("restaurantIndex", 0);
        getRestaurants(longitude, latitude);
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
                RestaurantActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        final Restaurant restaurant = restaurants.get(index);
                        TextView textView = (TextView) findViewById(R.id.restaurantNameTextView);
                        textView.setText(restaurant.getName());
                        TextView addressTextView = (TextView) findViewById(R.id.AddressTextView);
                        if (restaurant.getAddress().size() > 0)
                            addressTextView.setText(TextUtils.join(", ", restaurant.getAddress()));
                        TextView phoneTextView = (TextView) findViewById(R.id.PhoneTextView);
                        phoneTextView.setText(restaurant.getPhone());
                        TextView WebsiteTextView = (TextView) findViewById(R.id.WebsiteTextView);
                        WebsiteTextView.setText(restaurant.getWebsite());
                        ImageView imageView = (ImageView) findViewById(R.id.restaurantImageView);
                        if (!restaurant.getImageUrl().isEmpty()) {
                            Picasso.with(RestaurantActivity.this).load(restaurant.getImageUrl()).into(imageView);
                        }
                        Button clickButton = (Button) findViewById(R.id.directionsButton);
                        clickButton.setOnClickListener( new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                String uri = "geo:" + restaurant.getLatitude() + "," + restaurant.getLongitude() + "?q=" + restaurant.getLatitude() + "," + restaurant.getLongitude() + "(" + restaurant.getName() +")";
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                startActivity(intent);
                                }
                            });
                        }
                });
            }
        });
    }
}