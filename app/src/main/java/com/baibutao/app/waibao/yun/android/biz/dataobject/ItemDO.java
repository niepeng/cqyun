/**
 * 
 */
package com.baibutao.app.waibao.yun.android.biz.dataobject;

import java.io.Serializable;
import java.util.Date;

import com.baibutao.app.waibao.yun.android.biz.dataobject.constant.ItemConstant;

/**
 * @author niepeng
 *
 * @date 2012-10-11 下午12:31:07
 */
public class ItemDO implements Serializable, ItemConstant {

	private static final long serialVersionUID = -4519361576084203436L;
	
	private long id;
	
	// 标题
	private String name;
	
	// 主图
	private String image;
	
	// 推荐理由
	private String reason;
	
	// 感兴趣人数
	private int interest;
	
	// 参与人数
	private int join;
	
	private int longitude;
	
	private int latitude;
	
	// 原价
	private int oldPrice;
	
	// 现价
	private int newPrice;
	
	// 地址
	private String address;
	
	private Date startTime;
	
	private Date endTime;
	
	// 合作伙伴
	private String cooperation;
	
	// 活动照片s
	private String photos;
	
	// 活动内容
	private String content;
	
	private int type;
	
	// 是否已经收藏
	private boolean isCollectioned;
	
	private long collectionId;
	
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

	public String getCooperation() {
		return cooperation;
	}

	public void setCooperation(String cooperation) {
		this.cooperation = cooperation;
	}

	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public int getInterest() {
		return interest;
	}

	public void setInterest(int interest) {
		this.interest = interest;
	}

	public int getJoin() {
		return join;
	}

	public void setJoin(int join) {
		this.join = join;
	}

	public int getOldPrice() {
		return oldPrice;
	}

	public void setOldPrice(int oldPrice) {
		this.oldPrice = oldPrice;
	}

	public int getNewPrice() {
		return newPrice;
	}

	public void setNewPrice(int newPrice) {
		this.newPrice = newPrice;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isCollectioned() {
		return isCollectioned;
	}

	public void setCollectioned(boolean isCollectioned) {
		this.isCollectioned = isCollectioned;
	}

	public long getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(long collectionId) {
		this.collectionId = collectionId;
	}
	
}
