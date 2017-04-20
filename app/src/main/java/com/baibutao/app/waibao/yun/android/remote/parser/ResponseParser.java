package com.baibutao.app.waibao.yun.android.remote.parser;

import com.baibutao.app.waibao.yun.android.remote.Response;

/**
 * @author lsb
 *
 * @date 2012-5-29 下午11:30:02
 */
public interface ResponseParser {

	Response parse(byte[] content, String charset, String sessionId);
	
}
