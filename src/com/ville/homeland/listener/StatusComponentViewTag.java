package com.ville.homeland.listener;


public class StatusComponentViewTag{
	public static final int TAG_AVATER = 0;
	public static final int TAG_AVATER_USER = 1;
	public static final int TAG_UPLOAD_IMAGE = 2;
	private static final int TAG_IMAGE_SUB = 3;
	public static final int TAG_LINK = 4;
	
	public int type;
	public Object obj;
	private StatusComponentViewTag(int tagType, Object obj){
		this.type = tagType;
		this.obj = obj;
	}
	public static StatusComponentViewTag build(int tagType, Object obj){
		return new StatusComponentViewTag(tagType, obj);
	}
	
}
