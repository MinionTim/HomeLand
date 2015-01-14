package com.ville.homeland.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.example.android.bitmapfun.util.Utils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.ville.homeland.AppLog;
import com.ville.homeland.HomeLandApplication;
import com.ville.homeland.R;
import com.ville.homeland.view.MainComperesFragment;
import com.ville.homeland.view.MainMessagesFragment;
import com.ville.homeland.view.MainProgramsFragment;
import com.ville.homeland.view.MenuFragment;
import com.ville.homeland.view.VideoListFragment;
import com.ville.homeland.weibo.WeiboService;

public class MainActivity extends SlidingFragmentActivity {

	private Fragment mContent;
	private String mCurrTag = "";
	private static final String TAG = "MainActivity";
	private static final int MENU_ITEM_SEARCH = 0;
	private static final boolean DEBUG = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.content_frame);
		
		//set Behind View
		setBehindContentView(R.layout.menu_frame);
		SlidingMenu sm = getSlidingMenu();
		sm.setSlidingEnabled(true);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);
		
		// show home as up so we can toggle
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		setSlidingActionBarEnabled(false);
		
//		Fragment mContent = MainProgramsFragment.newInstance();
//		mCurrTag = MainProgramsFragment.TAG_FRAGMENT;
		
		Fragment mContent = MainMessagesFragment.newInstance();
		mCurrTag = MainMessagesFragment.TAG_FRAGMENT;
		
		mLastFragment = mContent;
		getSupportFragmentManager().beginTransaction()
				.add(R.id.content_frame, mContent, mCurrTag).commit();
		
		getSupportFragmentManager().beginTransaction()
		.replace(R.id.menu_frame, MenuFragment.newInstance()).commit();
		
	}
	
	private Fragment mLastFragment;
	public void switchContent2(String tag, String lastTag) {
		mCurrTag = tag;
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment fragment = fm.findFragmentByTag(tag);
		if(fragment == null){
			if(MainProgramsFragment.TAG_FRAGMENT.equals(tag)){
				fragment = new MainProgramsFragment();
				
			}else if(MainComperesFragment.TAG_FRAGMENT.equals(tag)){
				fragment = new MainComperesFragment();
				
			}else if(MainMessagesFragment.TAG_FRAGMENT.equals(tag)){
				fragment = new MainMessagesFragment();
			}
			
			ft.add(R.id.content_frame, fragment, tag);
		}
		
		if(!lastTag.equals(tag)){
			if(mLastFragment != fragment){
				AppLog.e(VideoListFragment.TAG, fragment.getTag());
				ft.detach(mLastFragment);
				ft.attach(fragment);
				ft.show(fragment);
			}
		}
		mLastFragment = fragment;
		ft.commit();
		getSupportFragmentManager().executePendingTransactions();
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				getSlidingMenu().showContent();
			}
		}, 50);
	}
	
	public void switchContent(String tag) {
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentByTag(tag);
		if(fragment == null){
			System.out.println("add New");
			if(MainProgramsFragment.TAG_FRAGMENT.equals(tag)){
				fm.beginTransaction().add(R.id.content_frame, new MainProgramsFragment(), tag).commit();
				
			}else if(MainComperesFragment.TAG_FRAGMENT.equals(tag)){
				fm.beginTransaction().add(R.id.content_frame, new MainComperesFragment(), tag).commit();
				
			}else if(MainMessagesFragment.TAG_FRAGMENT.equals(tag)){
				fm.beginTransaction().add(R.id.content_frame, new MainMessagesFragment(), tag).commit();
			}
		}
		
		getSlidingMenu().showContent();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		log("onCreateOptionsMenu");
		MenuItem item = menu.add(0, MENU_ITEM_SEARCH, 0, "Search");
		item.setIcon(R.drawable.ic_action_search);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
		searchView.setQueryHint("搜索标题");
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
//				HomeLandApplication.toastShort(MainActivity.this, "Query " + query);
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, QueryProgramsResultsActivity.class);
				intent.putExtra("query_key", query);
				startActivity(intent);
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		item.setActionView(searchView);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		log("onPrepareOptionsMenu");
		if(MainProgramsFragment.TAG_FRAGMENT.equals(mCurrTag)){
			menu.findItem(MENU_ITEM_SEARCH).setVisible(true);
		}else {
			menu.findItem(MENU_ITEM_SEARCH).setVisible(false);
		}
		return true;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		SsoHandler ssoHandler = WeiboService.getInstance().getSsoHandler();
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		boolean status = false;
		if (mCurrTag.equals(MainMessagesFragment.TAG_FRAGMENT)) {
			if (mIBackListener != null) {
				status = mIBackListener.onKeyBack();
			}
		}
		if (!status) {
//			super.onBackPressed();
			long now = System.currentTimeMillis();
			if((now - mLastExitTime) < 1500 ){
				super.onBackPressed();
			}else {
				Utils.toastShort(this, "再按一次退出");
				mLastExitTime = now;
			}
			
		}
	}
	private long mLastExitTime;
	private IBackListener mIBackListener;
	public void registerBackListener(IBackListener listener){
		mIBackListener = listener;
	}
	
	public interface IBackListener {
		boolean onKeyBack();
	}
	
	private void log(String msg){
		if(DEBUG){
			AppLog.d(TAG, msg);
		}
	}
}
