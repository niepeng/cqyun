package com.baibutao.app.waibao.yun.android.biz.enums;
/**
 * @author niepeng
 *
 * @date 2012-9-14 下午7:20:44
 */
public enum CityEnums {

	hangzhou(1,"杭州"),
	shanghai(2,"上海"),
	beijing(3,"北京"),
	tianjing(4,"天津"),
	guangzhou(5,"广州"),
	shenzheng(6,"深圳");
	
	private final int value;
	
	private final String name;
	
	private CityEnums(int value, String name) {
		this.value = value;
		this.name = name;
	}
	
	public static CityEnums getEnum(int value) {
		for(CityEnums temp : CityEnums.values()) {
			if(temp.getValue() == value) {
				return temp;
			}
		}
		return null;
	}

	public int getValue() {
		return value;
	}

	public String getName() {
		return name;
	}
	
}