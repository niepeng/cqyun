package com.baibutao.app.waibao.yun.android.localcache;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.util.cache.LRUCache;

/**
 * @author lsb
 * 
 * @date 2012-5-29 下午11:45:53
 */
public class ImageCache {

	private static LocalCacheManager localCacheManager;

	private static LRUCache<String, Bitmap> memCache = new LRUCache<String, Bitmap>();

	public static void init(Context context) {
		String IMAGE_CACHE_DIR = Config.getConfig().getProperty(Config.Names.IMG_FOLDER);
		localCacheManager = LocalCacheManagerFactory.createLocalCacheManager(context, IMAGE_CACHE_DIR);
	}

	public static boolean fileExist(String name) {
		String makeKeyName = makeKeyName(name);
		File file = localCacheManager.getTargetFile(makeKeyName);
		if (file != null && file.exists()) {
			return true;
		}
		return false;
	}

	public static Bitmap getBitmap(String name) {
		String makeKeyName = makeKeyName(name);
		Bitmap ret = memCache.get(makeKeyName);
		if (ret != null) {
			return ret;
		}
		byte[] data = getBitmapData(makeKeyName);
		if (data == null) {
			ret = null;
		} else {
			ret = BitmapFactory.decodeByteArray(data, 0, data.length);
		}
		memCache.put(makeKeyName, ret);
		return ret;
	}

	public static String makeKeyName(String name) {
		return name;
	}

	public static byte[] getBitmapData(String name) {
		assertInit();
		byte[] ret = localCacheManager.getData(name);
		return ret;
	}

	public static void setBitmapData(String name, byte[] data) {
		assertInit();
		localCacheManager.putData(name, data);
	}

	private static void assertInit() {
		if (localCacheManager == null) {
			throw new RuntimeException("You must call ImageCache.init first!");
		}
	}

	public static LocalCacheManager getLocalCacheManager() {
		return localCacheManager;
	}

}
