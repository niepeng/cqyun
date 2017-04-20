package com.baibutao.app.waibao.yun.android.biz;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.baibutao.app.waibao.yun.android.biz.dataobject.AlarmMsgDO;
import com.baibutao.app.waibao.yun.android.biz.dataobject.AreaDO;
import com.baibutao.app.waibao.yun.android.biz.dataobject.CatDO;
import com.baibutao.app.waibao.yun.android.biz.dataobject.EquipmentDO;
import com.baibutao.app.waibao.yun.android.common.Tuple;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.DateUtil;


/**
 * @author lsb
 *
 * @date 2012-6-29 下午12:46:22
 */
public class JSONHelper {
	
	public static List<EquipmentDO> json2ItemList(JSONObject jsonObject) {
		List<EquipmentDO> result = CollectionUtil.newArrayList();
		try {
//			if (jsonObject == null) {
//				return Tuple.tuple(null, result);
//			}
//			int pageSize = 0;
//			if(jsonObject.has("pageSize")) {
//				pageSize = jsonObject.getInt("pageSize");
//			}
			JSONArray data = jsonObject.getJSONArray("data");
			if (data == null) {
				return result;
			}

			for (int len = data.length(), i = 0; i < len; ++i) {
				EquipmentDO temp = json2ItemDO((JSONObject) data.get(i));
				if (temp != null) {
					result.add(temp);
				}
			}
			return result;
		} catch (Exception e) {
			Log.e("item list error", e.getMessage(), e);
			return result;
		}
	}
	
	public static List<AreaDO> json2AreaList(JSONObject jsonObject) {
		List<AreaDO> result = CollectionUtil.newArrayList();
		try {
			JSONArray data = jsonObject.getJSONArray("data");
			if (data == null) {
				return result;
			}

			for (int len = data.length(), i = 0; i < len; ++i) {
				AreaDO temp = json2AreaDO((JSONObject) data.get(i));
				if (temp != null) {
					result.add(temp);
				}
			}
			return result;
		} catch (Exception e) {
			Log.e("area list error", e.getMessage(), e);
		}
		return result;
	}
	
	public static Tuple.Tuple2<List<AlarmMsgDO>, Date>  json2AlarmList(JSONObject jsonObject) {
		List<AlarmMsgDO> result = CollectionUtil.newArrayList();
		Date time = null;
		try {
			JSONArray data = jsonObject.getJSONArray("data");
			if (data == null) {
				return  Tuple.tuple(result, time);
			}
			
			if(jsonObject.has("time")) {
				time = DateUtil.parse(jsonObject.getString("time"), DateUtil.DATE_FMT);
			}
				

			for (int len = data.length(), i = 0; i < len; ++i) {
				AlarmMsgDO temp = json2AlarmDO((JSONObject) data.get(i));
				if (temp != null) {
					result.add(temp);
				}
			}
			return Tuple.tuple(result, time);
		} catch (Exception e) {
			Log.e("alarm list error", e.getMessage(), e);
		}
		return Tuple.tuple(result, time);
	}
	
	private static AlarmMsgDO json2AlarmDO(JSONObject jsonObject) {
		AlarmMsgDO alarmMsgDO = new AlarmMsgDO();
		try {

			if (jsonObject.has(AlarmMsgDO.ID)) {
				alarmMsgDO.setId(jsonObject.getLong(AlarmMsgDO.ID));
			}

			if (jsonObject.has(AlarmMsgDO.ALARM_TIME)) {
				alarmMsgDO.setAlarmTime(DateUtil.parse(jsonObject.getString(AlarmMsgDO.ALARM_TIME)));
			}

			if (jsonObject.has(AlarmMsgDO.LAST_ALARM_TIME)) {
				alarmMsgDO.setLastAlarmTime(DateUtil.parse(jsonObject.getString(AlarmMsgDO.LAST_ALARM_TIME)));
			}

			if (jsonObject.has(AlarmMsgDO.REASON)) {
				alarmMsgDO.setReason(jsonObject.getString(AlarmMsgDO.REASON));
			}

			if (jsonObject.has(AlarmMsgDO.AREA_NAME)) {
				alarmMsgDO.setAreaName(jsonObject.getString(AlarmMsgDO.AREA_NAME));
			}

			if (jsonObject.has(AlarmMsgDO.SHOW_NAME)) {
				alarmMsgDO.setShowName(jsonObject.getString(AlarmMsgDO.SHOW_NAME));
			}

		} catch (Exception e) {
			Log.e("alarmMsgDO", e.getMessage(), e);
			return null;
		}
		return alarmMsgDO;
	}

