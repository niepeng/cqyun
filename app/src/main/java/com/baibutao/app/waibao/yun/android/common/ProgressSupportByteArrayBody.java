package com.baibutao.app.waibao.yun.android.common;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.entity.mime.content.ByteArrayBody;

/**
 * @author lsb
 *
 * @date 2012-5-29 下午11:23:10
 */
public class ProgressSupportByteArrayBody extends ByteArrayBody {
	
	private static final int BUF_LEN = 1024 * 4;
	
	private ProgressCallbackHelper progressCallbackHelper;
	
	private ProgressCallback progressCallback;

	public ProgressSupportByteArrayBody(byte[] data, String mimeType, String filename) {
		super(data, mimeType, filename);
	}

	public ProgressSupportByteArrayBody(byte[] data, String filename) {
		super(data, filename);
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		if (progressCallback == null) {
			super.writeTo(out);
		} else {
			if (data == null) {
				return;
			}
			int maxLength = data.length;
			int pos = 0;
			try {
				while (true) {
					int leftSize = maxLength - pos;
					int toWriteSize = Math.min(leftSize, BUF_LEN);
					if (toWriteSize <= 0) {
						break;
					}
					out.write(data, pos, toWriteSize);
					pos += toWriteSize;
					progressCallbackHelper.addProcess(toWriteSize);
				}
				progressCallbackHelper.releaseCount();
				if (progressCallbackHelper.isFinish()) {
					progressCallback.onFinish();
				}
			} catch (IOException e) {
				progressCallback.onException(e);
				throw e;
			}
		}
	}

	public ProgressCallback getProgressCallback() {
		return progressCallback;
	}

	public void setProgressCallback(ProgressCallback progressCallback) {
		this.progressCallback = progressCallback;
	}
	
	public int getMaxLength() {
		return data.length;
	}

	public ProgressCallbackHelper getProgressCallbackHelper() {
		return progressCallbackHelper;
	}

	public void setProgressCallbackHelper(ProgressCallbackHelper progressCallbackHelper) {
		this.progressCallbackHelper = progressCallbackHelper;
	}

}
