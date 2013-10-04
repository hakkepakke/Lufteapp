package com.hakkepakke.lufteapp;

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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityLog extends Activity {
	/*
	*  @author http://stackoverflow.com/questions/4540754/add-dynamically-elements-to-a-listview-android
	*/

	private SQLiteDatabase db;
	private Cursor positionLog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_log);
		
		final ListView listView = (ListView) findViewById(R.id.list);
		
		ArrayList<String> list = new ArrayList<String>();
		
		db = openOrCreateDatabase("gpsDataDB", MODE_PRIVATE,null);
		db.execSQL("CREATE TABLE IF NOT EXISTS gpsDataa(longitude BIGINT, latitude BIGINT, isHome INTEGER, name STRING);");
		Cursor cursor = db.rawQuery("SELECT * FROM gpsDataa WHERE isHome != 1", null);
		positionLog = cursor;
		
		if (cursor.getCount() ==0){
			//If cursor is empty, shows a text, and removes the 
			// "show all" button
			TextView txt = (TextView) findViewById(R.id.log_empty);
			txt.setText(R.string.log_empty);
			Button btn = (Button) findViewById(R.id.show_locations);
			btn.setVisibility(View.GONE);
			
		} else {
			//If the query returned rows
			//Shows a list with all checked in positions
			TextView txt = (TextView) findViewById(R.id.log_empty);
			txt.setVisibility(View.GONE);
			
			while(cursor.moveToNext()){
				
				String name = cursor.getString(3);
				
				list.add(name);
			} 
			
			final StableArrayAdapter adapter = new StableArrayAdapter(this,
			        android.R.layout.simple_list_item_1, list);
			listView.setAdapter(adapter);
			
			//Lets the user click on items in the list, showing the locations on
			// the map
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					openMap(getCoords(position));
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_log, menu);
		return true;
	}
	
	private class StableArrayAdapter extends ArrayAdapter<String> {
		/*
		 * @author http://stackoverflow.com/questions/18116173/list-wont-trigger-onclicklistener
		 */
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
	
	public void openMap(String[] coords) {
		//Open a map showing the spesific location of "coords"
		//Called from clicking a spesific list item
		
		Intent intent = new Intent(this, ViewMap.class);
		intent.putExtra("POSITION", coords);
		startActivity(intent);
	}
	
	public void openMap(View view){
		//Gets all coordinates from the list and
		// displays them on the map
		//Called from the "show all" button on the screen
		
		Intent intent = new Intent(this, ViewMap.class);
		
		ArrayList<String[]> positions = new ArrayList<String[]>();
		
		for(int i = 0; i < positionLog.getCount(); i++){
			
			positions.add(getCoords(i));
		}
		
		intent.putExtra("POSITIONS", positions);
		startActivity(intent);
	}
	
	public String[] getCoords (int logPosition) {
		//Takes a list position, and returns the coordinates
		// in a String array
		positionLog.moveToPosition(logPosition);
		
		String lat = positionLog.getString(1);
		String lng = positionLog.getString(0);
		String[] result = {lat, lng};
		
		return result;
	}
	
	/*
	 *  author: http://stackoverflow.com/questions/4238921/android-detect-whether-there-is-an-internet-connection-available
	 */	
}