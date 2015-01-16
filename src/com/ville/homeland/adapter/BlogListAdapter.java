package com.ville.homeland.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bitmapfun.util.ImageFetcher;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;
import com.ville.homeland.R;
import com.ville.homeland.ui.ImageDetailActivity;
import com.ville.homeland.util.BitmapManager;
import com.ville.homeland.util.StringUtils;
import com.ville.homeland.widget.BlogTextView;

/**
 * 新闻资讯Adapter类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class BlogListAdapter extends BaseAdapter {
	
	private List<Status> mStatuses;
	private LayoutInflater mInflater;
	private String mFromFormater;
	private Context mContext;
	private BitmapManager mBmpManager;
	private View.OnClickListener mComponentClickListener;
	public BlogListAdapter(Context context, BitmapManager bitmapManager, View.OnClickListener listener) {
		// TODO Auto-generated constructor stub
		mStatuses = new ArrayList<Status>();
		mBmpManager = bitmapManager;
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFromFormater = context.getResources().getString(R.string.from);
		
		mComponentClickListener = listener;
	}
	public void setImageFetcher(ImageFetcher fetcher){
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mStatuses.size();
	}

	@Override
	public Status getItem(int position) {
		// TODO Auto-generated method stub
		return mStatuses.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void append(ArrayList<Status> data){
		mStatuses.addAll(mStatuses.size(), data);
	}
	public void setAll(ArrayList<Status> data){
		mStatuses.clear();
		mStatuses.addAll(data);
	}
	
	public void clear(){
		mStatuses.clear();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.list_status_item, null);
		}
		Status status = mStatuses.get(position);
		StatusRowViewBinder.bindStatusRowView(mContext, convertView, status, mBmpManager,
				mComponentClickListener, StatusRowViewBinder.MODE_STATUS);
		return convertView;
	}

}