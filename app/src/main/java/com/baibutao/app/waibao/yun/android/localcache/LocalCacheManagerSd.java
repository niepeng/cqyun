package com.baibutao.app.waibao.yun.android.localcache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.os.Environment;
import android.util.Log;

import com.baibutao.app.waibao.yun.android.util.IoUtil;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

/**
 * @author lsb
 *
 * @date 2012-5-29 下午11:49:10
 */
public class LocalCacheManagerSd implements LocalCacheManager {

	private File baseDir;
	
	public LocalCacheManagerSd(String dirName) {
		super();
		init(dirName);
	}

	private void init(String dirName) {
		File sdDir = Environment.getExternalStorageDirectory();
		baseDir = new File(sdDir, dirName);
		baseDir.mkdirs();
	}
	
	@Override
	public void clearLocalCache() {
		IoUtil.delete(baseDir);
	}

	@Override
	public void delete(String key) {
		getTargetFile(key).delete();
	}

	@Override
	public byte[] getData(String key) {
		File file = getTargetFile(key);
		if (!file.exists()) {
			return null;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			return IoUtil.readAll(fis);
		} catch (Exception e) {
			Log.e("sd local cache", e.getMessage(), e);
			return null;
		} finally {
			IoUtil.closeQuietly(fis);
		}
	}

	@Override
	public void putData(String key, byte[] data) {
		if (StringUtil.isEmpty(key) || data == null) {
			return;
		}
		File file = getTargetFile(key);
		FileOutputStream fos = null;
		try {
			file.getParentFile().mkdirs();
			fos = new FileOutputStream(file);
			fos.write(data);
			fos.flush();
		} catch (Exception e) {
			Log.e("sd local cache", e.getMessage(), e);
		} finally {
			IoUtil.closeQuietly(fos);
		}
	}
	
	@Override
	public File getTargetFile(String name) {
		return new File(baseDir, name);
	}

}

