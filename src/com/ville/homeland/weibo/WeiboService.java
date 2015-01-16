package com.ville.homeland.weibo;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;

public class WeiboService {
	
	private static WeiboService mService = null;
	public static final int COUNT_PER_PAGE = 25;
	public static final int API_STATUS_HOMELINE = 0x0201;
	public static final int API_ON_ERROR 		= 0x0202;
	public static final int API_ON_IOEXCEPTION 	= 0x0203;
	public static final int API_COMMENTS_BY_ID 	= 0x0204;
	
	private WeiboService(Context context){
		mAuthInfo = new AuthInfo(context, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
		mCtx = context;
	}
	public synchronized static WeiboService getInstance(){
		return mService;
	}
	private AuthInfo mAuthInfo;
    
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;

    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    private final Context mCtx;
	
	public SsoHandler getSsoHandler(){
		return mSsoHandler;
	}
	public void updateOauth2AccessToken(Context context, Oauth2AccessToken token){
		mAccessToken = token;
		AccessTokenKeeper.writeAccessToken(context, token);
	}
	public static void init(Context context){
		mService = new WeiboService(context);
	}
	public boolean isOauthAccessed(Context context){
		if(mAccessToken == null){
			mAccessToken = AccessTokenKeeper.readAccessToken(context);
		}
		return mAccessToken.isSessionValid();
	}
	
	public void ssoAuthorizeCallBack(int requestCode, int resultCode, Intent data){
		// SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
	}

	public void ssoAuthorize(Activity activity, WeiboAuthListener listener){
		 mSsoHandler = new SsoHandler(activity, mAuthInfo);
		 mSsoHandler.authorize(listener);
	}
	public void ssoAuthorizeClient(Activity activity, WeiboAuthListener listener){
		mSsoHandler = new SsoHandler(activity, mAuthInfo);
		mSsoHandler.authorizeClientSso(listener);
	}
	public void ssoAuthorizeWeb(Activity activity, WeiboAuthListener listener){
		mSsoHandler = new SsoHandler(activity, mAuthInfo);
		mSsoHandler.authorizeWeb(listener);
	}
	
	/**
	 * @param sinceID 返回ID比sinceID大的微博（即比sinceID时间晚的微博）
	 * */
	public void requestHomeTimeLine(Context context, long maxId, int page, RequestListener listener){
//		System.out.println("requestHomeTimeLine");
		StatusesAPI api = new StatusesAPI(context, Constants.APP_KEY, mAccessToken);
		 /**
	     * 获取某个用户最新发表的微博列表。
	     * 
	     * @param uid           需要查询的用户ID
	     * @param since_id      若指定此参数，则返回ID比since_id大的微博（即比since_id时间晚的微博），默认为0
	     * @param max_id        若指定此参数，则返回ID小于或等于max_id的微博，默认为0
	     * @param count         单页返回的记录条数，默认为50
	     * @param page          返回结果的页码，默认为1
	     * @param base_app      是否只获取当前应用的数据。false为否（所有数据），true为是（仅当前应用），默认为false
	     * @param featureType   过滤类型ID，0：全部、1：原创、2：图片、3：视频、4：音乐，默认为0
	     *                      <li> {@link #FEATURE_ALL}
	     *                      <li> {@link #FEATURE_ORIGINAL}
	     *                      <li> {@link #FEATURE_PICTURE}
	     *                      <li> {@link #FEATURE_VIDEO}
	     *                      <li> {@link #FEATURE_MUSICE}
	     * @param trim_user     返回值中user字段开关，false：返回完整user字段、true：user字段仅返回user_id，默认为false
	     * @param listener      异步请求回调接口
	     */
		api.homeTimeline(0, maxId, 5, page, false, StatusesAPI.FEATURE_ALL, false, listener);
	}
	/**
	 * @param sinceID 返回ID比sinceID大的微博（即比sinceID时间晚的微博）
	 * */
	public void requestDefaultUserTimeLine(Context context, long maxId, int page, RequestListener listener){
//		System.out.println("requestHomeTimeLine");
		StatusesAPI api = new StatusesAPI(context, Constants.APP_KEY, mAccessToken);
		long userId = 2384122784L;
		api.userTimeline(userId, 0, maxId, COUNT_PER_PAGE, page, false, StatusesAPI.FEATURE_ALL, false, listener);
	}
	
	public void requestComments(Context context, long id, long maxId, int page, RequestListener listener){
		isOauthAccessed(context);
		CommentsAPI api = new CommentsAPI(context, Constants.APP_KEY, mAccessToken);
		/**
	     * 根据微博ID返回某条微博的评论列表。
	     * 
	     * @param id         需要查询的微博ID。
	     * @param since_id   若指定此参数，则返回ID比since_id大的评论（即比since_id时间晚的评论），默认为0。
	     * @param max_id     若指定此参数，则返回ID小于或等于max_id的评论，默认为0。
	     * @param count      单页返回的记录条数，默认为50
	     * @param page       返回结果的页码，默认为1。
	     * @param authorType 作者筛选类型，0：全部、1：我关注的人、2：陌生人 ,默认为0。可为以下几种 :
	     *                   <li>{@link #AUTHOR_FILTER_ALL}
	     *                   <li>{@link #AUTHOR_FILTER_ATTENTIONS}
	     *                   <li>{@link #AUTHOR_FILTER_STRANGER}
	     * @param listener   异步请求回调接口
	     */
		api.show(id, 0, maxId, 20, page, CommentsAPI.AUTHOR_FILTER_ALL, listener);
	}
	
	/**
     * 对一条微博进行评论。
     * 
     * @param comment     评论内容，内容不超过140个汉字。
     * @param id          需要评论的微博ID。
     * @param comment_ori 当评论转发微博时，是否评论给原微博
     * @param listener    异步请求回调接口
    */
	public void createComment(Context context, String comment, long id, RequestListener listener){
		//String comment, long id, boolean comment_ori, RequestListener listener
		CommentsAPI api = new CommentsAPI(context, Constants.APP_KEY, mAccessToken);
		api.create(comment, id, true, listener);
	}
	
	 /**
     * 根据用户昵称获取用户信息。
     * 
     * @param screen_name 需要查询的用户昵称
     * @param listener    异步请求回调接口
     */
	public void requestShowUserInfo(Context context, String screen_name, RequestListener listener){
		UsersAPI api = new UsersAPI(context, Constants.APP_KEY, mAccessToken);
		api.show(screen_name, listener);
	}
}
