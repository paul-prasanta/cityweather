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

package com.pras;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.pras.task.RemoteViewTask;
import com.pras.task.WeatherUpdateTask;
import com.pras.weather.WeatherRecord;

/**
 * 
 * The main class of the widget.
 * 
 * @author prasanta
 *
 */
public class CityWidget extends AppWidgetProvider {

	final String TAG = getClass().getName();
	Timer timer;
	/**
	 * Weather Update Interval- every 3 hours
	 */
	final int WEATHER_UPDATE_INTERVAL = 3*60*60*1000;
	/**
	 * This ArrayList will be used to close Task instances
	 */
	private static ArrayList<TimerTask> taskSchedules = new ArrayList<TimerTask>();

	public CityWidget(){
		super();
		Log.i(TAG, "<CityWidget>");
	}
	
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.i(TAG, "onEnabled_____");
		WeatherRecord.weatherDetails.clear();
		WeatherRecord.airportCodes.clear();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Log.v(TAG, "onReceive_____");
		String action = intent.getAction();
		Log.i(TAG, "Intent Action: "+ action);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Log.v(TAG, "onUpdate_____");
		/*
		 * Restore previous persistent state
		 */
		restoreStoredData(context);
		
		if (taskSchedules.size() < 2) {
			// Weather Widget
			Timer uiTimer = new Timer();
			RemoteViewTask uiTask = new RemoteViewTask(appWidgetManager,
					context, R.layout.city_weather, uiTimer);
			uiTimer.schedule(uiTask, 0, 5000);
			taskSchedules.add(uiTask);
			// Weather Record update
			Timer conTimer = new Timer();
			WeatherUpdateTask updateTask = new WeatherUpdateTask(conTimer);
			conTimer.schedule(updateTask, 0, WEATHER_UPDATE_INTERVAL);// 65000
			taskSchedules.add(updateTask);
		}
	}
	
	/**
	 * Read previously stored Airport codes
	 * @param context
	 */
	private void restoreStoredData(Context context){
		/*
		 * WeatherRecord.airportCodes structure should be updated in Synchronized manner
		 */
		synchronized (WeatherRecord.airportCodes) {
			 // Read stored values
	        SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(context);
			int count = WeatherRecord.airportCodes.size();
			if(count > 0){
				/*
				 * Data is already retrieved from persistent store
				 */
				return;
			}
			String countStr = spref.getString(WeatherRecord.CITY_COUNT_LABEL, "0");
			Log.i(TAG, "Stored value: "+ countStr);
			int storedCount = Integer.parseInt(countStr);
			if(storedCount == 0){
				/*
				 * No previous data stored
				 */
				return;
			}
			Log.v(TAG, "Number of stored Cities: "+ storedCount);
			// Read stored data
			HashMap<String, String> storedData = (HashMap<String, String>)spref.getAll();
			Object[] list = storedData.keySet().toArray();
			if(list.length <= 0){
				/*
				 * ignore the first entry "CITY_COUNT"
				 */
				return;
			}
			
			for(int i=0; i<list.length; i++){
				String code = (String)list[i];
				//Log.i(TAG, "Stored Code: "+ code);
				if(!code.equals(WeatherRecord.CITY_COUNT_LABEL))
					WeatherRecord.airportCodes.add(code);
			}
			
			if(WeatherRecord.airportCodes.size() > 0){
				/*
				 * fetch Weather details over the network
				 */
				WeatherRecord wr = new WeatherRecord();
				wr.updateWeather();
			}
		}
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		Log.v(TAG, "onDeleted_____"+ timer);
		Log.v(TAG, "Stored Schedules: "+ taskSchedules.size());
		
		/*
		 * Cancel any scheduled task
		 */
		synchronized (this) {
			for(int i=0; i<taskSchedules.size(); i++){
				if(taskSchedules.get(i) instanceof RemoteViewTask){
					RemoteViewTask rt = (RemoteViewTask)taskSchedules.get(i);
					rt.cancel();
				}
				else if(taskSchedules.get(i) instanceof WeatherUpdateTask){
					WeatherUpdateTask wt = (WeatherUpdateTask)taskSchedules.get(i);
					wt.cancel();
				}
				else{
					TimerTask t = (TimerTask)taskSchedules.get(i);
					t.cancel();
				}
			}
			taskSchedules.clear();
		}
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Log.v(TAG, "onDisabled_____"+ timer);
		Log.v(TAG, "Stored Schedules: "+ taskSchedules.size());
		/*
		 * Cancel any scheduled task
		 */
		synchronized (this) {
			for(int i=0; i<taskSchedules.size(); i++){
				TimerTask t = (TimerTask)taskSchedules.get(i);
				t.cancel();
			}
			taskSchedules.clear();
		}
	}
}
