package com.ville.homeland.ui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bitmapfun.util.Utils;
import com.ville.homeland.R;
import com.ville.homeland.bean.PlayInfo;
import com.ville.homeland.util.FileUtils;
import com.ville.homeland.util.NetUtils;
import com.ville.homeland.widget.VerticalSeekBar;

public class PlayerActivity2 extends Activity implements OnSeekCompleteListener, OnInfoListener, OnCompletionListener, OnErrorListener,
OnPreparedListener, SurfaceHolder.Callback, OnSeekBarChangeListener, View.OnClickListener, OnBufferingUpdateListener, OnVideoSizeChangedListener{
	
	private int mScreenW;
	private int mScreenH;
	public static final int MSG_UPDATE_UI_INFOS 	= 1;
	private final static int MSG_VIDEO_PLAY_NEXT 	= 2;
	
	private static final int MSG_SHOW_CONTROL_TITLE = 3;
	private static final int MSG_HIDE_CONTROL_TITLE = 4;
	
	private static final int FADEOUT_TIMEOUT = 3000;
	
	private ImageView mBtnCtrl;
	private ViMediaPlayer mCurrentPlayer;
	private ViMediaPlayer mNextPlayer;
	private SurfaceHolder mHolder;
	private SeekBar mSeekBar;
	private VerticalSeekBar mVolumeSeekBar;
	private TextView mTimePassedView;
	private TextView mTimeTotalView;
	private TextView mCacheTv;
	private TextView mSystemTimeTv;
	private View mTilteBar;
	private View mControlBar;
	private View mRootView;
	private View mVolumeBar;
	
	private TextView mTitleTv;
	private int mCommonProgress;
	private SurfaceView mSurfaceView;
	private ArrayList<VideoInfo> mInfos = new ArrayList<VideoInfo>();
	private VideoInfo mCurPlayingInfo;
	private VideoInfo mNextPlayingInfo;
	private ProgressDialog mDialog;
	private boolean mDragging;
	private boolean mIsControlBarShowing;
	private DateFormat mDateFormate;
	private AudioManager mAudioManager;
	
	private int mTempUnitPosition = 0;
	
	private int mediaTime = 0; //ms
	private boolean mBtnCtrlPlaying = false;
	
	static final class VideoInfo{
		String url;
		int time; //ms
		int index;
		public VideoInfo(String remoteURL, int time, int index){
			this.url = remoteURL;
			this.time = time;
			this.index = index;
		}
	}
	
	private Handler mUIHandler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				
			case MSG_UPDATE_UI_INFOS:
				int pos = 0;
				if (mCurrentPlayer != null && mCurrentPlayer.isPlaying() && !mDragging) {
					pos = updateProgress();
				}
				updateSystemTime();
				mUIHandler.sendEmptyMessageDelayed(MSG_UPDATE_UI_INFOS, 1000 - (pos%1000));
				break;
				
			case MSG_VIDEO_PLAY_NEXT:
				break;
				
			case MSG_HIDE_CONTROL_TITLE:
				hideControlBar();
				break;
				
			case MSG_SHOW_CONTROL_TITLE:
				showControlBar();
				break;
				
			}
		}

	};
	
	private void updateSystemTime() {
		// TODO Auto-generated method stub
		if(mSystemTimeTv != null ){
			String timeStr = mDateFormate.format(new java.util.Date());
			mSystemTimeTv.setText(timeStr);
		}
	};
	protected void hideControlBar() {
		// TODO Auto-generated method stub
		mIsControlBarShowing = false;
		mControlBar.setVisibility(View.GONE);
		mTilteBar.setVisibility(View.GONE);
		mVolumeBar.setVisibility(View.GONE);
	}
	protected void showControlBar() {
		// TODO Auto-generated method stub
		mIsControlBarShowing = true;
		mControlBar.setVisibility(View.VISIBLE);
		mTilteBar.setVisibility(View.VISIBLE);
		mVolumeBar.setVisibility(View.VISIBLE);
		
		mUIHandler.removeMessages(MSG_HIDE_CONTROL_TITLE);
		mUIHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_TITLE, FADEOUT_TIMEOUT);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		mScreenW = getResources().getDisplayMetrics().widthPixels;
		mScreenH = getResources().getDisplayMetrics().heightPixels;
		
		ContentResolver cv = this.getContentResolver();  
	    String str24_12 = android.provider.Settings.System.getString(cv,  
	                                          android.provider.Settings.System.TIME_12_24);  
	    String inFormat = null;
	    if("24".equals(str24_12)){
	    	inFormat = "HH:mm";
	    }else {
	    	inFormat = "hh:mm";
	    }
		mDateFormate = new SimpleDateFormat(inFormat);
		
		setContentView(R.layout.activity_play_view);
		init();
		
	}
	
	protected int updateProgress() {
		// TODO Auto-generated method stub
		int cur = getCurCommonMediaPostion();
		int duration = mediaTime;
		if(duration > 0){
			mTimePassedView.setText(formatTime(cur));
			mSeekBar.setProgress(cur);
		}
		if(mCurrentPlayer.getCurrentPosition() / mCurrentPlayer.getDuration() >= 0.9){
			if (mNextPlayer.getState() == STATE_STOPPED) {
				mNextPlayer.updateState(STATE_IDLE);
				int state = mNextPlayer.getState();
				if (state == STATE_IDLE) {
					prepareForNextPlayer();
				}
			}
		}
		duration = duration == 0 ? 1 : duration;

		int unitDuration = mCurrentPlayer.getDuration();
		unitDuration = unitDuration == 0 ? 1 : unitDuration;

		String s = "";
		s += String.format(" 播放: %s -- %s\n", formatTime(mCurrentPlayer.getCurrentPosition()), formatTime(unitDuration));
		s += String.format(" 播放：第%d段————缓冲：第%d段",mCurPlayingInfo.index, mNextPlayingInfo==null ? -1 : mNextPlayingInfo.index);

		mCacheTv.setText(s);
		mCacheTv.setVisibility(View.GONE);
		return cur;
	}

	private AsyncTask<String, Void, Boolean> mParseTask = new AsyncTask<String, Void, Boolean>(){

		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			String pid = params[0];
			String content = NetUtils.getContentByLink(NetUtils.getVideoPlayInfoUrl(pid));
			PlayInfo info = PlayInfo.createPlayInfo(content);
			List<PlayInfo.Info> temInfos = info.getListNor();
			for(int i = 0; i < temInfos.size(); i++){
				mInfos.add(new VideoInfo(temInfos.get(i).url, temInfos.get(i).duration * 1000 , i));
			}
			mediaTime = (int) info.getTotalLength() * 1000;
			mSeekBar.setMax(mediaTime);
			System.out.println();
			mCurPlayingInfo = mInfos.get(0);
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if(result){
				mCurrentPlayer.resetAndPrepare(mCurPlayingInfo);
			}else {
				dismissProgressDialog();
				Toast.makeText(PlayerActivity2.this, "视频地址解析失败", Toast.LENGTH_SHORT).show();
			}
		};
		
	};

	private void init() {
		// TODO Auto-generated method stub
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		mHolder = mSurfaceView.getHolder();
		mSeekBar = (SeekBar) findViewById(R.id.seekBar);
		mVolumeSeekBar = (VerticalSeekBar) findViewById(R.id.volume_sb);
		mBtnCtrl = (ImageView) findViewById(R.id.controller);
		mTimePassedView = (TextView) findViewById(R.id.timePassed);
		mTimeTotalView = (TextView) findViewById(R.id.timeTotal);
		mCacheTv = (TextView) findViewById(R.id.info_cache);
		mTitleTv = (TextView) findViewById(R.id.title);
		mSystemTimeTv = (TextView) findViewById(R.id.system_time);
		mTilteBar = findViewById(R.id.title_bar);
		mControlBar = findViewById(R.id.controlBar);
		mRootView = findViewById(R.id.root);
		mVolumeBar = findViewById(R.id.volume_bar);
		
		
		mTitleTv.setText(getIntent().getStringExtra("video-title"));
		
		mBtnCtrl.setOnClickListener(this);
		mRootView.setOnClickListener(this);
		mBtnCtrlPlaying = true;
		mDragging = false;
		
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mSeekBar.setOnSeekBarChangeListener(this);
		
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);    
		mVolumeSeekBar.setMax(100);
		final int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); 
		int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
		mVolumeSeekBar.setProgressAndThumb((int)(currentVolume * 1.0f / maxVolume * 100));
		mVolumeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				int vol = maxVolume * progress / seekBar.getMax();
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0);
			}
		});
		
		mCurrentPlayer = new ViMediaPlayer();
		mNextPlayer = new ViMediaPlayer();
		mCurrentPlayer.setOnVideoSizeChangedListener(this);
	}
	
	public int getCurCommonMediaPostion(){
		int current = mCurPlayingInfo.index;
		int position = mCurrentPlayer.getCurrentPosition();
		for(int i = 0; i < current; i++){
			position += mInfos.get(i).time;
		}
		return position;
	}
	public void resetAndPrepare(MediaPlayer player, String url){
		player.reset();
		try {
			player.setDataSource(url);
			player.prepareAsync();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void play(MediaPlayer player){
		player.start();
	}
	public void pause(MediaPlayer player){
		player.pause();
	}
	public void seekTo(int msec){
		boolean seekMyself = isSeekMyself(msec);
		mTempUnitPosition = getUnitSecAndUpdate(msec);
		String log = "seekTo ";
		if(seekMyself){
//			System.out.println("seekTo    AAAAAAAAAA :[mediaLength, readSize]=["+mediaLength+", "+readSize+"]");
			log += "AA " + mCurPlayingInfo.index;
			mCurrentPlayer.seekTo(Math.min(mCurrentPlayer.getDuration(), mTempUnitPosition));
			mTempUnitPosition = 0;
			mTimePassedView.setText(formatTime(msec));
		}else {
			log += "BB";
			if(mNextPlayingInfo != null && mCurPlayingInfo == mNextPlayingInfo){
				mCurrentPlayer.stop();
				ViMediaPlayer tempPlayer = mCurrentPlayer;
				mCurrentPlayer = mNextPlayer;
				switch (mCurrentPlayer.getState()) {
				case STATE_PREPARED:
					mCurrentPlayer.seekTo(mTempUnitPosition);
					mTempUnitPosition = 0;
					mCurrentPlayer.start();
					break;
					
				case STATE_PREPARING:
					break;
					
				default:
					mCurrentPlayer.resetAndPrepare(mCurPlayingInfo);
					break;
				}
				mNextPlayer = tempPlayer;
				mNextPlayer.updateState(STATE_IDLE);
//				mNextPlayer.stop();
			}else if(mCurPlayingInfo != null){
				log += "CC";
				mCurrentPlayer.stop();
//				mCurrentPlayer.initialize();
				int state = mNextPlayer.getState();
//				if(state == STATE_PREPARING || state == STATE_STOPPED){
//					mNextPlayer.stop();
//				}
				mCurrentPlayer.resetAndPrepare(mCurPlayingInfo);
			}
		}
		System.out.println(log);
	}
	
	public void prepareForNextPlayer(){
		mNextPlayingInfo = getNextInfo();
		mNextPlayer.resetAndPrepare(mNextPlayingInfo);
	}
	
	private boolean isSeekMyself(int commProgress){
		int index = mCurPlayingInfo.index;
		int start = 0;
		for(int i = 0; i < index; i++){
			start += mInfos.get(i).time;
		}
		System.out.println("p:"+commProgress + ", s:"+start);
		return (commProgress >= start) && (commProgress <= start+mCurPlayingInfo.time);
	}
	//由总的播放进度得到 该进度对应的某段视频的进度 
	private int getUnitSecAndUpdate(int commProgress) {
		// TODO Auto-generated method stub
		int unitCount = mInfos.size();
		int index = 0;
		for(index = 0; index < unitCount; index++){
			int delta = commProgress - mInfos.get(index).time;
			if(delta < 0){
				break;
			}else {
				commProgress -= mInfos.get(index).time;
			}
		}
		mCurPlayingInfo = mInfos.get(Math.min(index, unitCount-1));
		return Math.max(0, commProgress);
		
	}

	private void showProgressDialog() {
		mUIHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mDialog == null) {
					mDialog = ProgressDialog.show(PlayerActivity2.this, "视频缓存", "正在努力加载中 ...", true, false);
				}
			}
		});
	}

	private void dismissProgressDialog() {
		mUIHandler.post(new Runnable() {
			@Override
			public void run() {
				if (mDialog != null) {
					mDialog.dismiss();
					mDialog = null;
				}
			}
		});
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		showProgressDialog();
		mCurrentPlayer.initialize();
		mNextPlayer.initialize();
		
		mParseTask.execute(getIntent().getStringExtra("pid"));
		System.out.println("surfaceCreated: getDuration="+ mCurrentPlayer.getDuration());
//		mCurrentPlayer.resetAndPrepare(mCurPlayingInfo);
		showControlBar();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		System.out.println("surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		System.out.println("surfaceDestroyed");
		if(mCurrentPlayer != null){
			mCurrentPlayer.stop();
			mCurrentPlayer.release();
			mCurrentPlayer = null;
		}
		if(mNextPlayer != null){
			mNextPlayer.stop();
			mNextPlayer.release();
			mNextPlayer = null;
		}
	}
	
	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		// TODO Auto-generated method stub
		if(width == 0 || height == 0){
			return;
		}
		float scale = Math.min(mScreenH*1.0f / height, mScreenW*1.0f / width);
		LayoutParams params = new LayoutParams((int)(width * scale), (int)(height * scale));
		params.gravity = Gravity.CENTER;
		mSurfaceView.setLayoutParams(params);
		mSurfaceView.requestLayout();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if(mp == mCurrentPlayer){
			dismissProgressDialog();
			System.out.println("onPrepared  -- A");
//			mCurPlayingInfo.time = mp.getDuration();
			mUIHandler.sendEmptyMessage(MSG_UPDATE_UI_INFOS);
			mTimeTotalView.setText(formatTime(mediaTime));
			
			mCurrentPlayer.setDisplayHere();
			if(mTempUnitPosition!=0){
				mCurrentPlayer.seekTo(Math.min(mCurrentPlayer.getDuration(), mTempUnitPosition));
				mTempUnitPosition = 0;
			}
			mCurrentPlayer.start();
			
		}else {
			System.out.println("onPrepared  -- B");
			mNextPlayer.updateState(STATE_PREPARED);
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if(mp == mCurrentPlayer && mCurrentPlayer.getState() == STATE_PLAYING){
			System.out.println("onCompletion  -- A : "+ mCurrentPlayer.isPlaying());
			mCurrentPlayer.setDisplayNull();   // it is Very very very Important!!!
			mCurrentPlayer.stop();
			
			mCurPlayingInfo = getNextInfo();
			if(mCurPlayingInfo == null){
				System.out.println("Play   END");
				finish();
				return;
			}
			
			ViMediaPlayer tempPlayer = mCurrentPlayer;
			switch (mNextPlayer.getState()) {
			case STATE_PREPARED :
				System.out.println("onCompletion  READY");
				mCurrentPlayer = mNextPlayer;
				mCurrentPlayer.setDisplayHere();
				mCurrentPlayer.start();
				
				mNextPlayer = tempPlayer;
				mNextPlayer.updateState(STATE_IDLE);
				mNextPlayer.stop();
				break;

			case STATE_PREPARING:
				System.out.println("onCompletion  PREPARING");
				mCurrentPlayer = mNextPlayer;
				mNextPlayer = tempPlayer;
				mNextPlayer.updateState(STATE_IDLE);
				mNextPlayer.stop();
				break;
				
			default:
				System.out.println("onCompletion  NOt--READY");
				mCurrentPlayer = mNextPlayer;
				mCurrentPlayer.resetAndPrepare(mCurPlayingInfo);
				
				mNextPlayer = tempPlayer;
				mNextPlayer.updateState(STATE_IDLE);
				mNextPlayer.stop();
				break;
			}
		}else if(mp == mNextPlayer){
			System.out.println("onCompletion  -- B : "+mNextPlayer.isPlaying());
		}
	}
	
	public VideoInfo getNextInfo(){
		int index = mInfos.indexOf(mCurPlayingInfo);
		if(index < mInfos.size() -1){
			return mInfos.get(1+index);
		}
		return null;
	}
	
	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		if(mp == mCurrentPlayer){
			mCurrentPlayer.updateState(STATE_ERROR);
			System.out.println("onError  -- A");
		}else {
			System.out.println("onError  -- B");
		}
		mp.pause();
		showProgressDialog();
		return true;
	}
	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		if(mp == mCurrentPlayer){
			System.out.println("onInfo  -- A : "+ getInfoWhat(what));
		}else {
			System.out.println("onInfo  -- B : "+ getInfoWhat(what));
		}
		return true;
	}
	public String getInfoWhat(int what){
        switch (what) {
		case 1:
			return "MEDIA_INFO_UNKNOWN";
		case 700:
			return "MEDIA_INFO_VIDEO_TRACK_LAGGING";
		case 701:
			return "MEDIA_INFO_BUFFERING_START";
		case 702:
			return "MEDIA_INFO_BUFFERING_END";
		case 800:
			return "MEDIA_INFO_BAD_INTERLEAVING";
		case 801:
			return "MEDIA_INFO_NOT_SEEKABLE";
		case 802:
			return "MEDIA_INFO_METADATA_UPDATE";
		}
        return "I don't know !";
	}
	@Override
	public void onSeekComplete(MediaPlayer mp) {
//		 TODO Auto-generated method stub
		System.out.println("onSeekComplete");
	}
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub
		if(mp == mCurrentPlayer){
//			System.out.println("onBufferingUpdate  -- A : "+ percent + " 第几个 = "+mCurPlayingInfo.index);
			mSeekBar.setSecondaryProgress(getCommProgressByPercent(mCurPlayingInfo, percent));
			mNextPlayer.updateState(STATE_IDLE);
			if(percent > 90){
				int state = mNextPlayer.getState();
				if(state == STATE_IDLE){
					prepareForNextPlayer();
				}
			}
		}else {
//			System.out.println("onBufferingUpdate  -- BBBBB : " + percent+ " 第几个 = "+mInfos.indexOf(mNextPlayingInfo));
			mSeekBar.setSecondaryProgress(getCommProgressByPercent(mNextPlayingInfo, percent));
		}
	}
	private int getCommProgressByPercent(VideoInfo info, int percent){
		int index = info.index;
		int progress = info.time * percent / 100;
		for(int i = 0; i < index; i++){
			progress += mInfos.get(i).time;
		}
		return progress;
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
//		System.out.println("onProgressChanged : mMediaPlayer.getDuration() "+ mMediaPlayer.getDuration() + "; progress = "+progress);
		mCommonProgress = progress;
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		mDragging = true;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		mDragging = false;
		System.out.println("onStopTrackingTouch mProgress= "+mCommonProgress);
		mTimePassedView.setText(formatTime(mCommonProgress));
		seekTo(mCommonProgress);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		System.out.println("onClick");
		switch (v.getId()) {
		case R.id.controller:
			if(!mBtnCtrlPlaying){
				play(mCurrentPlayer);
				mBtnCtrl.setImageResource(R.drawable.ic_media_pause);
			}else {
				mBtnCtrl.setImageResource(R.drawable.ic_media_play);
				pause(mCurrentPlayer);
			}
			mBtnCtrlPlaying = !mBtnCtrlPlaying;
			break;

		case R.id.root:
			updateControlBarVisibility();
			break;
		}
	}
	
	private void updateControlBarVisibility() {
		// TODO Auto-generated method stub
		if(mIsControlBarShowing){
			mUIHandler.removeMessages(MSG_HIDE_CONTROL_TITLE);
			mUIHandler.sendEmptyMessageDelayed(MSG_HIDE_CONTROL_TITLE, 200);
		}else {
			mUIHandler.removeMessages(MSG_SHOW_CONTROL_TITLE);
			mUIHandler.sendEmptyMessageDelayed(MSG_SHOW_CONTROL_TITLE, 200);
		}
	}
	public String formatTime(int time){
		time = time / 1000;
		int hour = time / (60 * 60);
		int minute = time / 60 % 60;
		int second = time % 60;
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}
	private static final String getLocalURL(String remoteUrl){
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/AA_FlvPlayer/"
				+ FileUtils.getFileName(remoteUrl);
	}
	
	private static final int STATE_IDLE 			= 0x01;
	private static final int STATE_PAUSED			= 0x02;
	private static final int STATE_COMPLETED 		= 0x03;
	private static final int STATE_STOPPED 			= 0x04;
	private static final int STATE_PLAYING 			= 0x05;
	private static final int STATE_PREPARED 			= 0x06;
	private static final int STATE_ERROR 			= 0x07;
	private static final int STATE_PREPARING 		= 0x08;
	
	class ViMediaPlayer extends MediaPlayer {
		VideoInfo info;
		int state;
		public ViMediaPlayer(){
			super();
			state = STATE_IDLE;
		}
		public int getState(){
			return this.state;
		}
		public void updateState(int state){
			this.state = state;
		}
		public void setDisplayNull(){
			setDisplay(null);
		}
		public void setDisplayHere(){
			setDisplay(mHolder);
		}
		@Override
		public void start() throws IllegalStateException {
			// TODO Auto-generated method stub
			super.start();
			state = STATE_PLAYING;
		}
		@Override
		public void pause() throws IllegalStateException {
			// TODO Auto-generated method stub
			super.pause();
			state = STATE_PAUSED;
		}
		@Override
		public void stop() throws IllegalStateException {
			// TODO Auto-generated method stub
			super.stop();
			info = null;
			state = STATE_STOPPED;
		}
		public void initialize(){
			setLooping(false);
			setAudioStreamType(AudioManager.STREAM_MUSIC);
			setOnPreparedListener(PlayerActivity2.this);
			setOnBufferingUpdateListener(PlayerActivity2.this);
			setOnInfoListener(PlayerActivity2.this);
			setOnSeekCompleteListener(PlayerActivity2.this);
			setOnCompletionListener(PlayerActivity2.this);
		}
		public void resetAndPrepare(VideoInfo info){
			if(info == null){
				System.out.println("VideoInfo is NULL");
				return ;
			}
			this.info = info;
			reset();
			try {
				setDataSource(info.url);
				prepareAsync();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			state = STATE_PREPARING;
		}
	}
	public interface onPlayerViewSizeChangedListener{
		void onSizeChanged(int width, int height);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			trackSystemVolume(1);
			System.out.println("onKeyDown UP");
			break;
		

		case KeyEvent.KEYCODE_VOLUME_DOWN:
			trackSystemVolume(-1);
			System.out.println("onKeyDown Down");
			break;
		}
		
		
		
		return super.onKeyDown(keyCode, event);
	}
	
	private void trackSystemVolume(int delta){
		final int maxVol = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); 
		int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + delta; 
		mVolumeSeekBar.setProgressAndThumb((int)(currentVolume * 1.0f / maxVol * 100));
		System.out.println("[vol, max]=[" + currentVolume + ", " + maxVol + "], progress=" + (int)(currentVolume * 1.0f / maxVol * 100));
	}
	private long mLastExitTime = 0L;
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		long now = System.currentTimeMillis();
		if((now - mLastExitTime) < 1500 ){
			super.onBackPressed();
		}else {
			Utils.toastShort(this, "再按一次,退出播放");
			mLastExitTime = now;
		}
	}
}
