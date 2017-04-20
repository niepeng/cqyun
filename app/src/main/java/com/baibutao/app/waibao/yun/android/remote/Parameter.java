package com.baibutao.app.waibao.yun.android.remote;

/**
 * @author lsb
 * 
 * @date 2012-5-29 下午11:20:24
 */
public class Parameter {
	public Parameter() {
		super();
	}

	public Parameter(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	@Override
	public String toString() {
		return "Parameter [name=" + name + ", value=" + value + "]";
	}

	public String name;

	public String value;

}
