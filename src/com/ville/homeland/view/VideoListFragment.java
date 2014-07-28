package com.ville.homeland.view;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ville.homeland.AppLog;
import com.ville.homeland.HomeLandApplication;
import com.ville.homeland.R;
import com.ville.homeland.adapter.VideoListAdapter;
import com.ville.homeland.bean.VideoEntry;
import com.ville.homeland.ui.PlayerActivity2;
import com.ville.homeland.util.NetUtils;



public final class VideoListFragment extends Fragment implements LoaderCallbacks<List<VideoEntry.Data>>, 
OnClickListener, OnItemClickListener , OnModeNavListener{
    private static final String KEY_CONTENT = "VideoListFragment:typeId";
    public static final String TAG = "VideoListFragment: ";
    public static final int MODE_TYPE = 0x101;
    public static final int MODE_COMP = 0x102;
    public static final int MODE_QUERY = 0x103;
    
    private VideoListAdapter mAdapter;
    private PullToRefreshListView mPTRListView;
    private ProgressBar mProgressBar;
    private TextView mEmptyView;
    private Button mBtnRetry;
    private View mFrameView;
    private static final int LOADER_ID = 1;
    private int mVideoMode;
    private String mCompereId;
    private String mKeyword;
    private int mTypeId;
    private ProgramsFragmentAdapter mFragmentAdapter;
    private int mIndex;
    
    public static VideoListFragment newInstance(int videoMode, int typeID, String comp, String keyword, ProgramsFragmentAdapter adapter, int index) {
        VideoListFragment fragment = new VideoListFragment();
        fragment.mVideoMode = videoMode;
        fragment.mTypeId = typeID;
        fragment.mCompereId = comp;
        fragment.mFragmentAdapter = adapter;
        fragment.mIndex = index;
        fragment.mKeyword = keyword;
        
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	AppLog.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mTypeId = savedInstanceState.getInt(KEY_CONTENT);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	AppLog.d(TAG, "OnCreateView");
    	final View rootView = inflater.inflate(R.layout.view_pull_to_refresh, container, false);
    	mPTRListView = (PullToRefreshListView) rootView.findViewById(R.id.list);
    	mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress);
    	mEmptyView = (TextView) rootView.findViewById(R.id.empty);
    	mBtnRetry = (Button) rootView.findViewById(R.id.btn_retry);
    	mPTRListView.setMode(Mode.MANUAL_REFRESH_ONLY);
    	mPTRListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				// TODO Auto-generated method stub
				Loader loader = getLoaderManager().getLoader(LOADER_ID);
				if(!((VideoListLoader)loader).isLoading() && ((VideoListLoader)loader).hasMoreData()){
					mPTRListView.setRefreshing();
					getLoaderManager().getLoader(LOADER_ID).forceLoad();
				}
			}
    		
		});
    	mBtnRetry.setClickable(true);
    	mBtnRetry.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getLoaderManager().getLoader(LOADER_ID).forceLoad();
				showLoading(rootView);
			}
		});
    	mFrameView = rootView;
    	return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	AppLog.d(TAG, "onActivityCreated");
    	super.onActivityCreated(savedInstanceState);
    	
    	// Start out with a progress indicator.
