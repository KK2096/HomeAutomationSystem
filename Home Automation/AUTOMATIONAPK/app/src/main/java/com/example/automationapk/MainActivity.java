package com.example.automationapk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public SQLiteDatabase sdb;
    public Cursor cursor;

    //DB name
    public String db_name = "DBHOMEAUTO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //loading the default fragment
        loadFragment(new DeviceFragment());

        //Create Table
        sdb = Objects.requireNonNull(openOrCreateDatabase(db_name,android.content.Context.MODE_PRIVATE ,null));
        String table_creation = "CREATE TABLE IF NOT EXISTS " + "Appliance_Table" + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,APPLIANCE_NAME TEXT,PLACE TEXT);";
        sdb.execSQL(table_creation);

        String table_creation_prof = "CREATE TABLE IF NOT EXISTS " + "Profiles_Table" + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,PROFILE_NAME TEXT,SERVER_IP TEXT ,PORT_NO INTEGER);";
        sdb.execSQL(table_creation_prof);



        //getting bottom navigation view and attaching the listener
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_devices:
                fragment = new DeviceFragment();
                break;

            case R.id.navigation_dashboard:
                fragment = new DashboardFragment();
                break;

            case R.id.navigation_profiles:
                fragment = new ProfilesFragment();
                break;

        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}