package com.baibutao.app.waibao.yun.android.biz.dataobject;

import com.baibutao.app.waibao.yun.android.biz.dataobject.constant.AreaConstant;

public class AreaDO implements AreaConstant {

	private long id;

	private String name;

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

}