	private static AreaDO json2AreaDO(JSONObject jsonObject) {
		AreaDO areaDO = new AreaDO();
		try {
			if (jsonObject.has(AreaDO.ID)) {
				areaDO.setId(jsonObject.getLong(AreaDO.ID));
			}
			if (jsonObject.has(AreaDO.NAME)) {
				areaDO.setName(jsonObject.getString(AreaDO.NAME));
			}

			// itemDO.setStartTime(DateUtil.parse(jsonObject.getString(ItemDO.START_TIME)));
		} catch (Exception e) {
			Log.e("areaDO", e.getMessage(), e);
			return null;
		}
		return areaDO;
	}

	public static List<CatDO> json2CatList(JSONObject jsonObject) {
		List<CatDO> result = CollectionUtil.newArrayList();
		try {
			if (jsonObject == null) {
				return result;
			}
			JSONArray data = jsonObject.getJSONArray("data");
			if (data == null) {
				return result;
			}
			for (int len = data.length(), i = 0; i < len; i++) {
				CatDO temp = json2CatDO(data.getJSONObject(i));
				if (temp != null) {
					result.add(temp);
				}
			}
		} catch (Exception e) {
			Log.e("cat list error", e.getMessage(), e);
		}
		return result;
	}

	
	private static CatDO json2CatDO(JSONObject jsonObject) {
		CatDO catDO = new CatDO();
		try {
			if (jsonObject.has(CatDO.ID)) {
				catDO.setId(jsonObject.getLong(CatDO.ID));
			}
			if (jsonObject.has(CatDO.NAME)) {
				catDO.setName(jsonObject.getString(CatDO.NAME));
			}
			if (jsonObject.has(CatDO.IMAGE)) {
				catDO.setImageUrl(jsonObject.getString(CatDO.IMAGE));
			}
		} catch (Exception e) {
			Log.e("CatDO", e.getMessage(), e);
			return null;
		}
		return catDO;
	}
	

