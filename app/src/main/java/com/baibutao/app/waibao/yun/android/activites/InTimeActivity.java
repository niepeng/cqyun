package com.baibutao.app.waibao.yun.android.activites;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.AbstractBaseAdapter;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadAid;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadHelper;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadListener;
import com.baibutao.app.waibao.yun.android.activites.device.DeviceDetailActivity;
import com.baibutao.app.waibao.yun.android.activites.device.DeviceReadAddActivity;
import com.baibutao.app.waibao.yun.android.biz.JSONHelper;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceDataBean;
import com.baibutao.app.waibao.yun.android.biz.dataobject.AreaDO;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.remotesimple.Httpclient;
import com.baibutao.app.waibao.yun.android.util.ChangeUtil;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.DateUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;
import com.baibutao.app.waibao.yun.android.util.MD5;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

public class InTimeActivity extends BaseActivity  {
	
	private TextView gmtmodifiedTv;

	private Button areaChangeBtn;

	private Spinner areaSpinner;
	
	private boolean isNeedLoadArea;
	
	private long showAreaId;
	
	private List<AreaDO> areaList;
	
	private Future<Response> loadAreaResponseFuture;
	
	private GridView intimeGridView;

	private List<DeviceBean> equiList;
	
	private EquipmentAdapter equipmentAdapter;
	
	private FrameLayout largeLoadFramelayout;
	
	private int page;
	
	
//	private boolean isLastPage;
//	
//	private Button loadMoreBtn;
//	
//	private ProgressBar loadMoreProgressBar;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.intime);

		gmtmodifiedTv = (TextView) findViewById(R.id.intime_gmtmodified_tv);
		areaChangeBtn = (Button) findViewById(R.id.intime_area_btn);
		areaSpinner = (Spinner) findViewById(R.id.intime_area_spinner);
		intimeGridView = (GridView) findViewById(R.id.intime_grid_view);
		
		largeLoadFramelayout = (FrameLayout) findViewById(R.id.large_more_process_framelayout);
//		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		LinearLayout listFootLayout = (LinearLayout) layoutInflater.inflate(R.layout.btn_load_more_large, null);
		
