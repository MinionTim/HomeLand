package com.ville.homeland.adapter;

import java.util.Date;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;
import com.ville.homeland.R;
import com.ville.homeland.listener.StatusComponentViewTag;
import com.ville.homeland.util.BitmapManager;
import com.ville.homeland.util.StringUtils;
import com.ville.homeland.widget.BlogTextView;

public class StatusRowViewBinder {
	public static final int MODE_STATUS = 1;
	public static final int MODE_COMMENT = 2;
	private static class ViewHolder {
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
		public TextView commentNum;
	}
	public static void bindStatusRowView(Context context, View rootView, Status status, BitmapManager bitmapManager, OnClickListener listener, int mode){
		Object tempViews = rootView.getTag();
		final ViewHolder holder;
        if (tempViews != null) {
        	holder = (ViewHolder) tempViews;
        } else {
        	holder = new ViewHolder();
            rootView.setTag(holder);
            
			holder.portraitImage = (ImageView) rootView.findViewById(R.id.ivItemPortrait);
			holder.portraitImageV = (ImageView) rootView.findViewById(R.id.ivItemPortraitV);
			holder.contentView = (BlogTextView) rootView.findViewById(R.id.tvItemContent);
			holder.userName = (TextView) rootView.findViewById(R.id.tvItemName);
			holder.commentNum = (TextView) rootView.findViewById(R.id.tv_comment_num);
			
			holder.subContentView = (BlogTextView) rootView.findViewById(R.id.tvItemSubContent);
			holder.subLayout = (LinearLayout) rootView.findViewById(R.id.subLayout);
			
			holder.uploadImages1 = rootView.findViewById(R.id.rlUploadPic1);
			holder.uploadPic = (ImageView) rootView.findViewById(R.id.tweet_upload_pic1);
			holder.uploadGif = (ImageView) rootView.findViewById(R.id.ivUploadGif1);
			
			holder.uploadImages2 = rootView.findViewById(R.id.rlUploadPic2);
			holder.subUploadPic = (ImageView) rootView.findViewById(R.id.tweet_upload_pic2);
			holder.subUploadGif = (ImageView) rootView.findViewById(R.id.ivUploadGif2);
			
			holder.dateTv = (TextView) rootView.findViewById(R.id.tvItemDate);
			holder.fromTv = (TextView) rootView.findViewById(R.id.tweet_form);
			
			
		}
        
		User user = status.user;
		Status retweetStatus = status.retweeted_status;
		
		
		holder.contentView.setWeiboText(status.text);
		holder.userName.setText(user.screen_name);
		holder.contentView.setOnLinkClickedListener(listener);
		
//		holder.contentView.seton
//		System.out.println("image= "+status.getThumbnail_pic());
		if(bitmapManager != null){
			bitmapManager.loadBitmap(user.profile_image_url, holder.portraitImage);
		}
//		mImageFetcher.loadImage(user.getProfile_image_url(), itemView.portraitImage, R.drawable.portrait);
		if(mode == MODE_STATUS){
			holder.commentNum.setVisibility(View.VISIBLE);
			holder.commentNum.setText(String.valueOf(status.comments_count));
		}else {
			holder.commentNum.setVisibility(View.INVISIBLE);
		}
		holder.portraitImage.setOnClickListener(listener);
		holder.portraitImage.setTag(StatusComponentViewTag.build(
				StatusComponentViewTag.TAG_AVATER_USER, status.user));
		if(user.verified){
			holder.portraitImageV.setImageResource(R.drawable.portrait_v);
		}
		if(status.pic_urls != null && status.pic_urls.size() > 0){
			holder.uploadImages1.setVisibility(View.VISIBLE);
			String imageUrl = status.thumbnail_pic;
			holder.uploadPic.setVisibility(View.VISIBLE);
			holder.uploadPic.setOnClickListener(listener);
			holder.uploadPic.setTag(StatusComponentViewTag.build(
					StatusComponentViewTag.TAG_UPLOAD_IMAGE, status.original_pic));
			if(imageUrl.toLowerCase().endsWith(".gif")){
				holder.uploadGif.setVisibility(View.VISIBLE);
			}else {
				holder.uploadGif.setVisibility(View.GONE);
			}
			if(bitmapManager != null){
				bitmapManager.loadBitmap(imageUrl, holder.uploadPic, BitmapFactory.decodeResource(context.getResources(), R.drawable.preview_card_pic_loading));
			}
//			mImageFetcher.loadImage(imageUrl, itemView.uploadPic, R.drawable.preview_card_pic_loading);
		}else{
			holder.uploadImages1.setVisibility(View.GONE);
		}
		if(retweetStatus != null){
			holder.subContentView.setWeiboText("@"+retweetStatus.user.screen_name+": "+retweetStatus.text);
			holder.subContentView.setOnLinkClickedListener(listener);
			holder.subLayout.setVisibility(View.VISIBLE);
			if(retweetStatus.pic_urls != null && retweetStatus.pic_urls.size() > 0){
				holder.uploadImages2.setVisibility(View.VISIBLE);
				String imageUrl = retweetStatus.thumbnail_pic;
				holder.subUploadPic.setVisibility(View.VISIBLE);
				holder.subUploadPic.setOnClickListener(listener);
				holder.subUploadPic.setTag(StatusComponentViewTag.build(
						StatusComponentViewTag.TAG_UPLOAD_IMAGE, retweetStatus.original_pic));
				if(imageUrl.toLowerCase().endsWith(".gif")){
					System.out.println("Gif: " + imageUrl.toLowerCase());
					holder.subUploadGif.setVisibility(View.VISIBLE);
				}else {
					holder.subUploadGif.setVisibility(View.GONE);
				}
				if(bitmapManager != null){
					bitmapManager.loadBitmap(imageUrl, holder.subUploadPic, BitmapFactory.decodeResource(context.getResources(), R.drawable.preview_card_pic_loading));
				}
//				mImageFetcher.loadImage(imageUrl, itemView.subUploadPic, R.drawable.preview_card_pic_loading);
			}else{
				holder.uploadImages2.setVisibility(View.GONE);
			}
		}else {
			holder.subLayout.setVisibility(View.GONE);
		}
		holder.fromTv.setText(createSourceString(context, status));
		holder.dateTv.setText(StringUtils.friendly_time(new Date(status.created_at)));
	}
	
	private static String createSourceString(Context context, Status status){
		String dateFormater = context.getResources().getString(R.string.from);
		return String.format(dateFormater, Html.fromHtml(status.source));
	}
}
