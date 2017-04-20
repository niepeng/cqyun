package com.baibutao.app.waibao.yun.android.activites.common;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;
import com.baibutao.app.waibao.yun.android.common.ProgressCallback;
import com.baibutao.app.waibao.yun.android.util.IoUtil;

/**
 * @author lsb
 *
 * @date 2012-5-30 上午10:35:37
 */
public class DownLoader {
	private String url;
	
	private String fileName;
	
	public DownLoader(String url, String fileName, ProgressCallback downLoadCallback) {
		super();
		this.url = url;
		this.fileName = fileName;
		this.downLoadCallback = downLoadCallback;
	}

	private ProgressCallback downLoadCallback;
	
	public void asyDownload(EewebApplication cardApplication) {
		cardApplication.asyCall(new Runnable() {
			@Override
			public void run() {
				download();
			}
		});
	}
	
	private void download() {
		try {
			URL connUrl = new URL(url);
			URLConnection conn = (URLConnection)connUrl.openConnection();
			int maxSize = conn.getContentLength();
			if (downLoadCallback != null) {
				downLoadCallback.onSetMaxSize(maxSize);
			}
			InputStream is = conn.getInputStream();
			OutputStream os = getFileStream();
			int downloadSize = 0;
			byte[] data = new byte[LEN];
			while (true) {
				int len = is.read(data, 0, LEN);
				if (len < 0) {
					break;
				}
				os.write(data, 0, len);
				downloadSize += len;
				if (downLoadCallback != null) {
					downLoadCallback.onProgress(downloadSize);
				}
			}
			IoUtil.closeQuietly(is);
			IoUtil.closeQuietly(os);
			if (downLoadCallback != null) {
				downLoadCallback.onFinish();
			}
		} catch (Exception e) {
			Log.e("download", e.getMessage(), e);
			if (downLoadCallback != null) {
				downLoadCallback.onException(e);
			}
		}
		
	}
	
	private static final int LEN = 1024;
	
	private OutputStream getFileStream() throws FileNotFoundException {
		return new FileOutputStream(fileName);
	}

}
