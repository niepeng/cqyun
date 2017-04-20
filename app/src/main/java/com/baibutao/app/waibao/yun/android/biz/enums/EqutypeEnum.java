package com.baibutao.app.waibao.yun.android.biz.enums;
public enum EqutypeEnum {

	HUMI(1, "湿度"),
	TEMP(2, "温度"),
	TEMP_HUMI(3,"温湿度");
	
	private final int id;
	
	private final String meaning;

	private EqutypeEnum(int id, String meaning) {
		this.id = id;
		this.meaning = meaning;
	}

	public int getId() {
		return id;
	}

	public String getMeaning() {
		return meaning;
	}
	
} 
