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

package com.pras.weather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;

import com.pras.weather.DataParser.City;

/**
 * Maximum 10 City stations can be followed at a time
 * 
 * @author prasanta
 *
 */
public class WeatherRecord implements Callback {

	// URL for weather details
	private final String BASE_URL = "http://api.wunderground.com/auto/wui/geo/WXCurrentObXML/index.xml";
	/**
	 * Number of cities can be added at a time to track their weather
	 */
	private final int MAX_CITY_COUNT = 10;
	/*
	 * Number of selected cities and their Airport code will be persisted in SharedPreference
	 * SharedPreference Labels
	 */
	public static final String CITY_COUNT_LABEL = "CITY_COUNT";
	public static final String AIRPORT_LABEL = "AIRPORT";
	
	public static HashMap<String, Weather> weatherDetails = new HashMap<String, Weather>();
	
	/**
	 * Stores the Airport code of different cities
	 * You can track max 10 cities
	 */
	public static LinkedList<String> airportCodes = new LinkedList<String>();
	
	String TAG = getClass().getName();
	/**
	 * this handler will be used only for WEATHER_UPDATE messages
	 */
	//public Handler handler;
	WeatherCallback listener;
	
	public static int city_index = 0;
	/*
	 * Handler messages
	 */
	public static final int NO_MATCH = 0xA0;
	public static final int WEATHER_UPDATE = 0xA1;
	public static final int FIND_CITY = 0xA2;
	public static final int ADD_CITY = 0xA3;
	public static final int NO_CONNECTION_FOUND = 0xE1;
	
	public WeatherRecord(){}
	
	public WeatherRecord(WeatherCallback listener){
		this.listener = listener;
	}
	
	public void resetListener(){
		listener = null;
	}
	
