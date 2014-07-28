package com.ville.homeland.widget;

import android.content.Context;
import android.content.Intent;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class BlogClickableSpan extends ClickableSpan {

	int color = -1;
//	private Context context;
	private Intent intent;

	public BlogClickableSpan(Context context) {
		this(context, -1);
	}

	public BlogClickableSpan(Context context, int color) {
		if (color != -1) {
			this.color = color;
		}
//		this.context = context;
		this.intent = intent;
	}

	/**
	 * Performs the click action associated with this span.
	 */
	public void onClick(View widget) {
//		context.startActivity(intent);
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