		isNeedLoadArea = true;
		initData();
		setViewContent();
		equipmentAdapter = new EquipmentAdapter(equiList);
		request();
	}

	private void initData() {
//		equiList = CollectionUtil.newArrayList();
//		
//		for(int i=0;i<10;i++) {
//			EquipmentDO equipmentDO = new EquipmentDO();
//			equipmentDO.setId(i);
//			equipmentDO.setName("name" + i);
//			equipmentDO.setTemperature(3000 + i);
//			equipmentDO.setHumidity(6000 + i);
//			equiList.add(equipmentDO);
//		}
//		
//		equipmentAdapter = new EquipmentAdapter(equiList);
//		

		
//		isLastPage = false;
		
		page = 1;
		if (equiList == null) {
			equiList = CollectionUtil.newArrayList();
		} else {
			equiList.clear();
//			equipmentAdapter.notifyDataSetChanged();
		}
		
	}



	private void setViewContent() {
//		intimeGridView.setAdapter(equipmentAdapter);
		intimeGridView.setOnItemClickListener(new ItemClickListener());
	}

	private void request() {
//		if (isLastPage) {
//			return;
//		}
//		RemoteManager remoteManager = RemoteManager.getFullFeatureRemoteManager();
//		Request request = remoteManager.createQueryRequest(Config.getConfig().getProperty(Config.Names.ITEM_LIST_URL));
//		request.addParameter("page", page);
//		request.addParameter("userId", eewebApplication.getUserDO().getId());
//		request.addParameter("psw", MD5.getMD5(eewebApplication.getUserDO().getPsw().getBytes()));
//		if(showAreaId > 0) {
//			request.addParameter("areaId", showAreaId);
//		}
//		page++;
//		eewebApplication.asyInvoke(new ThreadAid(new LoadItems(), request));
		setUpdateTime(null);
		

		RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
		remoteManager.setResponseParser(new StringResponseParser());
		Request request = remoteManager.createPostRequest(Config.Values.URL);
		final Map<String, Object> map = CollectionUtil.newHashMap();
		map.put("user", eewebApplication.getUserDO().getUsername());
		request.setBody(JsonUtil.mapToJson(map));
		request.addHeader("type", "getAllDevice");

		eewebApplication.asyInvoke(new ThreadAid(new LoadItems(), request, remoteManager));
	}
	
	private void setUpdateTime(Date date) {
		if (date == null) {
			date = new Date();
		}
		String text = "最近更新时间：" + DateUtil.format(date, DateUtil.DATE_MMddHHmm);
		gmtmodifiedTv.setText(text);
	}

	public void handleReflush(View v) {
		initData();
		setViewContent();
		setViewVisiableBySynchronization(largeLoadFramelayout);
		request();
	}
	
	public void handleAdd(View v) {
		Intent intent = new Intent(InTimeActivity.this, DeviceReadAddActivity.class);
		startActivityForResult(intent, ACTIVITY_RESULT_CODE);
	}

	public void handleChangeArea(View v) {
		if(!isNeedLoadArea) {
			areaSpinner.performClick();
			return;
		}
		
		RemoteManager remoteManager = RemoteManager.getFullFeatureRemoteManager();
		Request request = remoteManager.createQueryRequest(Config.getConfig().getProperty(Config.Names.AREA_LIST_URL));
		request.addParameter("userId", eewebApplication.getUserDO().getId());
		request.addParameter("psw", MD5.getMD5(eewebApplication.getUserDO().getPsw().getBytes()));
		
		ProgressDialog progressDialog = showProgressDialog(R.string.app_read_data);
		progressDialog.setOnDismissListener(new LoadAreas());
		loadAreaResponseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request));
	}
	
	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
													// click happened
				View arg1,// The view within the AdapterView that was clicked
				int arg2,// The position of the view in the adapter
				long arg3// The row id of the item that was clicked
		) {
			Object obj = arg0.getItemAtPosition(arg2);
			if(!(obj instanceof DeviceBean)) {
				return;
			}
			DeviceBean bean = (DeviceBean) obj;
			Intent intent = new Intent(InTimeActivity.this, DeviceDetailActivity.class);
			intent.putExtra("deviceBean", bean);
			startActivityForResult(intent, ACTIVITY_RESULT_CODE);
			
//			EquipmentDO equipmentDO = (EquipmentDO) arg0.getItemAtPosition(arg2);
//			alert("click Equipment, name is " + equipmentDO.getName());
			
			
//			Intent intent = new Intent(CategoryActivity.this, ItemListActivity.class);
//			intent.putExtra("catDO", catDO);
//			startActivity(intent);
		}

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == ACTIVITY_DEL_RESULT_CODE) {
			handleReflush(null);
			return;
		}

		if(data != null) {
			boolean refush = data.getExtras().getBoolean("needRefulsh");
			if (refush) {
				handleReflush(null);
				return;
			}
		}
	}
	
	private class EquipmentAdapter extends AbstractBaseAdapter {

		public EquipmentAdapter(List<DeviceBean> catList) {
			super(catList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder(InTimeActivity.this);
			} else {
				holder = (ViewHolder) convertView;
			}
			
			DeviceBean deviceBean = (DeviceBean) getItem(position);
			if (deviceBean != null) {
				holder.nameTv.setText(deviceBean.getDevName());
				if (deviceBean.getDataBean() != null) {
					if (!StringUtil.isBlank(deviceBean.getDataBean().getTemp())) {
						holder.tempTv.setText(deviceBean.getDataBean().getTemp());
					} else {
						holder.tempTv.setText("-- ");
					}
					if (!StringUtil.isBlank(deviceBean.getDataBean().getHumi())) {
						holder.humiTv.setText(deviceBean.getDataBean().getHumi());
					} else {
						holder.humiTv.setText("-- ");
					}
				} else {
					holder.tempTv.setText("-- ");
					holder.humiTv.setText("-- ");
				}

				holder.timeTv.setText(deviceBean.getDataBean().getTime());
//				holder.shineTv.setText("-- ");
//				holder.co2Tv.setText("-- ");
			}
			

//			EquipmentDO equipmentDO = (EquipmentDO) getItem(position);
//			if (equipmentDO != null) {
//				holder.nameTv.setText(equipmentDO.getName());
//				// 温度设置
//				if(equipmentDO.getTemperature() == 0) {
//					holder.tempTv.setText("-- ");
//				} else {
//					holder.tempTv.setText(NumberFormatUtil.formatByInt(equipmentDO.getTemperature(), 2));
//				}
//				
//				// 湿度设置
//				if(equipmentDO.getHumidity() == 0) {
//					holder.humiTv.setText("-- ");
//				} else {
//					holder.humiTv.setText(NumberFormatUtil.formatByInt(equipmentDO.getHumidity(), 2));
//				}
//				
//				// 光照设置
//				if (equipmentDO.getShine() == 0) {
//					holder.shineTv.setText("-- ");
//				} else {
//					holder.shineTv.setText(String.valueOf(equipmentDO.getShine()));
//				}
//
//				// co2设置
//				if (equipmentDO.getCo2() == 0) {
//					holder.co2Tv.setText("-- ");
//				} else {
//					holder.co2Tv.setText(String.valueOf(equipmentDO.getCo2()));
//				}
//			} 
			
			
//			if (catDO != null) {
//				holder.nameTv.setText(catDO.getName());
//				String fileName = getNameByUrl(catDO.getImageUrl());
//				if (StringUtil.isNotEmpty(fileName)) {
//					Bitmap bitMap = ImageCache.getBitmap(fileName);
//					if (bitMap != null) {
//						holder.imageView.setImageBitmap(bitMap);
//					} else {
//						holder.imageView.setImageDrawable(defaultImageSmall);
//						asyLoadImages(holder.imageView, fileName, catDO.getImageUrl());
//					}
//				} else {
//					holder.imageView.setImageResource(R.drawable.lvxing);
//				}
//			}
			
			return holder;
		}

	}
	
	private class ViewHolder extends LinearLayout {

		public TextView nameTv;
		public TextView tempTv;
		public TextView humiTv;
		public TextView timeTv;
//		public TextView shineTv;
//		public TextView co2Tv;

		public ViewHolder(Context context) {
			super(context);
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout itemWrap = (LinearLayout) layoutInflater.inflate(R.layout.intime_grid_view_item, this);
			nameTv = (TextView) itemWrap.findViewById(R.id.intime_grid_view_item_name_tv);
			tempTv = (TextView) itemWrap.findViewById(R.id.intime_grid_view_item_temp_tv);
			humiTv = (TextView) itemWrap.findViewById(R.id.intime_grid_view_item_humi_tv);
			timeTv = (TextView) itemWrap.findViewById(R.id.intime_grid_view_item_time_tv);
//			shineTv = (TextView) itemWrap.findViewById(R.id.intime_grid_view_item_shine_tv);
//			co2Tv = (TextView) itemWrap.findViewById(R.id.intime_grid_view_item_co2_tv);
		}
	}
	
	class LoadAreas  implements OnDismissListener {
		@Override
		public void onDismiss(DialogInterface dialog) {
			if (loadAreaResponseFuture == null) {
				return;
			}
			
			try {
				Response response = loadAreaResponseFuture.get();
				if (!response.isSuccess()) {
					InTimeActivity.this.alert(response.getMessage());
					return;
				}
				isNeedLoadArea = false;
				areaList = JSONHelper.json2AreaList((JSONObject) response.getModel());
				showArea();
			} catch (Exception e) {
				InTimeActivity.this.logError(e.getMessage(), e);
			}
			
		}
	}
	
	class LoadItems implements ThreadListener {

		@Override
		public synchronized void  onPostExecute(Response response) {
			try {
				if (response == null) {
					return;
				}
				
				JSONArray array = JsonUtil.getJsonArray(response.getModel());
				if (array == null) {
					setViewGone(largeLoadFramelayout);
					toastLong("暂无设备数据");
					return;
				}
				
				List<DeviceBean> beanList = CollectionUtil.newArrayList();
				JSONObject jsonObject = null;
				DeviceBean bean = null;
				for (int i = 0, size = array.length(); i < size; i++) {
					jsonObject = array.getJSONObject(i);
					bean = JsonUtil.jsonToBean(jsonObject.toString(), DeviceBean.class);
					beanList.add(bean);
				}
				
				DeviceDataBean dataBean = null;
				for(DeviceBean deviceBean : beanList) {
					if(!StringUtil.isBlank(bean.getSnaddr())) {
						dataBean = requestDeviceDataBean(deviceBean);
						deviceBean.setDataBean(dataBean);
					}
				}
				
				Collections.sort(beanList, new DeviceSort());
				setViewGone(largeLoadFramelayout);
				equiList.clear();
				equiList.addAll(beanList);
				
				runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (intimeGridView.getAdapter() == null) {
						intimeGridView.setAdapter(equipmentAdapter);
					} else {
						equipmentAdapter.notifyDataSetChanged();
					}
				}
			});
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			if (response.isSuccess()) {
//				final List<EquipmentDO> currentItemList = JSONHelper.json2ItemList((JSONObject) response.getModel());  //JSONHelper.json2ItemList((JSONObject) response.getModel());
//				int currentSize = currentItemList.size();
//				if (currentSize == 0) {
//					runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							toastLong("没有更多的数据");
//						}
//					});
//					return;
//				} else {
//				}
//				
//				if(page == 2) {
//					equiList.clear();
//				}
//				equiList.addAll(currentItemList);
//				
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						if (intimeGridView.getAdapter() == null) {
//							intimeGridView.setAdapter(equipmentAdapter);
//						} else {
//							equipmentAdapter.notifyDataSetChanged();
//						}
//					}
//				});

			} else {
//				final String msg = response.getMessage();
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						toastLong(msg);
//					}
//				});
			}
		}
	}
	
	private class DeviceSort implements Comparator<DeviceBean> {

		@Override
		public int compare(DeviceBean o1, DeviceBean o2) {

			if (o1.getDataBean() == null) {
				return 1;
			}

			if (o2.getDataBean() == null) {
				return -1;
			}

			if (o1.getDataBean().getAbnormal().equals(o2.getDataBean().getAbnormal())) {
				return o1.getSnaddr().compareTo(o2.getSnaddr());
			}

			if (o1.getDataBean().isSuccess()) {
				return -1;
			}

			if (o2.getDataBean().isSuccess()) {
				return 1;
			}
			return o1.getSnaddr().compareTo(o2.getSnaddr());
		
		}
		
	}
	
	private DeviceDataBean requestDeviceDataBean(DeviceBean bean) {
		Map<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("TYPE", "getRTData");
		Map<String, String> bodyMap = new HashMap<String, String>();
		bodyMap.put("snaddr", bean.getSnaddr());
		bodyMap.put("curve", bean.getCurve());
		String content = Httpclient.subPostForBody(Config.Values.URL, JsonUtil.mapToJson(bodyMap), Httpclient.DEFAULT_CHARSET, headerMap);
		
		JSONObject json = JsonUtil.getJsonObject(content);
		if(json == null) {
			return null;
		}
		DeviceDataBean dataBean = new DeviceDataBean();
		dataBean.setAbnormal(JsonUtil.getString(json, "abnormal", null));
		dataBean.setTime(JsonUtil.getString(json, "time", null));
		JSONObject humiJson = JsonUtil.getJSONObject(json, "humi");
		dataBean.setHumi(JsonUtil.getString(humiJson, "value", null));
		dataBean.setHumiStatus(ChangeUtil.str2int(JsonUtil.getString(humiJson, "status", null)));
		
		JSONObject tempJson = JsonUtil.getJSONObject(json, "temp");
		dataBean.setTemp(JsonUtil.getString(tempJson, "value", null));
		dataBean.setTempStatus(ChangeUtil.str2int(JsonUtil.getString(tempJson, "status", null)));
		
		return dataBean;
	}

	public void showArea() {
		BaseAdapter spinnerAdapter = new BaseAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				MenuViewHolder layout;
				if (convertView == null) {
					layout = new MenuViewHolder(InTimeActivity.this);
				} else {
					layout = (MenuViewHolder) convertView;
				}
				String name;
				if (position == 0) {
					name = getString(R.string.intime_area_title_all);
				} else {
					AreaDO areaDO = areaList.get(position - 1);
					name = areaDO.getName();
				}
				layout.tv.setText(name);
				if (name.equals(areaChangeBtn.getText().toString())) {
					layout.radioBtn.setChecked(true);
				} else {
					layout.radioBtn.setChecked(false);
				}
				return layout;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public int getCount() {
				return areaList.size() + 1;
			}
		};

		areaSpinner.setAdapter(spinnerAdapter);
		areaSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				MenuViewHolder layout = (MenuViewHolder) view;
				if (layout == null) {
					return;
				}
				layout.radioBtn.setChecked(true);
				String catNameString = layout.tv.getText().toString();
				long choieCatId = getCatIdByName(catNameString);
				areaChangeBtn.setText(catNameString);
				// 选择全部
				if (choieCatId <= 0) {
					if (showAreaId == 0) {
						return;
					}
					showAreaId = 0;
					handleReflush(null);
				} else {
					if (choieCatId != showAreaId) {
						showAreaId = choieCatId;
						handleReflush(null);
					}
				}
			}

			private long getCatIdByName(String catName) {
				for (AreaDO area : areaList) {
					if (area.getName().equals(catName.trim())) {
						return area.getId();
					}
				}
				return -1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		areaSpinner.performClick();
		
	}
	
	private class MenuViewHolder extends LinearLayout {

		public TextView tv;
		public RadioButton radioBtn;

		public MenuViewHolder(Context context) {
			super(context);
			LinearLayout layout = (LinearLayout) View.inflate(context, R.layout.intime_area_menu_item, this);
			tv = (TextView) layout.findViewById(R.id.text);
			radioBtn = (RadioButton) layout.findViewById(R.id.radio_btn);
		}

	}

	

}
