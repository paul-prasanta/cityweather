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

/**
 * [] City1
 * [*] City2
 * [*] City3
 * @author prasanta
 *
 */
public class CityView extends LinearLayout {

	public CityView(Context context, String city) {
		super(context);
		
        // TextView- City Name
        LinearLayout.LayoutParams txtParams = 
            new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		txtParams.setMargins(5, 3, 5, 3);
		
		TextView cityName = new TextView(context);
		cityName.setText(city);
		cityName.setTextSize(20f);// 14f
		cityName.setTextColor(Color.WHITE);
		addView(cityName, txtParams);
	}

}
