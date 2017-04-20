package com.baibutao.app.waibao.yun.android.common;

import android.util.DisplayMetrics;

import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;

/**
 * @author lsb
 * 
 * @date 2012-6-4 下午04:19:39
 */
public class MobileUseInfo {

	private String mobile;

	private String netWork;

	private int width;
	 
	private int height;
	 

	private static MobileUseInfo mobileConfig = new MobileUseInfo();

	private EewebApplication cardApplication;

	private MobileUseInfo() {

	}

	public static MobileUseInfo getMobileUseInfo() {
		return mobileConfig;
	}

	public void init(EewebApplication cardApplication) {
		this.cardApplication = cardApplication;
		this.mobile = android.os.Build.MODEL + "/" + android.os.Build.VERSION.SDK + "/" + android.os.Build.VERSION.RELEASE;
		this.netWork = AndroidUtil.getNetwork(cardApplication);
		 DisplayMetrics dm =  this.cardApplication.getResources().getDisplayMetrics();
		 width = dm.widthPixels;
		 height = dm.heightPixels;
	}
	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getNetWork() {
		return netWork;
	}

	public void setNetWork(String netWork) {
		this.netWork = netWork;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
}
