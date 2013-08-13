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
package de.hackerspacebremen.fragments;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import de.greenrobot.event.EventBus;
import de.hackerspacebremen.HackerspaceApplication;
import de.hackerspacebremen.R;
import de.hackerspacebremen.common.Constants;
import de.hackerspacebremen.common.FragmentState;
import de.hackerspacebremen.communication.HackerspaceComm;
import de.hackerspacebremen.event.DataEvent;
import de.hackerspacebremen.event.FragmentStateChanged;
import de.hackerspacebremen.event.StatusChanged;
import de.hackerspacebremen.valueobjects.ChangeData;
import de.hackerspacebremen.valueobjects.SpaceData;
import de.hackerspacebremen.valueobjects.parser.ChangeDataJsonParser;
import de.hackerspacebremen.viewholders.ChangeViewHolder;
import de.neofonie.mobile.app.android.widget.crouton.Crouton;
import de.neofonie.mobile.app.android.widget.crouton.Style;

/**
 * Here you can change the status of the space.
 * 
 * -login (edittext) -pass (edittext) -save login-data (checkbox) -open/close
 * button
 * 
 * @author Steve Liedtke
 */
public final class ChangeStatusFragment extends Fragment {

	private ChangeCommunication comm = null;

	private Dialog progressDialog = null;
	
	private ChangeData change;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle extras = getArguments();
		if (savedInstanceState != null
				&& savedInstanceState.containsKey("change_json") && savedInstanceState.getString("change_json") != null){
			this.change = ChangeDataJsonParser.parse(savedInstanceState.getString("change_json"));
		}else if (extras != null
				&& extras.containsKey("change_json") && extras.getString("change_json") != null){
			this.change = ChangeDataJsonParser.parse(extras
					.getString("change_json"));
		}
		super.onCreate(savedInstanceState);
	}
	
	public ChangeData getChangeData(){
		final ChangeViewHolder cvh = ChangeViewHolder.get();
		return new ChangeData(cvh.login.getText().toString(), 
				cvh.password.getText().toString(), cvh.message.getText().toString(), cvh.checkBox.isChecked());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.change_fragment, container, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();

		EventBus.getDefault().register(this);

		ChangeViewHolder.init(this.getView());
		
		ChangeViewHolder.get().checkBox.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!((CheckBox) v).isChecked()){
//					if((DoorKeyKeeperEvent) EventBus.getDefault().getStickyEvent(DoorKeyKeeperEvent.class) != null){
					final Editor editor = PreferenceManager
							.getDefaultSharedPreferences(ChangeStatusFragment.this.getActivity()).edit();
					editor.remove("doorkeykeeper_login");
					editor.remove("doorkeykeeper_password");
					editor.commit();
//						EventBus.getDefault().removeStickyEvent(DoorKeyKeeperEvent.class);
//					}
				}
			}
		});
		final SharedPreferences pref = PreferenceManager
		.getDefaultSharedPreferences(ChangeStatusFragment.this.getActivity());
