package com.ville.homeland.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.android.bitmapfun.util.Utils;
import com.ville.homeland.R;

public class SettingsActivity extends SherlockPreferenceActivity implements OnPreferenceClickListener {

	private static final String KEY_CLEAR_CACHE = "clear_cache";
	private static final String KEY_ABOUT = "about";
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(R.string.title_settings);
		
		addPreferencesFromResource(R.xml.settings);
		findPreference(KEY_ABOUT).setOnPreferenceClickListener(this);
		findPreference(KEY_CLEAR_CACHE).setOnPreferenceClickListener(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}

	@Override
	public boolean onPreferenceClick(Preference pref) {
		// TODO Auto-generated method stub
		if(pref.getKey().equals(KEY_ABOUT)){
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setClass(this, AboutActivity.class);
			startActivity(intent);
		}else if(pref.getKey().equals(KEY_CLEAR_CACHE)){
			AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.alert)
				.setMessage(R.string.clear_alert_msg)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						Utils.toastShort(SettingsActivity.this, "清除");
						doClearCache();
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.create();
			dialog.show();
			
		}
		return true;
	}


	protected void doClearCache() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(0, R.anim.slide_out_right);
	}
}
