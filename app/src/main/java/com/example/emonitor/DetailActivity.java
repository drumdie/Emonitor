package com.example.emonitor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DetailActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    private final static int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 0;
    private GoogleApiClient googleApiClient;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private Location userLocation;
    private static final int RANGE_TO_DISPLAY_EQ_MARKER_IN_METERS = 1000;
    private Double eqLat;
    private Double eqLong;
    private String eqMag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.activiy_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //maps                                                                                      /////
        googleApiClient= new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        //////////////////////////////////////////////////////////////////////////////////////////////////

        TextView dateTimeTextView = (TextView) findViewById(R.id.eq_detail_date_time);
        TextView magnitudeTextView = (TextView) findViewById(R.id.eq_detail_magnitude);
        TextView placeTextView = (TextView) findViewById(R.id.eq_detail_place);
        TextView longitudeTextView =(TextView)findViewById(R.id.eq_detail_longitude);
        TextView latitudeTextView =(TextView)findViewById(R.id.eq_detail_latitude);
        Bundle extras = getIntent().getExtras(); // getting intent from main activity
        Earthquake earthquake = extras.getParcelable(MainActivity.SELECTED_EARTHQUAKE); // getting from Key from main act. from parcelable from extras to new earthquake

        if (earthquake!= null){

            dateTimeTextView.setText(getStringDateFromTimeStamp(earthquake.getDateTime()));
            magnitudeTextView.setText(getStringDecimalFromDouble(earthquake.getMagnitude()));
            placeTextView.setText(earthquake.getPlace());
            longitudeTextView.setText(earthquake.getLongitude());
            latitudeTextView.setText(earthquake.getLatitude());

            eqLat = Double.parseDouble(earthquake.getLatitude());
            eqLong = Double.parseDouble(earthquake.getLongitude());
            eqMag = getStringDecimalFromDouble(earthquake.getMagnitude());
        }

    }

    private String getStringDateFromTimeStamp (long timestamp ){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMMM/yyyy - H:mm:ss", Locale.getDefault());
        Date date = new Date(timestamp);
        return simpleDateFormat.format(date);

    }
    private String getStringDecimalFromDouble (double doublein) {
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        Double mag = new Double(doublein);
        return decimalFormat.format(mag);
    }

    public static String convertMeterToKilometer(float totalDistance) {
        double ff = totalDistance / 1000;
        BigDecimal bd = BigDecimal.valueOf(ff);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return String.valueOf(bd.intValue());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkSelfPermission(ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                Location userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                getUserLastLocation(userLocation);
            } else {
                final String[] permissions = new String[]{ACCESS_FINE_LOCATION};
                requestPermissions(permissions, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            Location userLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            getUserLastLocation(userLocation);

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // compara requestCode dado por el user con el dado por nosotros
        if(requestCode == ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults [0] == PackageManager.PERMISSION_GRANTED){
                googleApiClient.reconnect();
            }
            else if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)){
                AlertDialog.Builder builder= new AlertDialog.Builder(this);
                builder.setTitle("Acceder a la ubicación del teléfono");
                builder.setMessage("Debes aceptar el permiso para poder utilizar la app ");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String[] permissions = new String[]{ACCESS_FINE_LOCATION};
                        requestPermissions(permissions, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
                    }
                });
                builder.show();
            }
        }
    }

    private void getUserLastLocation(Location userLocation) {
        if (userLocation != null) {

            this.userLocation = userLocation;
            this.userLocation.setLatitude(-34.603722);
            this.userLocation.setLongitude(-58.381592);

            mapFragment.getMapAsync(this);


        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        BitmapDescriptor EqMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.eqicon);
        final LatLng userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        final LatLng eqLatLng = new LatLng(eqLat, eqLong);
        mMap.addMarker(new MarkerOptions().position(userLatLng).title("Mi ubicación"));

        Location eqLocation = new Location("");
        eqLocation.setLatitude(eqLat);
        eqLocation.setLongitude(eqLong);

        final float distanceToEq = Math.round(eqLocation.distanceTo(userLocation));  // compilador convertirlo a int redondeando

        mMap.addMarker(new MarkerOptions().position(eqLatLng).title("Magnitud " + eqMag)
                    .icon(EqMarkerIcon));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(eqLatLng,9));

        ImageButton locButton = (ImageButton) findViewById(R.id.imageButton);

        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng,7));

                if(distanceToEq < RANGE_TO_DISPLAY_EQ_MARKER_IN_METERS ) { // mts
                    Toast.makeText(DetailActivity.this, "Distancia del terremoto " + distanceToEq + " mts", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(DetailActivity.this, "Distancia del terremoto " + convertMeterToKilometer(distanceToEq) + " Kms", Toast.LENGTH_SHORT).show();

                }
            }
        });
        ImageButton eqButton = (ImageButton) findViewById(R.id.myEq);

        eqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng newEq = eqLatLng;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newEq,7));            }
        });

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}