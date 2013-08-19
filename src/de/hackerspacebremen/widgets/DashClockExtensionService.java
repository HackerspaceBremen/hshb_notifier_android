/*
 * Hackerspace Bremen Android App - An Open-Space-Notifier for Android
 * 
 * Copyright (C) 2012 Steve Liedtke <sliedtke57@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; either version 3 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See 
 * the GNU General Public License for more details.
 * 
 * You can find a copy of the GNU General Public License on http://www.gnu.org/licenses/gpl.html.
 * 
 * Contributors:
 *     Steve Liedtke <sliedtke57@gmail.com>
 */
package de.hackerspacebremen.widgets;

import java.util.Date;

import android.content.Intent;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import de.greenrobot.event.EventBus;
import de.hackerspacebremen.R;
import de.hackerspacebremen.StartActivity;
import de.hackerspacebremen.event.DataEvent;
import de.hackerspacebremen.format.SpeakingDateFormat;
import de.hackerspacebremen.valueobjects.parser.SpaceDataJsonParser;

/**
 * @author Steve
 *
 */
public class DashClockExtensionService extends DashClockExtension{

	/* (non-Javadoc)
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onUpdateData(int)
	 */
	@Override
	protected void onUpdateData(final int arg0) {
		
		final DataEvent statusEvent = (DataEvent) EventBus.getDefault().getStickyEvent(DataEvent.class);
		if(statusEvent!=null && statusEvent.getData()!=null){
			final ExtensionData data = new ExtensionData()
	        .visible(true);
			
			String timeString = this.getString(R.string.unknown);
			Date time = statusEvent.getData().getTime();
	        if (time != null) {
	            timeString = SpeakingDateFormat.format(time);
	        }
	        
			if(statusEvent.getData().isSpaceOpen()){
				final String title = this.getString(R.string.space_open, timeString);
				final String simpleTitle = this.getString(R.string.space_open_simple);
				data.status(simpleTitle).expandedTitle(title).icon(R.drawable.hshb_widget_icon);
			}else{
				final String title = this.getString(R.string.space_closed, timeString);
				final String simpleTitle = this.getString(R.string.space_closed_simple);
				data.status(simpleTitle).expandedTitle(title).icon(R.drawable.hshb_widget_icon);
			}
	        
	        if(statusEvent.getData().getMessage()!= null && statusEvent.getData().getMessage().length()>0){
	        	data.expandedBody("Nachricht: \n" + statusEvent.getData().getMessage());
	        }
	        
	        final Intent widgetIntent = new Intent(this, StartActivity.class);
	        widgetIntent.putExtra("widget", true);
	        widgetIntent.putExtra("status_json", SpaceDataJsonParser.parse(statusEvent.getData()).toString());
	        data.clickIntent(widgetIntent);
			
	        publishUpdate(data);
		}
	}

}
