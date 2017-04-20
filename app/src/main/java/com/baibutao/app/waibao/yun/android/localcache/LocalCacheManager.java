package com.baibutao.app.waibao.yun.android.localcache;

import java.io.File;

/**
 * @author lsb
 *
 * @date 2012-5-29 下午11:48:13
 */
public interface LocalCacheManager {
	
	byte[] getData(String key);

	void putData(String key, byte[] data);

	void delete(String key);

	File getTargetFile(String name);

	void clearLocalCache();

}
