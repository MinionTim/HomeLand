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
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.android.bitmapfun.util.Utils;
import com.ville.homeland.ui.WebBrowserActivity;
import com.ville.homeland.util.Constants;
import com.ville.homeland.util.SmileyParser;

public class BlogTextView extends TextView {

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
		super(context, attrs);
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
		return dontConsumeNonUrlClicks ? linkHit : super.onTouchEvent(event);
	}

	public void setWeiboText(CharSequence text) {
		CharSequence target = text;
//		target = addSmileySpans(target);
		target = addLinkSpans(text);
		setText(target);
		setMovementMethod(LocalLinkMovementMethod.getInstance());
	}

	private CharSequence addLinkSpans(CharSequence text) {
		// TODO Auto-generated method stub
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		Matcher matcherAt = mAtPattern.matcher(text);
        while (matcherAt.find()) {
        	final String info = matcherAt.group();
//        	System.out.println("At Find : "+ info);
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
//        	System.out.println("Sharp Find : "+ info);
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
	private void onLinkClicked(String text) {
		// TODO Auto-generated method stub
		if(text == null || "".equals(text)){
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
	
	public static class LocalLinkMovementMethod extends LinkMovementMethod {
		private static LocalLinkMovementMethod sInstance;
		
		public static LocalLinkMovementMethod getInstance() {
			if (sInstance == null)
				sInstance = new LocalLinkMovementMethod();
			
			return sInstance;
		}
		
		private Object mTarget = new BackgroundColorSpan(Color.GREEN);
		
		@Override
		public boolean onTouchEvent(TextView widget, Spannable buffer,
				MotionEvent event) {
			int action = event.getAction();
			
			if (action == MotionEvent.ACTION_UP
					|| action == MotionEvent.ACTION_DOWN
					|| action == MotionEvent.ACTION_CANCEL) {
				int x = (int) event.getX();
				int y = (int) event.getY();
				
				x -= widget.getTotalPaddingLeft();
				y -= widget.getTotalPaddingTop();
				
				x += widget.getScrollX();
				y += widget.getScrollY();
				
				Layout layout = widget.getLayout();
				int line = layout.getLineForVertical(y);
				int off = layout.getOffsetForHorizontal(line, x);
				
				ClickableSpan[] link = buffer.getSpans(off, off,
						ClickableSpan.class);
				
				if (link.length != 0) {
					if (action == MotionEvent.ACTION_UP) {
						// Selection.removeSelection(buffer);
						buffer.removeSpan(mTarget);
						link[0].onClick(widget);
					} else if (action == MotionEvent.ACTION_DOWN) {
						// Selection.setSelection(buffer,
						// buffer.getSpanStart(link[0]),
						// buffer.getSpanEnd(link[0]));
						buffer.setSpan(mTarget, buffer.getSpanStart(link[0]),
								buffer.getSpanEnd(link[0]),
								Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					} else if (action == MotionEvent.ACTION_CANCEL) {
						buffer.removeSpan(mTarget);
					}
					
					if (widget instanceof BlogTextView) {
						((BlogTextView) widget).linkHit = true;
					}
					return true;
				} else {
					// Selection.removeSelection(buffer);
					buffer.removeSpan(mTarget);
					Touch.onTouchEvent(widget, buffer, event);
					return false;
				}
			}
			return Touch.onTouchEvent(widget, buffer, event);
		}
	}
	
}
