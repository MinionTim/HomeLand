package com.ville.homeland.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.example.android.bitmapfun.util.ImageCache.ImageCacheParams;
import com.example.android.bitmapfun.util.ImageFetcher;
import com.example.android.bitmapfun.util.Utils;
import com.ville.homeland.R;
import com.ville.homeland.bean.CompereInfo;
import com.ville.homeland.ui.CompereAllImagesActivity;
import com.ville.homeland.util.Constants;
import com.ville.homeland.util.FileUtils;
import com.ville.homeland.util.NetUtils;

public class MainComperesFragment extends SherlockFragment implements OnItemClickListener {

	public static final String TAG_FRAGMENT = "tag:MainComperesFragment";
	private static final String IMAGE_CACHE_DIR = "comperes";
	public static MainComperesFragment newInstance(){
		return new MainComperesFragment();
	}
	private ImageFetcher mImageFetcher;
	private List<CompereInfo> mDataList = new ArrayList<CompereInfo>();
	private ImageAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		System.out.println("Comperes onCreate");
		mAdapter = new ImageAdapter(getActivity());
    	mLoadTask.execute();
		
		ImageCacheParams cacheParams = new ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), 320);
//        mImageFetcher.setImageSize(width, height);
        mImageFetcher.setImageFadeIn(false);
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getSherlockActivity().getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getSherlockActivity().getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSherlockActivity().getSupportActionBar().setTitle("风采");
		setHasOptionsMenu(true);
		
		final View v = inflater.inflate(R.layout.fragment_main_compere, container, false);
        final GridView mGridView = (GridView) v.findViewById(R.id.gridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    mImageFetcher.setPauseWork(true);
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
            }
        });
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
                final int columnWidth = mGridView.getWidth() / 3;
                mAdapter.setItemHeight(columnWidth *5 /3);
                mGridView.getViewTreeObserver()
                        .removeGlobalOnLayoutListener(this);
			}
		});
        
		return v;
	}
	private String getContentOfRawFile(int rawResId) {
		// TODO Auto-generated method stub
		InputStream is = getActivity().getResources().openRawResource(rawResId);
		return FileUtils.readInStream(is);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(getActivity(), CompereAllImagesActivity.class);
		intent.putExtra("comp_id", mDataList.get(position).getId());
		intent.putExtra("comp_name", mDataList.get(position).getName());
		startActivity(intent);
		mAdapter.notifyDataSetChanged();
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
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }
	private AsyncTask<Void, Void, Boolean> mLoadTask = new AsyncTask<Void, Void, Boolean>() {
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
//			String content = getContentOfRawFile(R.raw.comperes);
			String content = NetUtils.getContentByLink(Constants.URL_HOMELAND_ALLCOMPERES);
			List<CompereInfo> tem = CompereInfo.constructComInfoList(content);
			mDataList.addAll(tem);
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			mAdapter.notifyDataSetChanged();
		}
	};
	class ImageAdapter extends BaseAdapter{

		private LayoutInflater mInflater;
		private int mImageHeight;
		private LayoutParams mParams;
		public ImageAdapter(Context context){
			mInflater = LayoutInflater.from(context);
		}
		
		public void setItemHeight(int i) {
			// TODO Auto-generated method stub
			mImageHeight = i;
			mParams = new LayoutParams(LayoutParams.MATCH_PARENT, i);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDataList.size();
		}

		@Override
		public CompereInfo getItem(int position) {
			// TODO Auto-generated method stub
			return mDataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.compere_grid_itemview, null);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView.findViewById(R.id.image);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			}
			holder = (ViewHolder) convertView.getTag();
			CompereInfo info = getItem(position);
			holder.name.setText(info.getName());
			holder.image.setLayoutParams(mParams);
			mImageFetcher.loadImage(info.getImageUrl(), holder.image);
			return convertView;
		}
		
	}
	static class ViewHolder{
		public TextView name;
		ImageView image;
	}
	
}
