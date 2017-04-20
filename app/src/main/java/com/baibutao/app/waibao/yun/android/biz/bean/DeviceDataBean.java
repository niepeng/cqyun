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
	 */
	private String abnormal;
	
	// -------------- extend attribute --------------------
	
	private String startTime;
	
	private String endTime;
	
	private String rangeTime;

	// -------------- normal method -----------------------
	
	public boolean isSuccess() {
		return "0".equals(abnormal);
	}
	
	public boolean isNotConnection() {
		return "3".equals(abnormal);
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
	
}