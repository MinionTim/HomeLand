package com.ville.homeland.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.android.bitmapfun.util.Utils;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.Status;
import com.ville.homeland.R;
import com.ville.homeland.adapter.CommentListAdapter;
import com.ville.homeland.weibo.DefaultRequestListener;
import com.ville.homeland.weibo.WeiboService;
import com.ville.homeland.widget.PullToRefreshListView2;
import com.ville.homeland.widget.PullToRefreshListView2.OnRefreshListener;

public class StatusDetailActivity extends SherlockFragmentActivity {
	private static final String TAG = "StatusDetailActivity";
	private Status mStatus;
	private WeiboService mWeiboService;
	private PullToRefreshListView2 mLvComments;
	
	private CommentListAdapter mAdapter;
	private long mNextCursor;
	private boolean mIsRequestRunning;
	
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
		mNextCursor = 0;
		loadData();
	}
	private void loadData(){
		if(!mIsRequestRunning){
			mWeiboService.requestComments(this, Long.parseLong(mStatus.id), mNextCursor, 1, mListener);
			mIsRequestRunning = true;
		}
	}
	private DefaultRequestListener mListener = new DefaultRequestListener(this) {
		
		@Override
		public void onComplete(String responce) {
			// TODO Auto-generated method stub
			mIsRequestRunning = false;
			CommentList commList = CommentList.parse(responce);
			if(commList != null && commList.commentList != null && commList.commentList.size() >= 0 ){
				mNextCursor = Long.parseLong(commList.next_cursor);
				mAdapter.append(commList.commentList);
				mAdapter.updateCommentTotalNum(commList.total_number);
				if(mNextCursor > 0){
					mAdapter.updateLoadingState(CommentListAdapter.VIEW_LOADING_RUNNING);
				}else{
					mAdapter.updateLoadingState(CommentListAdapter.VIEW_LOADING_COMPLETE);
				}
				mAdapter.notifyDataSetChanged(); 
				
			}else {
				Utils.toastShort(StatusDetailActivity.this, "No comments!!");
			}
		}
	};
	
	private void initViews() {
		// TODO Auto-generated method stub
		mAdapter = new CommentListAdapter(this, mStatus);
		mLvComments = (PullToRefreshListView2) findViewById(R.id.lv_comments);
		mLvComments.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onLastItemVisiable() {
				// TODO Auto-generated method stub
				if(hasMoreResults()){
					loadData();
				}
			}
		});
		
		mLvComments.setAdapter(mAdapter);
	}
	
	private boolean hasMoreResults(){
		return mNextCursor != 0;
	}

	private void log(String msg){
		Log.d(TAG, msg);
	}
}
