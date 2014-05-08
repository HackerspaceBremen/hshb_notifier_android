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
package de.hackerspacebremen;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import de.hackerspacebremen.common.Constants;
import de.hackerspacebremen.fragments.NotificationPreferenceFragment;

public class SettingActivity extends PreferenceActivity{

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            onCreatePreferenceActivity();
        } else {
            onCreatePreferenceFragment();
        }
    }
	
	/**
	 * 
	 */
	@SuppressLint("NewApi")
	private void onCreatePreferenceFragment() {
		getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new NotificationPreferenceFragment())
        .commit();
	}

	@SuppressWarnings("deprecation")
    private void onCreatePreferenceActivity() {
		addPreferencesFromResource(R.xml.preferences);
		final Preference pref = (Preference) findPreference("permanent_notification_preference");
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(final Preference preference) {
				final CheckBoxPreference checkBox = (CheckBoxPreference) preference;
				if(!checkBox.isChecked()){
					final NotificationManager notificationManager = (NotificationManager) 
							  getSystemService(NOTIFICATION_SERVICE);
					notificationManager.cancel(Constants.GCM_NOTIFICATION_ID);
				}
				return false;
			}
		});
	}
}
