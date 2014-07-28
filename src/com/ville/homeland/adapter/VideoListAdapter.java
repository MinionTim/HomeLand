package com.ville.homeland.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ville.homeland.R;
import com.ville.homeland.bean.VideoEntry;
import com.ville.homeland.util.BitmapManager;
import com.ville.homeland.util.StringUtils;

public class VideoListAdapter extends BaseAdapter {
	
	private Context mContext;
//	private int mTypeId;
	private final LayoutInflater mInflater;
	private List<VideoEntry.Data> mList = new ArrayList<VideoEntry.Data>();
	private BitmapManager mBmpManager;
	
	private View.OnClickListener mStartPlayListener;
	
	public VideoListAdapter(Context context){
		mContext = context;
//		mTypeId = typeId;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mBmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.portrait));
	}
	public void setData(List<VideoEntry.Data> data){
		if(mList != null && data != null){
			mList.clear();
			mList.addAll(data);
		}
//		if(mList != null && data == null){
//			mList.clear();
//		}
	}
	public void removeAllData(){
		mList.clear();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}
	@Override
	public VideoEntry.Data getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder holder = null;
		VideoEntry.Data entry = mList.get(position);
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.video_list_itemview, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.image);
			holder.info = (TextView) convertView.findViewById(R.id.info);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.brief = (TextView) convertView.findViewById(R.id.brief);
			holder.compere = (TextView) convertView.findViewById(R.id.compere);
			holder.btnPlay = (TextView) convertView.findViewById(R.id.start);
			holder.details = (RelativeLayout) convertView.findViewById(R.id.details);
			
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.info.setText(entry.title);
		holder.compere.setText("".equals(entry.compere) ? "" : ("记者："+ entry.compere));
		holder.time.setText(entry.timestamp.split(" ")[0]);
		mBmpManager.loadBitmap(entry.imageUrl, holder.image);
		holder.brief.setText(StringUtils.isEmpty(entry.brief) ? "暂无" : entry.brief);
		holder.details.setVisibility(View.GONE);
		holder.btnPlay.setTag(position);
		holder.btnPlay.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mStartPlayListener != null){
					mStartPlayListener.onClick(v);
				}
			}
		});
		return convertView;
	}
	public class ViewHolder{
		ImageView image;
		TextView info;
		TextView time;
		TextView btnPlay;
		TextView brief;
		TextView compere;
		public RelativeLayout details;
	}
	
	public VideoEntry.Data getVideoEntryAt(int pos){
		return mList.get(pos);
	}

	public void setOnStartPlayListener(View.OnClickListener listener){
		mStartPlayListener = listener;
	}
}