//		final DoorKeyKeeperEvent keeperEvent = (DoorKeyKeeperEvent) EventBus.getDefault().getStickyEvent(DoorKeyKeeperEvent.class);
		final String keeperLogin = pref.getString("doorkeykeeper_login", null);
		final String keeperPwd = pref.getString("doorkeykeeper_password", null);
		if(change != null){
			ChangeViewHolder.get().checkBox.setChecked(change.isSaveLogin());
			ChangeViewHolder.get().login.setText(change.getLogin());
			ChangeViewHolder.get().password.setText(change.getPassword());
			ChangeViewHolder.get().message.setText(change.getMessage());
		}else if(keeperLogin != null && keeperPwd != null){
			ChangeViewHolder.get().checkBox.setChecked(true);
			ChangeViewHolder.get().login.setText(keeperLogin);
			ChangeViewHolder.get().password.setText(keeperPwd);
		}
		
		ChangeViewHolder.get().abortBtn
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						EventBus.getDefault().post(
								new FragmentStateChanged(
										FragmentState.ONLY_STATUS));
						// ((StartActivity) ChangeStatusFragment.this
						// .getActivity()).manageFragment(
						// FragmentState.ONLY_STATUS, null, false);
					}
				});
		if (((HackerspaceApplication) this.getActivity().getApplication()).spaceOpen) {
			ChangeViewHolder.get().sendBtn.setText(this
					.getText(R.string.close_space));
		} else {
			ChangeViewHolder.get().sendBtn.setText(this
					.getText(R.string.open_space));
		}

		ChangeViewHolder.get().sendBtn
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ChangeStatusFragment.this.startChangeCommunication();
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		EventBus.getDefault().unregister(this);
	}

	public void onEvent(final StatusChanged event) {
		// TODO
		// tell the user that meanwhile the status changed (with toast i.e)
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		final ChangeViewHolder cvh = ChangeViewHolder.get();
		final ChangeData change = new ChangeData(cvh.login.getText().toString(), 
				cvh.password.getText().toString(), cvh.message.getText().toString(), cvh.checkBox.isChecked());
		outState.putString("change_json",
				ChangeDataJsonParser.parse(change).toString());
		if(this.comm != null && !this.comm.isCancelled()){
			this.comm.cancel(true);
		}
		super.onSaveInstanceState(outState);
	}

	private void startChangeCommunication() {
		if (this.validate()) {
			this.comm = new ChangeCommunication(ChangeViewHolder.get().login
					.getText().toString(), ChangeViewHolder.get().password
					.getText().toString(), ChangeViewHolder.get().message
					.getText().toString());
			comm.execute();
		}
	}

	private boolean validate() {
		boolean result = true;
		if (ChangeViewHolder.get().login.getText().toString().length() < 4) {
			result = false;
			Crouton.makeText(this.getActivity(),
					this.getText(R.string.validate_login), Style.ALERT).show();
		}
		if (result
				&& ChangeViewHolder.get().password.getText().toString()
						.length() < 4) {
			result = false;
			Crouton.makeText(this.getActivity(),
					this.getText(R.string.validate_pwd), Style.ALERT).show();
		}
		return result;
	}

	private void outputSuccess(final SpaceData data) {
		EventBus.getDefault().post(new StatusChanged(data.isSpaceOpen()));
		EventBus.getDefault().postSticky(new DataEvent(data));
		if(ChangeViewHolder.get().checkBox.isChecked()){
			final Editor editor = PreferenceManager
					.getDefaultSharedPreferences(ChangeStatusFragment.this.getActivity()).edit();
			editor.putString("doorkeykeeper_login", ChangeViewHolder.get().login.getText().toString());
			editor.putString("doorkeykeeper_password", ChangeViewHolder.get().password.getText().toString());
			editor.commit();
		}

		Crouton.makeText(this.getActivity(),
				this.getText(R.string.change_success), Style.CONFIRM).show();
		EventBus.getDefault().post(
				new FragmentStateChanged(FragmentState.ONLY_STATUS));
		//EventBus.getDefault().post(new RefreshEvent());
	}

	private void outputError(final int code) {
		final CharSequence message;
		switch (code) {
		case -1:
			message = this.getText(R.string.change_error_0);
			break;
		case -2:
			message = this.getText(R.string.change_error_0);
			break;
		case 1:
			message = this.getText(R.string.change_error_1);
			break;
		case 3:
			message = this.getText(R.string.change_error_3);
			((HackerspaceApplication) this.getActivity().getApplication()).spaceOpen = true;
			ChangeViewHolder.get().sendBtn.setText(this
					.getText(R.string.close_space));
			ChangeViewHolder.get().message.setText("");
			break;
		case 4:
			message = this.getText(R.string.change_error_4);
			((HackerspaceApplication) this.getActivity().getApplication()).spaceOpen = false;
			ChangeViewHolder.get().sendBtn.setText(this
					.getText(R.string.open_space));
			ChangeViewHolder.get().message.setText("");
			break;
		default:
			message = this.getText(R.string.change_error);
		}

		Crouton.makeText(this.getActivity(), message, Style.ALERT).show();
	}

	private class ChangeCommunication extends HackerspaceComm {

		private final SpaceData data;

		public ChangeCommunication(final String name, final String pass,
				final String message) {
			super();
			this.httpReq = false;
			this.getReq = false;
			if (((HackerspaceApplication) ChangeStatusFragment.this
					.getActivity().getApplication()).spaceOpen) {
				this.servletUrl = "v2/cmd/close";
			} else {
				this.servletUrl = "v2/cmd/open";
			}

			data = new SpaceData(
					((HackerspaceApplication) ChangeStatusFragment.this
							.getActivity().getApplication()).spaceOpen);
			data.setMessage(message);
			data.setTime(new Date());
			
			try{
				this.postParams
						.add(new BasicNameValuePair("name", URLEncoder.encode(name,Constants.UTF8)));
				this.postParams
						.add(new BasicNameValuePair("pass", URLEncoder.encode(pass, Constants.UTF8)));
				if (message != null && message.length() > 0) {
					this.postParams.add(new BasicNameValuePair("message", URLEncoder.encode(message, Constants.UTF8)));
				}
			}catch(UnsupportedEncodingException e){
				Log.e(ChangeCommunication.class.getName(), "UnsupportedEncodingException occured: " + e.getMessage());
			}
			this.postParams.add(new BasicNameValuePair("encoded", "true"));
			
			try {
				this.appVersionName = getActivity().getPackageManager()
						.getPackageInfo(getActivity().getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				this.appVersionName = "??";
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(
					ChangeStatusFragment.this.getActivity(), null,
					ChangeStatusFragment.this.getText(R.string.change_waiting),
					true, false);
		}

		@Override
		protected void onCancelled() {
			progressDialog.cancel();
			ChangeStatusFragment.this.outputError(this.errorcode);
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			progressDialog.cancel();
			try {
				if (ChangeStatusFragment.this.getActivity() != null) {
					if (result.has("ERROR")) {

						ChangeStatusFragment.this.outputError(result
								.getInt("CODE"));
					} else if (result.has("SUCCESS")) {
						ChangeStatusFragment.this.outputSuccess(this.data);
					}
				}
			} catch (JSONException e) {
				Log.w(ChangeCommunication.class.toString(),
						"JSONException occured: " + e.getMessage());
			}
		}
	}
}
