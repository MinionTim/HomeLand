package com.ville.homeland.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.android.bitmapfun.util.ImageCache.ImageCacheParams;
import com.example.android.bitmapfun.util.ImageFetcher;
import com.huewu.pla.lib.internal.PLA_AdapterView;
import com.huewu.pla.lib.internal.PLA_AdapterView.OnItemClickListener;
import com.ville.homeland.HomeLandApplication;
import com.ville.homeland.R;
import com.ville.homeland.bean.ImageInfo;
import com.ville.homeland.util.FileUtils;
import com.ville.homeland.util.NetUtils;
import com.ville.homeland.widget.ScaleImageView;
/**
 * 单个主持人图片汇总页面
 */
public class ComperesItemActivity extends SherlockFragmentActivity implements IXListViewListener, OnItemClickListener {
	
	private static final int TYPE_LOAD_MORE 	= 1;
	private static final int TYPE_REFRESH	 	= 2;
	private static final int TYPE_LOAD_MORE_BACKGROUND	 	= 3;
	

	private String mCompId;
	private String mCompName;
	private ImageFetcher mImageFetcher;
    private XListView mXListView = null;
    private StaggeredAdapter mAdapter = null;
    private ProgressBar mProgress;
	private ConnTask mCurrTask;
	
	private int mCurrPage;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_pull_to_refresh_sample);
		mCompId = getIntent().getStringExtra("comp_id");
		mCompName = getIntent().getStringExtra("comp_name");
		
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg));
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(mCompName);
		
        mXListView = (XListView) findViewById(R.id.list);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        mProgress.setVisibility(View.VISIBLE);
        mXListView.setPullLoadEnable(true);
        mXListView.setXListViewListener(this);
        mXListView.setOnItemClickListener(this);

        mAdapter = new StaggeredAdapter(this, mXListView);
        mXListView.setAdapter(mAdapter);
        addItemToContainer(TYPE_LOAD_MORE, 0);
        
        ImageCacheParams cacheParams = new ImageCacheParams(this, "com");

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory
	    mImageFetcher = new ImageFetcher(this, 240);
	    mImageFetcher.setLoadingImage(R.drawable.empty_photo);
	    mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
	    
	    ((HomeLandApplication) getApplication()).setComperesItemActivity(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		 switch (item.getItemId()) {
         case android.R.id.home:
             finish();
             return true;
             
         case R.id.save:
             return true;
     }
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }
	
	private String getContentOfRawFile(int rawResId) {
		// TODO Auto-generated method stub
		InputStream is = this.getResources().openRawResource(rawResId);
		return FileUtils.readInStream(is);
	}
	
	public int getDataSize(){
		if(mAdapter != null){
			return mAdapter.getCount();
		}
		return 0;
	}
	
	public List<ImageInfo> getDataList(){
		if(mAdapter != null){
			return mAdapter.getDataItems();
		}
		return null;
	}
	
	class ConnTask extends AsyncTask<String, Void, List<ImageInfo>>{
		private int type;
		
		ConnTask(int type){
			this.type = type;
		}

		@Override
		protected List<ImageInfo> doInBackground(String... urls) {
			// TODO Auto-generated method stub
			String content = NetUtils.getContentByLink(urls[0]);
			List<ImageInfo> tem = ImageInfo.constructComInfoList(content);
			return tem;
		}
		@Override
		protected void onPostExecute(List<ImageInfo> dataList) {
			if(type == TYPE_LOAD_MORE){
				mXListView.stopLoadMore();
				mAdapter.addItemLast(dataList);
				mAdapter.notifyDataSetChanged();
				mProgress.setVisibility(View.INVISIBLE);
				
			}else if(type == TYPE_REFRESH){
				mXListView.stopRefresh();
				mAdapter.notifyDataSetChanged();
				mProgress.setVisibility(View.INVISIBLE);
				
			}else if(type == TYPE_LOAD_MORE_BACKGROUND){
				mAdapter.addItemLast(dataList);
				mAdapter.notifyDataSetChanged();
				
			}
		}
		
	}
	/**
     * 添加内容
     * 
     * @param pageindex
     * @param type
     *            1为下拉刷新 2为加载更多
     */
	private void addItemToContainer(int type, int pageIndex) {
		if(mCurrTask != null && mCurrTask.getStatus() == AsyncTask.Status.RUNNING)
			return;
		String url = NetUtils.getCompImagesUrl(mCompId, pageIndex);
		mCurrTask = new ConnTask(type);
		mCurrTask.execute(url);
	}
	
	public class StaggeredAdapter extends BaseAdapter {
        private Context mContext;
        private LinkedList<ImageInfo> mInfos;
        private XListView mListView;

        public StaggeredAdapter(Context context, XListView xListView) {
            mContext = context;
            mInfos = new LinkedList<ImageInfo>();
            mListView = xListView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            ImageInfo info = mInfos.get(position);

            if (convertView == null) {
                LayoutInflater layoutInflator = LayoutInflater.from(parent.getContext());
                convertView = layoutInflator.inflate(R.layout.compere_xlist_itemview, null);
                holder = new ViewHolder();
                holder.imageView = (ScaleImageView) convertView.findViewById(R.id.news_pic);
                holder.contentView = (TextView) convertView.findViewById(R.id.news_title);
                convertView.setTag(holder);
            }

            holder = (ViewHolder) convertView.getTag();
            holder.imageView.setImageWidth(info.getWidth());
            holder.imageView.setImageHeight(info.getHeight());
            holder.contentView.setText(info.getTitle());
            mImageFetcher.loadImage(info.getImageUrl(), holder.imageView);
            return convertView;
        }

        class ViewHolder {
            ScaleImageView imageView;
            TextView contentView;
        }

        @Override
        public int getCount() {
            return mInfos.size();
        }

        @Override
        public ImageInfo getItem(int arg0) {
            return mInfos.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }
        
        public void setDataItems(List<ImageInfo> datas){
        	mInfos.clear();
        	addItemLast(datas);
        }
        public List<ImageInfo> getDataItems(){
        	return mInfos;
        }

        public void addItemLast(List<ImageInfo> datas) {
            mInfos.addAll(datas);
        }

        public void addItemTop(List<ImageInfo> datas) {
            for (ImageInfo info : datas) {
                mInfos.addFirst(info);
            }
        }
    }

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		mXListView.stopRefresh();
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mCurrPage ++;
		addItemToContainer(TYPE_LOAD_MORE, mCurrPage);
	}

	@Override
	public void onItemClick(PLA_AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(ComperesItemActivity.this, ImageDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(ImageDetailActivity.EXTRA_IMAGE_URLS, getUrls());
		bundle.putInt(ImageDetailActivity.EXTRA_IMAGE_INDEX, position-1);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	public ArrayList<String> getUrls(){
		ArrayList<String> urls = new ArrayList<String>();
		if(mAdapter != null){
			List<ImageInfo> list= mAdapter.getDataItems();
			for(ImageInfo info : list){
				urls.add(info.getImageUrl());
			}
		}
		return urls;
	}
}
