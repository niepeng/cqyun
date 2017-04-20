package com.baibutao.app.waibao.yun.android.remote.parser;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.baibutao.app.waibao.yun.android.common.MessageCodes;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.http.HttpClientUtil;
import com.baibutao.app.waibao.yun.android.util.DateUtil;

/**
 * @author lsb
 *
 * @date 2012-5-29 下午11:30:26
 */
public class JsonResponseParser implements ResponseParser {

	private static final String CODE = "code";
	
	private static final String MESSAGE = "msg";
	
	private static final String QUERY_TIME = "queryTime";
	
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
			Log.d("json parse", contentString);
			Log.d("json parse", "sessionId:" + sessionId);
			JSONObject jsonObject = new JSONObject(contentString);
			int code = jsonObject.getInt(CODE);
			String message = null;
			if (jsonObject.has(MESSAGE)) {
				message = jsonObject.getString(MESSAGE);
			}
			Response response = new Response(code, message);
			if (jsonObject.has(QUERY_TIME)) {
				String queryTimeStr = jsonObject.getString(QUERY_TIME);
				Date queryTime = DateUtil.parse(queryTimeStr);
				response.setQueryTime(queryTime);
			}
			response.setSessionId(sessionId);
			response.setModel(jsonObject);
			return response;
		} catch (JSONException e) {
			Log.e("json parse", e.getMessage(), e);
			return new Response(MessageCodes.JSON_PARSE_FAILED, e.getMessage());
		}
		
	}

}

