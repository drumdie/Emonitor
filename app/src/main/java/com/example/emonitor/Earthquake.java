package com.example.emonitor;

import android.os.Parcel;
import android.os.Parcelable;

public class Earthquake implements Parcelable {

    private long dateTime;
    private double magnitude;
    private String place;
    private String longitude;
    private String latitude;

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Earthquake(long dateTime, double magnitude, String place, String longitude, String latitude) {
        this.dateTime = dateTime;
        this.magnitude = magnitude;
        this.place = place;
        this.longitude = longitude;
        this.latitude = latitude;
    }




    protected Earthquake(Parcel in) {
        dateTime = in.readLong();
        magnitude = in.readDouble();
        place = in.readString();
        longitude = in.readString();
        latitude = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(dateTime);
        dest.writeDouble(magnitude);
        dest.writeString(place);
        dest.writeString(longitude);
        dest.writeString(latitude);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Earthquake> CREATOR = new Parcelable.Creator<Earthquake>() {
        @Override
        public Earthquake createFromParcel(Parcel in) {
            return new Earthquake(in);
        }

        @Override
        public Earthquake[] newArray(int size) {
            return new Earthquake[size];
        }
    };


}