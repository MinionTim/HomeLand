package com.ville.homeland.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.android.bitmapfun.util.ImageCache;
import com.ville.homeland.widget.GifMovieView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
/**
 * 异步线程加载图片工具类
 * 使用说明：
 * BitmapManager bmpManager;
 * bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.loading));
 * bmpManager.loadBitmap(imageURL, imageView);
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-6-25
 */
public class GifLoader {  
	  
    private FileCache mFileCache;
    private MemoryCache mMemoryCache;
    private static ExecutorService pool;  
    private static Map<GifMovieView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<GifMovieView, String>());  
    
    public GifLoader(Context context){
    	pool = Executors.newFixedThreadPool(5);  //固定线程池
    	mFileCache = new FileCache(context);
    	mMemoryCache = new MemoryCache();
    }
    
    /**
     * 加载图片-可指定显示图片的高宽
     * @param url
     * @param imageView
     * @param width
     * @param height
     */
    public void loadGifView(String url, GifMovieView imageView) {  
        imageViews.put(imageView, url);  
        String cachedPath = mMemoryCache.get(url);  
   
        if (cachedPath != null) {
			//显示缓存图片
            imageView.setMovieResource(cachedPath);
        } else {  
        	//加载SD卡中的图片缓存
        	File file = mFileCache.getFile(url);
    		if(file.exists()){
				//显示SD卡中的图片缓存
    			imageView.setMovieResource(file.getAbsolutePath());
        	}else{
				//线程加载网络图片
//        		imageView.setImageBitmap(defaultBmp);
        		queueJob(url, imageView);
        	}
        }  
    }  
  
    /**
     * 从网络中加载图片
     * @param url
     * @param imageView
     * @param width
     * @param height
     */
    public void queueJob(final String url, final GifMovieView imageView) {  
        /* Create handler in UI thread. */  
        final Handler handler = new Handler() {  
            public void handleMessage(Message msg) {  
                String tag = imageViews.get(imageView);  
                if (tag != null && tag.equals(url)) {  
                    if (msg.obj != null) {  
                    	System.out.println("DownLoad Complete: " + (String) msg.obj);
                        imageView.setMovieResource((String) msg.obj);  
                    } 
                }  
            }  
        };  
  
        //将新的任务线程放入线程池
        pool.execute(new Runnable() {   
            public void run() {  
                Message message = Message.obtain();  
                message.obj = downloadGif(url);  
                handler.sendMessage(message);  
            }  
        });  
    } 
	
	private void copyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1) {
					break;
				}
				os.write(bytes, 0, count);
			}
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}
  
    /**
     * 下载图片-可指定显示图片的高宽
     * @param url
     * @param width
     * @param height
     */
	private String downloadGif(String urlStr) {
		String localPath = null;
		try {
			File downloadFile = mFileCache.getFile(urlStr);
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(downloadFile);
			copyStream(is, os);
			os.close();
			is.close();
			localPath = downloadFile.getAbsolutePath();
			mMemoryCache.put(urlStr, localPath);
			
		} catch (FileNotFoundException e) {
			// Storage state may changed, so call FileCache.init() again.
		} catch (IOException e) {
			// e.printStackTrace();
		}
		
		return localPath;
	}  
	
	public class MemoryCache {

		private Map<Object, SoftReference<String>> mCache = Collections
				.synchronizedMap(new HashMap<Object, SoftReference<String>>());

		public void clear() {
			mCache.clear();
		}

		public String get(Object tag) {
			if (!mCache.containsKey(tag)) return null;
			SoftReference<String> ref = mCache.get(tag);
			return ref.get();
		}

		public void put(Object id, String bitmap) {
			mCache.put(id, new SoftReference<String>(bitmap));
		}
	}

	private class FileCache {

		private static final String CACHE_DIR_NAME = "thumbnails";

		private File mCacheDir;
		private Context mContext;

		public FileCache(Context context) {
			mContext = context;
			init();
		}

		public void clear() {
			if (mCacheDir == null) return;
			File[] files = mCacheDir.listFiles();
			if (files == null) return;
			for (File f : files) {
				f.delete();
			}
		}

		/**
		 * I identify images by hashcode. Not a perfect solution, good for the
		 * demo.
		 */
		public File getFile(Object tag) {
			if (mCacheDir == null) return null;
			if (tag instanceof File) {
				if (mCacheDir.equals(((File) tag).getParentFile())) return (File) tag;
			}
			String filename = Integer.toHexString(tag.hashCode());
			File f = new File(mCacheDir, filename);
			return f;
		}

		public void init() {
			/* Find the dir to save cached images. */
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				mCacheDir = new File(mContext.getExternalCacheDir(), CACHE_DIR_NAME);
			} else {
				mCacheDir = new File(mContext.getCacheDir(), CACHE_DIR_NAME);
			}
			if (mCacheDir != null && !mCacheDir.exists()) {
				mCacheDir.mkdirs();
			}
		}

	}
}