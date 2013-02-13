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

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import de.greenrobot.event.EventBus;
import de.hackerspacebremen.common.FragmentState;
import de.hackerspacebremen.event.FragmentStateChanged;
import de.hackerspacebremen.event.RefreshEvent;
import de.hackerspacebremen.fragments.AboutDialogFragment;
import de.hackerspacebremen.fragments.ChangeStatusFragment;
import de.hackerspacebremen.fragments.EmptyFragment;
import de.hackerspacebremen.fragments.NewsFragment;
import de.hackerspacebremen.fragments.StatusFragment;
import de.hackerspacebremen.valueobjects.parser.ChangeDataJsonParser;
import de.neofonie.mobile.app.android.widget.crouton.Crouton;

public class StartActivity extends SherlockFragmentActivity {

	private FragmentState state = null;

	private boolean changeShown;
	
	private boolean mapShown;
	
	private boolean newsShown;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		this.state = null;
		this.changeShown = false;
		this.mapShown = false;
		this.newsShown = false;
		if (this.getIntent() != null
				&& this.getIntent().getAction() != null
				&& this.getIntent().getAction()
						.equals("android.intent.action.VIEW")) {
			this.manageFragment(FragmentState.CHANGE_SHOWN, null);
		} else {
			if (savedInstanceState != null
					&& savedInstanceState.getString("state") != null) {
				this.state = FragmentState.parseToState(savedInstanceState
						.getString("state"));
				if ((getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
						&& state == FragmentState.CHANGE_SHOWN && savedInstanceState
						.getString("change_json") != null)
						|| (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && state == FragmentState.CHANGE_SHOWN)) {
					this.manageFragment(state, savedInstanceState);
				}
				// this.manageFragment(FragmentState.parseToState(savedInstanceState.getString("state")),savedInstanceState);
			} else {
				this.manageFragment(FragmentState.ONLY_STATUS,
						savedInstanceState);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragmentActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		EventBus.getDefault().unregister(this);
		Crouton.cancelAllCroutons();
	}

	public void onEvent(FragmentStateChanged event) {
		this.manageFragment(event.getState(), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (state == null) {
			super.onBackPressed();
		} else if (state == FragmentState.ONLY_STATUS) {
			finish();
		} else {
			this.manageFragment(FragmentState.ONLY_STATUS, null);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		final MenuInflater inflater = this.getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		if(this.state == FragmentState.CHANGE_SHOWN){
			menu.getItem(1).setVisible(false);
		}else if(this.state == FragmentState.MAP_SHOWN){
			menu.getItem(2).setVisible(false);
		}else if(this.state == FragmentState.NEWS_SHOWN){
			// TODO change this to 3 when mapfragment is added
			menu.getItem(2).setVisible(false);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = true;
		switch (item.getItemId()) {
		case R.id.settings:
			final Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.change:
			this.manageFragment(FragmentState.CHANGE_SHOWN, null);
			item.setVisible(false);
			break;
//		case R.id.map:
//			this.manageFragment(FragmentState.MAP_SHOWN, null);
//			item.setVisible(false);
//			break;
		case R.id.news:
			this.manageFragment(FragmentState.NEWS_SHOWN, null);
			item.setVisible(false);
			break;
		case R.id.about:
			// use support library with fragment dialog instead
			final FragmentTransaction transaction = this
					.getSupportFragmentManager().beginTransaction();
			final SherlockDialogFragment newFragment = new AboutDialogFragment();
			newFragment.show(transaction, "dialog");
			break;
		case R.id.refresh:
			EventBus.getDefault().post(new RefreshEvent());
			break;
		default:
			result = super.onOptionsItemSelected(item);
		}
		
		return result;
	}

	private void showChange(final Bundle bundle) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		if (this.findViewById(R.id.second_part) == null) {
			transaction.replace(R.id.main_frame,
					this.createChangeFragment(bundle));
		} else {
			if (this.state == null || this.state == FragmentState.CHANGE_SHOWN) {
				transaction.replace(R.id.main_frame,
						createStatusFragment(bundle));
			}
			transaction.replace(R.id.second_frame,
					this.createChangeFragment(bundle));
			if (!this.changeShown) {
				this.changeShown = true;
				transaction.addToBackStack(null);
			}
		}
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.commit();
	}

	private void showStatus(final Bundle bundle) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		final View secondPart = this.findViewById(R.id.second_part);
		if (this.state == null || secondPart == null) {
			transaction.replace(R.id.main_frame, createStatusFragment(bundle));
		}

		if (secondPart != null) {
			transaction.replace(R.id.second_frame, new EmptyFragment());
		}
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		// transaction.addToBackStack(null);
		transaction.commit();
	}

	private Fragment createChangeFragment(final Bundle bundle) {
		final Fragment changeFragment = new ChangeStatusFragment();
		Bundle extras;
		if (bundle == null) {
			extras = getIntent().getExtras();
		} else {
			extras = bundle;
		}
		if (extras != null) {
			Log.i(this.getClass().getSimpleName(),
					"change_json (extras) found!");
			final Bundle data = new Bundle();
			final String changeJSON = extras.getString("change_json");
			data.putString("change_json", changeJSON);
			changeFragment.setArguments(data);
		}
		return changeFragment;
	}
	
	private Fragment createMapFragment(final Bundle bundle) {
		// TODO change to MapFragment
		//		final Fragment mapFragment = new MapFragment();
//		Bundle extras;
//		if (bundle == null) {
//			extras = getIntent().getExtras();
//		} else {
//			extras = bundle;
//		}
//		if (extras != null) {
//			Log.i(this.getClass().getSimpleName(),
//					"change_json (extras) found!");
//			final Bundle data = new Bundle();
//			final String changeJSON = extras.getString("change_json");
//			data.putString("change_json", changeJSON);
//			changeFragment.setArguments(data);
//		}
//		return changeFragment;
		return null;
	}
	
	private Fragment createNewsFragment(final Bundle bundle) {
		// TODO change to NewsFragment
		// final Fragment newsFragment = new NewsFragment();
		// Bundle extras;
		// if (bundle == null) {
		// extras = getIntent().getExtras();
		// } else {
		// extras = bundle;
		// }
		// if (extras != null) {
		// Log.i(this.getClass().getSimpleName(),
		// "change_json (extras) found!");
		// final Bundle data = new Bundle();
		// final String changeJSON = extras.getString("change_json");
		// data.putString("change_json", changeJSON);
		// changeFragment.setArguments(data);
		// }
		return new NewsFragment();
	}

	private Fragment createStatusFragment(final Bundle bundle) {
		final Fragment newFragment = new StatusFragment();

		Bundle extras;
		if (bundle == null) {
			extras = getIntent().getExtras();
		} else {
			extras = bundle;
		}
		if (extras != null) {
			Log.i(this.getClass().getSimpleName(),
					"status_json (extras) found!");
			final Bundle data = new Bundle();
			final String statusJSON = extras.getString("status_json");
			data.putString("status_json", statusJSON);
			newFragment.setArguments(data);
		}
		return newFragment;
	}

	private void manageFragment(final FragmentState state, final Bundle bundle) {
		switch (state) {
		case ONLY_STATUS:
			this.showStatus(bundle);
//			this.showChangeMenuBtn(true);
			break;
		case CHANGE_SHOWN:
			this.showChange(bundle);
//			this.showChangeMenuBtn(false);
			break;
		case MAP_SHOWN:
			this.showMap(bundle);
			break;
		case NEWS_SHOWN:
			this.showNews(bundle);
			break;
		}
		this.state = state;
		this.invalidateOptionsMenu();
	}

	/**
	 * @param bundle
	 */
	private void showMap(Bundle bundle) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		if (this.findViewById(R.id.second_part) == null) {
			transaction.replace(R.id.main_frame,
					this.createMapFragment(bundle));
		} else {
			if (this.state == null || this.state == FragmentState.MAP_SHOWN) {
				transaction.replace(R.id.main_frame,
						createStatusFragment(bundle));
			}
			transaction.replace(R.id.second_frame,
					this.createMapFragment(bundle));
			if (!this.mapShown) {
				this.mapShown = true;
				transaction.addToBackStack(null);
			}
		}
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.commit();
	}
	
	/**
	 * @param bundle
	 */
	private void showNews(Bundle bundle) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		if (this.findViewById(R.id.second_part) == null) {
			transaction.replace(R.id.main_frame,
					this.createNewsFragment(bundle));
		} else {
			if (this.state == null || this.state == FragmentState.MAP_SHOWN) {
				transaction.replace(R.id.main_frame,
						createStatusFragment(bundle));
			}
			transaction.replace(R.id.second_frame,
					this.createNewsFragment(bundle));
			if (!this.newsShown) {
				this.newsShown = true;
				transaction.addToBackStack(null);
			}
		}
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.commit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os
	 * .Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (this.state != null) {
			outState.putString("state", this.state.toString());

			if (state == FragmentState.CHANGE_SHOWN) {
				final ChangeStatusFragment changeFragment;
				if (this.findViewById(R.id.second_part) != null) {
					changeFragment = (ChangeStatusFragment) getSupportFragmentManager()
							.findFragmentById(R.id.second_frame);
				} else {
					changeFragment = (ChangeStatusFragment) getSupportFragmentManager()
							.findFragmentById(R.id.main_frame);
				}
				outState.putString(
						"change_json",
						ChangeDataJsonParser.parse(
								changeFragment.getChangeData()).toString());
			}
		}
		super.onSaveInstanceState(outState);
	}
}