/**
 * 
 */
package com.baibutao.app.waibao.yun.android.biz.dataobject;

import java.io.Serializable;

/**
 * @author niepeng
 *
 * @date 2012-9-17 下午5:39:29
 */
public class Person implements Serializable {
	
	private static final long serialVersionUID = -9007604076718943619L;

	private long id;
	
	private String name;
	
	private String phone;

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	

}
