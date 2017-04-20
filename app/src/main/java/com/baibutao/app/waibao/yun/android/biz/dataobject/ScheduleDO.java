/**
 * 
 */
package com.baibutao.app.waibao.yun.android.biz.dataobject;

import java.io.Serializable;
import java.util.Date;

import com.baibutao.app.waibao.yun.android.biz.dataobject.constant.ScheduleConstant;

/**
 * @author niepeng
 *
 * @date 2012-9-13 下午5:19:14
 */
public class ScheduleDO implements Serializable, ScheduleConstant {

	private static final long serialVersionUID = -6962894653985688899L;

	private long id;
	
	private Date startTime;
	
	private Date endTime;
	
	// 主题
	private String title;
	
	// 讲师s
	private String teachers;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTeachers() {
		return teachers;
	}

	public void setTeachers(String teachers) {
		this.teachers = teachers;
	}
	
	
}
