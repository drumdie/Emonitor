package com.example.emonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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

}