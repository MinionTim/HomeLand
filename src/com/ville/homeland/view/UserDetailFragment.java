package com.ville.homeland.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.example.android.bitmapfun.util.Utils;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.User;
import com.ville.homeland.R;
import com.ville.homeland.util.BitmapManager;
import com.ville.homeland.weibo.WeiboService;

public class UserDetailFragment extends SherlockDialogFragment {
	private static final String TAG = "UserDetailFragment";
	
	private User mUser;
	private TextView mName;
	private TextView mLocation;
	private TextView mFriends;
	private TextView mFollows;
	private TextView mDescription;
	private ImageView mAvatar;
	private ImageView mCover;
	private ProgressBar mProgressBar;
	private BitmapManager mBmpManager;
	private String mScreenName;
	
	
	public static UserDetailFragment newInstance(User user, BitmapManager bitmapManager) {
		UserDetailFragment fragment = new UserDetailFragment();
		fragment.mUser = user;
		fragment.mScreenName = null;
		fragment.mBmpManager = bitmapManager;
		fragment.setStyle(STYLE_NO_TITLE, 0);
		return fragment;
	}
	
	public static UserDetailFragment newInstance(String screenName, BitmapManager bitmapManager) {
		UserDetailFragment fragment = new UserDetailFragment();
		fragment.mUser = null;
		fragment.mBmpManager = bitmapManager;
		fragment.mScreenName = screenName;
		fragment.setStyle(STYLE_NO_TITLE, 0);
		return fragment;
	}
	
	private void fetchUserInfo(String screenName){
		WeiboService.getInstance().requestShowUserInfo(getActivity(), screenName, 
				new RequestListener() {
			
			@Override
			public void onWeiboException(WeiboException arg0) {
				// TODO Auto-generated method stub
				Utils.toastLong(getActivity(), arg0.getMessage());
			}
			
			@Override
			public void onComplete(String arg0) {
				// TODO Auto-generated method stub
				mProgressBar.setVisibility(View.GONE);
				mUser = User.parse(arg0);
				setupViewData(mUser);
			}
		});
	}
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_user_detail, container, false);
		mAvatar = (ImageView) rootView.findViewById(R.id.iv_avatar);
		mCover = (ImageView) rootView.findViewById(R.id.iv_backgound);
		mName = (TextView) rootView.findViewById(R.id.tv_name);
		mLocation = (TextView) rootView.findViewById(R.id.tv_location);
		mFriends = (TextView) rootView.findViewById(R.id.tv_friends);
		mFollows = (TextView) rootView.findViewById(R.id.tv_follows);
		mDescription = (TextView) rootView.findViewById(R.id.tv_descriptions);
		mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress);
		if(mUser != null){
			setupViewData(mUser);
		} else {
			mProgressBar.setVisibility(View.VISIBLE);
			fetchUserInfo(mScreenName);
		}
		return rootView;
	}
	
	
	private void setupViewData(User user) {
		// TODO Auto-generated method stub
		if(user != null){
			mName.setText(user.screen_name);
			mLocation.setText(user.location != null ? user.location : "");
			mFriends.setText("关注:" + friendlyNumer(user.friends_count));
			mFollows.setText("粉丝:" + friendlyNumer(user.followers_count));
			mDescription.setText(user.description != null ? user.description : "");
			
			mBmpManager.loadBitmap(user.avatar_large, mAvatar);
//			mBmpManager.loadBitmap(mUser.cover_image, mCover);
		}
	}

	private String friendlyNumer(int num){
		if(num > 100000){
			return Math.round(num / 10000F) + "万";
		}
		return String.valueOf(num);
	}
}
