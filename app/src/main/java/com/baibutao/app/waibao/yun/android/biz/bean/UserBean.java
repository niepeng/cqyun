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
	
	// -------------- normal method -----------------------

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
	
}