package com.ville.homeland.ui;

import java.util.List;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ville.homeland.R;
import com.ville.homeland.bean.VideoEntry;
import com.ville.homeland.util.NetUtils;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

public class MessagesActivity extends SherlockFragmentActivity {
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				String url = NetUtils.getContentByLink(NetUtils.getVideoListURL(1, 100));
//				List<VideoEntry> list = VideoEntry.constructVideoEntryList(url);
//				for(VideoEntry entry : list){
//					System.out.println(entry);
//				}
//			}
//		}).start();
	}

}
