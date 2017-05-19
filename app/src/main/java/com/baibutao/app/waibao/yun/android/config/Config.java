package com.baibutao.app.waibao.yun.android.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * @author lsb
 * 
 * @date 2012-5-29 下午11:10:13
 */
public class Config {
	
	public interface Values {
		
		String URL = "http://api.eefield.com";
//		String URL = "http://api.eefield.com:2501";


		String YUN_ALARM_URL = "http://yun.eefield.com/user/jsonRecentlyAlarmList.htm";

		String YUN_CHECK_VERSION_URL = "http://yun.eefield.com/user/version.htm";
	}

	public interface Names {
		
		// 本地存储的时候，线上与测试环境分开
		String IMG_FOLDER = "hui.img.folder";

		String CHECK_UPDATE_URL = "hui.check.update";
		
		String USE_SIGN = "hui.use.sign";
		
		String SIGN_KEY = "hui.sign.key";
		
		String USER_LOGIN_URL = "hui.userlogin.url";
		
		String ITEM_LIST_URL = "hui.itemlist.url";
		
		String AREA_LIST_URL = "hui.arealist.url";
		
		String ALARM_LIST_URL = "hui.alarmlist.url";
		
		String ALARM_DELERE_URL = "hui.alarmdelete.url";
		
		String ALARM_DELERE_ALL_URL = "hui.alarmdelete.all.url";
		
		
		
		
		
		
		String SIFT_LIST_URL = "hui.siftlist.url";
		
		String CAT_LIST_URL = "hui.catlist.url";
		
		
		
		String ITEM_DETAIL_URL = "hui.itemdetail.url";
		
		String SUGGESTION_URL = "hui.suggestion.url";
		
		String COLLCETION_OPT_URL = "hui.collectionopt.url";
		
		String USER_CENTER_URL = "hui.usercenter.url";
		
	}

	private static final String ENV_PATH = "configs/env.properties";
	private AtomicBoolean inited = new AtomicBoolean(false);

	private Properties properties = new Properties();

	private Properties versionProperties = new Properties();

	private static Config config = new Config();

	private Config() {
	}

	public static Config getConfig() {
		return config;
	}

	public String getVersionPerperty(String name) {
		return versionProperties.getProperty(name);
	}

	public String getProperty(String name) {
		return properties.getProperty(name);
	}

	public String getProperty(String name, String defaultValue) {
		return properties.getProperty(name, defaultValue);
	}

	public void init(Context context) {
		if (!inited.compareAndSet(false, true)) {
			return;
		}
		AssetManager assetManager = context.getAssets();
		try {
			String env = loadEnv(assetManager);
			String configFileName = getConfigFileName(env);
			InputStream is = assetManager.open(configFileName);
			properties.load(is);
			is.close();

			InputStream versionIs = assetManager.open("configs/version.properties");
			versionProperties.load(versionIs);
			versionIs.close();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String getConfigFileName(String env) {
		return "configs/config-" + env + ".properties";
	}

	private static String loadEnv(AssetManager assetManager) throws IOException {
		InputStream is = assetManager.open(ENV_PATH);
		Properties envProperties = new Properties();
		envProperties.load(is);
		is.close();

		String env = envProperties.getProperty("env", "daily");
		Log.d("Config", "env:" + env);
		return env;
	}

}
