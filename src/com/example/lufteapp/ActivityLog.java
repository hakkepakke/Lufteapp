package com.example.lufteapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ActivityLog extends Activity {
	/*
	 *  @author http://stackoverflow.com/questions/4540754/add-dynamically-elements-to-a-listview-android
	 */
	
	SQLiteDatabase db;
	//ArrayList<TextView> list = new ArrayList<TextView>();
	//ArrayAdapter<TextView> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_log);
		
		final ListView listView = (ListView) findViewById(R.id.list);
		
		ArrayList<String> list = new ArrayList<String>();
		
		
		db = openOrCreateDatabase("gpsDataDB", MODE_PRIVATE,null);
		
		Cursor cursor = db.rawQuery("SELECT * FROM gpsDataa WHERE isHome != 1", null);
		
		//list.add("Latitude | Longitude");
		
		while(cursor.moveToNext())
		{
			String lat = cursor.getString(0);
			String lng = cursor.getString(1);
			list.add(lat + " - " + lng);
		} 
		
		final StableArrayAdapter adapter = new StableArrayAdapter(this,
		        android.R.layout.simple_list_item_1, list);
		listView.setAdapter(adapter);
		
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				openMap(adapter.getItem(position));
			}
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_log, menu);
		return true;
	}
	
	private class StableArrayAdapter extends ArrayAdapter<String> {

	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	    public StableArrayAdapter(Context context, int textViewResourceId,
	        List<String> objects) {
	    	super(context, textViewResourceId, objects);
	    	for (int i = 0; i < objects.size(); ++i) {
	    		mIdMap.put(objects.get(i), i);
	    	}
	    }

	    @Override
	    public long getItemId(int position) {
	    	String item = getItem(position);
	    	return mIdMap.get(item);
	    }

	    @Override
	    public boolean hasStableIds() {
	    	return true;
	    }

	}
	
	public void openMap(String coords) {
		Intent intent = new Intent(this, ViewMap.class);
		intent.putExtra("POSITION", coords);
		startActivity(intent);
	}
	
	public void openMap(View view){
		Intent intent = new Intent(this, ViewMap.class);
		
		ListView list = (ListView) findViewById(R.id.list);
		StableArrayAdapter adapter = (StableArrayAdapter) list.getAdapter();
		
		ArrayList<String> positions = new ArrayList<String>();
		
		for(int i = 0; i < adapter.getCount(); i++){
			
			positions.add(adapter.getItem(i));
		}
		
		intent.putStringArrayListExtra("POSITIONS", positions);
		startActivity(intent);
	}
}