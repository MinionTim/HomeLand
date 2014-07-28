package com.ville.homeland.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockActivity;
import com.ville.homeland.R;
import com.ville.homeland.util.Constants;

public class WebBrowserActivity extends SherlockActivity {
	
	private WebView mWebView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		WebView webView = new WebView(this);
		mWebView = webView;
		setContentView(webView);
		init();
	}
	private void init() {
		// TODO Auto-generated method stub
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		String url = getIntent().getStringExtra(Constants.KEY_WEB_BROWSER_LINK);
		mWebView.loadUrl(url);  
		mWebView.getSettings().setJavaScriptEnabled(true);  
		mWebView.setWebViewClient(new WebViewClient(){
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
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (mWebView.canGoBack()) {  
			mWebView.goBack();  
		}else {
			super.onBackPressed();
		}
	}

}
