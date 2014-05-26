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
package de.hackerspacebremen.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import de.hackerspacebremen.HackerspaceApplication;
import de.hackerspacebremen.R;
import de.hackerspacebremen.common.Constants;
import de.hackerspacebremen.communication.HackerspaceComm;
import de.hackerspacebremen.event.DataEvent;
import de.hackerspacebremen.event.RefreshEvent;
import de.hackerspacebremen.format.SpeakingDateFormat;
import de.hackerspacebremen.push.NotificationRegistry;
import de.hackerspacebremen.valueobjects.SpaceData;
import de.hackerspacebremen.valueobjects.parser.SpaceDataJsonParser;
import de.hackerspacebremen.viewholders.StatusViewHolder;
import de.hackerspacebremen.widgets.HackerspaceWidgetProvider;

public class StatusFragment extends Fragment {

	private AnimationDrawable statusAnimation;

	private AnimationDrawable messageAnimation;

	private SpaceData spaceData = null;

	private StatusCommunication comm = null;
	private boolean commDone = true;

    private NotificationRegistry notificationRegistry;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
//		Bundle extras = getArguments();
		final DataEvent statusEvent = (DataEvent) EventBus.getDefault().getStickyEvent(DataEvent.class);
//		if (savedInstanceState != null
//				&& savedInstanceState.containsKey("status_json") && savedInstanceState.getString("status_json") != null) {
//			this.spaceData = SpaceDataJsonParser.parse(savedInstanceState
//					.getString("status_json"));
//			((HackerspaceApplication)this.getActivity().getApplication()).spaceOpen = this.spaceData.isSpaceOpen();
//		} else if (extras != null && extras.containsKey("status_json") && extras.getString("status_json") != null) {
//			this.spaceData = SpaceDataJsonParser.parse(extras
//					.getString("status_json"));
//			((HackerspaceApplication)this.getActivity().getApplication()).spaceOpen = this.spaceData.isSpaceOpen();
//		}else 
		if(statusEvent != null){
			this.spaceData = statusEvent.getData();
			// TODO 
		}else {
			this.spaceData = null;
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Resources res = getResources();
		SpeakingDateFormat.setFormatAds(res.getStringArray(R.array.format_ads));
		SpeakingDateFormat.setWeekdayAds(res
				.getStringArray(R.array.weekday_ads));
		return inflater.inflate(R.layout.status_fragment, container, false);
	}

	@Override
	public void onSaveInstanceState(final Bundle outState) {
//		if (this.commDone) {
//			if (spaceData != null) {
//				// final DataEvent stickyEvent = (DataEvent) EventBus.getDefault().getStickyEvent(DataEvent.class);
//				// if(stickyEvent == null){
//				// 	EventBus.getDefault().postSticky(event)
//				// }
//				outState.putString("status_json",
//						SpaceDataJsonParser.parse(spaceData).toString());
//			}
//		} else {
		if (!this.commDone) {
			this.comm.cancel(true);
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();

		EventBus.getDefault().register(this);
		
		// initializing the statusview holder
		StatusViewHolder.init(this.getView());

		StatusViewHolder.get().statusLayout
				.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						if (statusAnimation != null) {
							statusAnimation.start();
						}
						if (messageAnimation != null) {
							messageAnimation.start();
						}
						StatusViewHolder.get().messageBlock
								.setVisibility(RelativeLayout.GONE);
						StatusViewHolder.get().imgStatus
								.setVisibility(ImageView.VISIBLE);
						StatusViewHolder.get().imgConnErr
								.setVisibility(ImageView.GONE);
						final TextView text = StatusViewHolder.get().statusMessage;
						text.setTypeface(null, Typeface.NORMAL);
						text.setText(getString(R.string.fetch_status));
						final TextView messageText = StatusViewHolder.get().messageText;
						messageText.setText(getString(R.string.fetch_message));
						final TextView messageLabel = StatusViewHolder.get().messageLabel;
						messageLabel.setVisibility(TextView.GONE);
						startStatusCommunication();
						return true;
					}
				});

		if (this.spaceData == null) {
			this.startAnimation();
			this.startStatusCommunication();
			if(notificationRegistry == null){
                this.notificationRegistry = new NotificationRegistry(this.getActivity());
            }
            this.notificationRegistry.register();
		} else {
			this.outputStatus(this.spaceData);
		}
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		EventBus.getDefault().unregister(this);
	}
	
