/**
 * 
 */
package com.baibutao.app.waibao.yun.android.biz.dataobject;

import java.io.Serializable;

import com.baibutao.app.waibao.yun.android.biz.dataobject.constant.TeacherConstant;

/**
 * @author niepeng
 *
 * @date 2012-9-13 下午4:54:27
 */
public class TeacherDO implements Serializable, TeacherConstant {

	private static final long serialVersionUID = -9177027093036447803L;

	private long id;
	
	// 名称
	private String name;
	
	// 职位
	private String postion;
	

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

	public String getPostion() {
		return postion;
	}

	public void setPostion(String postion) {
		this.postion = postion;
	}
	
}
