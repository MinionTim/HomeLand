package com.ville.homeland;

import com.ville.homeland.ui.CompereAllImagesActivity;
import com.ville.homeland.util.SmileyMap;
import com.ville.homeland.util.SmileyParser;
import com.ville.homeland.weibo.WeiboService;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class HomeLandApplication extends Application {
	
	private static HomeLandApplication mInstance;
	public synchronized static HomeLandApplication getApp() {
		return mInstance;
	}
	
	private CompereAllImagesActivity mCoperesItemActivity;
	public static final int NETTYPE_NULL =  0x00;
	public static final int NETTYPE_WIFI  = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mInstance = this;
		WeiboService.init(this);
		SmileyParser.init(this);
		SmileyMap.init(this);
	}
	
	public void setComperesItemActivity(CompereAllImagesActivity activity){
		mCoperesItemActivity = activity;
	}
	
	public CompereAllImagesActivity getCoperesItemActivity(){
		return mCoperesItemActivity;
	}
	
	public static void toastLong(Context context, String msg){
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}
	public static void toastShort(Context context, String msg){
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 检测网络是否可用
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
//		return ni != null && ni.isConnectedOrConnecting();
		return ni != null && ni.isConnected();
	}

	/**
	 * 获取当前网络类型
	 * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
	 */
	public int getNetworkType() {
		int netType = NETTYPE_NULL;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}		
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if(extraInfo != null && !"".equals(extraInfo)){
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}
	
}
