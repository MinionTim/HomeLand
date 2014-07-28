package com.ville.homeland.bean;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
	
	private long id;
    private String name;
    private String screen_name;
    private String location;
    private String description;
    private String profile_image_url;
    private String profile_url;
    private String gender;
    private String created_at;
    
    private String remark;
    private String avatar_large;
    private String verified_reason;
    private String lang;
    
    private int online_status;
    private int bi_followers_count;
    private int followers_count;
    private int friends_count;
    private int statuses_count;
    private int favourites_count;
    
    private boolean follow_me;
    private boolean allow_all_act_msg;
    private boolean geo_enabled;
    private boolean verified;
    
	public User(String jsonStr) {
		// TODO Auto-generated constructor stub
		try {
			JSONObject obj = new JSONObject(jsonStr);
			id = obj.getLong("id");
			screen_name = obj.optString("screen_name");
			location = obj.optString("location");
			description = obj.optString("description");
			profile_image_url = obj.optString("profile_image_url");
			profile_url = obj.optString("profile_url");
			gender = obj.optString("gender");
			created_at = obj.optString("created_at");
			
			remark = obj.optString("remark");
			avatar_large = obj.optString("avatar_large");
			verified_reason = obj.optString("verified_reason");
			lang = obj.optString("lang");
			
			online_status = obj.getInt("online_status");
			bi_followers_count = obj.getInt("bi_followers_count");
			followers_count = obj.getInt("followers_count");
			friends_count = obj.getInt("friends_count");
			statuses_count = obj.getInt("statuses_count");
			favourites_count = obj.getInt("favourites_count");
			
			follow_me = obj.getBoolean("follow_me");
			allow_all_act_msg = obj.getBoolean("allow_all_act_msg");
			geo_enabled = obj.getBoolean("geo_enabled");
			verified = obj.getBoolean("verified");
			System.out.println("User: " + screen_name + ", "  + id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public User(JSONObject obj) {
		// TODO Auto-generated constructor stub
		try {
			id = obj.getLong("id");
			screen_name = obj.optString("screen_name");
			location = obj.optString("location");
			description = obj.optString("description");
			profile_image_url = obj.optString("profile_image_url");
			profile_url = obj.optString("profile_url");
			gender = obj.optString("gender");
			created_at = obj.optString("created_at");
			
			remark = obj.optString("remark");
			avatar_large = obj.optString("avatar_large");
			verified_reason = obj.optString("verified_reason");
			lang = obj.optString("lang");
			
			online_status = obj.getInt("online_status");
			bi_followers_count = obj.getInt("bi_followers_count");
			followers_count = obj.getInt("followers_count");
			friends_count = obj.getInt("friends_count");
			statuses_count = obj.getInt("statuses_count");
			favourites_count = obj.getInt("favourites_count");
			
			follow_me = obj.getBoolean("follow_me");
			allow_all_act_msg = obj.getBoolean("allow_all_act_msg");
			geo_enabled = obj.getBoolean("geo_enabled");
			verified = obj.getBoolean("verified");
			
			
			System.out.println("User: " + screen_name + ", "  + id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getScreen_name() {
		return screen_name;
	}

	public String getLocation() {
		return location;
	}

	public String getDescription() {
		return description;
	}

	public String getProfile_image_url() {
		return profile_image_url;
	}

	public String getProfile_url() {
		return profile_url;
	}

	public String getGender() {
		return gender;
	}

	public String getCreated_at() {
		return created_at;
	}

	public String getRemark() {
		return remark;
	}

	public String getAvatar_large() {
		return avatar_large;
	}

	public String getVerified_reason() {
		return verified_reason;
	}

	public String getLang() {
		return lang;
	}

	public int getOnline_status() {
		return online_status;
	}

	public int getBi_followers_count() {
		return bi_followers_count;
	}

	public int getFollowers_count() {
		return followers_count;
	}

	public int getFriends_count() {
		return friends_count;
	}

	public int getStatuses_count() {
		return statuses_count;
	}

	public int getFavourites_count() {
		return favourites_count;
	}

	public boolean isFollow_me() {
		return follow_me;
	}

	public boolean isAllow_all_act_msg() {
		return allow_all_act_msg;
	}

	public boolean isGeo_enabled() {
		return geo_enabled;
	}

	public boolean isVerified() {
		return verified;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", screen_name="
				+ screen_name + ", location=" + location + ", description="
				+ description + ", profile_image_url=" + profile_image_url
				+ ", profile_url=" + profile_url + ", gender=" + gender
				+ ", created_at=" + created_at + ", remark=" + remark
				+ ", avatar_large=" + avatar_large + ", verified_reason="
				+ verified_reason + ", lang=" + lang + ", online_status="
				+ online_status + ", bi_followers_count=" + bi_followers_count
				+ ", followers_count=" + followers_count + ", friends_count="
				+ friends_count + ", statuses_count=" + statuses_count
				+ ", favourites_count=" + favourites_count + ", follow_me="
				+ follow_me + ", allow_all_act_msg=" + allow_all_act_msg
				+ ", geo_enabled=" + geo_enabled + ", verified=" + verified
				+ "]";
	}
	
}
