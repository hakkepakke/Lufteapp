package com.example.lufteapp;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.R.bool;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class GpsData extends Activity {

	SQLiteDatabase db;
	double latitude;
	double longitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gps_data);
		
		db = openOrCreateDatabase("gpsDataDB", MODE_PRIVATE,null);
		db.execSQL("CREATE TABLE IF NOT EXISTS gpsData(longitude BIGINT, latitude BIGINT, isHome INTEGER, name STRING);");
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gps_data, menu);
		return true;
	}
	
	public void compareHomeCurrent(View view)
	{
		/*
		 * Midlertidig kode for å få ut siste fra DB
		 * WHERE isHome = 1*/
		Cursor cursor = db.rawQuery("SELECT * FROM gpsData WHERE isHome = 1;", null);
		if(cursor.moveToFirst())
		{
			String lat = cursor.getString(0);
			String lon = cursor.getString(1);
			String isHom = cursor.getString(2);
			int number = cursor.getCount();
		       Toast.makeText(getApplicationContext(), 
		    		   "fra DB number = " + number
		    		  + "lat= " + lat + "lon = " + lon + "isHom= " +isHom , Toast.LENGTH_LONG).show();
		}
	}
	
	public void setHome(View view) 
	{
		setGpsData();
		storeMapdataInDatabase(1);
	}
	
	public void setCurrent(View view)
	{
		setGpsData();
		storeMapdataInDatabase(0);
	}
	
	public void setGpsData() 
	{
		
	     LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	     Criteria criteria = new Criteria();
	     String bestProvider = locationManager.getBestProvider(criteria, false);
	     Location location = locationManager.getLastKnownLocation(bestProvider);

	     try {
	       latitude = location.getLatitude();
	       longitude = location.getLongitude ();
	       }
	     catch (NullPointerException e){
	         e.printStackTrace();
	     }
	}
	
		
		void storeMapdataInDatabase(int isHome)
		{
			if(latitude != 0 && longitude != 0)		//Has not acquired GPS data yet.
			{
				try {	
					
					//If home already exists, delete the current one first.
					if(isHome == 1)
					{
						db.execSQL("DELETE FROM gpsData WHERE isHome = 1;");
					}
					
					//Try to store GPS data to database*/
					db.execSQL("INSERT INTO gpsData VALUES('"+longitude + "','" + latitude + "','" + isHome + "');");
				       Toast.makeText(getApplicationContext(), 
				    		   "Inserting to DB lat= " + latitude + " lon= " + longitude, Toast.LENGTH_LONG).show();
				}
				
				catch(NullPointerException e) {
					e.printStackTrace();
				}
			}
			else 	//If GPS data not yet acquired.
			{
				setGpsData();
			}
		}
		
		public bool checkHome(int lat, int lon)
		{
			//Check if sent lat and lon is your home address
			return null;
		}
}

