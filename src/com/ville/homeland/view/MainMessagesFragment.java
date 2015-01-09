package com.ville.homeland.view;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.example.android.bitmapfun.util.ImageFetcher;
import com.example.android.bitmapfun.util.Utils;
import com.example.android.bitmapfun.util.ImageCache.ImageCacheParams;
import com.ville.homeland.R;
import com.ville.homeland.adapter.BlogListAdapter;
import com.ville.homeland.bean.Status;
import com.ville.homeland.util.WeiboUtils;
import com.ville.homeland.view.PullToRefreshListViewOSC.OnRefreshListener;

public class MainMessagesFragment extends SherlockFragment implements OnClickListener{
	
	private static final String TAG = MainMessagesFragment.class.getSimpleName();
	public static final String TAG_FRAGMENT = "tag:MainMessagesFragment";
	private WeiboUtils mWeibo;
	private PullToRefreshListViewOSC mXListView;
//	private PullToRefreshListView mXListView;
	private List<Status> mStatus = new ArrayList<Status>();
	private BlogListAdapter mAdapter;
	
	private static final int TYPE_REFRESH 	= 1;
	private static final int TYPE_LOADMORE 	= 2;
	
	public static MainMessagesFragment newInstance(){
		return new MainMessagesFragment();
	}
	
	private ImageFetcher mImageFetcher;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		System.out.println("onCreate");
		mWeibo = WeiboUtils.getInstance();
		mMaxId = 0;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSherlockActivity().getSupportActionBar().setTitle("动态");
		setHasOptionsMenu(true);
		
		final View v = inflater.inflate(R.layout.fragment_main_messages, container, false);
		mXListView = (PullToRefreshListViewOSC) v.findViewById(R.id.list);
		initImageFetcher();
		initXList();
		return v;
	}
	
	private void initImageFetcher(){
		ImageCacheParams cacheParams = new ImageCacheParams(getActivity(), "com");

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory
	    mImageFetcher = new ImageFetcher(getActivity(), 120);
	    mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
	}
	
	private void initXList() {
		// TODO Auto-generated method stub
		mXListView.setVisibility(View.VISIBLE);
		mAdapter = new BlogListAdapter(getActivity(), mStatus);
		mAdapter.setImageFetcher(mImageFetcher);
		mXListView.setAdapter(mAdapter);
		mXListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				loadData(1, TYPE_REFRESH);
			}
			
			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				loadData(mCurrPage, TYPE_LOADMORE);
			}
		});
		
		mXListView.performInit();
	}
	private int mCurrPage = 2;
	private void loadData(int page, int flag){
		if(flag== TYPE_REFRESH){
			System.out.println("loadData: Refresh Data. page is " + page);
//			mMaxId = 0;
		}else if (flag== TYPE_LOADMORE){
			System.out.println("loadData: Load More Data... request page is " + page);
		}
		new ConnTask(flag, page).execute();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 Utils.toastShort(getActivity(), "Click");
	}
	
	private long mMaxId;
	class ConnTask extends AsyncTask<Void, Void, List<Status>>{
		private int mCurrFlag;
		private int mPage;
		ConnTask(int type, int page){
			this.mCurrFlag = type;
			this.mPage = page;
			if(type == TYPE_REFRESH){
				mMaxId = 0;
			}
		}

		@Override
		protected List<com.ville.homeland.bean.Status> doInBackground(Void... urls) {
			// TODO Auto-generated method stub
			return mWeibo.friendsTimeline(mMaxId, mPage);
		}
		@Override
		protected void onPostExecute(List<com.ville.homeland.bean.Status> result) {
			if(mCurrFlag == TYPE_REFRESH){
				System.out.println("Sina Data Received : Refresh Complete!");
				mXListView.onRefreshComplete();
				if(result.size() > 0){
					mMaxId = result.get(0).getId();
					mStatus.clear();
					mStatus.addAll(result);
					mAdapter.notifyDataSetChanged();
				}
				
			}else if(mCurrFlag == TYPE_LOADMORE){
				System.out.println("Sina Data Received : Load Complete! page is " + mCurrPage);
				mXListView.onLoadComplete();
				if(result.size() > 0){
					mCurrPage ++;
					mStatus.addAll(mStatus.size(), result);
					mAdapter.notifyDataSetChanged();
				}else {
					Utils.toastLong(getActivity(), "别拉了，数据全部加载完了！！");
					System.out.println("Load Complete, Data is NULL ");
				}
			}
		}
		
	}
	
}
