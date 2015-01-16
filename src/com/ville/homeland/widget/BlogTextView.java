package com.ville.homeland.widget;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.android.bitmapfun.util.Utils;
import com.ville.homeland.R;
import com.ville.homeland.listener.StatusComponentViewTag;
import com.ville.homeland.ui.WebBrowserActivity;
import com.ville.homeland.util.Constants;
import com.ville.homeland.util.LocalLinkMovementMethod;
import com.ville.homeland.util.SmileyParser;

public class BlogTextView extends TextView {

	private static final String TAG = "BlogTextView";
	public TextView mTab;
	public boolean linkHit = false;
	private final SmileyParser sp = SmileyParser.getInstance();
	private boolean dontConsumeNonUrlClicks = true;
	private Context mContext;
	
	private final Pattern mAtPattern = Pattern.compile("@[\u4e00-\u9fa5a-zA-Z0-9_-]{2,30}");
	private final Pattern mSharpPattern = Pattern.compile("#[^#]+#");
	private final Pattern mHttpPattern = Pattern.compile("http://[a-zA-Z0-9./]{5,}");
	

	public BlogTextView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public BlogTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public BlogTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
		mContext = context;
	}

	private void init() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean hasFocusable() {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		linkHit = false;
		boolean res = super.onTouchEvent(event);
		return dontConsumeNonUrlClicks ? linkHit : res;
	}

	public void setWeiboText(CharSequence text) {
		CharSequence target = text;
		target = addSmileySpans(target);
		target = addLinkSpans(target);
		setText(target);
		setMovementMethod(LocalLinkMovementMethod.getInstance());
	}
	

	private CharSequence addLinkSpans(CharSequence text) {
		// TODO Auto-generated method stub
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		Matcher matcherAt = mAtPattern.matcher(text);
        while (matcherAt.find()) {
        	final String info = matcherAt.group();
        	Log.d(TAG, "At Find : "+ info);
        	builder.setSpan(new BlogClickableSpan(mContext) {
				
				@Override
				public void onClick(View widget) {
					// TODO Auto-generated method stub
					onLinkClicked(info);
				}
			},
			matcherAt.start(), matcherAt.end(),
        		Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        
        Matcher matcherSharp = mSharpPattern.matcher(text);
        while (matcherSharp.find()) {
        	final String info = matcherSharp.group();
        	Log.d(TAG, "Sharp Find : "+ info);
			builder.setSpan(new BlogClickableSpan(mContext) {

				@Override
				public void onClick(View widget) {
					// TODO Auto-generated method stub
					onLinkClicked(info);
				}
			}, 
			matcherSharp.start(), matcherSharp.end(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        Matcher matcherHttp = mHttpPattern.matcher(text);
        while (matcherHttp.find()) {
        	final String info = matcherHttp.group();
//        	System.out.println("Sharp Find : "+ info);
        	Log.d(TAG, "Http Find : "+ info);
        	builder.setSpan(new BlogClickableSpan(mContext) {
        		
        		@Override
        		public void onClick(View widget) {
        			// TODO Auto-generated method stub
//        			Toast.makeText(mContext, info,  Toast.LENGTH_SHORT).show();
        			onLinkClicked(info);
        		}
        	}, 
        	matcherHttp.start(), matcherHttp.end(),
        	Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
	}

	private CharSequence addSmileySpans(CharSequence text) {
		// TODO Auto-generated method stub
		return sp.addSmileySpans(text);
	}

	private void onLinkClicked(String text) {
		// TODO Auto-generated method stub
		if(text == null || "".equals(text)){
			return;
		}
		if(mOnLinkClickedListener != null){
			View v = new View(mContext);
			v.setTag(StatusComponentViewTag.build(StatusComponentViewTag.TAG_LINK, text));
			mOnLinkClickedListener.onClick(v);
			return;
		}
		String info = "";
		int len = text.length();
		if(text.startsWith("#")){
			info = text.substring(1, len-1);
			Utils.toastShort(getContext(), "话题:" + info);
//			System.out.println("话题:" + info);
		}else if(text.startsWith("@")){
			info = text.substring(1, len);
//			System.out.println("唉特:" + info);
			Utils.toastShort(getContext(), "唉特:" + info);
		}else if(text.startsWith("http")){
			info = text;
//			System.out.println("链接:" + info);
			Utils.toastShort(getContext(), "链接:" + info);
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.putExtra(Constants.KEY_WEB_BROWSER_LINK, info);
			intent.setClass(getContext(), WebBrowserActivity.class);
			getContext().startActivity(intent);
			
		}
	}
	
	
	public class BlogClickableSpan extends ClickableSpan {

		int color = -1;
//		private Context context;
		private Intent intent;

		public BlogClickableSpan(Context context) {
			this(context, -1);
		}

		public BlogClickableSpan(Context context, int color) {
			if (color != -1) {
				this.color = color;
			}
//			this.context = context;
			this.intent = intent;
		}

		/**
		 * Performs the click action associated with this span.
		 */
		public void onClick(View widget) {
//			context.startActivity(intent);
		};

		/**
		 * Makes the text without underline.
		 */
		@Override
		public void updateDrawState(TextPaint ds) {
			if (color == -1) {
				ds.setColor(ds.linkColor);
			} else {
				ds.setColor(color);
			}
			ds.setUnderlineText(false);
		}

	}
	
	private View.OnClickListener mOnLinkClickedListener;
	public void setOnLinkClickedListener(View.OnClickListener listener){
		mOnLinkClickedListener = listener;
	}
	
}
