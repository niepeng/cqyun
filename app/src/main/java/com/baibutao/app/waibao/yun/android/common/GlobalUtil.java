package com.baibutao.app.waibao.yun.android.common;

import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;



/**
 * @author lsb
 *
 * @date 2012-5-30 上午11:16:29
 */
public class GlobalUtil {
	
	private static EewebApplication cardApplication;

	private static float density = 1.0f;
	
	public static EewebApplication getCardApplication() {
		return cardApplication;
	}
	
	public static void init(EewebApplication cardApplication) {
		GlobalUtil.cardApplication = cardApplication;
		density = cardApplication.getResources().getDisplayMetrics().density;
	}
	
	public static int pixelToDip(int pixel) {
		return (int)(pixel / density);
	}
	
	public static int dipToPixel(int dip) {
		return (int)(dip * density);
	}
	
}