//    	mPTRListView.setListShown(false);
    	mAdapter = new VideoListAdapter(getActivity());
    	mAdapter.setOnStartPlayListener(this);
    	mPTRListView.setAdapter(mAdapter);
    	mPTRListView.setOnItemClickListener(this);
    	
    	getLoaderManager().initLoader(LOADER_ID, null, this);
    	if(!HomeLandApplication.getApp().isNetworkConnected()){
    		showRetry(mFrameView);
    	}
    }
    
    @Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		// TODO Auto-generated method stub
    	View detailsView = ((VideoListAdapter.ViewHolder)v.getTag()).details;
    	if(detailsView.getVisibility() == View.VISIBLE){
    		detailsView.setVisibility(View.GONE);
    	} else {
    		detailsView.setVisibility(View.VISIBLE);
    	}
	}
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CONTENT, mTypeId);
    }

    // This is called when a new Loader needs to be created.
	@Override
	public Loader<List<VideoEntry.Data>> onCreateLoader(int id, Bundle arg1) {
		// TODO Auto-generated method stub
		AppLog.d(TAG, "onCreateLoader");
		return new VideoListLoader(getActivity(), mVideoMode, mTypeId, mCompereId, mKeyword, mFrameView);
	}

	@Override
	public void onLoadFinished(Loader<List<VideoEntry.Data>> loader,
			List<VideoEntry.Data> data) {
		// TODO Auto-generated method stub
//		AppLog.d(TAG, "onLoadFinished : ");
		mPTRListView.onRefreshComplete();
		if(data != null){
			mProgressBar.setVisibility(View.INVISIBLE);
			if (data.size() == 0){
				mEmptyView.setVisibility(View.VISIBLE);
			}else {
				mEmptyView.setVisibility(View.INVISIBLE);
			}
		}
		
		// Set the new data in the adapter.
		mAdapter.setData(data);
		mAdapter.notifyDataSetChanged();
//		if(!((VideoListLoader)loader).hasMoreData()){
//			HomeLandApplication.toastLong(getActivity(),"数据已全部加载");
//		}
	}

	@Override
	public void onLoaderReset(Loader<List<VideoEntry.Data>> loader) {
		// TODO Auto-generated method stub
		 // Clear the data in the adapter.
		AppLog.d(TAG, "onLoaderReset");
		mProgressBar.setVisibility(View.VISIBLE);
		mAdapter.setData(null);
	}
	
	public static class VideoListLoader extends AsyncTaskLoader<List<VideoEntry.Data>>{
		private int mTypeId;
		private String mComp;
		private int mMode;
		List<VideoEntry.Data> mVideoList;
		private boolean mHasError;
        private boolean mIsLoading;
        private int mPage = 1;
        private boolean mHasMoreData;
        private View mRootView;
        private String mKeyword;
		public VideoListLoader(Context context, int mode, int typeId, String comp, String keyword, View rootView) {
			super(context);
			// TODO Auto-generated constructor stub
			mTypeId = typeId;
			mComp = comp;
			mMode = mode;
			mKeyword = keyword;
			
			mIsLoading = true;
			mVideoList = null;
			mHasError = false;
			mHasMoreData = false;
			mRootView = rootView;
		}
		
		public void setupParams(int mode, int typeId, String comp){
			mComp = comp;
			mTypeId = typeId;
			mMode = mode;
		}

		@Override
		public List<VideoEntry.Data> loadInBackground() {
			// TODO Auto-generated method stub
			mIsLoading = true;
			
			List<VideoEntry.Data> data = null;
			String url = createUrl(mTypeId, mComp, mKeyword, mPage);
			AppLog.d(TAG, "loadInBackground: "+ url);
			String content = NetUtils.getContentByLink(url);
			VideoEntry entry = VideoEntry.constructEntry(content);
			
			if(entry != null){
				mHasError = false;
				data = entry.dataList;
				mHasMoreData = entry.hasNext;
			}else {
				mHasError = true;
			}
			return data;
		}
		
		@Override
		public void deliverResult(List<VideoEntry.Data> data) {
			// TODO Auto-generated method stub
			mIsLoading = false;
			AppLog.d(TAG, "deliverResult: ["+ mTypeId + "," + mComp + "]; size = " + (data!=null ? data.size() : 0));	
			if(!HomeLandApplication.getApp().isNetworkConnected()){
				HomeLandApplication.toastShort(getContext(), "网络连接有问题喔！");
				showRetry(mRootView);
			}
			
            if (data != null) {
                if (mVideoList == null) {
                	mVideoList = data;
                } else {
                	mVideoList.addAll(data);
                }
                if(data.size() == 0){
                	HomeLandApplication.toastShort(getContext(), "没有更多数据了！");
                }else if(data.size() > 0){
					mPage ++;
				}
            }
            if (isStarted()) {
                // Need to return new ArrayList for some reason or onLoadFinished() is not called
                super.deliverResult(mVideoList == null ?
                        null : new ArrayList<VideoEntry.Data>(mVideoList));
            }
		}
		//该方法必须重载，否则 loadInBackground()将不会执行
		@Override
		protected void onStartLoading() {
			// TODO Auto-generated method stub
			AppLog.d(TAG, "onStartLoading..." + parseMode(mMode));
			if (mVideoList != null) {
                // If we already have results and are starting up, deliver what we already have.
				AppLog.d(TAG, "onStartLoading  NULL");
                deliverResult(null);
            } else {
            	if(HomeLandApplication.getApp().isNetworkConnected()){
            		showLoading(mRootView);
            		forceLoad();
            		AppLog.d(TAG, "onStartLoading:  id = " + mTypeId);
            	}else {
            		showRetry(mRootView);
            		HomeLandApplication.toastShort(getContext(), "网络连接有问题喔！");
            	}
            }
		}
		
		@Override
        protected void onStopLoading() {
            mIsLoading = false;
            cancelLoad();
        }

        @Override
        protected void onReset() {
            super.onReset();
            onStopLoading();
            mVideoList = null;
            mHasMoreData = false;
            mPage = 1;
        }
        public boolean isLoading() {
            return mIsLoading;
        }

        public boolean hasError() {
            return mHasError;
        }
        public boolean hasMoreData(){
        	return mHasMoreData;
        }
        public void refresh() {
            reset();
            startLoading();
        }
        
        public String createUrl(int type, String comp, String kd, int page) {
    		// TODO Auto-generated method stub
    		String url = "";
    		if(mMode == MODE_COMP){
    			url = NetUtils.getVideoListURLByComp(comp, page);
    		}else if (mMode == MODE_TYPE){
    			url = NetUtils.getVideoListURLByType(type, page);
    		}else if (mMode == MODE_QUERY){
    			String newKD = kd;
    			try {
					newKD = URLEncoder.encode(kd, "utf-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					newKD = " ";
					e.printStackTrace();
				}
    			url = NetUtils.getVideoListURLByQuery(newKD, page);//ville
    		}
    		return url;
    	}
	}
	
	//on start button clicked
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int pos = (Integer) v.getTag();
		VideoEntry.Data entry = mAdapter.getItem(pos);
		Intent intent = new Intent();
		intent.setClass(getActivity(), PlayerActivity2.class);
		intent.putExtra("pid", entry.pid);
		intent.putExtra("page-url", entry.url);
		intent.putExtra("video-title", entry.title);
		getActivity().startActivity(intent);
	}
	
	private static void showRetry(View v){
		if(v != null){
			v.findViewById(R.id.progress).setVisibility(View.GONE);
			v.findViewById(R.id.btn_retry).setVisibility(View.VISIBLE);
		}
	}
	private static void showLoading(View v){
		if(v != null){
			v.findViewById(R.id.progress).setVisibility(View.VISIBLE);
			v.findViewById(R.id.btn_retry).setVisibility(View.GONE);
		}
	}
	
	private static String parseMode(int mode){
		return mode == MODE_COMP ? "Comp" : "Type";
	}

	@Override
	public void onModeChanged(int mode) {
		// TODO Auto-generated method stub
		if(mode == mVideoMode || mFragmentAdapter == null){
			return;
		}
		mVideoMode = mode;
		Map<String, String> map = mFragmentAdapter.getParamsByMode(mode, mIndex);
		String tmpType = map.get("mode_type");
		String tmpComp = map.get("mode_comp");
		
        if (tmpType != null){
        	mTypeId = Integer.parseInt(tmpType);
        }
        if (tmpComp != null){
        	mCompereId = tmpComp;
        }
        AppLog.d(TAG, "onModeChanged " + parseMode(mode) + "[typeId, compId]=[" + tmpType + ", " + tmpComp + "]");
        mAdapter.removeAllData();
        mAdapter.notifyDataSetChanged();
        Loader loader = getLoaderManager().getLoader(LOADER_ID);
        ((VideoListLoader)loader).setupParams(mode, mTypeId, tmpComp);
        loader.reset();
        loader.startLoading();
	}

}
