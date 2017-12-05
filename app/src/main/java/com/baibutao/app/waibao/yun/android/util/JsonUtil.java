package com.baibutao.app.waibao.yun.android.util;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

/**
 * @author lsb
 * 
 * @date 2012-5-30 上午10:31:25
 */
public class JsonUtil {
	
	public static JSONObject getJsonObject(Object obj) {
		if (obj == null) {
			return null;
		}

		if(obj instanceof JSONObject) {
			return (JSONObject)obj;
		}

		String content = (String) obj;
		if (StringUtil.isBlank(content)) {
			return null;
		}
		try {
			JSONObject json = new JSONObject(content);
			return json;
		} catch (JSONException e) {
			return null;
		}
	}

	public static JSONArray getJsonArray(Object obj) {
		if (obj == null) {
			return null;
		}

		if(obj instanceof JSONArray) {
			return (JSONArray)obj;
		}

		String content = (String) obj;

		if (StringUtil.isBlank(content)) {
			return null;
		}
		try {
			JSONArray json = new JSONArray(content);
			return json;
		} catch (JSONException e) {
			return null;
		}
	}
	
	public static JSONArray getJsonArray(JSONObject json, String name) {
		if (json == null || name == null) {
			return null;
		}
		if (!json.has(name)) {
			return null;
		}
		try {
			return json.getJSONArray(name);
		} catch (JSONException e) {
			return null;
		}
	}

	public static JSONObject getJSONObject(JSONObject json, String name) {
		if (json == null || name == null) {
			return null;
		}
		if (!json.has(name)) {
			return null;
		}
		try {
			return json.getJSONObject(name);
		} catch (JSONException e) {
			return null;
		}
	}

	public static String getString(JSONObject json, String name, String defaultValue) {
		if (json == null || name == null) {
			return defaultValue;
		}
		if (!json.has(name)) {
			return defaultValue;
		}
		try {
			return json.getString(name);
		} catch (JSONException e) {
			return defaultValue;
		}
	}

	public static int getInt(JSONObject json, String name, int defaultValue) {
		if (json == null || name == null) {
			return defaultValue;
		}
		if (!json.has(name)) {
			return defaultValue;
		}
		try {
			return json.getInt(name);
		} catch (JSONException e) {
			return defaultValue;
		}
	}

	public static long getLong(JSONObject json, String name, long defaultValue) {
		if (json == null || name == null) {
			return defaultValue;
		}
		if (!json.has(name)) {
			return defaultValue;
		}
		try {
			return json.getLong(name);
		} catch (JSONException e) {
			return defaultValue;
		}
	}

	public static double getDouble(JSONObject json, String name, double defaultValue) {
		if (json == null || name == null) {
			return defaultValue;
		}
		if (!json.has(name)) {
			return defaultValue;
		}
		try {
			return json.getDouble(name);
		} catch (JSONException e) {
			return defaultValue;
		}
	}
	
	public static <T> String mapToJson(Map<String, T> map) {
		Gson gson = new Gson();
		String jsonStr = gson.toJson(map);
		return jsonStr;
	}
	public static <T> String listToJson(List<T> list) {
		Gson gson = new Gson();
		String jsonStr = gson.toJson(list);
		return jsonStr;
	}

	public static <T> T jsonToBean(String jsonString, Class<T> beanCalss) {
		try {
			Gson gson = new Gson();
			return gson.fromJson(jsonString, beanCalss);
		} catch (Exception e) {
		}
		return null;
	}
}
