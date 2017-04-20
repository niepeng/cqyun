/**
 * 
 */
package com.baibutao.app.waibao.yun.android.biz.dataobject;

import java.io.Serializable;

import com.baibutao.app.waibao.yun.android.biz.dataobject.constant.CatConstant;

/**
 * @author niepeng
 *
 * @date 2012-10-17 上午10:04:48
 */
public class CatDO implements Serializable, CatConstant {
	
	private static final long serialVersionUID = -948236265274298061L;

	private long id; 
	
	private String name;
	
	private String imageUrl;

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

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
}
