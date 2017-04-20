package com.baibutao.app.waibao.yun.android.activites.common;

import com.baibutao.app.waibao.yun.android.activites.AlarmActivity;
import com.baibutao.app.waibao.yun.android.activites.InTimeActivity;
import com.baibutao.app.waibao.yun.android.activites.SetupActivity;

public enum TabFlushEnum {
	IN_TIME(0, InTimeActivity.class),
	ALARM(1,AlarmActivity.class),
	MORE(2, SetupActivity.class);
	
	private int index;
	
	private Class<?> clazz;
	
	private boolean isFlush;
	
	private TabFlushEnum(int index, Class<?> claz) {
		this.index = index;
		this.clazz = claz;
		this.isFlush = false;
	}
	
	public int getIndex() {
		return index;
	}
	
	public TabFlushEnum getEnum(int index) {
		return index < (TabFlushEnum.values().length - 1) ? TabFlushEnum.values()[index] : null;
	}
	
	public static int getEnumIndex(Class<?> cla) {
		int index = -1;
		for(TabFlushEnum temp : TabFlushEnum.values()) {
			if(temp.getTabActivity() == cla) {
				index = temp.getIndex();
				break;
			}
		}
		return index;
	}
	
	public static String[] getClassName() {
		String[] classNames = new String[TabFlushEnum.values().length];
		for(int i= 0; i< TabFlushEnum.values().length; i++) {
			classNames[i] = TabFlushEnum.values().getClass().getName();
		}
		return classNames;
	}
	
	
	public Class<?> getTabActivity() {
		return clazz;
	}
	
	public boolean isFlush() {
		return isFlush;
	}
	
	public void setFlush(boolean flag) {
		isFlush = flag;
	}
}
