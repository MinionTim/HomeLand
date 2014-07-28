package com.ville.homeland.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import com.ville.homeland.AppLog;
import com.ville.homeland.R;

interface OnModeNavListener{
	void onModeChanged(int mode);
}

public class MainProgramsFragment extends SherlockFragment implements OnNavigationListener, OnQueryTextListener {

	public static final String TAG_FRAGMENT = "tag:MainProgramsFragment";
	public static final String TAG = "MainProgramsFragment";
	private ProgramsFragmentAdapter mAdapterType;
	private ViewPager mPager;
	private PageIndicator mIndicator;
	
	private ArrayList<OnModeNavListener> mNavTargets = new ArrayList<OnModeNavListener>();
	
	public static MainProgramsFragment newInstance(){
		return new MainProgramsFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		AppLog.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		Context context = getSherlockActivity().getSupportActionBar().getThemedContext();
		ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context, R.array.program_nav, R.layout.sherlock_spinner_item);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        getSherlockActivity().getSupportActionBar().setListNavigationCallbacks(list, this);
        setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		AppLog.d(TAG, "onCreateView");
		getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(false);
        
		View root = inflater.inflate(R.layout.activity_programs_layout, null);
		mPager = (ViewPager) root.findViewById(R.id.pager);
		mIndicator = (TitlePageIndicator) root.findViewById(R.id.indicator);
		return root;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		AppLog.d(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		
		mAdapterType = new ProgramsFragmentAdapter(getChildFragmentManager(), getSherlockActivity(), VideoListFragment.MODE_TYPE, mNavTargets);
		mPager.setAdapter(mAdapterType);
		mIndicator.setViewPager(mPager);
		
	}
	
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		AppLog.d(TAG, "NAV " + itemPosition);
		
		if(itemPosition == 0){
			mAdapterType.setProgramMode(VideoListFragment.MODE_TYPE);
		}else if (itemPosition == 1){
			mAdapterType.setProgramMode(VideoListFragment.MODE_COMP);
		}
		
		for(OnModeNavListener listener : mNavTargets){
			listener.onModeChanged(itemPosition == 0 ? VideoListFragment.MODE_TYPE : VideoListFragment.MODE_COMP);
		}
		mAdapterType.notifyDataSetChanged();
		mPager.setCurrentItem(0);
		mIndicator.setCurrentItem(0);
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		Toast.makeText(getSherlockActivity(), "You searched for: " + query, Toast.LENGTH_LONG).show();
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}
}
class ProgramsFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
	private Context mContext;
	private static final String TAG = "ProgramsFragmentAdapter";
	private String[] mTitles4Comp;
	private String[] mTitles4Type;
	private String[] mIds4Comp;
	private String[] mIds4Type;
	private int mMode;
	private List<OnModeNavListener> mTargets;

    public ProgramsFragmentAdapter(FragmentManager fm, Context context, int mode, ArrayList<OnModeNavListener> targets) {
        super(fm);
        mContext = context;
        mIds4Comp = mContext.getResources().getStringArray(R.array.video_mode_comp_ids);
        mIds4Type = mContext.getResources().getStringArray(R.array.video_mode_type_ids);
        
        mTitles4Comp = mContext.getResources().getStringArray(R.array.video_mode_comp_names);
        mTitles4Type = mContext.getResources().getStringArray(R.array.video_mode_type_names);
        
        mMode = mode;
        mTargets = targets;
    }
    public void setProgramMode(int mode){
    	mMode = mode;
    }

    @Override
    public Fragment getItem(int position) {
    	VideoListFragment f = null;
    	if(mMode == VideoListFragment.MODE_COMP){
    		f = VideoListFragment.newInstance(VideoListFragment.MODE_COMP, -2, mIds4Comp[position], "", this, position);
    	}else if (mMode == VideoListFragment.MODE_TYPE){
    		f = VideoListFragment.newInstance(VideoListFragment.MODE_TYPE, Integer.parseInt(mIds4Type[position]), "", "", this, position);
    	}else {
    		f = VideoListFragment.newInstance(VideoListFragment.MODE_TYPE, Integer.parseInt(mIds4Type[position]), "", "", this, position);
    	}
    	mTargets.add(f);
    	return f;
    }

    @Override
    public int getCount() {
        return getCountByMode(mMode);
    }
    
    private int getCountByMode(int mode){
    	if(mode == VideoListFragment.MODE_COMP){
    		return mIds4Comp.length;
    	}else if (mode == VideoListFragment.MODE_TYPE){
    		return mIds4Type.length;
    	}
        return 0;
    }
    
    @Override
    public int getItemPosition(Object object) {
    	// TODO Auto-generated method stub
    	return POSITION_NONE;  
    }
    public Map<String, String> getParamsByMode(int mode, int index){
    	Map<String, String> map = new HashMap<String, String>();
    	if(mode == VideoListFragment.MODE_COMP){
    		map.put("mode_comp", mIds4Comp[index%getCountByMode(mode)]);
    	}else if (mode == VideoListFragment.MODE_TYPE){
    		map.put("mode_type", mIds4Type[index%getCountByMode(mode)]);
    	}
    	return map;
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
    	if(mMode == VideoListFragment.MODE_COMP){
    		return mTitles4Comp[position];
    	}else if (mMode == VideoListFragment.MODE_TYPE){
    		return mTitles4Type[position];
    	}
    	return "..";
    }

    @Override
    public int getIconResId(int index) {
      return R.drawable.ic_launcher;
    }
}
