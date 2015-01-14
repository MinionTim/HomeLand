package com.ville.homeland.util;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ville.homeland.R;

/**
 * A class for annotating a CharSequence with spans to convert textual emoticons
 * to graphical ones.
 */
public class SmileyMap {
    // Singleton stuff
    private static SmileyMap sInstance;
    public static SmileyMap getInstance() { return sInstance; }
    public static void init(Context context) {
    	sInstance = new SmileyMap(context);
    }

    private final Context mContext;
    public final String[] mSmileyTexts;
    private final Pattern mSmileyPattern;
    public final HashMap<String, Integer> mSmileyToRes;

    private SmileyMap(Context context) {
        mContext = context;
        mSmileyTexts = mContext.getResources().getStringArray(DEFAULT_SMILEY_TEXTS);
        mSmileyToRes = buildSmileyToRes();
        mSmileyPattern = buildPattern();
    }
    

    public static final int DEFAULT_SMILEY_TEXTS = R.array.default_smiley_texts;  
    public static final int[] DEFAULT_SMILEY_RES_IDS = {
    	R.drawable.face_good,
    	R.drawable.face_ok,
    	R.drawable.face_ai,
    	R.drawable.face_aini,
    	R.drawable.face_baibai,
    	R.drawable.face_beishang,
    	R.drawable.face_bishi,
    	R.drawable.face_bizui,
    	R.drawable.face_canzui,
    	R.drawable.face_chijing,
    	R.drawable.face_dahaqie,
    	R.drawable.face_guzhang,
    	R.drawable.face_haha,
    	R.drawable.face_haixiu,
    	R.drawable.face_han,
    	R.drawable.face_hehe,
    	R.drawable.face_heixian,
    	R.drawable.face_heng,
    	R.drawable.face_huangxin,
    	R.drawable.face_jiyan,
    	R.drawable.face_keai,
    	R.drawable.face_kelian,
    	R.drawable.face_ku,
    	R.drawable.face_kun,
    	R.drawable.face_landelini,
    	R.drawable.face_nu,
    	R.drawable.face_numa,
    	R.drawable.face_qian,
    	R.drawable.face_qinqin,
    	R.drawable.face_shengbing,
    	R.drawable.face_shiwang,
    	R.drawable.face_shuijiao,
    	R.drawable.face_sikao,
    	R.drawable.face_taikaixin,
    	R.drawable.face_touxiao,
    	R.drawable.face_tu,
    	R.drawable.face_wabishi,
    	R.drawable.face_weiwu,
    	R.drawable.face_weiqu,
    	R.drawable.face_xixi,
    	R.drawable.face_xin,
    	R.drawable.face_xu,
    	R.drawable.face_yiwen,
    	R.drawable.face_yinxian,
    	R.drawable.face_youhengheng,
    	R.drawable.face_yun,
    	R.drawable.face_zan,
    	R.drawable.face_zhuangkuang,
    	R.drawable.face_zuohengheng
    };  

    public static final int DEFAULT_SMILEY_NAMES = R.array.default_smiley_names;

    /**
     * Builds the hashtable we use for mapping the string version
     * of a smiley (e.g. ":-)") to a resource ID for the icon version.
     */
    private HashMap<String, Integer> buildSmileyToRes() {
        if (DEFAULT_SMILEY_RES_IDS.length != mSmileyTexts.length) {
            // Throw an exception if someone updated DEFAULT_SMILEY_RES_IDS
            // and failed to update arrays.xml
            throw new IllegalStateException("Smiley resource ID/text mismatch");
        }

        HashMap<String, Integer> smileyToRes =
                            new HashMap<String, Integer>(mSmileyTexts.length);
        for (int i = 0; i < mSmileyTexts.length; i++) {
            smileyToRes.put(mSmileyTexts[i], DEFAULT_SMILEY_RES_IDS[i]);
        }

        return smileyToRes;
    }

    /**
     * Builds the regular expression we use to find smileys in {@link #addSmileySpans}.
     */
    private Pattern buildPattern() {
        // Set the StringBuilder capacity with the assumption that the average
        // smiley is 3 characters long.
        StringBuilder patternString = new StringBuilder(mSmileyTexts.length * 3);

        // Build a regex that looks like (:-)|:-(|...), but escaping the smilies
        // properly so they will be interpreted literally by the regex matcher.
        patternString.append('(');
        for (String s : mSmileyTexts) {
            patternString.append(Pattern.quote(s));
            patternString.append('|');
        }
        // Replace the extra '|' with a ')'
        patternString.replace(patternString.length() - 1, patternString.length(), ")");

        return Pattern.compile(patternString.toString());
    }


    /**
     * Adds ImageSpans to a CharSequence that replace textual emoticons such
     * as :-) with a graphical version.
     *
     * @param text A CharSequence possibly containing emoticons
     * @return A CharSequence annotated with ImageSpans covering any
     *         recognized emoticons.
     */
    public CharSequence addSmileySpans(CharSequence text) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);

        Matcher matcher = mSmileyPattern.matcher(text);
        while (matcher.find()) {
            int resId = mSmileyToRes.get(matcher.group());
            Drawable d = mContext.getResources().getDrawable(resId);
            d.setBounds(0, 0, 35, 35);
            builder.setSpan(new ImageSpan(d),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            builder.setSpan(new ImageSpan(mContext, resId),
//            		matcher.start(), matcher.end(),
//            		Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        
        return builder;
    }
    
}



