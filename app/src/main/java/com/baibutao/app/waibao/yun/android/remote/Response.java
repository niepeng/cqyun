package com.baibutao.app.waibao.yun.android.remote;

import com.baibutao.app.waibao.yun.android.common.MessageCodes;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;

import java.util.Date;

/**
 * @author lsb
 *
 * @date 2012-5-29 下午11:26:38
 */
public class Response {
	public Response() {
		super();
	}
	
	public Response(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	private int code = MessageCodes.UNKNOW_ERROR;
	
	private String message;
	
	private Date queryTime;
	
	private String sessionId;
	
	private Object model;
	
	public int getCode() {
		return code;
	}
	
	public boolean isSuccess() {
		return (code == MessageCodes.SUCCESS);
	}
	
	public boolean isDataSuccess() {
		if (!(model instanceof String)) {
			return false;
		}
		code = JsonUtil.getInt(JsonUtil.getJsonObject(model), "code", -1);
		return isSuccess();
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}

	public Date getQueryTime() {
		return queryTime;
	}

	public void setQueryTime(Date queryTime) {
		this.queryTime = queryTime;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
}


