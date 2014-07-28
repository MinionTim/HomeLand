package com.ville.homeland.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.android.bitmapfun.util.ImageFetcher;
import com.example.android.bitmapfun.util.Utils;
import com.example.android.bitmapfun.util.ImageCache.ImageCacheParams;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.LogoutAPI;
import com.ville.homeland.R;
import com.ville.homeland.adapter.BlogListAdapter;
import com.ville.homeland.bean.Status;
import com.ville.homeland.view.PullToRefreshListViewOSC.OnRefreshListener;
import com.ville.homeland.weibo.AccessTokenKeeper;
import com.ville.homeland.weibo.WeiboService;

public class MainMessagesFragment extends SherlockFragment implements OnClickListener{
	
	public static final String TAG_FRAGMENT = "tag:MainMessagesFragment";

	private WeiboService mWeibo;
	private boolean mAuthState;
	private PullToRefreshListViewOSC mXListView;
//	private PullToRefreshListView mXListView;
	private List<Status> mStatus = new ArrayList<Status>();
	private BlogListAdapter mAdapter;
	private long mMaxId;
	
	private static final int FLAG_REFRESH 	= 1;
	private static final int FLAG_LOADMORE 	= 2;
	private int mCurrFlag;
	
    /** 登出操作对应的listener */
    private LogOutRequestListener mLogoutListener = new LogOutRequestListener();
	
	public static MainMessagesFragment newInstance(){
		return new MainMessagesFragment();
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WeiboService.API_STATUS_HOMELINE:
				List<Status> result = null;
				result = Status.constructStatusList(msg.obj.toString());
				if(mCurrFlag == FLAG_REFRESH){
					System.out.println("Sina Data Received : Refresh Complete!");
					mXListView.onRefreshComplete();
					if(result.size() > 0){
						mMaxId = result.get(0).getId();
						mStatus.clear();
						mStatus.addAll(result);
						mAdapter.notifyDataSetChanged();
					}
					
				}else if(mCurrFlag == FLAG_LOADMORE){
					System.out.println("Sina Data Received : Load Complete! page is " + mCurrPage);
					mXListView.onLoadComplete();
					if(result.size() > 0){
						mCurrPage ++;
						mStatus.addAll(mStatus.size(), result);
						mAdapter.notifyDataSetChanged();
					}else {
						Utils.toastLong(getActivity(), "别拉了，数据全部加载完了！！");
						System.out.println("Load Complete, Data is NULL " + msg.obj.toString());
					}
				}
				
				break;

			case WeiboService.API_ON_ERROR:
			case WeiboService.API_ON_IOEXCEPTION:
				Utils.toastLong(getActivity(), msg.obj.toString());
				break;
			}
		};
	};

	private ImageFetcher mImageFetcher;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		System.out.println("onCreate");
		mWeibo = WeiboService.getInstance();
		mMaxId = 0;
		mCurrFlag = FLAG_REFRESH;
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
		
		if (WeiboService.getInstance().isOauthAccessed(getActivity())){
			mAuthState = true;
			Utils.toastShort(getActivity(), "已授权，直接登录!");
			initImageFetcher();
			initXList();
		}else {
			mXListView.setVisibility(View.GONE);
			Fragment loginFragment = LoginFragment.newInstance();
			mAuthState = false;
			FragmentManager fm = getActivity().getSupportFragmentManager();
			fm.beginTransaction().
			replace(R.id.frame_login, loginFragment, LoginFragment.TAG_FRAGMENT)
			.commit();
		}
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
				loadData(1, FLAG_REFRESH);
			}
			
			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				loadData(mCurrPage, FLAG_LOADMORE);
			}
		});
		
		mXListView.performInit();
	}
	private int mCurrPage = 2;
	public void loadData(int page, int flag){
		mCurrFlag = flag;
		if(mCurrFlag== FLAG_REFRESH){
			System.out.println("loadData: Refresh Data. page is " + page);
			mMaxId = 0;
		}else if (mCurrFlag== FLAG_LOADMORE){
			System.out.println("loadData: Load More Data... request page is " + page);
		}
		mWeibo.requestHomeTimeLine(getActivity(), mHandler, mMaxId, page);
//		mWeibo.requestDefaultUserTimeLine(getActivity(), mHandler, mMaxId, page);
	}

//	@Override
//	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		// TODO Auto-generated method stub
//		inflater.inflate(R.menu.menu_messages, menu);
//		super.onCreateOptionsMenu(menu, inflater);
//	}
	
//	@Override
//	public void onPrepareOptionsMenu(Menu menu) {
//		// TODO Auto-generated method stub
//		super.onPrepareOptionsMenu(menu);
//		if (mAuthState){
//			menu.findItem(R.id.logout).setVisible(true);
//		}else {
//			menu.findItem(R.id.logout).setVisible(false);
//		}
//	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.logout:
			Utils.toastLong(getActivity(), "登出!");
			new LogoutAPI(AccessTokenKeeper.readAccessToken(getActivity())).logout(mLogoutListener);
			AccessTokenKeeper.clear(getActivity());
			mAuthState = false;
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
     * 登出按钮的监听器，接收登出处理结果。（API 请求结果的监听器）
     */
    private class LogOutRequestListener implements RequestListener {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String value = obj.getString("result");

                    if ("true".equalsIgnoreCase(value)) {
                        AccessTokenKeeper.clear(getActivity());
                        Utils.toastShort(getActivity(), "登出成功！！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        
        @Override
        public void onComplete4binary(ByteArrayOutputStream responseOS) {
            // Do nothing
        }        
        
        @Override
        public void onIOException(IOException e) {
            Utils.toastShort(getActivity(), "登出失败！！");
        }

        @Override
        public void onError(WeiboException e) {
            Utils.toastShort(getActivity(), "登出失败！！");
        }
    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		 Utils.toastShort(getActivity(), "Click");
	}
	
}
