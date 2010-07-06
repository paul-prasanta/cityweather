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

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.pras.R;
import com.pras.weather.WeatherRecord;
import com.pras.weather.WeatherRecord.WeatherCallback;

/**
 * 
 * @author prasanta
 *
 */
public class CityListActivity extends ListActivity implements  WeatherCallback {
	
	ArrayList<String> names;
	ArrayList<String> links;
	int add = 1;
	CityAdapter cityAdapter;
	CityDeleteAdapter deleteAdapter;
	ProgressDialog dialog;
	String TAG = getClass().getName();
	String type;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city_list);
		
		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		
		if(type == null){
			finish();
		}
		
		if(type.equals("add")){
			Toast.makeText(this.getApplicationContext(), "Found multiple matching cities...", Toast.LENGTH_SHORT).show();
			names = intent.getStringArrayListExtra("names");
			links = intent.getStringArrayListExtra("links");
			
			cityAdapter = new CityAdapter(this.getApplicationContext(), names);
			setListAdapter(cityAdapter);
		}
		else if(type.equals("no_match") || type.equals("no_multi")){
			String msg = intent.getStringExtra("msg");
			Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			finish();
		}
		else{
			finish();
		}
	}

	/* (non-Javadoc)
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public synchronized void onListItemClick(ListView l, View v, int position, long id){
		
		if(type.equals("add")){
			Log.v(TAG, "Selected City: "+ names.get(position) +" Link: "+ links.get(position));
			// Show progress dialog
			dialog = new ProgressDialog(this);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setTitle("Adding City");
			dialog.setMessage("Please wait...");
			dialog.show();
			// Add the city
			WeatherRecord record = new WeatherRecord(this);
			record.addCity(links.get(position));
		}
	}

	public void update(int what, Object obj) {
		/*
		 * Operation over, you can now relax :-)
		 * FIXME: Can't show Toast here, as it is getting invoked from
		 * the Handler of a seperate thread.
		 */
		if(dialog.isShowing())
			dialog.cancel();
		
		Intent intent = new Intent(this, ButtonHandler.class);
		/*
		 * If I set Intent.FLAG_ACTIVITY_REORDER_TO_FRONT, ButtonHandler.java
		 * doesn't get updated Intent extras which are set here.
		 */
		//intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra("showToast", "yes");
		Log.v(TAG, "response for Add City ");
		if(what == WeatherRecord.ADD_CITY){
			if(obj == null){
				// City Add is success
				intent.putExtra("msg", WeatherRecord.airportCodes.getLast()+" is added");
				intent.putExtra("isSuccess", true);
			}
			else{
				// failed to add City
				intent.putExtra("msg", "failed to add "+ WeatherRecord.airportCodes.getLast()+ ": "+ (String) obj);
				intent.putExtra("isSuccess", false);
			}
		}
		else if(what == WeatherRecord.NO_CONNECTION_FOUND){
			// failed to add City
			intent.putExtra("msg", "failed to add "+ WeatherRecord.airportCodes.getLast()+ ": "+ (String) obj);
			intent.putExtra("isSuccess", false);
		}
		startActivity(intent);
		finish();
	}
}
