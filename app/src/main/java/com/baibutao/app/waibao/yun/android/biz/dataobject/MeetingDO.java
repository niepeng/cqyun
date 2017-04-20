package com.baibutao.app.waibao.yun.android.biz.dataobject;

import java.io.Serializable;
import java.util.Date;

public class MeetingDO implements Serializable, MeetingConstant {

	private static final long serialVersionUID = -9218232284455016666L;
	
	private long id;
	
	private String title;
	
	private String imageUrl;
	
	private Date startTime;
	
	private Date endTime;
	
	private String city;
	
	private String location;
	
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	

}
