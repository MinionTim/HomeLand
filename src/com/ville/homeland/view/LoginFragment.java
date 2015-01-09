package com.ville.homeland.view;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.android.bitmapfun.util.Utils;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.widget.LoginButton;
import com.ville.homeland.R;
import com.ville.homeland.ui.MainActivity;
import com.ville.homeland.ui.MainActivity.IBackListener;
import com.ville.homeland.weibo.AccessTokenKeeper;
import com.ville.homeland.weibo.WeiboService;

public class LoginFragment extends SherlockFragment implements IBackListener{

	public static final String TAG_FRAGMENT = "tag:LoginFragment";
	private WebView webView;
	private LoginButton mLoginButton;
	private WeiboService mWeibo;
	private int flag  = 0;
	/** 登陆认证对应的listener */
    private AuthListener mLoginListener = new AuthListener();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		final View view = inflater.inflate(R.layout.fragment_login, container, false);
		webView = (WebView) view.findViewById(R.id.webView);
		mLoginButton = (LoginButton) view.findViewById(R.id.login);
		initLoginViews();
		
		mWeibo = WeiboService.getInstance();
		return view;
	}
	public static LoginFragment newInstance(){
		LoginFragment fragment = new LoginFragment();
		return fragment;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void initLoginViews() {
		// TODO Auto-generated method stub
		System.out.println("initLoginViews");
//		webView.loadUrl("http://weibo.com/yuanfangdejia");  
		webView.loadUrl("http://m.weibo.cn/p/1005051841685571");  
		webView.getSettings().setJavaScriptEnabled(true);  
		FragmentActivity activity = getActivity();
		if (activity instanceof MainActivity){
			((MainActivity) activity).registerBackListener(this);
		}
		
//		webView.setAlpha(0.6F);
//		webView.setBackgroundColor(0xaa000000);
		mLoginButton.setVisibility(View.GONE);
		int mScreenH2 = getResources().getDisplayMetrics().heightPixels >> 1;
		mLoginButton.setTranslationY(-mScreenH2);
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WeiboService.getInstance().authorizeSSO(getActivity(), mLoginListener);
			}
		});
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url); 
				return true;
			}
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				System.out.println("onPageStarted " + url);
			}
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
				System.out.println("onReceivedError");
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
//				if (++flag == 2){
//					animateToDestination();
//					System.out.println("onPageFinished");
//				}
				
			}
		});
	}
	@SuppressLint("NewApi") 
	private void animateToDestination(){
		webView.setBackgroundColor(0xaa000000);
		int mScreenH2 = getResources().getDisplayMetrics().heightPixels >> 1;
		webView.setAlpha(1.0F);
		TranslateAnimation transAnim = new TranslateAnimation(0, 0, 0, mScreenH2);  
        transAnim.setDuration(400);
        transAnim.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mLoginButton.setTranslationY(0);
			}
		});
        mLoginButton.startAnimation(transAnim);
	}
	
private class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
        	Oauth2AccessToken token = Oauth2AccessToken.parseAccessToken(values);
        	mWeibo.setOauth2AccessToken(token);
            if (token.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(getActivity(), token);
                String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(
                        new java.util.Date(token.getExpiresTime()));
                String format = "Token：%1$s \n有效期：%2$s";
                String message = String.format(format, token.getToken(), date);
                Utils.toastLong(getActivity(), "授权成功: " + message);
                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(MainMessagesFragment.TAG_FRAGMENT);
                if(fragment == null){
                	fragment = MainMessagesFragment.newInstance();
                }
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				ft.remove(LoginFragment.this)
				.attach(fragment)
				.show(fragment)
				.commit();
                
            } else {
                // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                String code = values.getString("code");
                String message = "授权失败";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                Utils.toastLong(getActivity(), message);
            }
        }

        @Override
        public void onCancel() {
        	 Utils.toastLong(getActivity(), "取消");
        }

        @Override
        public void onWeiboException(WeiboException e) {
        	 Utils.toastLong(getActivity(), "Auth exception : " + e.getMessage());
        }
    }
	
	@Override
	public boolean onKeyBack() {
		// TODO Auto-generated method stub
		if (webView.canGoBack()) {  
	    	webView.goBack();  
	    	return true;
		}
		return false;
	}
}
