package com.baibutao.app.waibao.yun.android.common;

/**
 * @author lsb
 *
 * @date 2012-5-29 下午11:22:11
 */
public interface ProgressCallback {

	void onSetMaxSize(int maxSize);

	void onProgress(int dealedSize);

	void onFinish();

	void onException(Exception e);
	
}
