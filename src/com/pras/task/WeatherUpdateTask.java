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

import com.pras.weather.WeatherRecord;

import android.util.Log;

/**
 * Weather update Task. This will be invoked periodicaly
 * @author prasanta
 *
 */
public class WeatherUpdateTask extends TimerTask {

	String TAG = this.getClass().getName();
	
	Timer myTimer;
	public WeatherUpdateTask(Timer mytimer){
		this.myTimer = mytimer;
	}
	
	@Override
	public void run() {
		Log.i(TAG, "Weather record update....");
		WeatherRecord wr = new WeatherRecord();
		wr.updateWeather();
	}

	@Override
	public boolean cancel() {
		if(myTimer != null)
			myTimer.cancel();
		myTimer = null;
		return super.cancel();
	}
}
