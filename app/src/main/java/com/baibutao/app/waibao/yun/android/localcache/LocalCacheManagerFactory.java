package com.baibutao.app.waibao.yun.android.localcache;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * @author lsb
 *
 * @date 2012-5-29 下午11:48:28
 */
public class LocalCacheManagerFactory {

	public static LocalCacheManager createLocalCacheManager(Context context, String dirName) {
		if (Environment.MEDIA_REMOVED.equals(Environment.getExternalStorageState())) {
			// sd卡不可用
			Log.i("local cache factory", "use private storage");
			return new LocalCacheManagerFs(context, dirName);
		} else {
			Log.i("local cache factory", "use sd card");
			return new LocalCacheManagerSd(dirName);
		}
	}
	
}
