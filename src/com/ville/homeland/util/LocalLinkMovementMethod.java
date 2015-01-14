package com.ville.homeland.util;

import android.graphics.Color;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import com.ville.homeland.widget.BlogTextView;

public class LocalLinkMovementMethod extends LinkMovementMethod {
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
