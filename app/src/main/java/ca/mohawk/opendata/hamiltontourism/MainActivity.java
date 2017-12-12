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

public class MainActivity extends AppCompatActivity {

    public MyDBHelper dbHelper = new MyDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView lv = (ListView) findViewById(R.id.categoriesList);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = { "_id, name" };
        String sortOrder = "name";

        String selection;
        selection = null;
        String[] selectionArgs = null;

        Cursor mycursor = db.query("categories", projection, selection, selectionArgs, null, null, sortOrder);


        String fromColumns[] = {"_id", "name"};
        int toViews[] = {R.id.categoryTextView};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.category_list_item, mycursor, fromColumns, toViews, 0);

        lv.setAdapter(adapter);

        lv.setClickable(true);
       /*lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {

                //pass necessary values to intent. values in IF statement are only used to pass back to main
                Intent intent = new Intent(view.getContext(), LocationActivity.class);
                startActivity(intent);

            }


        });

        */








    }
}
