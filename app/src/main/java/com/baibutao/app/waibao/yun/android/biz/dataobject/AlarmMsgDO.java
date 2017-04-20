package com.baibutao.app.waibao.yun.android.biz.dataobject;

import java.util.Date;

import com.baibutao.app.waibao.yun.android.biz.dataobject.constant.AlarmMsgConstant;

public class AlarmMsgDO implements AlarmMsgConstant{

	private long id;

	// 首次报警时间
	private Date alarmTime;
		
	// 最近报警时间
	private Date lastAlarmTime;

	private String reason;
	
	private String areaName;
	
	private String showName;
	

	// ================== normal method ===================
	

	// ================ setter/getter =====================

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public Date getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(Date alarmTime) {
		this.alarmTime = alarmTime;
	}
	
	public Date getLastAlarmTime() {
		return lastAlarmTime;
	}

	public void setLastAlarmTime(Date lastAlarmTime) {
		this.lastAlarmTime = lastAlarmTime;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

}