//	public void onEvent(final DataEvent event){
//		this.outputStatus(event.getData());
//	}
	
	public void onEventMainThread(final DataEvent event){
		this.outputStatus(event.getData());
	}
	
	public void onEvent(final RefreshEvent event){
		if (statusAnimation != null) {
			statusAnimation.start();
		}
		if (messageAnimation != null) {
			messageAnimation.start();
		}
		StatusViewHolder.get().messageBlock
				.setVisibility(RelativeLayout.GONE);
		StatusViewHolder.get().imgStatus
				.setVisibility(ImageView.VISIBLE);
		StatusViewHolder.get().imgConnErr
				.setVisibility(ImageView.GONE);
		final TextView text = StatusViewHolder.get().statusMessage;
		text.setTypeface(null, Typeface.NORMAL);
		text.setText(getString(R.string.fetch_status));
		final TextView messageText = StatusViewHolder.get().messageText;
		messageText.setText(getString(R.string.fetch_message));
		final TextView messageLabel = StatusViewHolder.get().messageLabel;
		messageLabel.setVisibility(TextView.GONE);
		startStatusCommunication();
	}

	private void startAnimation() {
		final ImageView imgStatus = StatusViewHolder.get().imgStatus;
		StatusViewHolder.get().imgStatus
				.setBackgroundResource(R.anim.clock_animation);

		// Get the background, which has been compiled to an AnimationDrawable
		// object.
		statusAnimation = (AnimationDrawable) imgStatus.getBackground();

		// Start the animation (looped playback by default).
		imgStatus.post(new Runnable() {

			@Override
			public void run() {
				statusAnimation.start();
			}
		});
	}

	private void updateAppWidget(SpaceData data) {
		SharedPreferences dataPersistence = getActivity().getSharedPreferences(
				Constants.SPACE_DATA_PERSISTENCE, Context.MODE_PRIVATE);
		Editor editor = dataPersistence.edit();
		editor.putBoolean(Constants.SPACE_OPEN_DATA_KEY, data.isSpaceOpen());
		editor.commit();

		RemoteViews remoteViews = new RemoteViews(getActivity()
				.getPackageName(), R.layout.appwidget);
		remoteViews.setViewVisibility(R.id.indicatorImage, View.VISIBLE);
		remoteViews.setViewVisibility(R.id.errorText, View.GONE);

		if (data.isSpaceOpen()) {
			remoteViews.setImageViewResource(R.id.indicatorImage,
					R.drawable.banner);
		} else {
			remoteViews.setImageViewResource(R.id.indicatorImage,
					R.drawable.banner_blur);
		}

		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(getActivity());
		ComponentName componentName = new ComponentName(getActivity(),
				HackerspaceWidgetProvider.class);
		appWidgetManager.updateAppWidget(componentName, remoteViews);
	}

	private void startStatusCommunication() {
		this.comm = new StatusCommunication();
		comm.execute();
	}

	private void outputStatus(final SpaceData data) {
		if (statusAnimation != null) {
			statusAnimation.stop();
		}
		if (messageAnimation != null) {
			messageAnimation.stop();
		}
		
		StatusViewHolder.get().imgStatus.setVisibility(ImageView.GONE);
		if (data == null) {
			StatusViewHolder.get().imgConnErr.setVisibility(ImageView.VISIBLE);
			final TextView statusView = StatusViewHolder.get().statusMessage;
			statusView.setText(this.getText(R.string.connection_error));
		} else {
			this.spaceData = data;
			final TextView text = StatusViewHolder.get().statusMessage;
			String timeString = getString(R.string.unknown);
			if (data.getTime() != null) {
				timeString = SpeakingDateFormat.format(data.getTime());
			}
			((HackerspaceApplication)this.getActivity().getApplication()).spaceOpen = data.isSpaceOpen();
			if (data.isSpaceOpen()) {
				text.setText(getString(R.string.space_open, timeString));
			} else {
				text.setText(getString(R.string.space_closed, timeString));
			}
			text.setTypeface(null, Typeface.BOLD);
			if (data.getMessage() != null && data.getMessage().length() > 0) {
				String message = data.getMessage();
				final TextView messageView = StatusViewHolder.get().messageText;
				messageView.setText(message);
				final TextView messageLabel = StatusViewHolder.get().messageLabel;
				messageLabel.setVisibility(TextView.VISIBLE);
				StatusViewHolder.get().messageBlock
						.setVisibility(RelativeLayout.VISIBLE);
			} else {
				StatusViewHolder.get().messageBlock
						.setVisibility(RelativeLayout.INVISIBLE);
			}
			// TODO EventBus nutzen
			updateAppWidget(data);
		}
	}

	private class StatusCommunication extends HackerspaceComm {

		public StatusCommunication() {
			super();
			this.httpReq = true;
			this.getReq = true;
			this.servletUrl = "v2/status";
			StatusFragment.this.commDone = false;
			try {
				this.appVersionName = getActivity().getPackageManager()
						.getPackageInfo(getActivity().getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				this.appVersionName = "??";
			}
		}

		@Override
		protected void onCancelled() {
			if (StatusFragment.this.getActivity() != null) {
				StatusFragment.this.commDone = true;
				StatusFragment.this.outputStatus(null);
			}
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			if (StatusFragment.this.getActivity() != null) {
				StatusFragment.this.commDone = true;
				try {
					if (result.has("RESULT")) {
						final JSONObject json = result.getJSONObject("RESULT");
						SpaceData data = SpaceDataJsonParser.parse(json);
						if (data != null) {
							EventBus.getDefault().postSticky(new DataEvent(data));
						}
					}
				} catch (JSONException e) {
					Log.w(StatusCommunication.class.getSimpleName(),
							"JSONException occured: " + e.getMessage());
				}
			}
		}
	}
}
