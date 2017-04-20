package com.baibutao.app.waibao.yun.android.biz.dataobject;

import com.baibutao.app.waibao.yun.android.biz.dataobject.constant.EquipmentConstant;

public class EquipmentDO implements EquipmentConstant {

	private long id;
	
	private String name;
	
	private String area;
	
	// EqutypeEnum 
	private int equType;
	
	private int temperature;
	
	private int humidity;
	
	private int shine;
	
	private int co2;
	
	
	// ================== normal method ===================
	
	// 区域名称  + 设备名称
	public String showAreaName() {
		return area + " - " + name;
	}
	
	
	// ================ setter/getter =====================
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTemperature() {
		return temperature;
	}

	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	public int getHumidity() {
		return humidity;
	}

	public void setHumidity(int humidity) {
		this.humidity = humidity;
	}


	public String getArea() {
		return area;
	}


	public void setArea(String area) {
		this.area = area;
	}


	public int getEquType() {
		return equType;
	}


	public void setEquType(int equType) {
		this.equType = equType;
	}

	public int getShine() {
		return shine;
	}

	public void setShine(int shine) {
		this.shine = shine;
	}

	public int getCo2() {
		return co2;
	}

	public void setCo2(int co2) {
		this.co2 = co2;
	}
	
}
