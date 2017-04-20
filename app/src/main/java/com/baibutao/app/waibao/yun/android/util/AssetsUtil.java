package com.baibutao.app.waibao.yun.android.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;

/**
 * @author lsb
 *
 * @date 2012-5-30 下午08:03:14
 */
public class AssetsUtil {
	/**
	 * @param context
	 * @param name
	 * @return
	 */
	public static InputStream getAssertAsStream(Context context, String name) {
		AssetManager assetManager = context.getAssets();
		try {
			return assetManager.open(name);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getAssertAsString(Context context, String name, String charset) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			IoUtil.ioAndClose(getAssertAsStream(context, name), bos);
			return new String(bos.toByteArray(), charset);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getAssertAsString(Context context, String name) {
		return getAssertAsString(context, name, "gbk");
	}

}
