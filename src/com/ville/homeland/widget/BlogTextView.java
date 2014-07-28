package com.ville.homeland.widget;

import com.example.android.bitmapfun.util.Utils;
import com.ville.homeland.ui.WebBrowserActivity;
import com.ville.homeland.util.Constants;
import com.ville.homeland.util.SmileyParser;
import com.ville.homeland.util.SmileyParser.OnLinkClickListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class BlogTextView extends TextView implements OnLinkClickListener{

	public TextView mTab;
	private boolean linkHit = false;
	private final SmileyParser sp = SmileyParser.getInstance();
	private boolean dontConsumeNonUrlClicks = true;

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
	}

	private void init() {
		// TODO Auto-generated method stub
		sp.setOnLinkClickListener(this);
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

	public void setWeiboText(String text) {
		setText(sp.addSmileySpans(text));
		setMovementMethod(LocalLinkMovementMethod.getInstance());
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

	@Override
	public void onLinkClicked(String text) {
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

}
