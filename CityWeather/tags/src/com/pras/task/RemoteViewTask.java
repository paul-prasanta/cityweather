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

package com.pras.task;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.pras.CityWidget;
import com.pras.preference.ButtonHandler;
import com.pras.weather.Weather;
import com.pras.weather.WeatherRecord;

public class RemoteViewTask extends TimerTask {

	Context ctx;
	AppWidgetManager appWidgetManager;
	ComponentName thisWidget;
	Timer myTimer;
	RemoteViews remote;
	String packageName;
	String TAG = this.getClass().getName();
	int layout;
	/*
	 * Actions to be invoked when user press buttons
	 */
	final String ACTION_WIDGET_SETTINGS_BTN = "WIDGET_BTN_SETTINGS";
	
	public RemoteViewTask(AppWidgetManager appWidgetManager, Context ctx, int layout, Timer timer){
		this.appWidgetManager = appWidgetManager;
		this.ctx = ctx;
		this.packageName = ctx.getPackageName();
		this.layout = layout;
		this.myTimer = timer;
		
		thisWidget = new ComponentName(ctx, CityWidget.class);
		remote = new RemoteViews(packageName, layout);
	}

	@Override
	public void run() {
		if(WeatherRecord.airportCodes.size() > 0){
			remote = new RemoteViews(packageName, layout);
			createWeatherRemoteView();
		}else{
			// show loading layout
			remote = new RemoteViews(packageName, com.pras.R.layout.main);
			Intent settingsIntent = new Intent(ctx, ButtonHandler.class);
			settingsIntent.setAction(ACTION_WIDGET_SETTINGS_BTN);
			settingsIntent.putExtra("key", "you clicked on [SETTINGS]");
			PendingIntent settingsPendingIntent = PendingIntent.getActivity(ctx, 0, settingsIntent, 0);
			
			// Set the pending intents for buttons
			remote.setOnClickPendingIntent(com.pras.R.id.setting, settingsPendingIntent);
		}
		appWidgetManager.updateAppWidget(thisWidget, remote);
	}
	
	/**
	 * Remote view
	 * TODO: handle refresh button
	 */
	private void createWeatherRemoteView(){
		Log.v(TAG, "createRemoteView....");
		Intent settingsIntent = new Intent(ctx, ButtonHandler.class);
		settingsIntent.setAction(ACTION_WIDGET_SETTINGS_BTN);
		settingsIntent.putExtra("key", "you clicked on [SETTINGS]");
		PendingIntent settingsPendingIntent = PendingIntent.getActivity(ctx, 0, settingsIntent, 0);
		
		// Set the pending intents for buttons
		remote.setOnClickPendingIntent(com.pras.R.id.setting, settingsPendingIntent);
		//remote.setOnClickPendingIntent(com.pras.R.id.icon_setting, settingsPendingIntent);
		
		// Set Remote Text View
		Weather w = WeatherRecord.getNextCityWeather();
		Log.v(TAG, "weather...."+ w);
		remote.setTextViewText(com.pras.R.id.city_count, "[Cities: "+ WeatherRecord.airportCodes.size() +"/10]");
		if(w != null){
			remote.setTextViewText(com.pras.R.id.city_name, w.getCity()+ " ("+ w.getCountry() +")");
			remote.setTextViewText(com.pras.R.id.weather, "("+ w.getWeather() +")");
			remote.setTextViewText(com.pras.R.id.temp, w.getTemperature()); //Temperature: 
			remote.setTextViewText(com.pras.R.id.humidity, "Humidity: "+ w.getHumidity());
		}
	}
	
	@Override
	public boolean cancel() {
		/*
		 * Close the timer which started it
		 */
		Log.v(TAG, "TimerTask Cancel()");
		boolean flag = super.cancel();
		if(myTimer != null)
			myTimer.cancel();
		return flag;
	}
}
