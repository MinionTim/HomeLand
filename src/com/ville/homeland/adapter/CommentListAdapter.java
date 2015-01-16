package com.ville.homeland.adapter;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.Status;
import com.ville.homeland.R;
import com.ville.homeland.util.StringUtils;
import com.ville.homeland.widget.BlogTextView;


public class CommentListAdapter extends BaseAdapter {
	
//	private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_LIST = 1;
    private static final int VIEW_TYPE_LOADER= 2;
    
    public static final int VIEW_LOADING_COMPLETE 	= 1;
    public static final int VIEW_LOADING_RUNNING 	= 2;
//    public static final int VIEW_LOADING_RUNNING 	= 2;
    public static final int VIEW_LOADING_ERROR 		= 3;
    
	private ArrayList<Comment> mList = new ArrayList<Comment>();
	private Context mContext;
	private LayoutInflater mInflater;
	private Status mStatus;
	private String mLoadLabel;
	private int mLoadingState;
	private int mTotalNumber;
	
	public CommentListAdapter(Context context, Status status){
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mStatus = status;
		mLoadingState = VIEW_LOADING_RUNNING;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size() + 1;
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return getItemViewType(position) == VIEW_TYPE_LIST;
	}
	
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		if(position >= mList.size()){
			return VIEW_TYPE_LOADER;
		}else {
			return VIEW_TYPE_LIST;
		}
	}
	
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return getItemViewType(position) == VIEW_TYPE_LIST
					? mList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void append(ArrayList<Comment> data){
		mList.addAll(mList.size(), data);
	}
	public void setAll(ArrayList<Comment> data){
		mList.clear();
		mList.addAll(data);
	}
	
	public void clear(){
		mList.clear();
	}
	
	public void updateLoadingState(int state){
		mLoadingState = state;
		if(state == VIEW_LOADING_RUNNING){
			mLoadLabel = "正在加载";
		}else if (state == VIEW_LOADING_COMPLETE){
			mLoadLabel = mList.size() == 0 ? "无数据" : "已全部加载";
		}else {
			mLoadLabel = "数据异常!";
		}
	}
//	public void updateCommentTotalNum(int total){
//		mTotalNumber = total;
//	}
//	@Override
//	public boolean hasStableIds() {
//		// TODO Auto-generated method stub
//		return true;
//	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (getItemViewType(position) == VIEW_TYPE_LOADER){
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.list_comment_status, null);
			}
			if (mLoadingState == VIEW_LOADING_COMPLETE) {
                convertView.findViewById(android.R.id.progress).setVisibility(View.GONE);
            } else {
                convertView.findViewById(android.R.id.progress).setVisibility(View.VISIBLE);
            }
			((TextView) convertView.findViewById(android.R.id.text1)).setText(mLoadLabel);
			return convertView;
		} else {
			ViewHolder holder;
			if(convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.list_comment_item, null);
				holder.tvName = (TextView) convertView.findViewById(R.id.tvUserName);
				holder.ivPortrait = (ImageView) convertView.findViewById(R.id.ivItemPortrait);
				holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
				holder.btvText = (BlogTextView) convertView.findViewById(R.id.tvItemContent);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			Comment comm = (Comment) getItem(position);
			holder.tvName.setText(comm.user.screen_name);
			holder.tvDate.setText(StringUtils.friendly_time(new Date(comm.created_at)));
			holder.btvText.setWeiboText(comm.text);
			return convertView;
		}
	}
	private static class ViewHolder {
		TextView tvName;
		ImageView ivPortrait;
		TextView tvDate;
		BlogTextView btvText;
	}

}
