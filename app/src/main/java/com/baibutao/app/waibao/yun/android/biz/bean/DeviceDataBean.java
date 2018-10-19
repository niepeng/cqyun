package com.baibutao.app.waibao.yun.android.biz.bean;

import java.io.Serializable;

/**
 * <p>标题: 设备普通数据</p>
 * <p>描述: </p>
 * <p>版权: lsb</p>
 * <p>创建时间: 2017年1月19日  下午3:42:15</p>
 * <p>作者：niepeng</p>
 */
public class DeviceDataBean implements Serializable {

	private static final long serialVersionUID = 6310369464846269974L;

	private String snaddr;
	
	// 湿度
	private String humi;
	
	// -1为低于阈值下限，0为正常处于阈值内，1超出最高阈值
	private int humiStatus;
	
	// 温度
	private String temp;
	
	// -1为低于阈值下限，0为正常处于阈值内，1超出最高阈值
	private int tempStatus;
	
	// 开关量
	private String in1;
	
	private String time;
	
	/*
	 * 0 -- 无异常; 
	 * 1 -- 备离线（优先级最高） 
	 * 2 -- 传感器异常（优先级第二高，一旦传感器异常，无视下列所有异常，损坏） 
	 * 3 -- 传感器未连接
	 * 4 -- 四路开关量有一路或多路处于触发状态
	 */
	private String abnormal;

	//	pow：停电来电报警状态 默认为0 触发报警为1 传感器未接入为F
	private String pow;

	//	water：浸水报警状态  默认为0 触发报警为1 传感器未接入为F
	private String water;

	//	smoke：烟感报警状态 默认为0 触发报警为1 传感器未接入为F
	private String smoke;

	//	door： 门磁报警状态 默认为0 触发报警为1 传感器未接入为F
	private String door;

	//	bat: 电池电量  百分比
	private String bat;



	// -------------- extend attribute --------------------
	
	private String startTime;
	
	private String endTime;
	
	private String rangeTime;

	// -------------- normal method -----------------------

	public boolean kaiguanNormal(String value) {
		return "0".equals(value);
	}

	public boolean kaiguanAlarm(String value) {
		return "1".equals(value);
	}

	public boolean kaiguanNotConnection(String value) {
		return "F".equals(value);
	}
	
	public boolean isSuccess() {
		return "0".equals(abnormal);
	}
	
	public boolean isNotConnection() {
		return "3".equals(abnormal);
	}

	public boolean isKaiguanAlarm() {
		return "4".equals(abnormal);
	}

	public boolean isOffline() {
		return "1".equals(abnormal);
	}
	
	public String showStatus() {
		StringBuilder sb = new StringBuilder();
		if(tempStatus < 0) {
			sb.append("温度偏低");
		} else if(tempStatus == 0) {
			sb.append("温度正常");
		}else if(tempStatus > 0) {
			sb.append("温度偏高");
		}
		
		sb.append(",");
		
		if(humiStatus < 0) {
			sb.append("湿度偏低");
		} else if(humiStatus == 0) {
			sb.append("湿度正常");
		}else if(humiStatus > 0) {
			sb.append("湿度偏高");
		}
		return sb.toString();
	}
	
	// -------------- setter/getter -----------------------
	
	public String getSnaddr() {
		return snaddr;
	}

	public void setSnaddr(String snaddr) {
		this.snaddr = snaddr;
	}

	public String getHumi() {
		return humi;
	}

	public void setHumi(String humi) {
		this.humi = humi;
	}

	public int getHumiStatus() {
		return humiStatus;
	}

	public void setHumiStatus(int humiStatus) {
		this.humiStatus = humiStatus;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public int getTempStatus() {
		return tempStatus;
	}

	public void setTempStatus(int tempStatus) {
		this.tempStatus = tempStatus;
	}

	public String getIn1() {
		return in1;
	}

	public void setIn1(String in1) {
		this.in1 = in1;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAbnormal() {
		return abnormal;
	}

	public void setAbnormal(String abnormal) {
		this.abnormal = abnormal;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getRangeTime() {
		return rangeTime;
	}

	public void setRangeTime(String rangeTime) {
		this.rangeTime = rangeTime;
	}

	public String getPow() {
		return pow;
	}

	public void setPow(String pow) {
		this.pow = pow;
	}

	public String getWater() {
		return water;
	}

	public void setWater(String water) {
		this.water = water;
	}

	public String getSmoke() {
		return smoke;
	}

	public void setSmoke(String smoke) {
		this.smoke = smoke;
	}

	public String getDoor() {
		return door;
	}

	public void setDoor(String door) {
		this.door = door;
	}

	public String getBat() {
		return bat;
	}

	public void setBat(String bat) {
		this.bat = bat;
	}
}