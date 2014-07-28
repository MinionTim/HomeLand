package com.ville.homeland.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VideoEntry implements Serializable{
	private static final long serialVersionUID = 6700613404399214409L;
	
	private static final int TYPE_ERROR = -999;
	public boolean hasNext;
	public List<Data> dataList;
	
	public static class Data{
		public String pid;
		public String title;
		public String brief;
		public String imageUrl;
		public String url;
		public String length;
		public String timestamp;
		public String compere;
		public int type;
		
		public Data(JSONObject jObj){
			pid = jObj.optString("pid");
			title = jObj.optString("title");
			brief = jObj.optString("brief");
			url = jObj.optString("url");
			imageUrl = jObj.optString("image");
			length = jObj.optString("video_length");
			timestamp = jObj.optString("timestamp");
			type = jObj.optInt("type");
			compere = jObj.optString("compName");
			if(compere != null && compere.equals("null")){
				compere = "";
			}
		}
		
	}
	
	public static VideoEntry constructEntry(String responce){
		if(responce == null || "".equals(responce)){
			return null;
		}
		VideoEntry entry = new VideoEntry();
		JSONObject temObj;
		try {
			temObj = new JSONObject(responce);
			entry.hasNext = temObj.optBoolean("hasNext");
			JSONArray dataArray = temObj.optJSONArray("data");
			if(dataArray != null){
				entry.dataList = new ArrayList<VideoEntry.Data>();
				int len = dataArray.length();
				for(int i = 0; i < len; i++){
					JSONObject dataItem = dataArray.getJSONObject(i);
					entry.dataList.add(new VideoEntry.Data(dataItem));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ERROR: " + responce);
			entry = null;
		}
		return entry;
		
	}
	
	
	public static long getSerialversionUid() {
		return serialVersionUID;
	}
	
}
