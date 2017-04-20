package com.baibutao.app.waibao.yun.android.localcache;

import java.io.UnsupportedEncodingException;

import android.content.Context;

import com.baibutao.app.waibao.yun.android.config.Config;

public class FileCache {
	
	private final static String UID_FILE_NAME = "u.txt";
	
	private static LocalCacheManager localCacheManager;

	public static void init(Context context) {
		String IMAGE_CACHE_DIR = Config.getConfig().getProperty(Config.Names.IMG_FOLDER);
		localCacheManager = LocalCacheManagerFactory.createLocalCacheManager(context, IMAGE_CACHE_DIR);
	}
	
	public static void putUser(String uid) {
		assertInit();
		localCacheManager.putData(UID_FILE_NAME, uid.getBytes());
	}
	
	public static String getUser() {
		byte[] data = localCacheManager.getData(UID_FILE_NAME);
		if (data != null) {
			try {
				return new String(data,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				return data.toString();
			} 
		}
		return null;
	}
	
	
	private static void assertInit() {
		if (localCacheManager == null) {
			throw new RuntimeException("You must call ImageCache.init first!");
		}
	}

}
