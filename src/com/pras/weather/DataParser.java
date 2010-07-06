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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parses incoming weather details using SAX Parser. It uses
 * DefaultHandler's callback to process nodes and their values
 * 
 * @author prasanta
 */
public class DataParser extends DefaultHandler {

	final String TAG = getClass().getName();
	String node = null;
	public Weather w = null;
	private City city;
	public ArrayList<City> cities;
	public String airportCode = null;
	public String wuiError = null;
	
	// nodes of XML response sent by Server
	final String CITY = "city";
	final String COUNTRY = "country";
	final String WEATHER = "weather";
	final String TEMPERATURE_STRING = "temperature_string";
	final String RELATIVE_HUMIDITY = "relative_humidity";
	final String LOCAL_TIME = "local_time";
	final String AIRPORT_CODE = "icao";
	final String CITY_NAME = "name";
	final String LINK = "link";
	final String LOCATION = "location";
	final String LOCATION_START = "locations";
	final String WUI_ERROR = "wui_error";
	
	public void parse(byte[] data){
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try{
			SAXParser parser = factory.newSAXParser();
			parser.parse(new ByteArrayInputStream(data), this);
		}catch(Exception ex){
			System.out.println("Error in parsing: "+ ex.toString());
			ex.printStackTrace();
		}
		
		if(w.isEmpty())
			w = null;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if(node == null)
			return;
		
		else if(node.equals(WEATHER)){
			//Log.v(TAG, "==> "+ WEATHER);
			w.setWeather(new String(ch).substring(start, start+length));
		}
		else if(node.equals(TEMPERATURE_STRING)){
			//Log.v(TAG, "==> "+ TEMPERATURE_STRING);
			w.setTemperature(new String(ch).substring(start, start+length));
		}
		else if(node.equals(RELATIVE_HUMIDITY)){
			//Log.v(TAG, "==> "+ RELATIVE_HUMIDITY);
			w.setHumidity(new String(ch).substring(start, start+length));
		}
		else if(node.equals(LOCAL_TIME)){
			//Log.v(TAG, "==> "+ LOCAL_TIME);
			w.setLocal_time(new String(ch).substring(start, start+length));
		}
		else if(node.equals(CITY)){
			//Log.v(TAG, "==> "+ CITY);
			w.setCity(new String(ch).substring(start, start+length));
		}
		else if(node.equals(COUNTRY)){
			if(w != null)
				w.setCountry(new String(ch).substring(start, start+length));
		}
		else if(node.equals(AIRPORT_CODE)){
			airportCode = new String(ch).substring(start, start+length);
		}
		else if(node.equals(CITY_NAME)){
			if(city != null)
				city.name = new String(ch).substring(start, start+length);
		}
		else if(node.equals(LINK)){
			if(city != null)
				city.link = new String(ch).substring(start, start+length);
		}
		else if(node.equals(WUI_ERROR)){
			wuiError = WUI_ERROR;
		}
		//Log.v(TAG, "characters: "+ new String(ch).substring(start, start+length));//+ new String(ch));
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		//Log.v(TAG, "startDocument");
		w = new Weather();
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if(name.trim().length() == 0)
			node = localName;
		else
			node = name;
		
		if(node.equals(LOCATION_START)){
			cities = new ArrayList<City>();
		}
		else if(node.equals(LOCATION)){
			city = new City();
		}
	}
	
	@Override
	public void endDocument() throws SAXException {
		//Log.v(TAG, "endDocument");
		super.endDocument();
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		//Log.v(TAG, "endElement -->"+ name +" | "+ localName);
		super.endElement(uri, localName, name);
		
		if(name.trim().length() == 0)
			node = localName;
		else
			node = name;
		
		if(node == null)
			return;
		
		if(node.equals(LOCATION)){
			if(cities != null)
				cities.add(city);
			city = null;
		}
		node = null;
	}
	
	/**
	 * City details
	 * @author prasanta
	 */
	public class City {
		public String name;
		public String link;
		public String airportCode;
	}
}
