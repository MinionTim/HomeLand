package com.ville.homeland.view;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.android.bitmapfun.util.ImageFetcher;
import com.example.android.bitmapfun.util.Utils;
import com.example.android.bitmapfun.util.ImageCache.ImageCacheParams;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.ville.homeland.R;
import com.ville.homeland.adapter.BlogListAdapter;
import com.ville.homeland.ui.StatusDetailActivity;
import com.ville.homeland.util.WeiboUtils;
import com.ville.homeland.view.PullToRefreshListViewOSC.OnRefreshListener;
import com.ville.homeland.weibo.AccessTokenKeeper;
import com.ville.homeland.weibo.WeiboService;

public class MainMessagesFragment extends SherlockFragment implements OnItemClickListener{
	
	private static final String TAG = MainMessagesFragment.class.getSimpleName();
	public static final String TAG_FRAGMENT = "tag:MainMessagesFragment";
	private WeiboUtils mWeibo;
	private PullToRefreshListViewOSC mXListView;
//	private PullToRefreshListView mXListView;
//	private List<Status> mStatus = new ArrayList<Status>();
	private BlogListAdapter mAdapter;
	private ConnTask mCurrTask;
	
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
		mAdapter = new BlogListAdapter(getActivity());
		mAdapter.setImageFetcher(mImageFetcher);
		mXListView.setAdapter(mAdapter);
		mXListView.setOnItemClickListener(this);
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
		if(mCurrTask != null && mCurrTask.getStatus() == AsyncTask.Status.RUNNING)
			return;
		if(flag== TYPE_REFRESH){
			System.out.println("loadData: Refresh Data. page is " + page);
//			mMaxId = 0;
		}else if (flag== TYPE_LOADMORE){
			System.out.println("loadData: Load More Data... request page is " + page);
		}
		mCurrTask = new ConnTask(flag, page);
		mCurrTask.execute();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if(position >= 1){
			position--;
		}
		Status status = mAdapter.getItem(position);
//		Utils.toastShort(getActivity(), "Click: " + position);
		Intent intent = new Intent();
		intent.putExtra("key_status", status);
		intent.setClass(getActivity(), StatusDetailActivity.class);
		getActivity().startActivity(intent);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
//		inflater.inflate(R.menu.main_menu, menu);
		menu.addSubMenu(0, 8, 0, "登录");
		menu.addSubMenu(0, 9, 1, "登出");
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case 8:
			Toast.makeText(getActivity(), "Login", 0).show();
			WeiboService.getInstance().ssoAuthorize(getActivity(), new AuthListener());
			break;
			
		case 9:
			Toast.makeText(getActivity(), "Logout", 0).show();
			AccessTokenKeeper.clear(getActivity());
			WeiboService.getInstance().updateOauth2AccessToken(getActivity(), null);
			break;
			

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private long mMaxId;
	class ConnTask extends AsyncTask<Void, Void, StatusList>{
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
		protected StatusList doInBackground(Void... urls) {
			// TODO Auto-generated method stub
			return mWeibo.friendsTimeline(mMaxId, mPage);
		}
		@Override
		protected void onPostExecute(StatusList result) {
			if(mCurrFlag == TYPE_REFRESH){
				System.out.println("Sina Data Received : Refresh Complete!");
				mXListView.onRefreshComplete();
				if(result != null && result.statusList.size() > 0){
					mMaxId = Long.parseLong(result.statusList.get(0).id);
					mAdapter.setAll(result.statusList);
					mAdapter.notifyDataSetChanged();
				} else {
					Utils.toastLong(getActivity(), "没有数据");
				}
				
			}else if(mCurrFlag == TYPE_LOADMORE){
				System.out.println("Sina Data Received : Load Complete! page is " + mCurrPage);
				mXListView.onLoadComplete();
				if(result.statusList.size() > 0){
					mCurrPage ++;
					mAdapter.append(result.statusList);
					mAdapter.notifyDataSetChanged();
				}else {
					Utils.toastLong(getActivity(), "别拉了，数据全部加载完了！！");
					System.out.println("Load Complete, Data is NULL ");
				}
			}
		}
		
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);
		 WeiboService.getInstance().ssoAuthorizeCallBack(requestCode, resultCode, data);
	}
	/**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
        	Oauth2AccessToken token = Oauth2AccessToken.parseAccessToken(values);
            if (token.isSessionValid()) {
                
            	WeiboService.getInstance().updateOauth2AccessToken(getActivity(), token);
                Toast.makeText(getActivity(), 
                        "登录成功", Toast.LENGTH_SHORT).show();
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = "weibosdk_demo_toast_auth_failed";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCancel() {
            Toast.makeText(getActivity(), 
                    "weibosdk_demo_toast_auth_canceled", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(getActivity(), 
                    "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
	
}
