package ca.mohawk.opendata.hamiltontourism;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Daniel on 2018-01-07.
 */

public class YelpService {
    public static void findRestaurants(String longitude, String latitude, Callback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        String yelptoken = "Bearer ytyx5h9T9RbbIaxlOlZiQ6jBQ7QQkHp9s0X7jrR2iHHlW3UJOD9o1BX0-CAMHO2C3zLB4hL-LWJ6KELufTTSm-EY0Y6vexPYIPACFE9w_u9QPt6yevR3dRZU-CEfWnYx";

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.yelp.com/v3/businesses/search?term=restaurants").newBuilder();
        if (longitude != "" && latitude != "") {
            urlBuilder.addQueryParameter("longitude", longitude);
            urlBuilder.addQueryParameter("latitude", latitude);
        }
        else {
            urlBuilder.addQueryParameter("location", "Hamilton");
        }
        urlBuilder.addQueryParameter("sort_by", "distance");
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .header("Authorization", yelptoken)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public ArrayList<Restaurant> processResults(Response response) {
        ArrayList<Restaurant> restaurants = new ArrayList<>();

        try {
            String jsonData = response.body().string();
            JSONObject yelpJSON = new JSONObject(jsonData);
            JSONArray businessesJSON = yelpJSON.getJSONArray("businesses");
            for (int i = 0; i < businessesJSON.length(); i++) {
                JSONObject restaurantJSON = businessesJSON.getJSONObject(i);
                String name = restaurantJSON.getString("name");
                String phone = restaurantJSON.optString("display_phone", "Phone not available");
                String website = restaurantJSON.getString("url");
                double rating = restaurantJSON.getDouble("rating");

                String imageUrl = restaurantJSON.getString("image_url");

                double latitude = (double) restaurantJSON.getJSONObject("coordinates").getDouble("latitude");

                double longitude = (double) restaurantJSON.getJSONObject("coordinates").getDouble("longitude");

                ArrayList<String> address = new ArrayList<>();
                JSONArray addressJSON = restaurantJSON.getJSONObject("location")
                        .getJSONArray("display_address");
                for (int y = 0; y < addressJSON.length(); y++) {
                    address.add(addressJSON.get(y).toString());
                }

                ArrayList<String> categories = new ArrayList<>();
                JSONArray categoriesJSON = restaurantJSON.getJSONArray("categories");

                for (int y = 0; y < categoriesJSON.length(); y++) {
                    categories.add(categoriesJSON.getJSONObject(y).getString("title"));
                }
                Restaurant restaurant = new Restaurant(name, phone, website, rating,
                        imageUrl, address, latitude, longitude, categories);
                restaurants.add(restaurant);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return restaurants;
    }
}
