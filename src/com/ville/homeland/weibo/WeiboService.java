package com.ville.homeland.weibo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.ville.homeland.weibo.WBAuthActivity.AuthListener;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.provider.CalendarContract.Reminders;
import android.widget.Toast;

public class WeiboService {
	
	private static WeiboService mService = null;
	public static final int COUNT_PER_PAGE = 25;
	public static final int API_STATUS_HOMELINE = 0x0201;
	public static final int API_ON_ERROR 		= 0x0202;
	public static final int API_ON_IOEXCEPTION 	= 0x0203;
	
	private WeiboService(Context context){
		mWeiboAuth = new WeiboAuth(context, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
		mCtx = context;
	}
	public synchronized static WeiboService getInstance(){
		return mService;
	}
    /**
     * SsoHandler 仅当sdk支持sso时有效，
     */
	/** 微博 Web 授权类，提供登陆等功能  */
    private WeiboAuth mWeiboAuth;
    
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;

    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    private final Context mCtx;
	
	public SsoHandler getSsoHandler(){
		return mSsoHandler;
	}
	public void setOauth2AccessToken(Oauth2AccessToken token){
		mAccessToken = token;
	}
	public static void init(Context context){
		mService = new WeiboService(context);
	}
	public boolean isOauthAccessed(Context context){
		if(mAccessToken == null){
			mAccessToken = AccessTokenKeeper.readAccessToken(context);
		}
		return mAccessToken.isSessionValid();
	}

	public void authorizeSSO(Activity activity, WeiboAuthListener listener){
		 mSsoHandler = new SsoHandler(activity, mWeiboAuth);
         mSsoHandler.authorize(listener);
	}
	
	/**
	 * @param sinceID 返回ID比sinceID大的微博（即比sinceID时间晚的微博）
	 * */
	public void requestHomeTimeLine(Context context, Handler handler, long maxId, int page){
//		System.out.println("requestHomeTimeLine");
		StatusesAPI api = new StatusesAPI(mAccessToken);
		api.homeTimeline(0, maxId, 5, page, false, StatusesAPI.FEATURE_ALL, false,
				new FixedRequestListener(context, handler, API_STATUS_HOMELINE));
	}
	/**
	 * @param sinceID 返回ID比sinceID大的微博（即比sinceID时间晚的微博）
	 * */
	public void requestDefaultUserTimeLine(Context context, Handler handler, long maxId, int page){
//		System.out.println("requestHomeTimeLine");
		StatusesAPI api = new StatusesAPI(mAccessToken);
		long userId = 2384122784L;
		api.userTimeline(userId, 0, maxId, COUNT_PER_PAGE, page, false, StatusesAPI.FEATURE_ALL, false, 
				new FixedRequestListener(context, handler, API_STATUS_HOMELINE));
	}
	
	class FixedRequestListener implements RequestListener{
		private Handler mHandler;
		private int mWhatMsg;
		private Context mContext;
		public FixedRequestListener(Context context, Handler handler, int what) {
			// TODO Auto-generated constructor stub
			mContext = context;
			mHandler = handler;
			mWhatMsg = what;
		}

		@Override
		public void onComplete(String jsonStr) {
			// TODO Auto-generated method stub
			mHandler.obtainMessage(mWhatMsg, jsonStr).sendToTarget();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			// TODO Auto-generated method stub
			mHandler.obtainMessage(API_ON_ERROR, e.getMessage()).sendToTarget();
		}

	}
}
