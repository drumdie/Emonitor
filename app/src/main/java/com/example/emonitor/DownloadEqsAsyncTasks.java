package com.example.emonitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.audiofx.DynamicsProcessing;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class DownloadEqsAsyncTasks extends AsyncTask <URL, Void, ArrayList<Earthquake> > {          //---> Arguments that are originally <Void, Void, String >
                                                                                                    // 1st Void (here URL) is the one that goes to "doInBackground" (Void, here URL)
    public DownloadEqsInterface delegate;   //--> instance of interface DownloadEqsInterface        //2nd value (Void) is used whe using onProgressUpdate method( not used here) (is executed on main threat, is use to give to USER the what percentage is already downloaded from data ur downloading // also is used preExecute method that do things before doInBackground
    private Context context;                                                                                                //3rd -> String here "ArrayList<Earthquake>" is the value we r going to give as an argument from return to doInBackground method

    DownloadEqsAsyncTasks(Context context){
        this.context= context;
    }

    public interface DownloadEqsInterface { //--> we create it and it has one method
        void onEqsDownloaded(ArrayList<Earthquake> eqList);
    }

    @Override                                       //... means that u can add more arguments and all of them ll be stored in an Array (urls/voids )*
    protected ArrayList<Earthquake> doInBackground(URL... urls) { //by default is like // protected String doInBackground (Void...voids){ return null}
        String eqData;                                          // method from AsyncTask for doing processes un background(not main Threat)
        ArrayList<Earthquake> eqList = null;                   // output goes directly to onPostExecute method that is executed on main Threat
        try {                                                  // meaning that return(ex: eqList) goes straight ahead to "onPostExecute" as an String argument by default, in this case is "ArrayList<Earthquake> eqList"
            eqData = downloadData(urls[0]);   //-------------> *urls[0] means-> Array url, position 0. That is the only url that we are giving, it could be more., in that case the 2nd url
            eqList = parseDataFromJson(eqData);     //                                                 // ? should be url[1] and so on-
            saveEqsOnDatabase(eqList);    // its done here for better processing capacity
        } catch (IOException e) {
            e.printStackTrace();
        }
        return eqList;
    }

    @Override
    protected void onPostExecute(ArrayList<Earthquake> eqList) { // receive eqList from ´parseDataFromJson and give it to MAin to onEqDownloaded using interface with delegate and then that method pass it to list view onItemClick
        super.onPostExecute(eqList);
        delegate.onEqsDownloaded(eqList); /// ** here we are calling the method from interface, but its not being implemented until u type  in main activity downloadEqsAsyncTasks.delegate = this;
        //Log.d("manzana",s);                //  and u have to implement it on main class and then right click (generate/implement methods/onEqsDownloaded) that gives u the body, then put code on method for implement functionality
        // To see what is returning
    }

    private void saveEqsOnDatabase(ArrayList<Earthquake> eqList){
        EqDbHelper dbHelper = new EqDbHelper(context);   // For database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        for (Earthquake earthquake : eqList){ // getting eqs for database
            ContentValues contentValues = new ContentValues();
            contentValues.put(EqContract.EqColumns.MAGNITUDE,earthquake.getMagnitude());
            contentValues.put(EqContract.EqColumns.PLACE,earthquake.getPlace());
            contentValues.put(EqContract.EqColumns.TIMESTAMP,earthquake.getDateTime());
            contentValues.put(EqContract.EqColumns.LATITUDE,earthquake.getLatitude());
            contentValues.put(EqContract.EqColumns.LONGITUDE,earthquake.getLongitude());

            database.insert(EqContract.EqColumns.TABLE_NAME,null,contentValues); // inserting eqs to database
        }

    }




    private String downloadData (URL url) throws IOException {  //Getting connection and data From USGS in binary format
        String jsonResponse = "";
        HttpURLConnection urlConnection= null;
        InputStream inputStream = null;

        try {
            urlConnection=(HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000/*milliseconds*/);
            urlConnection.setConnectTimeout(15000/*milliseconds*/);
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();   //input stream = binary
            jsonResponse = readFromStream(inputStream);        //readFromStream change it to Json format
        }
        catch (IOException e) {
            e.printStackTrace();

        }
        finally {
            if (urlConnection != null) { urlConnection.disconnect(); }

            if (inputStream != null){inputStream.close(); }
        }
        return jsonResponse;

    }

    private String readFromStream(InputStream inputStream ) throws IOException { //Getting binary data From inputStream and setting Json (output)
        StringBuilder output = new StringBuilder();
        if (inputStream !=null ){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line!=null ) {
                output.append(line);
                line=reader.readLine();
            }
        }
        return output.toString();
    }

    private ArrayList<Earthquake> parseDataFromJson(String eqsData) { // takes arraylist and give it back to on PostExecute
        ArrayList<Earthquake> eqList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(eqsData);
            JSONArray featuresJsonArray = jsonObject.getJSONArray("features");
            for (int i = 0; i < featuresJsonArray.length(); i++) {
                JSONObject featuresJsonObject = featuresJsonArray.getJSONObject(i);
                JSONObject propertiesJsonObjects = featuresJsonObject.getJSONObject("properties");
                long dateTime = propertiesJsonObjects.getLong("time");
                double magnitude = propertiesJsonObjects.getDouble("mag");
                String place = propertiesJsonObjects.getString("place");

                JSONObject geometryJsonObject = featuresJsonObject.getJSONObject("geometry");
                JSONArray coordinatesJsonArrays = geometryJsonObject.getJSONArray("coordinates");
                String longitude = coordinatesJsonArrays.getString(0);
                String latitude = coordinatesJsonArrays.getString(1);

                eqList.add(new Earthquake(dateTime, magnitude, place,longitude,latitude));

                Log.d("MANZANA", dateTime +  " + " + magnitude + " + " + place + " + " + longitude + " + " + latitude);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return eqList;
    }



}
