package com.ville.homeland.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ville.homeland.util.Constants;

public class PlayInfo implements Serializable{
	private static final long serialVersionUID = -374993298672335234L;
	
	private String pid;
	private double totalLength;
	
	private ArrayList<Info> listLow = new ArrayList<Info>();
	private ArrayList<Info> listNor = new ArrayList<Info>();
	private ArrayList<Info> listHigh = new ArrayList<Info>();
	
	
	
	public static PlayInfo createPlayInfo(String jsonStr) {
		// TODO Auto-generated constructor stub
		PlayInfo info = new PlayInfo();
		try {
			JSONObject item = new JSONObject(jsonStr);
			JSONObject obj = item.optJSONObject("video");
			if(obj != null){
				info.totalLength = obj.optDouble("totalLength", 2400.00);
				JSONArray norArray = obj.optJSONArray("chapters");
				if(norArray != null){
					int len = norArray.length();
					for(int i = 0; i < len; i++){
						JSONObject unit = norArray.getJSONObject(i);
						info.listNor.add(new Info(unit));
					}
				}
				JSONArray lowArray = obj.optJSONArray("lowChapters");
				if(lowArray != null){
					int len = lowArray.length();
					for(int i = 0; i < len; i++){
						JSONObject unit = lowArray.getJSONObject(i);
						info.listLow.add(new Info(unit));
					}
				}
				JSONArray highArray = obj.optJSONArray("chapters2");
				if(highArray != null){
					int len = highArray.length();
					for(int i = 0; i < len; i++){
						JSONObject unit = highArray.getJSONObject(i);
						info.listHigh.add(new Info(unit));
					}
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			info = null;
			e.printStackTrace();
		}
		return info;
	}
	

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public double getTotalLength() {
		return totalLength;
	}

	public void setTotalLength(double totalLength) {
		this.totalLength = totalLength;
	}

	public ArrayList<Info> getListLow() {
		return listLow;
	}
	public ArrayList<Info> getListNor() {
		return listNor;
	}
	public ArrayList<Info> getListHigh() {
		return listHigh;
	}

	
	
	/**
	 * 
	 * Info  视频片段，由视频地址和视频长度构成
	 * @author ville
	 *
	 */
	public static class Info{
		public int duration;			//秒
		public String url;
		
		public Info(JSONObject jObj){
			duration = jObj.optInt("duration", 0);
			url = jObj.optString("url", "");
		}

		@Override
		public String toString() {
			return "Info [duration=" + duration + ", url=" + url + "]";
		}
		
	}



	@Override
	public String toString() {
//		return "PlayInfo [listLow=" + listLow + ", listNor=" + listNor
//				+ ", listHigh=" + listHigh + "]";
		StringBuffer sb = new StringBuffer();
		sb.append("低清：");
		for(int i = 0; i < listLow.size(); i++){
			sb.append(listLow.get(i)).append("\n");
		}
		sb.append("标清：");
		for(int i = 0; i < listNor.size(); i++){
			sb.append(listNor.get(i)).append("\n");
		}
		sb.append("高清：");
		for(int i = 0; i < listHigh.size(); i++){
			sb.append(listHigh.get(i)).append("\n");
		}
		return sb.toString();
		
	}
	
	
}
