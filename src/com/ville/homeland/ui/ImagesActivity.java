package com.ville.homeland.ui;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ville.homeland.R;
import com.ville.homeland.view.MenuFragment;

public class ImagesActivity extends SherlockFragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getSupportActionBar().setTitle(R.string.main_tab_compere);
		ContentValues cv;
		
//		FragmentManager ft = getSupportFragmentManager();
//		ft.beginTransaction()
//		.replace(R.id.comp_frame, new ImageDetailActivity()).commit();
	}

}
