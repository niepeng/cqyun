package com.baibutao.app.waibao.yun.android.localcache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;

import com.baibutao.app.waibao.yun.android.util.IoUtil;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

/**
 * @author lsb
 *
 * @date 2012-5-29 下午11:48:46
 */
public class LocalCacheManagerFs implements LocalCacheManager {

	private Context context;
	
	private String dirName;
	
	public LocalCacheManagerFs(Context context, String dirName) {
		super();
		this.context = context;
		this.dirName = dirName;
	}

	@Override
	public void clearLocalCache() {
		File f = context.getDir(dirName, 0);
		IoUtil.delete(f);
	}

	@Override
	public void delete(String key) {
		context.deleteFile(makeFileName(key));
	}

	@Override
	public byte[] getData(String key) {
		if (StringUtil.isEmpty(key)) {
			return null;
		}
		String filename = makeFileName(key);
		File file = context.getFileStreamPath(filename);
		if (file == null) {
			return null;
		}
		if (!file.exists()) {
			return null;
		}
		InputStream is = null;
		try {
			is = context.openFileInput(filename);
			return IoUtil.readAll(is);
		} catch(FileNotFoundException e) {
			Log.i("local cache", "file not exist:" + e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("local cache", e.getMessage(), e);
			return null;
		} finally {
			IoUtil.closeQuietly(is);
		}
	}

	@Override
	public void putData(String key, byte[] data) {
		if (StringUtil.isEmpty(key) || data == null) {
			return;
		}
		OutputStream os = null;
		try {
			os = context.openFileOutput(makeFileName(key), Context.MODE_PRIVATE);
			os.write(data);
		} catch (IOException e) {
			Log.e("local cache", e.getMessage(), e);
		} finally {
			IoUtil.closeQuietly(os);
		}
	}
	
	@Override
	public File getTargetFile(String name) {
		return context.getFileStreamPath(name);
	}

	protected String makeFileName(String key) {
		return dirName + "__" + key;
	}

}

