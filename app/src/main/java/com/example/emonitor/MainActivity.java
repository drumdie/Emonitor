package com.example.emonitor;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/*add on Manifest  // <uses-permission android:name="android.permission.INTERNET" />
*/
public class MainActivity extends AppCompatActivity implements DownloadEqsAsyncTasks.DownloadEqsInterface {
        private ListView earthquakeListView ;
        public final static String SELECTED_EARTHQUAKE= "selected earthquake";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.activiy_main_toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);


        earthquakeListView = findViewById(R.id.earthquake_listview);

        if (Utils.isNetworkAvailable(this)) {
            downloadEarthquakes();
        } else {
                getEarthquakesFromDB();
        } // reading database

    }

    private void getEarthquakesFromDB() {
        EqDbHelper eqDbHelper = new EqDbHelper(this);
        SQLiteDatabase  database = eqDbHelper.getReadableDatabase();

        Cursor cursor = database.query(EqContract.EqColumns.TABLE_NAME,null,null,null,null,null,null);

        ArrayList<Earthquake> eqList = new ArrayList<>();
        while (cursor.moveToNext()){
            long dateTime = cursor.getLong(EqContract.EqColumns.TIMESTAMP_COLUMN_INDEX);
            double magnitude = cursor.getDouble(EqContract.EqColumns.MAGNITUDE_COLUMN_INDEX);
            String place = cursor.getString(EqContract.EqColumns.PLACE_COLUMN_INDEX);
            String longitude = cursor.getString(EqContract.EqColumns.LONGITUDE_COLUMN_INDEX);
            String latitude = cursor.getString(EqContract.EqColumns.LATITUDE_COLUMN_INDEX);

            eqList.add(new Earthquake(dateTime,magnitude,place,longitude,latitude));
        }
        cursor.close();
        fillEqList(eqList);
    }

    private void downloadEarthquakes(){
        DownloadEqsAsyncTasks downloadEqsAsyncTasks = new DownloadEqsAsyncTasks(this);
        downloadEqsAsyncTasks.delegate = this;

        try {
            downloadEqsAsyncTasks.execute(new URL("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onEqsDownloaded (ArrayList<Earthquake> eqList){ // receive argument from °delegate using interface DownloadEqsInterface
        fillEqList(eqList);
    }

    private void fillEqList(ArrayList<Earthquake> eqList){
        final EarthquakeAdapter eqAdapter = new EarthquakeAdapter(this, R.layout.eq_list_item, eqList);
        earthquakeListView.setAdapter(eqAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override                                                                        // implementes on Item click listener in a ListView
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) { //3rd value int i(here position)--> position of element that is clicked
                Earthquake selectedEarthquake = eqAdapter.getItem(position);
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra(SELECTED_EARTHQUAKE,selectedEarthquake); // put selectedEarthquake by position and KEY is public final static String into Detail activiy

                startActivity(intent);
            }

        });
    }

}