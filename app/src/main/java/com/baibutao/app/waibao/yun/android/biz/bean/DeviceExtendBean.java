package com.baibutao.app.waibao.yun.android.biz.bean;

import java.io.Serializable;

/**
 * <p>标题: 设备的扩展信息</p>
 * <p>描述: </p>
 * <p>版权: lsb</p>
 * <p>创建时间: 2017年1月20日  下午12:22:21</p>
 * <p>作者：niepeng</p>
 */
public class DeviceExtendBean implements Serializable {
	
	private static final long serialVersionUID = -4967215127510314319L;

	private String snaddr;

	// 最高温度
	private String maxTemp;
	
	// 最低温度
	private String minTemp;
	
	// 最高湿度
	private String maxHumi;
	
	// 最低湿度
	private String minHumi;
	
	// 温度回差
	private String tempHC;
	
	// 湿度回差
	private String humiHC;
	
	
	
	// -------------- extend attribute --------------------

	// -------------- normal method -----------------------
	
	public boolean isDataChange(DeviceExtendBean deviceExtendBean) {
		if(deviceExtendBean == null) {
			return false;
		}
		
		if(!maxHumi.equals(deviceExtendBean.getMaxHumi())) {
			return true;
		}
		
		if(!minHumi.equals(deviceExtendBean.getMinHumi())) {
			return true;
		}
		
		if(!humiHC.equals(deviceExtendBean.getHumiHC())) {
			return true;
		}
		
		if(!maxTemp.equals(deviceExtendBean.getMaxTemp())) {
			return true;
		}
		
		if(!minTemp.equals(deviceExtendBean.getMinTemp())) {
			return true;
		}
		
		if(!tempHC.equals(deviceExtendBean.getTempHC())) {
			return true;
		}

		return false;
	}

	// -------------- setter/getter -----------------------

	public String getMaxTemp() {
		return maxTemp;
	}

	public void setMaxTemp(String maxTemp) {
		this.maxTemp = maxTemp;
	}

	public String getMinTemp() {
		return minTemp;
	}

	public void setMinTemp(String minTemp) {
		this.minTemp = minTemp;
	}

	public String getMaxHumi() {
		return maxHumi;
	}

	public void setMaxHumi(String maxHumi) {
		this.maxHumi = maxHumi;
	}

	public String getMinHumi() {
		return minHumi;
	}

	public void setMinHumi(String minHumi) {
		this.minHumi = minHumi;
	}

	public String getTempHC() {
		return tempHC;
	}

	public void setTempHC(String tempHC) {
		this.tempHC = tempHC;
	}

	public String getHumiHC() {
		return humiHC;
	}

	public void setHumiHC(String humiHC) {
		this.humiHC = humiHC;
	}

	public String getSnaddr() {
		return snaddr;
	}

	public void setSnaddr(String snaddr) {
		this.snaddr = snaddr;
	}
	
}
