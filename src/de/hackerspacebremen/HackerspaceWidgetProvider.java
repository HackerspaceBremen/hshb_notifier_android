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
 *     Matthias Friedrich <mtthsfrdrch@gmail.com>
 */
package de.hackerspacebremen;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RemoteViews;
import de.hackerspacebremen.common.Constants;

/**
 * @author Matthias
 *
 */
public class HackerspaceWidgetProvider extends AppWidgetProvider {

    
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        
        SharedPreferences dataPersistence = context.getSharedPreferences(Constants.SPACE_DATA_PERSISTENCE, Context.MODE_PRIVATE);
        
        boolean containsSpaceOpen = dataPersistence.contains(Constants.SPACE_OPEN_DATA_KEY); 
        boolean spaceOpen = false;
        
        if(containsSpaceOpen) {
            spaceOpen = dataPersistence.getBoolean(Constants.SPACE_OPEN_DATA_KEY, false);
        }
                
        // Perform this loop procedure for each App-Widget that belongs to this provider
        for (int i=0; i<appWidgetIds.length; i++) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget);
            
            if(containsSpaceOpen) {
                
                views.setViewVisibility(R.id.indicatorImage, View.VISIBLE);
                views.setViewVisibility(R.id.errorText, View.GONE);
                
                if(spaceOpen) {
                    views.setImageViewResource(R.id.indicatorImage, R.drawable.banner);
                } else {
                    views.setImageViewResource(R.id.indicatorImage, R.drawable.banner_blur);
                }
                    
            } else {
                views.setViewVisibility(R.id.indicatorImage, View.GONE);
                views.setViewVisibility(R.id.errorText, View.VISIBLE);
            }
            
            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }
           
    }

}
