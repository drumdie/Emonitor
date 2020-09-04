package com.example.emonitor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {
    private ArrayList<Earthquake> eqList;
    private Context context ;
    private int layoutId;

    public EarthquakeAdapter(@NonNull Context context, int resource, @NonNull List<Earthquake> earthquakes) {
        super(context, resource, earthquakes);
        this.context = context;
        this.layoutId = resource;
        eqList = new ArrayList<>(earthquakes);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
               LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(layoutId, null);
                holder = new ViewHolder();
                holder.magnitudTexView = (TextView) convertView.findViewById(R.id.eq_list_item_magnitude);
                holder.placeTexView = (TextView) convertView.findViewById(R.id.eq_list_item_place);
                convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Earthquake earthquake = eqList.get(position);
        holder.magnitudTexView.setText(String.valueOf(earthquake.getMagnitude()));
        holder.placeTexView.setText(earthquake.getPlace());


        return convertView;
    }

    class ViewHolder {
        public TextView magnitudTexView;
        public TextView placeTexView;

    }
}
