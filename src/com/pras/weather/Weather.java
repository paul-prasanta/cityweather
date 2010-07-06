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

/**
 * Weather details
 * @author prasanta
 */
public class Weather {
	String city;
	String country;
	String weather;
	String temperature;
	String humidity;
	String local_time;
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public String getHumidity() {
		return humidity;
	}
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}
	public String getLocal_time() {
		return local_time;
	}
	public void setLocal_time(String local_time) {
		this.local_time = local_time;
	}

	public boolean isEmpty(){
		if(city == null && weather == null && temperature == null && humidity == null && local_time == null)
			return true;
		return false;
	}
	
	@Override
	public String toString(){
		String str = "";
		if(city != null)
			str = str.concat(city).concat(" ");
		if(weather != null)
			str = str.concat(weather).concat(" ");
		if(temperature != null)
			str = str.concat(temperature).concat(" ");
		if(humidity != null)
			str = str.concat(humidity).concat(" ");
		if(local_time != null)
			str = str.concat(local_time).concat(" ");
		return str;
	}
}
