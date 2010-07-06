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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Parcelable class which holds forecast data of each day
 * 
 * @author prasanta
 */
public class Forecast implements Parcelable {

	String day;
	String date;
	String lowTemp;
	String highTemp;
	String condition;
	
	public static final Parcelable.Creator<Forecast> CREATOR = new MyCreator(); 
	
	public Forecast(){}
	
	/**
	 * Get the data back from Parcel
	 * @param src
	 */
	public Forecast(Parcel src){
		day = src.readString();
		date = src.readString();
		lowTemp = src.readString();
		highTemp = src.readString();
		condition = src.readString();
	}
	
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		/*
		 * Input Date: 11:30 PM IST on July 02, 2010
		 * Required Data: July 02, 2010
		 */
		this.date = date;
		if(date == null)
			return;
		String[] subs = date.split(" ");
		if(subs.length != 7)
			// not in expected format
			return;
		// Month Day Year
		date = date.concat(subs[4]).concat(subs[5]).concat(subs[6]);
	}
	public String getLowTemp() {
		return lowTemp;
	}
	public void setLowTemp(String lowTemp) {
		this.lowTemp = lowTemp;
	}
	public String getHighTemp() {
		return highTemp;
	}
	public void setHighTemp(String highTemp) {
		this.highTemp = highTemp;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	public int describeContents() {
		return hashCode();
	}
	
	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(day);
		dest.writeString(date);
		dest.writeString(lowTemp);
		dest.writeString(highTemp);
		dest.writeString(condition);
	}
	
	/**
	 * Creator class used for reading data back from Parcel
	 * @author prasanta
	 */
	public static class MyCreator implements Parcelable.Creator<Forecast> {

		String TAG = MyCreator.class.getName();
		
		public MyCreator(){
			super();
		}
		
		public Forecast createFromParcel(Parcel source) {
			//Log.v(TAG, "createFromParcel....");
			return new Forecast(source);
		}

		public Forecast[] newArray(int size) {
			//Log.v(TAG, "newArray..."+ size);
			return new Forecast[size];
		}
	}
}
