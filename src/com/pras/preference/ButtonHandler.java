/*
 * Copyright (C) 2010 Prasanta Paul, http://as-m-going-on.blogspot.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pras.preference;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pras.R;
import com.pras.weather.WeatherRecord;
import com.pras.weather.DataParser.City;
import com.pras.weather.WeatherRecord.WeatherCallback;

/**
 * TODO: Define a Preference or Setting screen
 * 1. Select City
 * 2. 
 * @author prasanta
 */
public class ButtonHandler extends Activity implements WeatherCallback, OnClickListener {

	String TAG = getClass().getName();
	WeatherRecord record;
	
	final int MENU_DELETE = 0xA1;
	final int MENU_EXIT = 0xA2;
	final int MENU_ABOUT = 0xA3;
	String city;
	ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pref_screen);
		// add listener
		Button add = (Button)findViewById(R.id.add);
		add.setOnClickListener(this);
		Intent intent = getIntent();
		/*
		 * Process request from CityListActivity
		 */
		String showToast = intent.getStringExtra("showToast");
		if(showToast != null){
			// I can recognize, you are coming from CityListActivity.java
			Log.v(TAG, "show Toast of CityListActivity....");
			String msg = intent.getStringExtra("msg");
			Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			boolean isSuccess = intent.getBooleanExtra("isSuccess", false);
			if(isSuccess)
				addCityInToPersistentStore();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_DELETE, 0, "Delete");
		menu.add(0, MENU_ABOUT, 0, "About");
		menu.add(0, MENU_EXIT, 0, "Exit");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case MENU_DELETE:
			if(WeatherRecord.airportCodes.size() == 0){
				Toast.makeText(this.getApplicationContext(), "Nothing to delete", Toast.LENGTH_LONG).show();
				return false;
			}
			Intent intent = new Intent(this, CityListActivity.class);
			intent.putExtra("type", "delete");
			startActivity(intent);
		break;
		case MENU_ABOUT:
			Intent about_intent = new Intent(this, AboutActivity.class);
			startActivity(about_intent);
		break;
		case MENU_EXIT:
			finish();
		break;
		}
		return false;
	}

	/**
	 * It will save city information in SharedPreference
	 */
	private void addCityInToPersistentStore(){
		/*
		 * WeatherRecord.airportCodes structure should be updated in Synchronized manner
		 */
		synchronized (WeatherRecord.airportCodes) {
			 // Save some variable
	        SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = spref.edit();
			// store the latest city count
			editor.putString(WeatherRecord.CITY_COUNT_LABEL, ""+WeatherRecord.airportCodes.size());
			editor.putString(WeatherRecord.airportCodes.getLast(), WeatherRecord.airportCodes.getLast());
			// Commit changes
			//Log.i(TAG, "Storing in SP: "+ WeatherRecord.airportCodes.getLast());
	 	    editor.commit();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		Log.i(TAG, "Button clicked...");
		if(v.getId() == R.id.add){
			// Show progress dialog
			dialog = new ProgressDialog(this);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setTitle("Adding City");
			dialog.setMessage("Please wait...");
			dialog.show();
			/*
			 * Search/Add City
			 */
			city = ((EditText)findViewById(R.id.city_name)).getText().toString();
			// make the text box blank
			((EditText)findViewById(R.id.city_name)).setText("");
			record = new WeatherRecord(this);
			record.findCity(city);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.easy.weather.WeatherRecord.WeatherCallback#update(int, java.lang.Object)
	 */
	public void update(int what, Object obj) {
		record.resetListener();
		record = null;
		/*
		 * Operation over, you can now relax :-)
		 */
		if(dialog.isShowing())
			dialog.cancel();
		
		if(what == WeatherRecord.NO_MATCH){
			Log.i(TAG, "No Match Found: "+ (String)obj);
			Intent intent = new Intent(this, CityListActivity.class);
			intent.putExtra("type", "no_match");
			intent.putExtra("msg", "Unable to add "+ city);
			startActivity(intent);
			//Toast.makeText(this.getApplicationContext(), "Unable to add "+ (String)obj, Toast.LENGTH_SHORT).show();
			return;
		}
		if(what == WeatherRecord.NO_CONNECTION_FOUND){
			Log.i(TAG, "No Connection Found: "+ (String)obj);
			Intent intent = new Intent(this, CityListActivity.class);
			intent.putExtra("type", "no_match");
			intent.putExtra("msg", (String)obj);
			startActivity(intent);
			//Toast.makeText(this.getApplicationContext(), "Unable to add "+ (String)obj, Toast.LENGTH_SHORT).show();
			return;
		}
		if(what == WeatherRecord.FIND_CITY){
			if(obj == null){
				/*
				 * There was no multiple choice for your selected
				 * City 
				 */
				// save this into persistent store
				addCityInToPersistentStore();
				// Go to next activity to display Toast. Handler throws exception.
				//Toast.makeText(this.getApplicationContext(), city +" added "+ (String)obj, Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(this, CityListActivity.class);
				intent.putExtra("type", "no_multi");
				intent.putExtra("msg", city +" is added ");
				startActivity(intent);
				return;
			}
				
			ArrayList<City> cities = (ArrayList<City>)obj;
			/*
			 * FIXME: Implement Percelabel in City class
			 * and make it suitable to pass through intent. Instead of creating
			 * 2 seperate ArrayLists for name and links
			 */
			ArrayList<String> names = new ArrayList<String>();
			ArrayList<String> links = new ArrayList<String>();
			for(int i=0; i<cities.size(); i++){
				City c = (City) cities.get(i);
				names.add(c.name);
				links.add(c.link);
			}
			Intent intent = new Intent(this, CityListActivity.class);
			intent.putExtra("type", "add");
			intent.putStringArrayListExtra("names", names);
			intent.putStringArrayListExtra("links", links);
			startActivity(intent);
			finish();
		}
	}
	
}
