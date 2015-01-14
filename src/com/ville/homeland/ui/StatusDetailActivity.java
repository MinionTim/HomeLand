package com.ville.homeland.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.android.bitmapfun.util.Utils;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.Status;
import com.ville.homeland.R;
import com.ville.homeland.adapter.CommentListAdapter;
import com.ville.homeland.util.SmileyPicker;
import com.ville.homeland.weibo.WeiboService;
import com.ville.homeland.widget.PullToRefreshListView2;
import com.ville.homeland.widget.PullToRefreshListView2.OnRefreshListener;

public class StatusDetailActivity extends SherlockFragmentActivity implements OnClickListener {
	private static final String TAG = "StatusDetailActivity";
	private Status mStatus;
	private WeiboService mWeiboService;
	private PullToRefreshListView2 mLvComments;
	
	private CommentListAdapter mAdapter;
	private long mNextCursor = 0L;
	private int mLastResultCount;
	private boolean mIsRequestRunning;
	private boolean mHasError = false;
	private ImageView mBtnEmoji;
	private EditText mEtInput;
	private SmileyPicker mSmileyPicker;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		Intent intent = getIntent(); 
		if(intent != null) {
			Bundle extra = intent.getExtras();
			mStatus = (Status) extra.getSerializable("key_status");
			log("getUsr: " + mStatus.user.screen_name);
		}
		setContentView(R.layout.activity_status_detail);
		initViews();
		
		mWeiboService = WeiboService.getInstance();
		loadData();
	}
	private void loadData(){
		if(!mIsRequestRunning){
			mWeiboService.requestComments(this, Long.parseLong(mStatus.id), mNextCursor, 1, mReqCommentsListener);
			Log.d(TAG, "loadData [id, NextCursor] = [" + mStatus.id + ", " + mNextCursor + "]");
			mIsRequestRunning = true;
		}
	}
	private RequestListener mReqCommentsListener = new RequestListener() {
		
		@Override
		public void onWeiboException(WeiboException e) {
			mIsRequestRunning = false;
			mHasError = true;
			Utils.toastShort(StatusDetailActivity.this, e.getMessage());
			mAdapter.updateLoadingState(CommentListAdapter.VIEW_LOADING_ERROR);
			mAdapter.notifyDataSetChanged(); 
		}
		
		@Override
		public void onComplete(String responce) {
			// TODO Auto-generated method stub
			mIsRequestRunning = false;
			mHasError = false;
			Log.d(TAG, "onComplete ...");
			mLvComments.onRefreshComplete();
			CommentList commList = CommentList.parse(responce);
			if(commList != null && commList.commentList != null && commList.commentList.size() >= 0 ){
				mNextCursor = Long.parseLong(commList.next_cursor);
				mLastResultCount = commList.commentList.size();
				mAdapter.append(commList.commentList);
				mAdapter.updateCommentTotalNum(commList.total_number);
				if(hasMoreResults()){
					mAdapter.updateLoadingState(CommentListAdapter.VIEW_LOADING_RUNNING);
				}else{
					mAdapter.updateLoadingState(CommentListAdapter.VIEW_LOADING_COMPLETE);
				}
				mAdapter.notifyDataSetChanged(); 
			}else {
				mNextCursor = 0L;
				mLastResultCount = 0;
				mAdapter.updateLoadingState(CommentListAdapter.VIEW_LOADING_COMPLETE);
				mAdapter.notifyDataSetChanged(); 
			}
		}
	}; 
	
	private void initViews() {
		// TODO Auto-generated method stub
		mAdapter = new CommentListAdapter(this, mStatus);
		mLvComments = (PullToRefreshListView2) findViewById(R.id.lv_comments);
		mBtnEmoji = (ImageView) findViewById(R.id.iv_emoji);
		mEtInput = (EditText) findViewById(R.id.et_input);
		mSmileyPicker = (SmileyPicker) findViewById(R.id.sp_emoji_input_panel);
		mSmileyPicker.setEditText(mEtInput);
		
		findViewById(R.id.tv_send).setOnClickListener(this);
		mEtInput.setOnClickListener(this);
		mLvComments.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				mNextCursor = 0;
				mAdapter.clear();
				mAdapter.updateLoadingState(CommentListAdapter.VIEW_LOADING_RUNNING);
				mAdapter.notifyDataSetInvalidated();
				loadData();
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				if(hasMoreResults() && !mHasError){
					Log.d(TAG, "onLoadMore...");
					loadData();
				}
			}
		});
		mBtnEmoji.setOnClickListener(this);
		mBtnEmoji.setImageLevel(0);
		
		TextView v = new TextView(this);
		v.setText("heHE");
		mLvComments.addHeaderView(v);
		mLvComments.setAdapter(mAdapter);
		
	}
	
	private boolean hasMoreResults(){
		return mNextCursor != 0 && mLastResultCount == 20;
	}

	private void log(String msg){
		Log.d(TAG, msg);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_emoji:
			if(mSmileyPicker.getVisibility() == View.VISIBLE){
				mBtnEmoji.setImageLevel(0);
				mSmileyPicker.hide(StatusDetailActivity.this);
				showKeyBoard(this);
			}else {
				mBtnEmoji.setImageLevel(1);
				mSmileyPicker.show(StatusDetailActivity.this);
				hideSoftInput(this);
			}
			break;
			
		case R.id.et_input:
			if(mSmileyPicker.isShowing()){
				mBtnEmoji.setImageLevel(0);
				mSmileyPicker.hide(StatusDetailActivity.this);
				showKeyBoard(this);
			}
			break;
		case R.id.tv_send:
			onSendClicked();
			Log.d(TAG, "tv_send " + mEtInput.getText().toString());
			break;

		default:
			break;
		}
	}
	private void onSendClicked() {
		// TODO Auto-generated method stub
		mSmileyPicker.hide(this);
		hideSoftInput(this);
		final ProgressDialog dialog = ProgressDialog.show(this, "", "正在发表评论...");
		dialog.setCancelable(false);
		mWeiboService.createComment(StatusDetailActivity.this, mEtInput.getText().toString(), 
				Long.parseLong(mStatus.id), new RequestListener() {
					
					@Override
					public void onWeiboException(WeiboException e) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						Utils.toastLong(StatusDetailActivity.this, e.getMessage());
					}
					
					@Override
					public void onComplete(String arg0) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						Toast.makeText(StatusDetailActivity.this, "评论成功!", Toast.LENGTH_SHORT).show();
						mEtInput.setText("");
						mLvComments.clickRefresh();
					}
				});
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(mSmileyPicker.isShowing()){
			mSmileyPicker.hide(StatusDetailActivity.this);
			mBtnEmoji.setImageLevel(0);
		}else {
			super.onBackPressed();
		}
	}
	private void hideSoftInput(Context context) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(mEtInput.getWindowToken(), 0);
        //open smilepicker, press home, press app switcher to return to write weibo interface,
        //softkeyboard will be opened by android system when smilepicker is showing,
        // this method is used to fix this issue
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    
    private void showKeyBoard(final Context context) {
    	mEtInput.requestFocus();
    	((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
    		.showSoftInput(mEtInput, 0);
    }
}
