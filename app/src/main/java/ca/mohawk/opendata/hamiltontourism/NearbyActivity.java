package ca.mohawk.opendata.hamiltontourism;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class NearbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        Intent intentHome = new Intent(NearbyActivity.this, MainActivity.class);
                        startActivity(intentHome);
                        break;
                    case R.id.action_nearby:
                        break;
                    case R.id.action_settings:
                        Intent intentSettings = new Intent(NearbyActivity.this, SettingsActivity.class);
                        startActivity(intentSettings);
                        break;
                }
                return true;
            }
        });

    }
}
