package com.baibutao.app.waibao.yun.android.biz;

import android.os.Handler;
import android.widget.ImageView;

/**
 * @author lsb
 *
 * @date 2012-5-29 下午11:02:58
 */
public class LoadImgDO {

	private ImageView imageView;
	
	private String fileName;

	private String picUrl;

	private Handler handler;
	
	private int newWidth;

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public int getNewWidth() {
		return newWidth;
	}

	public void setNewWidth(int newWidth) {
		this.newWidth = newWidth;
	}
	
}
