package com.ville.homeland.ui;

import com.ville.homeland.R;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.app.Activity;
import android.content.Intent;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
//		new Handler().postDelayed(mChooser, 500);
	}

	Runnable mChooser = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Intent intent = new Intent(Intent.ACTION_MAIN);
//			intent.setClass(SplashActivity.this, MainTabsActivity.class);
			startActivity(intent);
			SplashActivity.this.finish();
		}
	};

}
