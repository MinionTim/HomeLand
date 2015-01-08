package com.ville.homeland.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ville.homeland.bean.VideoEntry;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;

public class Constants {
	public static final int LISTVIEW_DATA_INIT 				= 0x1;
	public static final int LISTVIEW_DATA_REFRESH 			= 0x2;
	public static final int LISTVIEW_DATA_MOREING 			= 0x4;
	public static final int LISTVIEW_DATA_MORE_END 			= 0x5;
	public static final int LISTVIEW_DATA_FULL 				= 0x6;
	
	public static final int TYPE_VIDEO_BJX			= 1;
	public static final int TYPE_VIDEO_YHX			= 2;
	public static final int TYPE_VIDEO_ZGX			= 3;
	public static final int TYPE_VIDEO_BSBCX		= 4;
	public static final int TYPE_VIDEO_JHWLX		= 5;
	public static final int TYPE_VIDEO_QITA		    = -1;
	
	public static final String KEY_WEB_BROWSER_LINK = "key_web_link";
	
	
	public static final String FLV_HOST = "http://www.flvxz.com";
	public static final String URL_HOMELAND_HOME = "http://yuanfangdejia2.duapp.com";
	public static final String URL_HOMELAND_ALLCOMPERES = URL_HOMELAND_HOME + "/all_comp";
	public static final String URL_HOMELAND_COMP_IMGS_FORMAT = URL_HOMELAND_HOME + "/image?id=%s&pn=%d";
	
	public static final Pattern PATTERN_PAGELINK = Pattern.compile("(\\{'title')(.+?)(jpg'\\})");
	public static final Pattern PATTERN_VIDEOTITLE = Pattern.compile("第\\d+集");
	private static SimpleDateFormat sSdf = new SimpleDateFormat("MM-dd HH:mm");
	private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);  
	
	public static final Comparator<VideoEntry.Data> CMP_TIME = new Comparator<VideoEntry.Data>() {

		@Override
		public int compare(VideoEntry.Data lhs, VideoEntry.Data rhs) {
			// TODO Auto-generated method stub
			//比较 集数
			String timeL = lhs.timestamp.split(" ")[0].replaceAll("-", "");
			String timeR = rhs.timestamp.split(" ")[0].replaceAll("-", "");
			int left = Integer.valueOf(timeL);
			int right =Integer.valueOf(timeR);
			return (right - left);
		}
	};
	
	public static void saveBitmapToFile(Bitmap bitmap, String _file)
            throws IOException {
        BufferedOutputStream os = null;
        try {
            File file = new File(_file);
            // String _filePath_file.replace(File.separatorChar +
            // file.getName(), "");
            int end = _file.lastIndexOf(File.separator);
            String _filePath = _file.substring(0, end);
            File filePath = new File(_filePath);
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                	e.printStackTrace();
                }
            }
        }
    }
}
