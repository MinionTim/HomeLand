package com.ville.homeland.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;

public class WeiboUtils {
	
	private static final String URL_WEIBO_SERVER = "http://yuanfangdejia2.duapp.com"; 
	private static final String URL_WEIBO_STATUSES_PRIX = URL_WEIBO_SERVER + "/weibo_api/statuses"; 
	
	private static WeiboUtils mInstance = null;
	private WeiboUtils(){}
	public static WeiboUtils getInstance(){
		if(mInstance == null){
			mInstance = new WeiboUtils();
		}
		return mInstance;
	}
	/**
     * 获取当前登录用户及其所关注用户的最新微博。
     * 
     * @param since_id      若指定此参数，则返回ID比since_id大的微博（即比since_id时间晚的微博），默认为0
     * @param max_id        若指定此参数，则返回ID小于或等于max_id的微博，默认为0
     * @param count         单页返回的记录条数，默认为50
     * @param page          返回结果的页码，默认为1
     * @param base_app      是否只获取当前应用的数据。false为否（所有数据），true为是（仅当前应用），默认为false
     * @param featureType   过滤类型ID，0：全部、1：原创、2：图片、3：视频、4：音乐，默认为0。 
     *                      <li> {@link #FEATURE_ALL}
     *                      <li> {@link #FEATURE_ORIGINAL}
     *                      <li> {@link #FEATURE_PICTURE}
     *                      <li> {@link #FEATURE_VIDEO}
     *                      <li> {@link #FEATURE_MUSICE}
     * @param trim_user     返回值中user字段开关，false：返回完整user字段、true：user字段仅返回user_id，默认为false
     */
    private StatusList friendsTimeline(long since_id, long max_id, int count, int page, boolean base_app, int featureType,
            boolean trim_user) {
    	WeiboParam params = buildTimeLineWithAppTrim(since_id, max_id, count, page, base_app, trim_user, featureType);
    	String timelines = requestGet(URL_WEIBO_STATUSES_PRIX + "/home_timeline.json", params);
    	return StatusList.parse(timelines);
    }
    
    public StatusList friendsTimeline(long maxId, int page) {
    	return friendsTimeline(0, maxId, 10, page, false, StatusesAPI.FEATURE_ALL, false);
    }
    
    private String requestGet(String url, WeiboParam params){
    	return NetUtils.getContentByLink(url + "?" + params.toQueryString());
    }
    
	private WeiboParam buildTimeLineWithAppTrim(long since_id, long max_id, int count, int page, boolean base_app,
			boolean trim_user, int featureType) {
		// TODO Auto-generated method stub
		WeiboParam params = new WeiboParam();
		params.put("since_id", since_id);
        params.put("max_id", max_id);
        params.put("count", count);
        params.put("page", page);
        params.put("base_app", base_app ? 1 : 0);
        params.put("trim_user", trim_user ? 1 : 0);
        params.put("feature", featureType);
		return params;
	}
    
    
}
class WeiboParam {
	private HashMap<String, String> mParamMap = new HashMap<String, String>(15);
	public void put(String key, int value){
		mParamMap.put(key, String.valueOf(value));
	}
	public void put(String key, long value){
		mParamMap.put(key, String.valueOf(value));
	}
	public void put(String key, String value){
		mParamMap.put(key, value);
	}
	public String toQueryString(){
		if(mParamMap.size() == 0){
			return "";
		}
		Iterator<String> keyIter = mParamMap.keySet().iterator();
		StringBuilder sb = new StringBuilder();
		while(keyIter.hasNext()){
			String key = keyIter.next();
			String value = String.valueOf(mParamMap.get(key));
			sb.append(key).append("=").append(value);
			if(keyIter.hasNext()){
				sb.append("&");
			}
		}
		return sb.toString();
	}
}
