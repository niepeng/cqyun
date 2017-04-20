package com.baibutao.app.waibao.yun.android.biz.bean;

import java.io.Serializable;
import java.util.List;

import com.baibutao.app.waibao.yun.android.util.StringUtil;

/**
 * <p>标题: 设备</p>
 * <p>描述: </p>
 * <p>版权: lsb</p>
 * <p>创建时间: 2017年1月19日  下午3:37:28</p>
 * <p>作者：niepeng</p>
 */
public class DeviceBean implements Serializable {

	private static final long serialVersionUID = 384246528336691812L;

	// 设备唯一标识
	private String snaddr;
	
	// ac码
	private String ac;
	
	// 权限,暂不使用
	private int authority;
	
	// 设备名称
	private String devName;
	
	// 分组区域
	private String area;
	
	// 上传间隔时间
	private String devGap;
	
	// 曲线,固定暂时没有使用
	private String curve = "allLast";
	
	// -------------- extend attribute --------------------
	
	private DeviceExtendBean deviceExtendBean;
	
	private DeviceDataBean dataBean;
	
	private List<DeviceDataBean> deviceDataBeanList;
	
	private List<AlarmBean> alarmBeanList;
	
	private AlarmBean alarmBean;
	
	private String user;
	
	// -------------- normal method -----------------------
	
	public boolean isShowAlarmMsg() {
		return alarmBean != null;
	}
	
	public String getShowValue() {
		if (!StringUtil.isBlank(area)) {
			return area + "-" + devName;
		}
		return devName;
	}

	// -------------- setter/getter -----------------------

	

	public String getSnaddr() {
		return snaddr;
	}

	public void setSnaddr(String snaddr) {
		this.snaddr = snaddr;
	}

	public int getAuthority() {
		return authority;
	}

	public void setAuthority(int authority) {
		this.authority = authority;
	}

	public String getDevName() {
		return devName;
	}

	public void setDevName(String devName) {
		this.devName = devName;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCurve() {
		return curve;
	}

	public void setCurve(String curve) {
		this.curve = curve;
	}

	public DeviceDataBean getDataBean() {
		return dataBean;
	}

	public void setDataBean(DeviceDataBean dataBean) {
		this.dataBean = dataBean;
	}

	public List<AlarmBean> getAlarmBeanList() {
		return alarmBeanList;
	}

	public void setAlarmBeanList(List<AlarmBean> alarmBeanList) {
		this.alarmBeanList = alarmBeanList;
	}

	public DeviceExtendBean getDeviceExtendBean() {
		return deviceExtendBean;
	}

	public void setDeviceExtendBean(DeviceExtendBean deviceExtendBean) {
		this.deviceExtendBean = deviceExtendBean;
	}

	public String getDevGap() {
		return devGap;
	}

	public void setDevGap(String devGap) {
		this.devGap = devGap;
	}

	public String getAc() {
		return ac;
	}

	public void setAc(String ac) {
		this.ac = ac;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public List<DeviceDataBean> getDeviceDataBeanList() {
		return deviceDataBeanList;
	}

	public void setDeviceDataBeanList(List<DeviceDataBean> deviceDataBeanList) {
		this.deviceDataBeanList = deviceDataBeanList;
	}

	public AlarmBean getAlarmBean() {
		return alarmBean;
	}

	public void setAlarmBean(AlarmBean alarmBean) {
		this.alarmBean = alarmBean;
	}
	
}