	private static EquipmentDO json2ItemDO(JSONObject jsonObject) {
		EquipmentDO equipmentDO = new EquipmentDO();
		try {
			if (jsonObject.has(EquipmentDO.ID)) {
				equipmentDO.setId(jsonObject.getLong(EquipmentDO.ID));
			}
			if (jsonObject.has(EquipmentDO.HUMIDITY)) {
				equipmentDO.setHumidity(jsonObject.getInt(EquipmentDO.HUMIDITY));
			}
			if (jsonObject.has(EquipmentDO.SHINE)) {
				equipmentDO.setShine(jsonObject.getInt(EquipmentDO.SHINE));
			}
			if (jsonObject.has(EquipmentDO.CO2)) {
				equipmentDO.setCo2(jsonObject.getInt(EquipmentDO.CO2));
			}
			if (jsonObject.has(EquipmentDO.TEMPERATURE)) {
				equipmentDO.setTemperature(jsonObject.getInt(EquipmentDO.TEMPERATURE));
			}
			if (jsonObject.has(EquipmentDO.NAME)) {
				equipmentDO.setName(jsonObject.getString(EquipmentDO.NAME));
			}
			if(jsonObject.has(EquipmentDO.EQU_TYPE)) {
				equipmentDO.setEquType(jsonObject.getInt(EquipmentDO.EQU_TYPE));
			}
			// itemDO.setStartTime(DateUtil.parse(jsonObject.getString(ItemDO.START_TIME)));
		} catch (Exception e) {
			Log.e("equipmentDO", e.getMessage(), e);
			return null;
		}
		return equipmentDO;
	}


//	private static FatherChildrenDO getFatherChildren(JSONObject item) {
//		FatherChildrenDO fatherChildrenDO = new FatherChildrenDO(true);
//		try {
//			if (item.has(FatherChildrenDO.ID)) {
//				fatherChildrenDO.setId(item.getLong(FatherChildrenDO.ID));
//			}
//			if (item.has(FatherChildrenDO.NAME)) {
//				fatherChildrenDO.setName(item.getString(FatherChildrenDO.NAME));
//			}
//			if(item.has(FatherChildrenDO.ICON)){
//				fatherChildrenDO.setIcon(item.getString(FatherChildrenDO.ICON));
//			}
//			if (item.has(FatherChildrenDO.IMAGE)) {
//				fatherChildrenDO.setImage(item.getString(FatherChildrenDO.IMAGE));
//			}
//			if (item.has("subs")) {
//				JSONArray subs = new JSONArray(item.getString("subs"));
//				for (int len = subs.length(), i = 0; i < len; ++i) {
//					JSONObject sub = (JSONObject) subs.get(i);
//					FatherChildrenDO subFatherChildrenDO = new FatherChildrenDO();
//					if (sub.has(FatherChildrenDO.ID)) {
//						subFatherChildrenDO.setId(sub.getLong(FatherChildrenDO.ID));
//					}
//					if (sub.has(FatherChildrenDO.NAME)) {
//						subFatherChildrenDO.setName(sub.getString(FatherChildrenDO.NAME));
//					}
//					if (sub.has(FatherChildrenDO.LNG)) {
//						subFatherChildrenDO.setLng(sub.getInt(FatherChildrenDO.LNG));
//					}
//					if (sub.has(FatherChildrenDO.LAT)) {
//						subFatherChildrenDO.setLat(sub.getInt(FatherChildrenDO.LAT));
//					}
//					if (sub.has(FatherChildrenDO.IS_COLLECTION)) {
//						subFatherChildrenDO.setCollection(sub.getInt(FatherChildrenDO.IS_COLLECTION) == 1 ? true : false);
//					}
//					fatherChildrenDO.add(subFatherChildrenDO);
//				}
//			}
//		} catch (Exception e) {
//			Log.e("getFatherChildren error", e.getMessage(), e);
//		}
//		return fatherChildrenDO;
//	}
//
//	public static NotificationContentDO json2NotificationContentDO(JSONObject jsonObject) {
//		NotificationContentDO temp = new NotificationContentDO();
//		try {
//			JSONObject object = jsonObject.getJSONObject("data");
//			if (object.has("type")) {
//				temp.setTypes(object.getString("type"));
//			}
//
//			if (object.has("content")) {
//				temp.setContent(object.getString("content"));
//			}
//
//			if (object.has("title")) {
//				temp.setTitle(object.getString("title"));
//			}
//		} catch (Exception e) {
//			Log.e("notification content error", e.getMessage(), e);
//			return null;
//		}
//		return temp;
//	}
//	
//	public static List<ItemDO> json2ItemList(JSONObject jsonObject) {
//		try {
//			if (jsonObject == null) {
//				return CollectionUtil.newArrayList();
//			}
//			JSONArray data = jsonObject.getJSONArray("data");
//			if (data == null) {
//				return CollectionUtil.newArrayList();
//			}
//			List<ItemDO> result = CollectionUtil.newArrayList();
//			for (int len = data.length(), i = 0; i < len; ++i) {
//				ItemDO itemDO = json2ItemDO((JSONObject) data.get(i));
//				if(itemDO != null) {
//					result.add(itemDO);
//				}
//			}
//			return result;
//		} catch (Exception e) {
//			Log.e("item list error", e.getMessage(), e);
//		}
//		return CollectionUtil.newArrayList();
//	}
//	
//	public static List<WishDO> json2WishList(JSONObject jsonObject) {
//		try {
//			if (jsonObject == null) {
//				return CollectionUtil.newArrayList();
//			}
//			JSONArray data = jsonObject.getJSONArray("data");
//			if (data == null) {
//				return CollectionUtil.newArrayList();
//			}
//			List<WishDO> result = CollectionUtil.newArrayList();
//			for (int len = data.length(), i = 0; i < len; ++i) {
//				WishDO wishDO = json2WishDO((JSONObject) data.get(i));
//				if(wishDO != null) {
//					result.add(wishDO);
//				}
//			}
//			return result;
//		} catch (Exception e) {
//			Log.e("wish list error", e.getMessage(), e);
//		}
//		return CollectionUtil.newArrayList();
//	}
//	
//	public static List<ShopDO> json2ShopList(JSONObject jsonObject) {
//		try {
//			if (jsonObject == null) {
//				return CollectionUtil.newArrayList();
//			}
//			JSONArray data = jsonObject.getJSONArray("data");
//			if (data == null) {
//				return CollectionUtil.newArrayList();
//			}
//			List<ShopDO> result = CollectionUtil.newArrayList();
//			for (int len = data.length(), i = 0; i < len; ++i) {
//				ShopDO shopDO = json2ShopDO((JSONObject) data.get(i));
//				if(shopDO != null) {
//					result.add(shopDO);
//				}
//			}
//			return result;
//		} catch (Exception e) {
//			Log.e("shopDO list error", e.getMessage(), e);
//		}
//		return CollectionUtil.newArrayList();
//	}
//	
//	public static List<ShoppingDO> json2ShoppingList(JSONObject jsonObject) {
//		
//		List<ShoppingDO> result = CollectionUtil.newArrayList();
//		try {
//			if (jsonObject == null) {
//				return CollectionUtil.newArrayList();
//			}
//			
//			
//			JSONArray itemData = jsonObject.getJSONArray("items");
//			List<ShoppingDO> itemList = getItemData(itemData);
//			result.addAll(itemList);
//			
//			JSONArray wishsData = jsonObject.getJSONArray("wishs");
//			List<ShoppingDO> wishList = getWishData(wishsData);
//			result.addAll(wishList);
//			
//			
//			// {"wishs":[{"id":143,"createTime":"2012-08-16 09:05","name":"饮料"}],
////			"items":[{"startTime":"2012-08-11 00:00","id":125,"oldPrice":59000,"newPrice":59000,"shopName":"乐游旅游网","name":"舟山普陀游(杭州出发)","shopSub":"杭州总站","images":"http:\/\/www.kajiefang.com\/youhuiImages\/product\/min\/20120810135336.jpg,http:\/\/www.kajiefang.com\/youhuiImages\/product\/min\/20120810135356.jpg,http:\/\/www.kajiefang.com\/youhuiImages\/product\/min\/20120810135408.jpg","lng":120214995,"desp":"","endTime":"2012-08-31 00:00","type":"","lat":30290804},
////			{"startTime":"2012-08-11 00:00","id":126,"oldPrice":55800,"newPrice":45000,"shopName":"乐游旅游网","name":"苏州、上海二日休闲游","shopSub":"杭州总站","images":"http:\/\/www.kajiefang.com\/youhuiImages\/product\/min\/20120810135253.jpg,http:\/\/www.kajiefang.com\/youhuiImages\/product\/min\/20120810135306.jpg","lng":120214995,"desp":"","endTime":"2012-08-31 00:00","type":"","lat":30290804},{"startTime":"2012-08-11 00:00","id":127,"oldPrice":52000,"newPrice":47000,"shopName":"乐游旅游网","name":"徽黄山两日游","shopSub":"杭州总站","images":"http:\/\/www.kajiefang.com\/youhuiImages\/product\/min\/20120810135228.jpg,http:\/\/www.kajiefang.com\/youhuiImages\/product\/min\/20120810135237.jpg","lng":120214995,"desp":"","endTime":"2012-08-31 00:00","type":"","lat":30290804},{"startTime":"2012-08-11 00:00","id":129,"oldPrice":13500,"newPrice":12000,"shopName":"乐游旅游网","name":"乌镇一日游","shopSub":"杭州总站","images":"http:\/\/www.kajiefang.com\/youhuiImages\/product\/min\/20120810135128.jpg,http:\/\/www.kajiefang.com\/youhuiImages\/product\/min\/20120810135137.jpg","lng":120214995,"desp":"","endTime":"2012-08-31 00:00","type":"","lat":30290804}],"code":0}
//			
//			return result;
//		} catch (Exception e) {
//			Log.e("ShoppingDO list error", e.getMessage(), e);
//		}
//		return CollectionUtil.newArrayList();
//	}
//	
//	private static List<ShoppingDO> getWishData(JSONArray wishsData) {
//		List<ShoppingDO> result = CollectionUtil.newArrayList();
//		
//		// [{"id":143,"createTime":"2012-08-16 09:05","name":"饮料"}]
//		
//		try {
//			for (int len = wishsData.length(), i = 0; i < len; ++i) {
//				ShoppingDO tempDO = new ShoppingDO();
//				tempDO.setType(ShoppingListTypeEnum.WISH.getType());
//				JSONObject object = (JSONObject) wishsData.get(i);
//				
//				if(object.has(ShoppingDO.ID)) {
//					tempDO.setId(object.getLong(ShoppingDO.ID));
//				}
//				
//				if(object.has(ShoppingDO.NAME)) {
//					tempDO.setWishName(object.getString(ShoppingDO.NAME));
//				}
//				
//				if(object.has(ShoppingDO.NUM)) {
//					tempDO.setNum(object.getInt(ShoppingDO.NUM));
//				}
//				
//				if(object.has(ShoppingDO.CREATE_TIME)) {
//					tempDO.setCreateTime(DateUtil.parse(object.getString(ShoppingDO.CREATE_TIME)));
//				}
//				
//				result.add(tempDO);
//				
//			}
//		} catch (Exception e) {
//			Log.e("getWishData error", e.getMessage(), e);
//		}
//		
//		return result;
//	}
//	private static List<ShoppingDO> getItemData(JSONArray itemsData) {
//		List<ShoppingDO> result = CollectionUtil.newArrayList();
//		for (int len = itemsData.length(), i = 0; i < len; ++i) {
//			try {
//				ShoppingDO tempDO = new ShoppingDO();
//				tempDO.setType(ShoppingListTypeEnum.ITEM.getType());
//				JSONObject object = (JSONObject) itemsData.get(i);
//				// "id":125 ,,"oldPrice":59000,"newPrice":59000,
//				// "endTime":"2012-08-31 00:00", "startTime":"2012-08-11 00:00"
//				// "shopName":"乐游旅游网","name":"舟山普陀游(杭州出发)","shopSub":"杭州总站",
//				// "images" "lng":120214995,"desp":"","type":"","lat":30290804
//
//				
//				if(object.has(ShoppingDO.ID)) {
//					tempDO.setId(object.getLong(ShoppingDO.ID));
//				}
//				
//				if(object.has(ShoppingDO.OLD_PRICE)) {
//					tempDO.setOldPrice(object.getInt(ShoppingDO.OLD_PRICE));
//				}
//				
//				if(object.has(ShoppingDO.NEW_PRICE)) {
//					tempDO.setNewPrice(object.getInt(ShoppingDO.NEW_PRICE));
//				}
//				
//				if(object.has(ShoppingDO.START_TIME)) {
//					tempDO.setStartTime(DateUtil.parse(object.getString(ShoppingDO.START_TIME)));
//				}
//				
//				if(object.has(ShoppingDO.END_TIME)) {
//					tempDO.setEndTime(DateUtil.parse(object.getString(ShoppingDO.END_TIME)));
//				}
//				
//				if(object.has(ShoppingDO.SHOP_NAME)) {
//					String shopName = object.getString(ShoppingDO.SHOP_NAME);
//					String sub = null;
//					if(object.has(ShoppingDO.SHOP_SUB)) {
//						sub = object.getString(ShoppingDO.SHOP_SUB);
//						if(StringUtil.isNotBlank(sub)) {
//							sub = "(" + sub + ")";
//						}
//					}
//					tempDO.setShopName(shopName + sub);
//				}
//				
//				if(object.has(ShoppingDO.NAME)) {
//					tempDO.setName(object.getString(ShoppingDO.NAME));
//				}
//				
//				if(object.has(ShoppingDO.NUM)) {
//					tempDO.setNum(object.getInt(ShoppingDO.NUM));
//				}
//				
//				if(object.has(ShoppingDO.IMAGES)) {
//					tempDO.setImages(object.getString(ShoppingDO.IMAGES));
//				}
//
//				if(tempDO != null) {
//					result.add(tempDO);
//				}
//				
//			} catch (Exception e) {
//				Log.e("getItemData error", e.getMessage(), e);
//			}
//		}
//		
//		return result;
//	}
//
//	public static void json2ActDetail(JSONObject jsonObject, ActivityDO activityDO) {
//		try {
//			if (jsonObject == null || activityDO == null) {
//				return;
//			}
//
//			JSONObject json = jsonObject.getJSONObject("data");
//			if (json.has(ActivityDO.CATS)) {
//				JSONArray catArray = json.getJSONArray(ActivityDO.CATS);
//				List<CategoryDO> catList = CollectionUtil.newArrayList();
//				for (int len = catArray.length(), i = 0; i < len; ++i) {
//					CategoryDO catDO = json2CatDO((JSONObject) catArray.get(i));
//					if (catDO != null) {
//						catList.add(catDO);
//					}
//				}
//				activityDO.setCatList(catList);
//			}
//
//			if (json.has(ActivityDO.ITEMS)) {
//				JSONArray itemArray = json.getJSONArray(ActivityDO.ITEMS);
//				List<ItemDO> itemList = CollectionUtil.newArrayList();
//				for (int len = itemArray.length(), i = 0; i < len; ++i) {
//					ItemDO itemDO = json2ItemDO((JSONObject) itemArray.get(i));
//					if (itemDO != null) {
//						itemList.add(itemDO);
//					}
//				}
//				activityDO.setItemList(itemList);
//			}
//			if (json.has(ActivityDO.IS_FOLLOWED)) {
//				activityDO.setFollowed(json.getInt(ActivityDO.IS_FOLLOWED) == 1 ? true : false);
//			}
//			
//			if(json.has(ActivityDO.SHOP_ID)) {
//				activityDO.setShopId(json.getLong(ActivityDO.SHOP_ID));
//			}
//
//		} catch (Exception e) {
//			Log.e("json2ActDetail error", e.getMessage(), e);
//		}
//	}
//	
//	
//	public static final UserDO json2UserDO(JSONObject jsonObject) {
//		try {
//			if (jsonObject == null) {
//				return null;
//			}
//
//			JSONObject json = jsonObject.getJSONObject("user");
//			UserDO userDO = new UserDO();
//			if(json.has(UserDO.UID)) {
//				userDO.setUid(json.getLong(UserDO.UID));
//			}
//			
//			if(json.has(UserDO.UF)) {
//				userDO.setUf(json.getString(UserDO.UF));
//			}
//			
//			if(json.has(UserDO.NICK)) {
//				userDO.setNick(new String(json.getString(UserDO.NICK).getBytes(), "UTF-8"));
//			}
//			
//			return userDO;
//		} catch (Exception e) {
//			Log.e("json2UserDO error", e.getMessage(), e);
//		}
//		return null;
//	}
//	
//	public static WishDO json2WishDO(JSONObject jsonItem) {
//		WishDO wishDO = new WishDO();
//		try {
//			if (jsonItem.has(WishDO.ID)) {
//				wishDO.setId(jsonItem.getLong(WishDO.ID));
//			}
//
//			if (jsonItem.has(WishDO.NAME)) {
//				wishDO.setName(jsonItem.getString(WishDO.NAME));
//			}
//
//			if (jsonItem.has(WishDO.CREATE_TIME)) {
//				wishDO.setCreateTime(DateUtil.parse(jsonItem.getString(WishDO.CREATE_TIME)));
//			}
//
//		} catch (Exception e) {
//			Log.e("WishDO", e.getMessage(), e);
//			return null;
//		}
//		return wishDO;
//	}
//	
//	public static ShopDO json2ShopDO(JSONObject jsonItem) {
//		ShopDO shopDO = new ShopDO();
//		try {
//			if (jsonItem.has(ShopDO.ID)) {
//				shopDO.setId(jsonItem.getLong(ShopDO.ID));
//			}
//
//			if (jsonItem.has(ShopDO.NAME)) {
//				shopDO.setName(jsonItem.getString(ShopDO.NAME));
//			}
//			
//			if (jsonItem.has(ShopDO.SUB)) {
//				shopDO.setSub(jsonItem.getString(ShopDO.SUB));
//			}
//
//			if (jsonItem.has(ShopDO.IMAGE)) {
//				shopDO.setImage(jsonItem.getString(ShopDO.IMAGE));
//			}
//			
//			if(jsonItem.has(ShopDO.LAT)) {
//				shopDO.setLat(jsonItem.getInt(ShopDO.LAT));
//			}
//			
//			if(jsonItem.has(ShopDO.LNG)) {
//				shopDO.setLng(jsonItem.getInt(ShopDO.LNG));
//			}
//
//		} catch (Exception e) {
//			Log.e("ShopDO", e.getMessage(), e);
//			return null;
//		}
//		return shopDO;
//	}
//	
//	public static ItemDO json2ItemDO(JSONObject jsonData) {
//		ItemDO itemDO = new ItemDO();
//		try {
//			if (jsonData.has(ItemDO.ID)) {
//				itemDO.setId(jsonData.getLong(ItemDO.ID));
//			}
//			if (jsonData.has(ItemDO.NAME)) {
//				itemDO.setName(jsonData.getString(ItemDO.NAME));
//			}
//			if (jsonData.has(ItemDO.TYPE)) {
//				itemDO.setType(jsonData.getString(ItemDO.TYPE));
//			}
//			if (jsonData.has(ItemDO.IMAGES)) {
//				itemDO.setImages(jsonData.getString(ItemDO.IMAGES));
//			}
//			if (jsonData.has(ItemDO.LNG)) {
//				itemDO.setLng(jsonData.getInt(ItemDO.LNG));
//			}
//			if (jsonData.has(ItemDO.LAT)) {
//				itemDO.setLat(jsonData.getInt(ItemDO.LAT));
//			}
//			if (jsonData.has(ItemDO.START_TIME)) {
//				itemDO.setStartTime(DateUtil.parse(jsonData.getString(ItemDO.START_TIME)));
//			} else if(jsonData.has(ItemDO.START_TIME2)) {
//				itemDO.setStartTime(DateUtil.parse(jsonData.getString(ItemDO.START_TIME2)));
//			}
//			if (jsonData.has(ItemDO.END_TIME)) {
//				itemDO.setEndTime(DateUtil.parse(jsonData.getString(ItemDO.END_TIME)));
//			} else if(jsonData.has(ItemDO.END_TIME2)) {
//				itemDO.setEndTime(DateUtil.parse(jsonData.getString(ItemDO.END_TIME2)));
//			}
//			if (jsonData.has(ItemDO.SHOP_NAME)) {
//				itemDO.setShowName(jsonData.getString(ItemDO.SHOP_NAME));
//			}
//			if (jsonData.has(ItemDO.SHOP_SUB)) {
//				itemDO.setShopSub(jsonData.getString(ItemDO.SHOP_SUB));
//			}
//			if (jsonData.has(ItemDO.NEW_PRICE)) {
//				itemDO.setNewPrice(jsonData.getInt(ItemDO.NEW_PRICE));
//			}
//			if (jsonData.has(ItemDO.OLD_PRICE)) {
//				itemDO.setOldPrice(jsonData.getInt(ItemDO.OLD_PRICE));
//			}
//			if (jsonData.has(ItemDO.DESP)) {
//				itemDO.setDesp(jsonData.getString(ItemDO.DESP));
//			}
//			
//			if(jsonData.has(ActivityConstant.IS_COLLECTIONED)) {
//				itemDO.setCollectioned(jsonData.getInt(ActivityConstant.IS_COLLECTIONED) == 1 ? true : false);
//			}
//		} catch (Exception e) {
//			Log.e("itemDO", e.getMessage(), e);
//			return null;
//		}
//		return itemDO;
//	}
//
//	private static ActivityDO json2ActivityDO(JSONObject jsonItem) {
//		ActivityDO activityDO = new ActivityDO();
//		try {
//			if (jsonItem.has(ActivityDO.ID)) {
//				activityDO.setId(jsonItem.getInt(ActivityDO.ID));
//			}
//			if (jsonItem.has(ActivityDO.NAME)) {
//				activityDO.setName(jsonItem.getString(ActivityDO.NAME));
//			}
//			if (jsonItem.has(ActivityDO.SUB)) {
//				activityDO.setSub(jsonItem.getString(ActivityDO.SUB));
//			}
//			if (jsonItem.has(ActivityDO.TITLE)) {
//				activityDO.setTitle(jsonItem.getString(ActivityDO.TITLE));
//			}
//			if (jsonItem.has(ActivityDO.LNG)) {
//				activityDO.setLng(jsonItem.getInt(ActivityDO.LNG));
//			}
//			if (jsonItem.has(ActivityDO.LAT)) {
//				activityDO.setLat(jsonItem.getInt(ActivityDO.LAT));
//			}
//			if (jsonItem.has(ActivityDO.ICON)) {
//				activityDO.setIcon(jsonItem.getString(ActivityDO.ICON));
//			}
//			if (jsonItem.has(ActivityDO.START_TIME)) {
//				activityDO.setStartTime(DateUtil.parse(jsonItem.getString(ActivityDO.START_TIME)));
//			}
//			if (jsonItem.has(ActivityDO.END_TIME)) {
//				activityDO.setEndTime(DateUtil.parse(jsonItem.getString(ActivityDO.END_TIME)));
//			}
//			if (jsonItem.has(ActivityDO.DESP)) {
//				activityDO.setDesp(jsonItem.getString(ActivityDO.DESP));
//			}
//
//		} catch (Exception e) {
//			Log.e("ActivityDO", e.getMessage(), e);
//			return null;
//		}
//		return activityDO;
//	}
//
//	private static CategoryDO json2CatDO(JSONObject jsonItem) {
//		CategoryDO categoryDO = new CategoryDO();
//		try {
//			if (jsonItem.has(CategoryDO.ID)) {
//				categoryDO.setId(jsonItem.getInt(CategoryDO.ID));
//			} else if (jsonItem.has(CategoryDO.CAT_ID)) {
//				categoryDO.setId(jsonItem.getInt(CategoryDO.CAT_ID));
//			}
//			if (jsonItem.has(CategoryDO.NAME)) {
//				categoryDO.setName(jsonItem.getString(CategoryDO.NAME));
//			} else if (jsonItem.has(CategoryDO.CAT_NAME)) {
//				categoryDO.setName(jsonItem.getString(CategoryDO.CAT_NAME));
//			}
//		} catch (Exception e) {
//			Log.e("cat", e.getMessage(), e);
//			return null;
//		}
//		return categoryDO;
//	}
	
	
}
