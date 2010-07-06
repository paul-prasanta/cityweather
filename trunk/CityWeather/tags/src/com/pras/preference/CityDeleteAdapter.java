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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.pras.weather.Weather;
import com.pras.weather.WeatherRecord;

public class CityDeleteAdapter extends BaseAdapter {
	
	Context context;
	
	public CityDeleteAdapter(Context context){
		this.context = context;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		//return WeatherRecord.weatherDetails.size();
		return WeatherRecord.airportCodes.size();
	}

	public Object getItem(int position) {
		//return (String)WeatherRecord.weatherDetails.keySet().toArray()[position];
		return WeatherRecord.airportCodes.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		//String airportCode = (String)WeatherRecord.weatherDetails.keySet().toArray()[position];
		String airportCode = WeatherRecord.airportCodes.get(position);
		Weather w = WeatherRecord.weatherDetails.get(airportCode);
		return new CityDeleteView(context, airportCode, w);
	}
}
