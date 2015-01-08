package com.ville.homeland.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageInfo {
	private String title;
	private String imageUrl;
	private int width;
	private int height;
	private String timeStr;
	
	public ImageInfo(String jsonStr){
		JSONObject jObj;
		try {
			jObj = new JSONObject(jsonStr);
			this.title = jObj.optString("title");
			this.imageUrl = jObj.optString("img");
			this.width = jObj.optInt("width");
			this.height = jObj.optInt("height");
			this.timeStr = formateTimeString(jObj.optString("date"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ImageInfo(JSONObject jObj){
		this.title = jObj.optString("title");
		this.imageUrl = jObj.optString("img");
		this.width = jObj.optInt("width");
		this.height = jObj.optInt("height");
		this.timeStr = formateTimeString(jObj.optString("date"));
	}
	private String formateTimeString(String timeStr) {
		// TODO Auto-generated method stub
		//2011-10-23 21:35:00
		String newStr = null;
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		Date now = new Date();
		try {
			Date date = sdf.parse(timeStr);
			if(now.getYear() == date.getYear()){
				newStr = new SimpleDateFormat("MM-dd HH:mm", Locale.US).format(date);
			}else {
				newStr = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			newStr = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.US).format(now);
		}
		return newStr;
	}

	public static List<ImageInfo> constructComInfoList(String json){
		try {
			JSONObject temObj = new JSONObject(json);
			JSONArray array = temObj.optJSONArray("list");
			List<ImageInfo> list = new ArrayList<ImageInfo>();
			if(array != null){
				int count = array.length();
				for (int i = 0; i < count; i++) {
					JSONObject obj = array.getJSONObject(i);
					ImageInfo info = new ImageInfo(obj);
					list.add(info);
				}
			}
			return list;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	
	public String getTimeStr() {
		return timeStr;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}

	@Override
	public String toString() {
		return "ImageInfo [title=" + title + ", imageUrl=" + imageUrl
				+ ", width=" + width + ", height=" + height + "]";
	}
	
}
