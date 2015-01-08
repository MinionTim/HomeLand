package com.ville.homeland.adapter;

import java.util.ArrayList;
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
import com.example.android.bitmapfun.util.Utils;
import com.ville.homeland.R;
import com.ville.homeland.bean.Status;
import com.ville.homeland.bean.User;
import com.ville.homeland.ui.CompereAllImagesActivity;
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
public class BlogListAdapter extends BaseAdapter implements OnClickListener {
	
	private List<Status> mStatuses;
	private LayoutInflater mInflater;
	private String mFromFormater;
	private Context mContext;
//	private BitmapManager mBmpManager;
	private ImageFetcher mImageFetcher;
	public BlogListAdapter(Context context, List<Status> list) {
		// TODO Auto-generated constructor stub
		mStatuses = list;
//		for(Status s: list){
//			System.out.println(s);
//		}
//		mBmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.portrait));
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFromFormater = context.getResources().getString(R.string.from);
	}
	public void setImageFetcher(ImageFetcher fetcher){
		mImageFetcher = fetcher;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mStatuses.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mStatuses.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListItemView itemView;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.itemview, null);
			itemView = new ListItemView();
			
			itemView.portraitImage = (ImageView) convertView.findViewById(R.id.ivItemPortrait);
			itemView.portraitImageV = (ImageView) convertView.findViewById(R.id.ivItemPortraitV);
			itemView.contentView = (BlogTextView) convertView.findViewById(R.id.tvItemContent);
			itemView.subContentView = (BlogTextView) convertView.findViewById(R.id.tvItemSubContent);
			itemView.subLayout = (LinearLayout) convertView.findViewById(R.id.subLayout);
			itemView.userName= (TextView) convertView.findViewById(R.id.tvItemName);
			
			itemView.fromTv = (TextView) convertView.findViewById(R.id.tweet_form);
			itemView.uploadPic = (ImageView) convertView.findViewById(R.id.tweet_upload_pic1);
			itemView.uploadGif = (ImageView) convertView.findViewById(R.id.ivUploadGif1);
			itemView.subUploadPic = (ImageView) convertView.findViewById(R.id.tweet_upload_pic2);
			itemView.subUploadGif = (ImageView) convertView.findViewById(R.id.ivUploadGif2);
			itemView.dateTv = (TextView) convertView.findViewById(R.id.tvItemDate);
			
			itemView.uploadImages1 = convertView.findViewById(R.id.rlUploadPic1);
			itemView.uploadImages2 = convertView.findViewById(R.id.rlUploadPic2);
			
			convertView.setTag(itemView);
		}else {
			itemView = (ListItemView) convertView.getTag();
		}
		Status status = mStatuses.get(position);
		User user = status.getUser();
		Status retweetStatus = status.getRetweeted_status();
		
		String content = status.getText();
		String name = user.getScreen_name();
		
		itemView.contentView.setWeiboText(content);
		itemView.userName.setText(name);
//		System.out.println("image= "+status.getThumbnail_pic());
//		mBmpManager.loadBitmap(user.getProfile_image_url(), itemView.portraitImage);
//		mImageFetcher.loadImage(user.getProfile_image_url(), itemView.portraitImage, R.drawable.portrait);
		if(user.isVerified()){
			itemView.portraitImageV.setImageResource(R.drawable.portrait_v);
		}
		if(status.hasPictrues()){
			String imageUrl = status.getThumbnail_pic();
			itemView.uploadPic.setVisibility(View.VISIBLE);
			itemView.uploadPic.setOnClickListener(this);
			itemView.uploadPic.setTag(status.getHD_pic());
			if(imageUrl.toLowerCase().endsWith(".gif")){
				itemView.uploadGif.setVisibility(View.VISIBLE);
			}else {
				itemView.uploadGif.setVisibility(View.GONE);
			}
//			mBmpManager.loadBitmap(imageUrl, itemView.uploadPic, BitmapFactory.decodeResource(mContext.getResources(), R.drawable.preview_card_pic_loading));
//			mImageFetcher.loadImage(imageUrl, itemView.uploadPic, R.drawable.preview_card_pic_loading);
		}else{
			itemView.uploadImages1.setVisibility(View.GONE);
		}
		if(retweetStatus != null){
			itemView.subContentView.setWeiboText("@"+retweetStatus.getUser().getScreen_name()+": "+retweetStatus.getText());
			itemView.subLayout.setVisibility(View.VISIBLE);
			if(retweetStatus.hasPictrues()){
				String imageUrl = retweetStatus.getThumbnail_pic();
				itemView.subUploadPic.setVisibility(View.VISIBLE);
				itemView.subUploadPic.setOnClickListener(this);
				itemView.subUploadPic.setTag(retweetStatus.getHD_pic());
				if(imageUrl.toLowerCase().endsWith(".gif")){
					System.out.println("Gif: " + imageUrl.toLowerCase());
					itemView.subUploadGif.setVisibility(View.VISIBLE);
				}else {
					itemView.subUploadGif.setVisibility(View.GONE);
				}
//				mBmpManager.loadBitmap(imageUrl, itemView.subUploadPic, BitmapFactory.decodeResource(mContext.getResources(), R.drawable.preview_card_pic_loading));
//				mImageFetcher.loadImage(imageUrl, itemView.subUploadPic, R.drawable.preview_card_pic_loading);
			}else{
				itemView.uploadImages2.setVisibility(View.GONE);
			}
		}else {
			itemView.subLayout.setVisibility(View.GONE);
		}
		itemView.fromTv.setText(String.format(mFromFormater, Html.fromHtml(status.getSource())));		
//		itemView.dateTv.setText(Utilities.getTimeDiff(mContext, status.getCreated_at()));
		itemView.dateTv.setText(StringUtils.friendly_time(status.getCreated_at()));
//		itemView.dateTv.setText(status.getCreated_at().toString());
		
		return convertView;
	}
	
	static class ListItemView{				//自定义控件集合  
		public TextView userName;
		public ImageView portraitImage;
		public ImageView portraitImageV;
		public BlogTextView contentView;
		public BlogTextView subContentView;
		public LinearLayout subLayout;
		public TextView fromTv;
		public ImageView uploadPic;
		public ImageView uploadGif;
		public ImageView subUploadPic;
		public ImageView subUploadGif;
		public TextView dateTv;
		public View uploadImages1;
		public View uploadImages2;
 }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
//		Utils.toastShort(mContext, "Image Click:");
		int id = v.getId();
		if(id == R.id.tweet_upload_pic2 || id == R.id.tweet_upload_pic1){
			ArrayList<String> list = new ArrayList<String>();
			String str = (String) v.getTag();
			list.add(str);
			System.out.println("URL = " + str);
			Intent intent = new Intent();
			intent.setClass(mContext, ImageDetailActivity.class);
			Bundle bundle = new Bundle();
			bundle.putStringArrayList(ImageDetailActivity.EXTRA_IMAGE_URLS, list);
			intent.putExtras(bundle);
			mContext.startActivity(intent);
		}
	}  
}