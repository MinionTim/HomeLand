package com.ville.homeland.view;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class MyTestFragment extends SherlockFragment {

	public static final String TAG_FRAGMENT = "tag:MyTestFragment";
	private String mContent = "";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		TextView view = new TextView(getActivity());
		view.setText(mContent);
		view.setTextSize(50);
		view.setGravity(Gravity.CENTER);
		return view;
	}
	public static MyTestFragment newInstance(String content){
		MyTestFragment fragment = new MyTestFragment();
		fragment.mContent = content;
		return fragment;
	}
	public static MyTestFragment newInstance(int content){
		MyTestFragment fragment = new MyTestFragment();
		fragment.mContent = String.valueOf(content);
		return fragment;
	}
	
}
