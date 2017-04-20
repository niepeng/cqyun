package com.baibutao.app.waibao.yun.android.remote;

import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;
import com.baibutao.app.waibao.yun.android.remote.http.HttpRemoteManager;
import com.baibutao.app.waibao.yun.android.remote.parser.JsonResponseParser;
import com.baibutao.app.waibao.yun.android.remote.parser.ResponseParser;
import com.baibutao.app.waibao.yun.android.remote.proxy.AutoConnectRemoteManager;
import com.baibutao.app.waibao.yun.android.remote.proxy.CheckNetworkStateRemoteManager;
import com.baibutao.app.waibao.yun.android.remote.proxy.SecurityRemoteManager;

/**
 * @author lsb
 *
 * @date 2012-5-29 下午11:18:35
 */
public abstract class RemoteManager {

public abstract Request createQueryRequest(String target);
	
	public abstract Request createPostRequest(String target);
	
	public abstract Response execute(Request request);
	
	protected static EewebApplication cardApplication;
	
//	private static RemoteManager defaultRemoteManager = new HttpRemoteManager();
	
	protected ResponseParser responseParser = new JsonResponseParser();
	
	public static void init(EewebApplication cardApplication) {
		RemoteManager.cardApplication = cardApplication;
	}
	
	public void setResponseParser(ResponseParser paramResponseParser) {
		this.responseParser = paramResponseParser;
	}
	
	/**
	 * 原生的RemoteManager，没有做过任何处理， 一般不推荐使用
	 * @return
	 */
	public static RemoteManager getRawRemoteManager() {
		return new HttpRemoteManager();
	}
	
	/**
	 * 后台触发的不用登录建议使用这个
	 * 
	 * 带安全签名的RemoteManager
	 * @return
	 */
	public static RemoteManager getSecurityRemoteManager() {
		return new CheckNetworkStateRemoteManager(new SecurityRemoteManager(new HttpRemoteManager()));
	}
	
	
	public static RemoteManager getAutoLoginSecurityRemoteManager() {
		return new CheckNetworkStateRemoteManager(new SecurityRemoteManager(new HttpRemoteManager()));
	}
	
	/**
	 * 新增数据的时候建议用这个，这个不会进行重试
	 * 
	 * 1、带安全签名
	 * 4、检查网络状态
	 * @return
	 */
	public static RemoteManager getPostOnceRemoteManager() {
		return new CheckNetworkStateRemoteManager(new SecurityRemoteManager(new HttpRemoteManager()));
	}
	
	/**
	 * 用户主动触发的一般建议使用这个
	 * 
	 * 1、带安全签名
	 * 2、自动重连（3次）
	 * 3、自动登录
	 * 4、检查网络状态
	 * @return
	 */
	public static RemoteManager getFullFeatureRemoteManager() {
		return new CheckNetworkStateRemoteManager(new AutoConnectRemoteManager(new SecurityRemoteManager(new HttpRemoteManager()), 1));
	}
	
}
