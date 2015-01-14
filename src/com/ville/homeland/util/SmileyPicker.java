package com.ville.homeland.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ville.homeland.R;

/**
 * User: qii
 * Date: 13-1-18
 */
public class SmileyPicker extends LinearLayout {

    private static final String TAG = "SmileyPicker";

    private EditText mEditText;
    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private LinearLayout mIndicatorLayout;
    private Context mContext;
    private List<List<String>> mSimpleyKeyLists;
    private int mPageCount;

    private final LayoutTransition transitioner = new LayoutTransition();

    public SmileyPicker(Context context) {
        this(context, null);
    }

    public SmileyPicker(Context context, AttributeSet attr) {
        super(context, attr);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.smileypicker_layout, this);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mIndicatorLayout = (LinearLayout) view.findViewById(R.id.ll_indicator_layout);
        mViewPager.setAdapter(new SmileyPagerAdapter());
        
        mSimpleyKeyLists = new ArrayList<List<String>>();
        String[] originKeys = SmileyMap.getInstance().mSmileyTexts;
        mPageCount = (int) Math.ceil(originKeys.length / 20F);
        Log.d(TAG, "mPageCount = " + mPageCount + ", originKeys.length = " + originKeys.length);
        for(int i = 0; i < mPageCount; i ++){
        	List<String> list = new ArrayList<String>();
        	int pageSize = (i == mPageCount -1) ? mPageCount%20 : 20;
        	for(int j = 0; j < pageSize; j ++){
        		list.add(originKeys[i*20 + j]);
        	}
        	mSimpleyKeyLists.add(list);
        	ImageView iv = new ImageView(context);
        	Drawable d = context.getResources().getDrawable(R.drawable.ic_viewpager_indicator_point);
        	iv.setImageDrawable(d);
        	mIndicatorLayout.addView(iv, new LayoutParams(30, 30));
        }
        mViewPager.setCurrentItem(0);
        ((ImageView) mIndicatorLayout.getChildAt(0)).setImageLevel(1);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				Log.d(TAG, "onPageSelected " + position);
				int count = mIndicatorLayout.getChildCount();
				for(int i = 0; i < count; i ++){
					ImageView dotView = (ImageView) mIndicatorLayout.getChildAt(i);
					if(i == position){
						dotView.setImageLevel(1);
					}else {
						dotView.setImageLevel(0);
					}
				}
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});
        
    }

    public void setEditText(EditText paramEditText) {
        this.mEditText = paramEditText;
//        rootLayout.setLayoutTransition(transitioner);
//        setupAnimations(transitioner);
    }
    private void setupAnimations(LayoutTransition transition) {
//    	int mPickerHeight = 79;
//        ObjectAnimator animIn = ObjectAnimator.ofFloat(null, "translationY",
//                SmileyPickerUtility.getScreenHeight(this.activity), mPickerHeight).
//                setDuration(transition.getDuration(LayoutTransition.APPEARING));
//        transition.setAnimator(LayoutTransition.APPEARING, animIn);
//
//        ObjectAnimator animOut = ObjectAnimator.ofFloat(null, "translationY", mPickerHeight,
//                SmileyPickerUtility.getScreenHeight(this.activity)).
//                setDuration(transition.getDuration(LayoutTransition.DISAPPEARING));
//        transition.setAnimator(LayoutTransition.DISAPPEARING, animOut);
    }

    public void show(Activity context) {
    	boolean showAnimation = false;
    	Log.d("Ville", "show widget height is " + mEditText.getHeight());
        if (showAnimation) {
            transitioner.setDuration(200);
        } else {
            transitioner.setDuration(0);
        }
        mEditText.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				setVisibility(View.VISIBLE);
			}
		});
    }

    public void hide(Activity paramActivity) {
        setVisibility(View.GONE);
    }

    private class SmileyPagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            GridView gridView = new GridView(mContext);
            gridView.setNumColumns(7);
            gridView.setVerticalSpacing(6);
            gridView.setAdapter(new SmileyGridAdapter(mContext, position));
            container.addView(gridView, 0,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
            return gridView;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
        }

        @Override
        public int getCount() {
            return mPageCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }

    private final class SmileyGridAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private List<String> mKeys;
        private Map<String, Drawable> mDrawables;

        public SmileyGridAdapter(Context context, int pageIndex) {
            this.mInflater = LayoutInflater.from(context);
            mDrawables = new HashMap<String, Drawable>();
            HashMap<String, Integer> originMap = SmileyMap.getInstance().mSmileyToRes;
            Resources r = context.getResources();
            if(pageIndex < mSimpleyKeyLists.size()){
            	mKeys = mSimpleyKeyLists.get(pageIndex);
            	for(String key : mKeys){
            		Drawable d = r.getDrawable(originMap.get(key));
            		mDrawables.put(key, d);
            	}
            }
        }

        public int getCount() {
            return mKeys.size();
        }

        public Drawable getItem(int position) {
            return mDrawables.get(mKeys.get(position));
        }

        public long getItemId(int position) {
            return 0L;
        }

        public View getView(final int position, View convertView, ViewGroup paramViewGroup) {
        	if(convertView == null){
        		convertView = mInflater.inflate(R.layout.smileypicker_layout_gridview_item, null);
        	}
        	((ImageView)(convertView.findViewById(R.id.iv_smiley_item))).setImageDrawable(getItem(position));
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                	int size = (int) mEditText.getTextSize();
                	int height = mEditText.getHeight();
                	Log.d("Ville", "[TextSize, Height] = [" + size + ", " +height + "]");
                	size += 10;
                    String newInput = mKeys.get(position);
	                SpannableString ss = new SpannableString(newInput);
	                int resId = SmileyMap.getInstance().mSmileyToRes.get(newInput);
					Drawable d = mContext.getResources().getDrawable(resId);
					d.setBounds(0, 5, size, size+5);	// 设置表情图片的显示大小
					ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
					ss.setSpan(span, 0, newInput.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					// 在光标所在处插入表情
					mEditText.getText().insert(mEditText.getSelectionStart(), ss);
                }
            });
            return convertView;
        }
    }

    public boolean isShowing(){
    	return getVisibility() == View.VISIBLE;
    }
}