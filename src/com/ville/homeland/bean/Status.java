package com.ville.homeland.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Status {
	private Date created_at;
    private long id;
    private String text;
    private String source;
    private boolean favorited;
    private User user;
    private Status retweeted_status;
//    private String in_reply_to_status_id;
//    private String in_reply_to_user_id;
//    private String in_reply_to_screen_name;
    
    private String thumbnail_pic;
    private String bmiddle_pic;
    private String original_pic;
    
    private int reposts_count;
    private int comments_count;
    private int attitudes_count;
    private boolean mIsRepost = false;
    
    public Status(String jsonStr) {
		// TODO Auto-generated constructor stub
		try {
			JSONObject obj = new JSONObject(jsonStr);
			init(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    public Status(JSONObject obj){
    	init(obj);
    }
    private void init(JSONObject obj){
    	try {
    		JSONObject userObj = obj.optJSONObject("user");
    		if(userObj != null){
    			user = new User(userObj);
    		}
			JSONObject statusObj = obj.optJSONObject("retweeted_status");
			if(statusObj != null){
				retweeted_status = new Status(statusObj);
				mIsRepost = true;
			}
			created_at = new Date(obj.optString("created_at"));
			id = obj.getLong("id");
			text = obj.optString("text");
			source = obj.optString("source");
			favorited = obj.getBoolean("favorited");
//			in_reply_to_status_id = obj.optString("in_reply_to_status_id");
//			in_reply_to_user_id = obj.optString("in_reply_to_user_id");
//			in_reply_to_screen_name = obj.optString("in_reply_to_screen_name");
			thumbnail_pic = obj.optString("thumbnail_pic");
			bmiddle_pic = obj.optString("bmiddle_pic");
			original_pic = obj.optString("original_pic");
			
			reposts_count = obj.getInt("reposts_count");
			comments_count = obj.getInt("comments_count");
			attitudes_count = obj.getInt("attitudes_count");
			
    	} catch (JSONException e) {
    		// TODO Auto-generated catch block
    		System.out.println("ERROR : "/*+ user.getScreen_name()*/);
    		e.printStackTrace();
    	}
    }
    
	public static List<Status> constructStatusList(String responce) {
		try {
			JSONObject mainObj = new JSONObject(responce);
			JSONArray array = mainObj.getJSONArray("statuses");
			int count = array.length();
			List<Status> list = new ArrayList<Status>(count);
			for (int i = 0; i < count; i++) {
				JSONObject obj = array.getJSONObject(i);
				list.add(new Status(obj));
			}
//			System.out.println(list);
			return list;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	public Date getCreated_at() {
		return created_at;
	}
	public long getId() {
		return id;
	}
	public String getText() {
		return text;
	}
	public String getSource() {
		return source;
	}
	public boolean isFavorited() {
		return favorited;
	}
	public String getThumbnail_pic() {
		return thumbnail_pic;
	}
	public String getBmiddle_pic() {
		return bmiddle_pic;
	}
	public String getOriginal_pic() {
		return original_pic;
	}
	public String getHD_pic(){
		if(original_pic != null && !"".equals(original_pic)){
			System.out.println("HD_PIC: " + original_pic);
			return original_pic;
		}
		if(bmiddle_pic != null && !"".equals(bmiddle_pic)){
			System.out.println("HD_PIC: " + bmiddle_pic);
			return bmiddle_pic;
		}
		if(thumbnail_pic != null && !"".equals(thumbnail_pic)){
			System.out.println("HD_PIC: " + thumbnail_pic);
			return thumbnail_pic;
		}
		System.out.println("HD_PIC: NULL");
		return null;
	}
	public int getReposts_count() {
		return reposts_count;
	}
	public int getComments_count() {
		return comments_count;
	}
	public int getAttitudes_count() {
		return attitudes_count;
	}
	
	public Status getRetweeted_status() {
		return retweeted_status;
	}
	public User getUser() {
		return user;
	}
	public boolean isRepost(){
		return mIsRepost;
	}
	public boolean hasPictrues(){
		return (thumbnail_pic != null) && !"".equals(thumbnail_pic)
				&& (bmiddle_pic != null) && !"".equals(bmiddle_pic)
				&& (original_pic != null) && !"".equals(original_pic);
	}
	@Override
	public String toString() {
		return "Status [created_at=" + created_at + ", id=" + id + ", text="
				+ text + ", source=" + source + ", favorited=" + favorited
				+ ", thumbnail_pic=" + thumbnail_pic + ", bmiddle_pic="
				+ bmiddle_pic + ", original_pic=" + original_pic
				+ ", reposts_count=" + reposts_count + ", comments_count="
				+ comments_count + ", attitudes_count=" + attitudes_count + "]";
	}
	
}
