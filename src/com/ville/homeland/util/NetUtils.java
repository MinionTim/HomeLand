package com.ville.homeland.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class NetUtils {
	public static final String UTF_8 = "UTF-8";
	public static final String GBK = "gbk";
	public static final String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET4.0C; .NET4.0E)";
	public static final String HOST = "http://cctv.cntv.cn/";
	
	public static final String URL_PAST = "http://cctv.cntv.cn/lm/yuanfangdejia/vedio/index.shtml";
//	public static final String URL_PAST = "http://cctv.cntv.cn/lm/yuanfangdejia/bjx/wq/index.shtml";
	
	// formaters
	private static final String URL_VIDEO_LIST_FMT = "http://hot.app.cntv.cn/iphoneInterface/cntv/getVideoListBySort.json?page=%d&pageSize=%d&video_primary_page_id=PAGE1356594759188210";
	private static final String URL_VIDEO_LIST_TYPE_FMT = "http://yuanfangdejia2.duapp.com/program?type=%d&size=%d&pn=%d";
	private static final String URL_VIDEO_LIST_COMP_FMT = "http://yuanfangdejia2.duapp.com/program?comp=%s&size=%d&pn=%d";
	private static final String URL_VIDEO_LIST_QUERY_FMT= "http://yuanfangdejia2.duapp.com/program?query=%s&size=%d&pn=%d";
	private static final String URL_VIDEO_PLAY_INFO_FMT = "http://vdn.apps.cntv.cn/api/getHttpVideoInfo.do?pid=%s";
	
	//http://1.yuanfangdejia.duapp.com/?comp&id=wangyaojie&pn=100
	
	private final static int TIMEOUT_CONNECTION = 20000;
	private final static int TIMEOUT_SOCKET = 20000;
	private final static int RETRY_TIME = 3;
	
	
	private final static int COMPERE_PAGE_SIZE = 30;
	/** 
     *   1. 使用 HttpURLConnection 实现 
     * */  
    public static Bitmap getBitmapByUrl(String link) {
        // TODO Auto-generated method stub
    	Bitmap bitmap = null;
        try {
            URL url = new URL(link);  
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
                  
            if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){  
                InputStream in = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
                in.close();
            }else{  
                System.out.println("Connection Failed !");
            }  
            conn.disconnect();  
        } catch (Exception e) {  
            System.out.println("getBitmapByUrl Exception:"+e.getMessage());  
        }
        return bitmap;
    }  
    
    /**
     * 
     * @param pageNum  range from 1
     * @param pageSize 500 for default
     * @return
     */
    public static String getVideoListURL(int pageNum, int pageSize){
    	return String.format(Locale.US, URL_VIDEO_LIST_FMT, pageNum, pageSize);
    }
    public static String getVideoListURLByType(int typeId, int page){
    	return String.format(Locale.US, URL_VIDEO_LIST_TYPE_FMT, typeId, COMPERE_PAGE_SIZE, page);
    }
    public static String getVideoListURLByComp(String comp, int page){
    	return String.format(Locale.US, URL_VIDEO_LIST_COMP_FMT, comp, COMPERE_PAGE_SIZE, page);
    }
    public static String getVideoListURLByQuery(String keyword, int page){
    	return String.format(Locale.US, URL_VIDEO_LIST_QUERY_FMT, keyword, COMPERE_PAGE_SIZE, page);
    }
    public static String getVideoPlayInfoUrl(String pid){
    	return String.format(Locale.US, URL_VIDEO_PLAY_INFO_FMT, pid );
    }
    /**
     * 
     * @param name like, "wangyaojie"
     * @param pn page count, range from 0
     * @return
     */
    public static String getCompImagesUrl(String name, int pn){
    	return String.format(Locale.US, Constants.URL_HOMELAND_COMP_IMGS_FORMAT, name, pn);
    }
    public static String getContentByLink(String url)  {
		HttpClient httpClient = null;
		GetMethod httpGet = null;
		String content = null;
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, null, null);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					System.out.println("ERROR");
				}
				content = httpGet.getResponseBodyAsString();
		        break;
			} catch (HttpException e) {
				System.out.println("HttpException " + e.getMessage());
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
			} catch (IOException e) {
				time++;
				System.out.println("IOException " + e.getMessage());
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
			} finally {
				// 释放连接
				if(httpGet != null){
					httpGet.releaseConnection();
				}
				httpClient = null;
			}
		}while(time < RETRY_TIME);
		
		return content;
	}
	private static HttpClient getHttpClient() {        
        HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		// 设置 读数据超时时间 
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset(GBK);
		return httpClient;
	}
	private static GetMethod getHttpGet(String url, String cookie, String userAgent) {
		GetMethod httpGet = new GetMethod(url);
		// 设置 请求超时时间
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Host", "www.baidu.com");
		httpGet.setRequestHeader("Connection","Keep-Alive");
		httpGet.setRequestHeader("Cookie", cookie);
		httpGet.setRequestHeader("User-Agent", userAgent);
		return httpGet;
	}
}
