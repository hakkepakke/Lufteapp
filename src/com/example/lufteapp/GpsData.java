package com.example.lufteapp;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GpsData extends Activity {

	SQLiteDatabase db;
	double latitude;
	double longitude;
	TextView editLocation;
	LocationManager locationManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gps_data);
		
		 editLocation = (TextView) findViewById(R.id.kords);
		 
		 /*
		  * This code gets a new GPS position every 20. second with a 5 metre interval.
		  */
		 locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		 LocationListener locationListener = new MyLocationListener();  
		 locationManager.requestLocationUpdates(  
		 LocationManager.GPS_PROVIDER, 20000, 5, locationListener);
		
		db = openOrCreateDatabase("gpsDataDB", MODE_PRIVATE,null);
		db.execSQL("CREATE TABLE IF NOT EXISTS gpsDataa(longitude BIGINT, latitude BIGINT, isHome INTEGER, name STRING);");
	    
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
		 * Gjelder da kun hjemmeposisjon.
		 */
		Cursor cursor = db.rawQuery("SELECT * FROM gpsDataa WHERE isHome = 1;", null);
		if(cursor.moveToFirst())
		{
			String lat = cursor.getString(0);
			String lon = cursor.getString(1);
			String isHom = cursor.getString(2);
			int number = cursor.getCount();
		       editLocation.setText(
		    		   "fra DB number = " + number
		    		  + "lat= " + lat + "lon = " + lon + "isHom= " +isHom);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void setHome(View view) 
	{
		/*
		 *Checks if the user already has set a home
		 *If the user has set a home already, it will ask before
		 *setting a new home.
		 */
		Cursor cursor = db.rawQuery("SELECT * FROM gpsDataa WHERE isHome = 1;", null);
		if(cursor.getCount() > 0)
		{
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("New home?");
			alertDialog.setMessage("Are you sure you want a new home?");
			alertDialog.setButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				db.execSQL("DELETE FROM gpsDataa WHERE isHome = 1;");
				storeMapdataInDatabase(1);
			}
			});
			alertDialog.setIcon(R.drawable.icon);
			alertDialog.show();
		}
		else
		{
			storeMapdataInDatabase(1);
		}

	}
	
	public void setCurrent(View view)
	{
		storeMapdataInDatabase(0);
	}
	
		
		void storeMapdataInDatabase(int isHome)
		{
			if(latitude != 0 && longitude != 0)		//Has not acquired GPS data yet.
			{
				try {	
					
					//If home already exists, delete the current one first.
					
					//Try to store GPS data to database*/
					db.execSQL("INSERT INTO gpsDataa VALUES('"+longitude + "','" + latitude + "','" + isHome + "','homeyo');");
				       Toast.makeText(getApplicationContext(), 
				    		   "Inserting to DB lat= " + latitude + " lon= " + longitude, Toast.LENGTH_LONG).show();
				}
				
				catch(NullPointerException e) {
					e.printStackTrace();
				}
			}
			else
			{
		    	 Toast.makeText(getApplicationContext(), 
			     "Please wait for GPS to find position" +
			     "\nTry again in a moment",
			     Toast.LENGTH_LONG).show();
			}
		}
		
		public bool checkHome(int lat, int lon)
		{
			//Check if sent lat and lon is your home address
			return null;
		}
		
		/*----------Listener class to get coordinates ------------- */
		/*
		 *  Code from http://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android
		 *  Helperclass to get GPS
		 */
		private class MyLocationListener implements LocationListener {

		    @Override
		    public void onLocationChanged(Location loc) {
		    	 Toast.makeText(getApplicationContext(), 
			     "Location changed \nLon=" + longitude + "\nlat = "+latitude,
			     Toast.LENGTH_SHORT).show();
		        longitude = loc.getLongitude();
		        latitude = loc.getLatitude();
		    }

		    public void onProviderDisabled(String provider) {}

		    public void onProviderEnabled(String provider) {}

		    public void onStatusChanged(String provider, int status, Bundle extras) {}
		}
}

