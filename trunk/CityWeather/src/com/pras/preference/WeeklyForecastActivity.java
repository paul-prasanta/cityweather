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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pras.R;
import com.pras.weather.Forecast;

/**
 * This class displays weekly forecast details
 * @author prasanta
 *
 */
public class WeeklyForecastActivity extends Activity {

	final String TAG = getClass().getName();
	ArrayList<Forecast> weekly;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		String type = intent.getStringExtra("type");
		if(type == null){
			finish();
			return;
		}
		
		if(type.equals("error")){
			String msg = intent.getStringExtra("msg");
			Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			finish();
		}
		else if(type.equals("show_forecast")){
			weekly = intent.getParcelableArrayListExtra("forecast_details");
			if(weekly == null){
				finish();
				return;
			}
			String city = intent.getStringExtra("city");
			createForecastScreen(city, weekly);
		}
		else
			finish();
	}
	
	private void createForecastScreen(String city, ArrayList<Forecast> fs){
		Log.i(TAG, "Weather Forecast for: "+ city);
		Log.i(TAG, "Number of days: "+ fs.size());
		
		setContentView(R.layout.forecast);
		
		// Update City name
		TextView title = (TextView)findViewById(R.id.title);
		title.setText(city);
		
		/*
		 * FIXME: Server (www.wunderground.com) is returning incorrect local time
		 * Update Local Time
		 */
		/*TextView local_time = (TextView) findViewById(R.id.current_time);
		Forecast tmpf = fs.get(0);
		local_time.setText(tmpf.getDate());*/
		
		LinearLayout layout = (LinearLayout)findViewById(R.id.forecast_main);
		
		// Create a Layout Inflater
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		

		for(int i=0; i<fs.size(); i++){
			
			Forecast f = fs.get(i);
			View dayView = inflater.inflate(R.layout.day_view, null);
			
			Log.i(TAG, "Forecast Day: "+ f.getDay());
			// Update Day
			TextView day = (TextView)dayView.findViewById(R.id.day);
			day.setText(f.getDay());
			
			// Update Temperature
			TextView tempv = (TextView) dayView.findViewById(R.id.tempv);
			tempv.setText(f.getLowTemp()+" - "+ f.getHighTemp());
			
			// Update Weather Condition
			TextView weatherv = (TextView) dayView.findViewById(R.id.weatherv);
			weatherv.setText(f.getCondition());
			
			dayView.invalidate();
			
			// Add the view into Layout
			layout.addView(dayView);
		}
	}
}
