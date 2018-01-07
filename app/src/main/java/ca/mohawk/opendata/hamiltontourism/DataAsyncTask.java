package ca.mohawk.opendata.hamiltontourism;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;


class DataAsyncTask extends AsyncTask<String, Void, String> {

    private Activity myActivity;

    public DataAsyncTask(Activity inActivity) {
        myActivity = inActivity;
    }


    @Override
    protected String doInBackground(String... params) {
        Log.d("log", "Starting Background Task");
        String results = "";



        try {

            URL url = new URL("https://hamiltontourismapi.azurewebsites.net/locations");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int statusCode = conn.getResponseCode();
            Log.d("log", "Response Code: " + statusCode);

            if (statusCode == 200) {
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());

                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    results += line;
                }



            }

        } catch (IOException ex) {
            Log.d("DataAsync","This shouldn't happen - doinbackground.");
        }

        return results;
    }

    @Override
    protected void onPostExecute(String result) {

        JSONArray jArray = null;
        try {
            jArray = new JSONArray(result);
            MyDBHelper dbHelper = new MyDBHelper(myActivity);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            for (int i=0; i < jArray.length(); i++)
            {
                String  name, type, address, additionalInfo;
                int id, category;
                double longitude, latitude;


                try {
                    JSONObject jObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    id =jObject.getInt("_id");
                    category =jObject.getInt("categoryId");
                    name = jObject.getString("name");
                    address = jObject.getString("address");
                    type = jObject.getString("type");
                    additionalInfo = jObject.getString("additionalInfo");

                    if (Objects.equals(jObject.getString("longitude"), "")) {
                        longitude = 10000;
                    } else {
                        longitude = Double.valueOf(jObject.getString("longitude"));
                    }

                    if (Objects.equals(jObject.getString("latitude"), "")){
                        latitude = 10000;
                    } else {
                        latitude = Double.valueOf(jObject.getString("latitude"));
                    }



                    ContentValues values = new ContentValues();
                    values.put("_id", id);
                    values.put("categoryId", category);
                    values.put("name",name);
                    values.put("longitude",longitude);
                    values.put("latitude",latitude);
                    values.put("address",address);
                    values.put("type",type);
                    values.put("additionalInfo", additionalInfo);


                    long newRowId = db.insert(MyDBHelper.TABLE_LOCATIONS, null, values);



                } catch (JSONException e) {
                    Log.d("DataAsync","This shouldn't happen - onPostexecute.");
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }


    }
}