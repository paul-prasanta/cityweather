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
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pras.weather.Weather;

public class CityDeleteView extends LinearLayout {

	public CityDeleteView(Context context, String airportCode, Weather w) {
		super(context);
		
		// TextView- Airport Code
        LinearLayout.LayoutParams acParams = 
            new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		acParams.setMargins(5, 3, 5, 3);
		
		TextView code = new TextView(context);
		code.setText(airportCode);
		code.setTextSize(20f);
		code.setTextColor(Color.WHITE);
		code.setWidth(50);
		code.setLines(1);
		code.setMarqueeRepeatLimit(-1);
		addView(code, acParams);
		
		// TextView- City
		LinearLayout.LayoutParams cityParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		cityParams.setMargins(5, 3, 5, 3);

		TextView city = new TextView(context);
		if(w != null)
			city.setText(w.getCity());
		else
			city.setText("-");
		city.setTextSize(20f);
		city.setWidth(100);
		city.setLines(1);
		city.setMarqueeRepeatLimit(-1);
		city.setTextColor(Color.WHITE);
		addView(city, cityParams);
		
		// TextView- Country
		LinearLayout.LayoutParams countryParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		countryParams.setMargins(5, 3, 5, 3);

		TextView country = new TextView(context);
		if(w != null)
			country.setText(w.getCountry());
		else
			country.setText("-");
		country.setTextSize(20f);
		country.setWidth(50);
		country.setLines(1);
		country.setMarqueeRepeatLimit(-1);
		country.setTextColor(Color.WHITE);
		addView(country, countryParams);
		
		// TextView- Weather
		/*LinearLayout.LayoutParams weatherParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		weatherParams.setMargins(5, 3, 5, 3);

		TextView weather = new TextView(context);
		weather.setText(w.getWeather());
		weather.setTextSize(20f);
		weather.setTextColor(Color.WHITE);
		addView(weather, weatherParams);*/
	}
}
