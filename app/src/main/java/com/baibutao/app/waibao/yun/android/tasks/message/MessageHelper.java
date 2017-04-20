package com.baibutao.app.waibao.yun.android.tasks.message;

/**
 * @author lsb
 * 
 * @date 2012-5-30 上午10:26:19
 */
public class MessageHelper {

	private volatile boolean set;

	public MessageHelper() {
		super();
	}

	public MessageHelper(boolean set) {
		super();
		this.set = set;
	}

	public boolean set() {
		return set;
	}

	public void set(boolean set) {
		this.set = set;
	}

}
