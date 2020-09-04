package com.example.emonitor;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EqDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "earthquakes.db";

    public  EqDbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override     //Call When we create  database // id PRIMARY KEY: tells to line "id , magnitude, place, etc" that it will be use as an id  // AUTOINCREMENT: autoincrement id on each row we add
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String EARTHQUAKES_DATABASE = "CREATE TABLE " + EqContract.EqColumns.TABLE_NAME + " (" +
                EqContract.EqColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                EqContract.EqColumns.MAGNITUDE + " REAL NOT NULL," +
                EqContract.EqColumns.PLACE + " TEXT NOT NULL," +
                EqContract.EqColumns.LONGITUDE + " TEXT NOT NULL,"+
                EqContract.EqColumns.LATITUDE + " TEXT NOT NULL," +
                EqContract.EqColumns.TIMESTAMP + " TEXT NOT NULL" +
                ")";
        sqLiteDatabase.execSQL(EARTHQUAKES_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {       //Call when updating database
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(sqLiteDatabase);

    }
}
