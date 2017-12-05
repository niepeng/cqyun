package com.baibutao.app.waibao.yun.android.biz.bean;

/**
 * <p>标题: </p>
 * <p>描述: </p>
 * <p>版权: lsb</p>
 * <p>创建时间: 2017年1月19日  下午2:30:17</p>
 * <p>作者：niepeng</p>
 */
public class UserBean {

	private String user;
	
	private String password;
	
	private String mail;
	

	// -------------- extend attribute --------------------

	private String newPsw;
	
	private String newPsw2;

	// 1 有权限，0没有权限
	private String authority;


	// 报警总开关
	private String allAlarmStatus;

	// App推送 的 离线报警
	private String state_push;

	// 短信通知 的 离线报警
	private String state_msm;

	// 电话通知 的 离线报警
	private String state_call;

	// 邮件通知 的 离线报警
	private String state_mail;


	// App推送 的 数据异常报警
	private String data_push;

	// 短信通知 的 数据异常报警
	private String data_msm;

	// 电话通知 的 数据异常报警
	private String data_call;

	// 邮件通知 的 数据异常报警
	private String data_mail;

	
	// -------------- normal method -----------------------

	public boolean hasAuth() {
		return "1".equals(authority);
	}

	// -------------- setter/getter -----------------------


	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPsw() {
		return newPsw;
	}

	public void setNewPsw(String newPsw) {
		this.newPsw = newPsw;
	}

	public String getNewPsw2() {
		return newPsw2;
	}

	public void setNewPsw2(String newPsw2) {
		this.newPsw2 = newPsw2;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public String getAllAlarmStatus() {
		return allAlarmStatus;
	}

	public void setAllAlarmStatus(String allAlarmStatus) {
		this.allAlarmStatus = allAlarmStatus;
	}

	public String getData_mail() {
		return data_mail;
	}

	public void setData_mail(String data_mail) {
		this.data_mail = data_mail;
	}

	public String getState_push() {
		return state_push;
	}

	public void setState_push(String state_push) {
		this.state_push = state_push;
	}

	public String getState_msm() {
		return state_msm;
	}

	public void setState_msm(String state_msm) {
		this.state_msm = state_msm;
	}

	public String getState_call() {
		return state_call;
	}

	public void setState_call(String state_call) {
		this.state_call = state_call;
	}

	public String getState_mail() {
		return state_mail;
	}

	public void setState_mail(String state_mail) {
		this.state_mail = state_mail;
	}

	public String getData_push() {
		return data_push;
	}

	public void setData_push(String data_push) {
		this.data_push = data_push;
	}

	public String getData_msm() {
		return data_msm;
	}

	public void setData_msm(String data_msm) {
		this.data_msm = data_msm;
	}

	public String getData_call() {
		return data_call;
	}

	public void setData_call(String data_call) {
		this.data_call = data_call;
	}
}