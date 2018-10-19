package com.baibutao.app.waibao.yun.android.biz.bean;

import java.io.Serializable;

/**
 * <p>标题: 报警信息</p>
 * <p>描述: </p>
 * <p>版权: lsb</p>
 * <p>创建时间: 2017年1月19日  下午8:26:09</p>
 * <p>作者：niepeng</p>
 */
public class AlarmBean implements Serializable {

	private static final long serialVersionUID = -7173358265352967918L;

//	private String msg;
//
//	// 1:温度过高;2:温度过低;3:湿度过高;4:湿度过低;5:开关报警;6:设备离线;7:传感器异常;8:传感器未连接
//	private String type;
//
//	private String alarmTime;
//
//	private String beginEndMark;

	/**
	 * 报警id
	 */
	private String alarmId;

	/**
	 * 报警内容
	 */
	private String info;

	/**
	 * 是否处理的标识位，如果已处理，handle=1，如果未处理handle=0
	 */
	private String handle;

	/**
	 * 第一次报警的时间
	 */
	private String startTime;

	/**
	 * 报警结束时间，如果设备尚未结束报警，该参数为最后一次上传报警信息的时间
	 */
	private String endTime;


	/**
	 * 报警状态 0 正在报警, 1 已结束
	 */
	private String alarmState;

	/**
	 * 用户备注信息
	 */
	private String additionInfo;

	/**
	 * 处理用户
	 */
	private String handleUser;




	/**
	 * 只有部分接口有内容：
	 * getUnresolvedError 这个接口有值
	 */
	private String devName;
	/**
	 * 只有部分接口有内容：
	 * getUnresolvedError 这个接口有值
	 */
	private String snaddr;

	// -------------- extend attribute --------------------





	// -------------- normal method -----------------------

	public boolean isAlarmEnd() {
		return "1".equals(alarmState);
	}


	// -------------- setter/getter -----------------------

//	public String getMsg() {
//		return msg;
//	}
//
//	public void setMsg(String msg) {
//		this.msg = msg;
//	}
//
//	public String getType() {
//		return type;
//	}
//
//	public void setType(String type) {
//		this.type = type;
//	}
//
//	public String getAlarmTime() {
//		return alarmTime;
//	}
//
//	public void setAlarmTime(String alarmTime) {
//		this.alarmTime = alarmTime;
//	}
//
//	public String getBeginEndMark() {
//		return beginEndMark;
//	}
//
//	public void setBeginEndMark(String beginEndMark) {
//		this.beginEndMark = beginEndMark;
//	}

	public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
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

	public String getAlarmState() {
		return alarmState;
	}

	public void setAlarmState(String alarmState) {
		this.alarmState = alarmState;
	}

	public String getAdditionInfo() {
		return additionInfo;
	}

	public void setAdditionInfo(String additionInfo) {
		this.additionInfo = additionInfo;
	}

	public String getHandleUser() {
		return handleUser;
	}

	public void setHandleUser(String handleUser) {
		this.handleUser = handleUser;
	}

	public String getDevName() {
		return devName;
	}

	public void setDevName(String devName) {
		this.devName = devName;
	}

	public String getSnaddr() {
		return snaddr;
	}

	public void setSnaddr(String snaddr) {
		this.snaddr = snaddr;
	}
}