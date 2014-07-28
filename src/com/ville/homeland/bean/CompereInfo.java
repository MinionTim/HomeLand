package com.ville.homeland.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CompereInfo {
	private String id;
	private String name;
	private String imageUrl;
	
	public CompereInfo(String jsonStr){
		JSONObject jObj;
		try {
			jObj = new JSONObject(jsonStr);
			this.id = jObj.optString("id");
			this.name = jObj.optString("name");
			this.imageUrl = jObj.optString("src");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public CompereInfo(JSONObject jObj){
		this.id = jObj.optString("id");
		this.name = jObj.optString("name");
		this.imageUrl = jObj.optString("src");
	}
	public static List<CompereInfo> constructComInfoList(String json){
		try {
			JSONObject temObj = new JSONObject(json);
			JSONArray array = temObj.optJSONArray("list");
			int count = array.length();
			List<CompereInfo> list = new ArrayList<CompereInfo>(count);
			for (int i = 0; i < count; i++) {
				JSONObject obj = array.getJSONObject(i);
				CompereInfo info = new CompereInfo(obj);
				list.add(info);
			}
			return list;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	@Override
	public String toString() {
		return "CompereInfo [id=" + id + ", name=" + name + ", imageUrl="
				+ imageUrl + "]";
	}
}
