package com.ville.homeland.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.ville.homeland.R;
import com.ville.homeland.ui.MainActivity;
import com.ville.homeland.ui.SettingsActivity;

public class MenuFragment extends SherlockFragment implements
		OnItemClickListener {

	private List<HashMap<String, Integer>> mData;
	private LayoutInflater mInflater;
	private int mCurrentIndex = -1;
	private static final int INDEX_PROGRAMS = 0;
	private static final int INDEX_MESSAGES = 1;
	private static final int INDEX_COMPERE 	= 2;
	
	private String mLastTag = "";

	public static MenuFragment newInstance() {
		return new MenuFragment();
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mInflater = inflater;
		View root = inflater.inflate(R.layout.fragment_menu_layout, null);
		ListView listView = (ListView) root.findViewById(R.id.title_list);
		listView.setOnItemClickListener(this);

		listView.setAdapter(new TitleAdapter());
		return root;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initData();
	}

	private void initData() {
		// TODO Auto-generated method stub
		mData = new ArrayList<HashMap<String, Integer>>();
		mData.add(makeMap(R.string.main_tab_programs, R.drawable.icon_menu_1));
		mData.add(makeMap(R.string.main_tab_message, R.drawable.icon_menu_1));
		mData.add(makeMap(R.string.main_tab_compere, R.drawable.icon_menu_1));
		mData.add(makeMap(R.string.main_tab_settings, R.drawable.icon_menu_1));
		mCurrentIndex = INDEX_PROGRAMS;
		mLastTag = MainProgramsFragment.TAG_FRAGMENT;
//		mLastTag = MainMessagesFragment.TAG_FRAGMENT;
//		mLastTag = MainComperesFragment.TAG_FRAGMENT;
	}

	private HashMap<String, Integer> makeMap(int titleResId, int iconResId) {
		// TODO Auto-generated method stub
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("text", titleResId);
		map.put("icon", iconResId);
		return map;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
		// TODO Auto-generated method stub
		String tag = "";
		if(position == 3){
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setClass(getActivity(), SettingsActivity.class);
			startActivity(intent);
			return;
		}
		switch (position) {
		case 0:
			tag = MainProgramsFragment.TAG_FRAGMENT;
			break;
			
		case 1:
			tag = MainMessagesFragment.TAG_FRAGMENT;
			break;

		case 2:
			tag = MainComperesFragment.TAG_FRAGMENT;
			break;
		}
		FragmentActivity activity = getActivity();
		if(activity != null && activity instanceof MainActivity){
			((MainActivity)activity).switchContent2(tag, mLastTag);
		}
		mLastTag = tag;
	}
	
//	public void switchContent(String tag){
//		
//		
//		if(!mCurrentTag.equals(tag)){
//			
//		}
//
//        if (mLastTab != newTab) {
//            FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
//            if (mLastTab != null) {
//                if (mLastTab.fragment != null) {
//                    ft.detach(mLastTab.fragment);
//                }
//            }
//            if (newTab != null) {
//                if (newTab.fragment == null) {
//                    newTab.fragment = Fragment.instantiate(mActivity,
//                            newTab.clss.getName(), newTab.args);
//                    ft.add(mContainerId, newTab.fragment, newTab.tag);
//                } else {
//                    ft.attach(newTab.fragment);
//                }
//            }
//
//            mLastTab = newTab;
//            ft.commit();
//            mActivity.getSupportFragmentManager().executePendingTransactions();
//        }
//    
//	}
	
	

	private class TitleAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public HashMap<String, Integer> getItem(int position) {
			// TODO Auto-generated method stub
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.title_list_item_view,	null);
			}
			HashMap<String, Integer> map = getItem(position);
			((ImageView) convertView.findViewById(R.id.icon)).setImageResource(map.get("icon"));
			((TextView) convertView.findViewById(R.id.title)).setText(map.get("text"));
			return convertView;
		}
	}

}
