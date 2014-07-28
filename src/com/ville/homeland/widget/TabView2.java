package com.ville.homeland.widget;


import com.ville.homeland.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class TabView2 extends RadioButton {
	
	public TextView mTab;
	public TextView mUnreadView;
	public ImageView mDotView;
	private String mLabel;
	private int mDawableId;
	private Drawable mUnreadDrawable;

	public TabView2(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public TabView2(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public TabView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mUnreadDrawable = getResources().getDrawable(R.drawable.tab_unread_bg);
	}

	private void init() {
		// TODO Auto-generated method stub
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Bitmap b = Bitmap.createBitmap(mUnreadDrawable.getIntrinsicWidth(), mUnreadDrawable.getIntrinsicHeight(),
				Config.ARGB_8888);
		final Drawable d = mUnreadDrawable;
		d.setBounds(0, 0, b.getWidth(), b.getHeight());
		d.draw(canvas);
	}
	
	public void setUnreadNum(int num){
		mUnreadView.setText(String.valueOf(num));
	}
	public void setTabSelected(){
		mTab.setSelected(true);
		setBackgroundResource(R.drawable.maintab_item_bg_d);
	}
	public void setTabUnSelected(){
		setBackgroundResource(R.drawable.transparent);
		mTab.setSelected(false);
	}

}
