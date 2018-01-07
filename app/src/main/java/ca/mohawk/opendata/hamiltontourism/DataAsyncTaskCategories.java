package ca.mohawk.opendata.hamiltontourism;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
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


class DataAsyncTaskCategories extends AsyncTask<String, Void, String> {

    private Activity myActivity;

    public DataAsyncTaskCategories(Activity inActivity) {
        myActivity = inActivity;
    }

    @Override
    protected String doInBackground(String... params) {
        Log.d("log", "Starting Background Task");
        String results = "";



        try {
            URL url = new URL("https://hamiltontourismapi.azurewebsites.net/categories");

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
                String  name;
                int id;


                try {
                    JSONObject jObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    id =jObject.getInt("_id");
                    name = jObject.getString("name");


                    ContentValues values = new ContentValues();
                    values.put("_id", id);
                    values.put("name",name);


                    long newRowId = db.insert(MyDBHelper.TABLE_CATEGORIES, null, values);



                } catch (JSONException e) {
                    Log.d("DataAsync","This shouldn't happen - onPostexecute.");
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }


    }
}