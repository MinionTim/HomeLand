/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ville.homeland.ui;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import uk.co.senab.photoview.HackyViewPager;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.example.android.bitmapfun.util.ImageCache;
import com.example.android.bitmapfun.util.ImageFetcher;
import com.ville.homeland.R;
import com.ville.homeland.util.FileUtils;
import com.ville.homeland.view.GifImageDetailFragment;
import com.ville.homeland.view.ImageDetailFragment;

public class ImageDetailActivity extends SherlockFragmentActivity implements OnPageChangeListener {
    private static final String IMAGE_CACHE_DIR = "images";
    public static final String EXTRA_IMAGE_INDEX 	= "extra_image_index";
    public static final String EXTRA_IMAGE_URLS 	= "extra_image_urls";

    private ImagePagerAdapter mAdapter;
    private ImageFetcher mImageFetcher;
    private HackyViewPager mPager;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_image_detail_pager);
        
        // Fetch screen height and width, to use as our max size when loading images as this
        // activity runs full screen
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        // For this sample we'll use half of the longest width to resize our images. As the
        // image scaling ensures the image is larger than this, we should be left with a
        // resolution that is appropriate for both portrait and landscape. For best image quality
        // we shouldn't divide by 2, but this will use more memory and require a larger memory
        // cache.
        final int longest = (height > width ? height : width);

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, longest);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);
        

        // Set up ViewPager and backing adapter
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS));
        mPager = (HackyViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setPageMargin((int) getResources().getDimension(R.dimen.image_detail_pager_margin));
        mPager.setOffscreenPageLimit(2);
        
        mPager.setOnPageChangeListener(this);

        final ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_gray_bg));
        
        // Set the current item based on the extra passed in to this activity
        final int extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        mPager.setCurrentItem(extraCurrentItem);
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// TODO Auto-generated method stub
    	getSupportMenuInflater().inflate(R.menu.main_menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub

        switch (item.getItemId()) {
            case android.R.id.home:
                ImageDetailActivity.this.finish();
                return true;
                
            case R.id.save:
            	doSaveImage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    
    }

    private void doSaveImage() {
		// TODO Auto-generated method stub
    	String url = mAdapter.getImageUrl(mPager.getCurrentItem());
    	//this is not the real name, normal format is like xxxxxx.0
    	String fileName = ImageCache.hashKeyForDisk(url) + ".jpg";
    	System.out.println(fileName);
    	
    	int curr = mPager.getCurrentItem();
    	System.out.println("Page: CurrentItem = " + curr);
//    	ViewGroup childA = (ViewGroup) mPager.getChildAt(curr);
    	ViewGroup childA = (ViewGroup) mPager.findViewWithTag(mAdapter.getImageUrl(curr));
    	View image = null;
    	if(childA == null){
    		System.out.println("ChildA is NULL!!");
    	}else {
    		ViewGroup child = (ViewGroup) childA.getChildAt(0);
    		image = child.findViewById(R.id.imageView);
    	}
    	Bitmap bmp = null;
//    	if(image instanceof GestureImageView){
//    		bmp = ((BitmapDrawable)((GestureImageView) image).getDrawable()).getBitmap();
//    	}else if (image instanceof ImageView) {
//    		bmp = ((BitmapDrawable)((ImageView) image).getDrawable()).getBitmap();
//    	}
    	bmp = ((BitmapDrawable)((ImageView) image).getDrawable()).getBitmap();
    	boolean status = false;
    	if(bmp == null){
    		Toast.makeText(this, "图片保存失败！！", Toast.LENGTH_SHORT).show();
    	} else {
    		String dirName = "HomeLand";
    		if(!FileUtils.checkFileExists(dirName) || !FileUtils.checkIsDirectory(dirName)){
//    			System.out.println("BBBBB Creaate");
    			FileUtils.createDirectory(dirName);
    		}
    		String filePath = Environment.getExternalStorageDirectory()
					+ File.separator + dirName + File.separator + fileName;
    		File outFile = new File(filePath);
    		if (!outFile.exists()){
    			try {
    				FileOutputStream os = new FileOutputStream(outFile);
					bmp.compress(CompressFormat.JPEG, 100, os);
					os.close();
					status = true;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					status = false;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					status = false;
				}
    		}else {
    			Toast.makeText(this, "图片已保存！！", Toast.LENGTH_SHORT).show();
    		}
    		if (status){
    			Toast.makeText(this, "保存成功：" + filePath, Toast.LENGTH_LONG).show();
    		}
    	}
	}

	/**
     * Called by the ViewPager child fragments to load images via the one ImageFetcher
     */
    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

    /**
     * The main adapter that backs the ViewPager. A subclass of FragmentStatePagerAdapter as there
     * could be a large number of items in the ViewPager and we don't want to retain them all in
     * memory at once but create/destroy them on the fly.
     */
    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
//        private final int mSize;
        
        private List<String> mUrls;
 
        public ImagePagerAdapter(FragmentManager fm, List<String> list) {
            super(fm);
            if(list == null){
            	System.out.println("ImagePagerAdapter list is null");
            	list = new LinkedList<String>();
            }
            mUrls = list;
        }

        @Override
        public int getCount() {
            return mUrls.size();
        }
        
        public String getImageUrl(int position){
        	return mUrls.get(position);
        }

        @Override
        public Fragment getItem(int position) {
        	String url = mUrls.get(position);
        	if(url.toLowerCase().endsWith(".gif")){
        		return GifImageDetailFragment.newInstance(url);
        	}
            return ImageDetailFragment.newInstance(url);
        }
    }

	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		String title = (mPager.getCurrentItem() + 1) + "/" + mAdapter.getCount();
		System.out.println("Page: Selected=" + title);
		getSupportActionBar().setTitle(title);
	}
}
