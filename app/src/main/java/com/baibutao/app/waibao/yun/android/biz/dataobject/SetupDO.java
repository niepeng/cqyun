package com.baibutao.app.waibao.yun.android.biz.dataobject;

public class SetupDO {

	// 及时数据更新周期，单位 秒
	private int updateIntime;

	// 报警更新时间周期：单位 秒
	private int alarmtime;

	public SetupDO() {
		this.alarmtime = 1 * 30;
		this.updateIntime = 30;
	}

	public SetupDO(int intime, int alarm) {
		this.updateIntime = intime;
		this.alarmtime = alarm;
	}

	public int getUpdateIntime() {
		return updateIntime;
	}

	public void setUpdateIntime(int updateIntime) {
		this.updateIntime = updateIntime;
	}

	public int getAlarmtime() {
		return alarmtime;
	}

	public void setAlarmtime(int alarmtime) {
		this.alarmtime = alarmtime;
	}

}
