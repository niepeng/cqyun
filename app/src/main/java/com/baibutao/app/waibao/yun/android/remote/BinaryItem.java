package com.baibutao.app.waibao.yun.android.remote;

/**
 * @author lsb
 * 
 * @date 2012-5-29 下午11:20:05
 */
public class BinaryItem {

	public BinaryItem() {
		super();
	}

	public BinaryItem(String name, byte[] data) {
		super();
		this.name = name;
		this.data = data;
	}

	public String name;

	public byte[] data;

}