	/**
	 * Update weather records for each City
	 */
	public synchronized void updateWeather(){
		
		Log.i(TAG, "Update Weather records....");
		// Create a new Handler
		HandlerThread handlerThread = new HandlerThread("Weather_Record_Update");
		handlerThread.start();
		Handler handler = new Handler(handlerThread.getLooper(), this);
		
		for(int i=0; i<airportCodes.size(); i++){
			try{
				String url = BASE_URL.concat("?query=").concat(airportCodes.get(i));
				Message msg = handler.obtainMessage();
				String[] info = {url, airportCodes.get(i)};
				msg.what = WEATHER_UPDATE;
				msg.obj = info;
				handler.sendMessage(msg);
			}catch(Exception ex){
				Log.i(TAG, "Error: "+ ex.toString());
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * Find City- this query may result into multiple matching city names
	 * through out the world. If this is the case, user will be prompted
	 * with the list of these names and they need to find out their relevant City.
	 * 
	 * @param cityName
	 */
	public synchronized void findCity(String cityName){
		Log.v(TAG, "findCity: "+ cityName);
		String url = "http://api.wunderground.com/auto/wui/geo/GeoLookupXML/index.xml?query=".concat(cityName);
		// Create a new Handler
		HandlerThread handlerThread = new HandlerThread("City_Search");
		handlerThread.start();
		Handler handler = new Handler(handlerThread.getLooper(), this);
		Message msg = handler.obtainMessage();
		msg.what = FIND_CITY;
		msg.obj = url;
		handler.sendMessage(msg);
	}
	
	/**
	 * Add a City code
	 * @param urlSubString- selected City URL e.g. /global/stations/42182.html
	 */
	public synchronized void addCity(String urlSubString){
		Log.v(TAG, "addCity: "+ urlSubString);
		String url = "http://api.wunderground.com/auto/wui/geo/GeoLookupXML".concat(urlSubString);
		// Create a new Handler
		HandlerThread handlerThread = new HandlerThread("City_Add");
		handlerThread.start();
		Handler handler = new Handler(handlerThread.getLooper(), this);
		Message msg = handler.obtainMessage();
		msg.what = ADD_CITY;
		msg.obj = url;
		handler.sendMessage(msg);
	}
	/**
	 * This will send weather data stored in weatherDetails repeatedly
	 * @return
	 */
	public static Weather getNextCityWeather(){
		/*
		 * While weatherDetails is getting updated with background
		 * Handler, it shouldn't be read
		 */
		synchronized (weatherDetails) {
			String code = airportCodes.get(city_index);
			Log.i("WeatherRecord", "Display Weather of: "+ code+ " "+ airportCodes.size());
			city_index++;
			if(city_index >= airportCodes.size())
				city_index = 0;
			Weather w = weatherDetails.get(code);
			return w;
		}
	}

	public boolean handleMessage(Message msg) {
		Log.i(TAG, "Handle Message..."+ msg.what);
		switch(msg.what){
		case WEATHER_UPDATE:
			String[] info = (String[]) msg.obj;
			String url = info[0];
			String code = info[1];
			
			try{
				HttpXML http = new HttpXML();
				http.connect(url);
				String data = http.getXML();
				
				// Check for invalid Response 
				if(data == null || data.trim().length() == 0){
					Handler handler = msg.getTarget();
					if(!handler.hasMessages(WEATHER_UPDATE)){
						handler.getLooper().quit();
						handler = null;
					}
					return false;
				}
				
				// Parse XML response
				DataParser parser = new DataParser();
				System.out.println("Parsing data....");
				parser.parse(data.getBytes());
				
				Weather w = parser.w;
				
				if(w == null)
					Log.i(TAG, "No weather details...");
				else
					Log.i(TAG, "Weather details: \n"+ w.toString());
				
				// update weather details
				weatherDetails.put(code, w);
				
			}
			catch(Exception ex){
				Log.i(TAG, "Error: "+ ex.toString());
				ex.printStackTrace();
			}finally{
				Handler handler = msg.getTarget();
				if(!handler.hasMessages(WEATHER_UPDATE)){
					handler.getLooper().quit();
					handler = null;
				}
			}
		break;
		case FIND_CITY:
			url = (String)msg.obj;
			try{
				HttpXML http = new HttpXML();
				Log.i(TAG, "Connecting to: "+ url);
				http.connect(url);
				String data = http.getXML();
				
				// Check for invalid Response 
				if(data == null || data.trim().length() == 0){
					msg.getTarget().getLooper().quit();
					return false;
				}
				
				// Parse XML response
				DataParser parser = new DataParser();
				Log.i(TAG, "Parsing data....");
				parser.parse(data.getBytes());
				
				if(parser.wuiError != null){
					/*
					 * No match found
					 */
				    listener.update(NO_MATCH, "Search not found");
					msg.getTarget().getLooper().quit();
					return false;
				}
				ArrayList<City> cities = parser.cities;
				if(cities == null || cities.size() == 0){
					/*
					 * There is no multiple search result for City query.
					 * Read the Airport Code and Add that
					 */
					code = parser.airportCode;
					Log.i(TAG, "Adding Airport Code..."+ code);
					if(code == null){
						Log.i(TAG, "No Airport Code: "+ code);
						listener.update(NO_MATCH, "No weather station/airport available");
						msg.getTarget().getLooper().quit();
						return false;
					}
					if(airportCodes.size() < MAX_CITY_COUNT){
						airportCodes.add(code);
					}
					else{
						synchronized (weatherDetails) {
							// removed the top most airport code to make space
							String delCode = airportCodes.remove();
							// remove the entry from weatherDetails store
							weatherDetails.remove(delCode);
							airportCodes.add(code);
						}
					}
					/*
					 * Ask for weather update for all cities, with the newly added city as first index
					 * FIXME: following call shouldn't conflict with scheduled weather update
					 */
					city_index = airportCodes.indexOf(code);
					updateWeather();
					listener.update(FIND_CITY, null);
					Log.i(TAG, "Size of stored Airport Codes: "+ airportCodes.size());
				}
				else{
					/*
					 * Multiple search result for City Query
					 * Give a prompt to user to select one
					 */
					Log.i(TAG, "Number of matched cities..."+ cities.size());
					if(listener != null){
						listener.update(msg.what, cities);
					}
				}
			}catch(IOException iox){
				 listener.update(NO_CONNECTION_FOUND, "Check your data connection/Wifi");
			}
			catch(Exception ex){
				Log.i(TAG, "Error: "+ ex.toString());
				ex.printStackTrace();
			}finally{
				msg.getTarget().getLooper().quit();
			}
		break;
		case ADD_CITY:
			url = (String)msg.obj;
			try{
				HttpXML http = new HttpXML();
				Log.i(TAG, "Connecting to: "+ url);
				http.connect(url);
				String data = http.getXML();
				
				// Check for invalid Response 
				if(data == null || data.trim().length() == 0){
					msg.getTarget().getLooper().quit();
					listener.update(ADD_CITY, "No Result from Server");
					return false;
				}
				
				// Parse XML response
				DataParser parser = new DataParser();
				System.out.println("Parsing data....");
				parser.parse(data.getBytes());
				code = parser.airportCode;
				
				// save Airport ID
				if(code != null){
					if(airportCodes.size() < MAX_CITY_COUNT)
						airportCodes.add(code);
					else{
						synchronized (weatherDetails) {
							// removed the top most airport code to make space
							String delCode = airportCodes.remove();
							// remove the entry from weatherDetails store
							weatherDetails.remove(delCode);
							airportCodes.add(code);
						}
					}
					/*
					 * Ask for weather update for all cities, with the newly added city as first index
					 * FIXME: following call shouldn't conflict with scheduled weather update
					 */
					city_index = airportCodes.indexOf(code);
					listener.update(ADD_CITY, null);
					updateWeather();
				}
			}catch(IOException iox){
				 listener.update(NO_CONNECTION_FOUND, "Check your data connection/Wifi");
			}
			catch(Exception ex){
				Log.i(TAG, "Error: "+ ex.toString());
				ex.printStackTrace();
			}finally{
				msg.getTarget().getLooper().quit();
				listener = null;
			}
		break;
		}
		
		return false;
	}
	
	public interface WeatherCallback{
		public void update(int what, Object obj);
	}
}
