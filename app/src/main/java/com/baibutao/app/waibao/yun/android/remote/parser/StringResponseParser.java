package com.baibutao.app.waibao.yun.android.remote.parser;

import java.io.UnsupportedEncodingException;

import android.util.Log;

import com.baibutao.app.waibao.yun.android.common.MessageCodes;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.http.HttpClientUtil;

/**
 * <p>标题: </p>
 * <p>描述: </p>
 * <p>版权: lsb</p>
 * <p>创建时间: 2017年2月26日  下午11:33:56</p>
 * <p>作者：niepeng</p>
 */
public class StringResponseParser implements ResponseParser {

	@Override
	public Response parse(byte[] content, String charset, String sessionId) {
		try {
			String contentString = null;
			try {
				if (charset == null) {
					charset = HttpClientUtil.DEFAULT_CHARSET;
				}
				contentString = new String(content, charset);
			} catch (UnsupportedEncodingException e) {
				try {
					contentString = new String(content, HttpClientUtil.DEFAULT_CHARSET);
				} catch (UnsupportedEncodingException e1) {
					throw new RuntimeException(e1);
				}
			}
			Log.d("string parse", contentString);
			Log.d("string parse", "sessionId:" + sessionId);
			Response response = new Response();
			response.setModel(contentString);
			return response;
		} catch (Exception e) {
			Log.e("string parse", e.getMessage(), e);
			return new Response(MessageCodes.JSON_PARSE_FAILED, e.getMessage());
		}
		
	}

}
