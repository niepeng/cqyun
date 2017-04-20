package com.baibutao.app.waibao.yun.android.remote.http;

import com.baibutao.app.waibao.yun.android.remote.Request;

/**
 * @author lsb
 * 
 * @date 2012-5-29 下午11:32:19
 */
public class HttpRemoteRequest extends Request {

	private Method method;

	public HttpRemoteRequest(String target, Method method) {
		super(target);
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public static enum Method {
		GET, POST, DELETE, PUT
	}

}
