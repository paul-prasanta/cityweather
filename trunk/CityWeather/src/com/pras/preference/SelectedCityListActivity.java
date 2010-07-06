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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.pras.R;
import com.pras.weather.Forecast;
import com.pras.weather.Weather;
import com.pras.weather.WeatherRecord;
import com.pras.weather.WeatherRecord.WeatherCallback;

/**
 * This class will list all selected cities
 * If you long press on list item, it will prompt for 
 * - delete
 * - weekly forecast
 * @author prasanta
 *
 */
public class SelectedCityListActivity extends ListActivity implements  WeatherCallback {

	private final String TAG = getClass().getName();
	private final int MENU_WEEKLY_FORECAST = 0xA1;
	private final int MENU_DELETE = 0xA2;
	private String type;
	ProgressDialog dialog;
	CityDeleteAdapter deleteAdapter;
	Weather selectedCity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city_list);
		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		
		if(type == null){
			finish();
			return;
		}
		if(type.equals("selected_city_list")){
			/*
			 * Display list of cities which are selected.
			 * User can get the weekly forecast of the selected city
			 * or Delete
			 */
			deleteAdapter = new CityDeleteAdapter(this.getApplicationContext());
			setListAdapter(deleteAdapter);
		}
		else{
			finish();
			return;
		}
		// Register the view where you want the Context Menu
		registerForContextMenu(getListView());
	}

	public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, MENU_WEEKLY_FORECAST, 0, "Weekly Forecast");
		menu.add(0, MENU_DELETE, 0, "Delete");
	}

	public boolean onContextItemSelected(MenuItem item) { 
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo(); 
		
		switch (item.getItemId()) { 
		case MENU_WEEKLY_FORECAST:
			// Show progress dialog
			dialog = new ProgressDialog(this);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setTitle("Retrieving Weekly Forecast");
			dialog.setMessage("Please wait...");
			dialog.show();
			
			// Request for Weekly Forecast details
			WeatherRecord record = new WeatherRecord(this);
			String airportCode = WeatherRecord.airportCodes.get(info.position);
			selectedCity = WeatherRecord.weatherDetails.get(airportCode);
			record.getWeeklyForecast(WeatherRecord.airportCodes.get(info.position));
			return true;
			
		case MENU_DELETE:
			airportCode = WeatherRecord.airportCodes.get(info.position);
			Weather w = WeatherRecord.weatherDetails.get(airportCode);
			String toastMsg = airportCode+ " is deleted";
			if(w != null)
				toastMsg = w.getCity()+" ("+ w.getCountry()+ ") is deleted";
			Toast.makeText(this.getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
			Log.v(TAG, "deleting "+ airportCode);
			// reset index
			WeatherRecord.city_index = 0;
			// remove entries
			WeatherRecord.weatherDetails.remove(airportCode);
			WeatherRecord.airportCodes.remove(airportCode);
			// update persistent store
			deleteCityFromPersistentStore(airportCode);
			// refresh the list
			deleteAdapter.notifyDataSetChanged();
			return true;
		}
		return super.onContextItemSelected(item); 
	}
	
	private void deleteCityFromPersistentStore(String code) {
		/*
		 * WeatherRecord.airportCodes structure should be updated in Synchronized manner
		 */
		synchronized (WeatherRecord.airportCodes) {
			SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = spref.edit();
			// store the latest city count
			editor.putString(WeatherRecord.CITY_COUNT_LABEL,
					""+WeatherRecord.airportCodes.size());
			editor.remove(code);
			// Commit changes
			editor.commit();
		}
	}
			 
	public void update(int what, Object obj) {
		if(dialog != null && dialog.isShowing()){
			dialog.cancel();
			dialog = null;
		}
		
		Intent intent = new Intent(this, WeeklyForecastActivity.class);
		
		if(what == WeatherRecord.WEEKLY_FORECAST){
			ArrayList<Forecast> weeklyForecast = (ArrayList<Forecast>) obj;
			intent.putExtra("type", "show_forecast");
			intent.putParcelableArrayListExtra("forecast_details", weeklyForecast);
			intent.putExtra("city", selectedCity.getCity()+ " "+ selectedCity.getCountry());
		}
		else{
			intent.putExtra("type", "error");
			intent.putExtra("msg", "<Connection Error>");
		}
		startActivity(intent);
		//finish();
	}
}
