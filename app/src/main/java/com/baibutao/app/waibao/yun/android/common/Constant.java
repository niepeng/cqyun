package com.baibutao.app.waibao.yun.android.common;
/**
 * @author lsb
 *
 * @date 2012-6-8 上午10:57:52
 */
public interface Constant {
	
	public static final int ACCOUNT_LENGTH_MIN = 6;
	
	public static final int ACCOUNT_LENGTH_MAX = 30;
	
	public static final int NICK_LENGTH_MIN = 4;
	
	public static final int NICK_LENGTH_MAX = 20;
	
	public static final int PSW_LENGTH_MIN = 6;
	
	public static final int PSW_LENGTH_MAX = 20;
	
	public interface Type {
//		public static final int ACTIVITY = 1;
		
		public static final int SHOP = 2;
		
		public static final int ITEM = 3;
	}

	public static final String ARG_SHOW_TYPE = "show_type";
	public static final int SHOW_TYPE_LINEAR = 1;
	public static final int SHOW_TYPE_GRID = 2;
	public static final String ARG_AREA = "area";
	public static final String ARG_SELECTED_POSITION = "selected_position";

	public static final int TAB_INTIME_POSITION = 0;
	public static final int TAB_WARNING_POSITION = 1;
	public static final int TAB_MORE_POSITION = 2;
	
}