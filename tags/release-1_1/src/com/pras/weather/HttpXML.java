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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

/**
 * Weather Service: www.wunderground.com
 * API Document: http://wiki.wunderground.com/index.php/API_-_XML
 * City details:
 * http://api.wunderground.com/auto/wui/geo/GeoLookupXML/index.xml?query=<city_name>
 * Read Airport ID from above URL-
 * <icao>VEGT</icao> - which is the Airport ID of Guwahati
 * 
 * Weather details
 * http://api.wunderground.com/auto/wui/geo/WXCurrentObXML/index.xml?query=VEGT 
 * 
 * @author prasanta
 */
public class HttpXML {

	String TAG = getClass().getName();
	// URL for city details
	//private String url_ = "http://api.wunderground.com/auto/wui/geo/GeoLookupXML/index.xml?query=94107";
	HttpURLConnection http_;
	
	public void connect(String url) throws IOException {
		// Set Proxy
		System.setProperty("http.proxyHost", "168.219.61.250");
		System.setProperty("http.proxyPort", "8080");
		
		Log.i(TAG, "Connecting to.."+ url);
		http_ = (HttpURLConnection)new URL(url).openConnection();
		http_.connect();
		if(http_.getResponseCode() != HttpURLConnection.HTTP_OK){
			http_.disconnect();
			http_ = null;
			new IOException("Error in connection: "+ http_.getResponseCode());
		}
	}
	
	public String getXML() throws IOException {
		if(http_ == null)
			return "";
		
		InputStream is = http_.getInputStream();
		StringBuffer strBuf = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = "";
		while((line = reader.readLine()) != null)
			strBuf.append(line+"\n");
		reader.close();
		is.close();
		http_.disconnect();
		//Log.i(TAG, "Response: "+ strBuf.toString());
		return strBuf.toString();
	}
}
