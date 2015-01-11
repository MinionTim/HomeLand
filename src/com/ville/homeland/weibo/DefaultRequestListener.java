package com.ville.homeland.weibo;

import android.content.Context;
import android.widget.Toast;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

public abstract class DefaultRequestListener implements RequestListener{
	private Context mContext;
	public DefaultRequestListener(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	
	@Override
	public void onWeiboException(WeiboException arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(mContext, arg0.getMessage(), Toast.LENGTH_SHORT).show();
	}
}
