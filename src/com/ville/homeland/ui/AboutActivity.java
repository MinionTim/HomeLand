package com.ville.homeland.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.android.bitmapfun.util.Utils;
import com.ville.homeland.R;

public class AboutActivity extends SherlockActivity implements OnClickListener {

	private TextView mTvVersion;
	private Button mBtnUpdate;
	private Button mBtnShare;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		actionBar.setTitle(R.string.title_about);
		
		initViews();
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

	private void initViews() {
		// TODO Auto-generated method stub
		mTvVersion = (TextView) findViewById(R.id.version);
		mBtnUpdate = (Button) findViewById(R.id.btn_update);
		mBtnShare = (Button) findViewById(R.id.btn_share);
		
		mTvVersion.setText("版本:" + getAppVersionName(this));
		mBtnUpdate.setOnClickListener(this);
		mBtnShare.setOnClickListener(this);
	}
	
	
	public String getAppVersionName(Context context) {    
	    String versionName = "";    
	    try {    
	        PackageManager pm = context.getPackageManager();    
	        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);    
	        versionName = pi.versionName;    
	        if (versionName == null || versionName.length() <= 0) {    
	            return "";    
	        }    
	    } catch (Exception e) {
	    }    
	    return versionName;    
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_update:
			Utils.toastShort(this, "update");
			break;

		case R.id.btn_share:
			doShare();
			break;
		}
	}

	private void doShare() {
		// TODO Auto-generated method stub
		String content = "远方的家APP，随时随地观看节目，感受节目风采，点击下载：xxxx.com";
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "");
		intent.putExtra(Intent.EXTRA_TEXT, content);
		startActivity(Intent.createChooser(intent, getTitle()));
	}   


}
