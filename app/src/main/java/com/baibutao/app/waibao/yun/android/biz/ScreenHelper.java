package com.baibutao.app.waibao.yun.android.biz;

public enum ScreenHelper {
	
	WIDTH_240(240, 60, 30,  220, 180),
	WIDTH_320(320, 80, 60, 300, 240),
	WIDTH_480(480, 120, 90, 460, 360),
	WIDTH_540(540, 135, 100, 520, 400),
	WIDTH_640(640, 160, 120, 620, 480);
	
	private final int width;
	
	private final int smallWidth;
	
	private final int smallHeight;
	
	private final int maxWidth;
	
	private final int maxHeight;
	
	
	private ScreenHelper(int width, int smallWidth, int smallHeight, int maxWidth, int maxHeight) {
		this.width = width;
		this.smallWidth = smallWidth;
		this.smallHeight = smallHeight;
		this.maxWidth  = maxWidth;
		this.maxHeight = maxHeight;
	}
	

	public static ScreenHelper getCurrentScreenHelper(int width) {
		for (ScreenHelper temp : ScreenHelper.values()) {
			if (width <= temp.getWidth()) {
				return temp;
			}
		}
		return WIDTH_640;
	}
	
	

	public int getSmallWidth() {
		return smallWidth;
	}


	public int getSmallHeight() {
		return smallHeight;
	}


	public int getMaxWidth() {
		return maxWidth;
	}


	public int getMaxHeight() {
		return maxHeight;
	}


	public int getWidth() {
		return width;
	}

	
	
}
