package com.baibutao.app.waibao.yun.android.activites.common;
public enum NavigationTabIndexEnum {
	
	IN_TIME(0),
	ALARM(1),
	SET_UP(2);
	
	private int index;
	
	private NavigationTabIndexEnum(int index) {
		this.index = index;
	}

	public int getValue() {
		return index;
	}
	
	public String getStringValue() {
		return String.valueOf(index);
	}
}

