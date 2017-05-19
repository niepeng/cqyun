package com.baibutao.app.waibao.yun.android.activites;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.AbstractBaseAdapter;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadAid;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadListener;
import com.baibutao.app.waibao.yun.android.biz.bean.AlarmBean;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.DateUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;
import com.baibutao.app.waibao.yun.android.util.MD5;

public class AlarmActivity extends BaseActivity implements ThreadListener {

	private ListView listView;

	private List<DeviceBean> alarmList;

	private AlarmAdapter alarmAdapter;

	private FrameLayout largeLoadFramelayout;

	private LinearLayout listFootLayout;

	private int page;

	private Button loadMoreBtn;

	private ProgressBar loadMoreProgressBar;

	private boolean isLastPage;
	
	private TextView lastUpdateTimeTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.alarm);
		
		if(eewebApplication.isReflushAlarmActivity()) {
			eewebApplication.setReflushAlarmActivity(false);
		}
		
		lastUpdateTimeTv = (TextView) findViewById(R.id.intime_gmtmodified_tv);
		largeLoadFramelayout = (FrameLayout) findViewById(R.id.large_more_process_framelayout);
		listView = (ListView) findViewById(R.id.alarm_listview);
		LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listFootLayout = (LinearLayout) layoutInflater.inflate(R.layout.btn_load_more_large, null);
		listView.addFooterView(listFootLayout);
		loadMoreBtn = (Button) findViewById(R.id.load_more_btn);
		loadMoreProgressBar = (ProgressBar) findViewById(R.id.load_more_progress_bar);

		initData();
		setViewContent();

		alarmAdapter = new AlarmAdapter(alarmList);

		request();
	}
	
	@Override
	public void onResume() {
		if(eewebApplication.isReflushAlarmActivity()) {
			eewebApplication.setReflushAlarmActivity(false);
			handleReflush(null);
		}
		super.onResume();
	}

	private void initData() {
		page = 1;
		isLastPage = false;
		if (alarmList == null) {
			alarmList = CollectionUtil.newArrayList();
		} else {
			alarmList.clear();
			alarmAdapter.notifyDataSetChanged();
		}
	}

	private void request() {
		if (isLastPage) {
			return;
		}
//		RemoteManager remoteManager = RemoteManager.getFullFeatureRemoteManager();
//		Request request = remoteManager.createQueryRequest(Config.getConfig().getProperty(Config.Names.ALARM_LIST_URL));
//		request.addParameter("page", page++);
//		request.addParameter("userId", eewebApplication.getUserDO().getId());
//		request.addParameter("psw", MD5.getMD5(eewebApplication.getUserDO().getPsw().getBytes()));
//		eewebApplication.asyInvoke(new ThreadAid(this, request));

		RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
		remoteManager.setResponseParser(new StringResponseParser());
		Request request = remoteManager.createPostRequest(Config.Values.URL);
		final Map<String, Object> map = CollectionUtil.newHashMap();
		map.put("user", eewebApplication.getUserDO().getUsername());
		request.setBody(JsonUtil.mapToJson(map));
		request.addHeader("type", "getAccountErr");

//		responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
		eewebApplication.asyInvoke(new ThreadAid(this, request, remoteManager));
		
		setUpdateTime(null);
	}
	
	private void setUpdateTime(Date date) {
		if (date == null) {
			date = new Date();
		}
		String text = "最近更新时间：" + DateUtil.format(date, DateUtil.DATE_MMddHHmm);
		lastUpdateTimeTv.setText(text);
	}

	private void setViewContent() {
		listView.setOnItemClickListener(null);
//		listView.setOnItemClickListener(new ItemClickListener());
	}

	public void handleLoadMore(View v) {
		if (loadMoreBtn.getVisibility() == View.GONE) {
			return;
		}
		setViewGoneBySynchronization(loadMoreBtn);
		setViewVisiableBySynchronization(loadMoreProgressBar);
		request();
	}

	public synchronized void handleReflush(View v) {
		initData();
		setViewContent();
		setViewVisiableBySynchronization(largeLoadFramelayout);
		request();
	}

	@Override
	public void onPostExecute(Response response) {
		setViewGone(largeLoadFramelayout);

		if (response == null) {
			return;
		}
		setViewGone(loadMoreBtn, loadMoreProgressBar);

		JSONObject mainJson = JsonUtil.getJsonObject(response.getModel());
		JSONArray array = JsonUtil.getJsonArray(mainJson, "array");
//		JSONArray array = JsonUtil.getJsonArray(response.getModel());
		final List<DeviceBean> currentList = CollectionUtil.newArrayList();
		
		if (array != null) {
			try {
				List<DeviceBean> tmpList = CollectionUtil.newArrayList();
				JSONObject json = null;
				DeviceBean bean = null;
				JSONArray alarmArray = null;
				AlarmBean alarmBean = null;
				List<AlarmBean> alarmBeanList = null;
				for (int i = 0, size = array.length(); i < size; i++) {
					json = array.getJSONObject(i);
					if (json == null) {
						continue;
					}
					bean = new DeviceBean();
					bean.setSnaddr(JsonUtil.getString(json, "snaddr", null));
					bean.setDevName(JsonUtil.getString(json, "devName", null));
					bean.setArea(JsonUtil.getString(json, "area", null));
					alarmArray = json.getJSONArray("detail");
					alarmBeanList = CollectionUtil.newArrayList();
					for (int j = 0, alarmLenth = alarmArray.length(); j < alarmLenth; j++) {
						alarmBean = JsonUtil.jsonToBean(alarmArray.getJSONObject(j).toString(), AlarmBean.class);
						alarmBeanList.add(alarmBean);
					}
					bean.setAlarmBeanList(alarmBeanList);
					tmpList.add(bean);
				}
				
				formatCurrent(currentList, tmpList);
				
				
//			Tuple.Tuple2<List<AlarmBean>, Date> result = JSONHelper.json2AlarmList((JSONObject) response.getModel());
//			final List<AlarmBean> currentList = result._1();
//			Date date = result._2();
//			if (date != null) {
//				eewebApplication.setLastRequestAlarmTime(date);
//			}
//			int currentSize = currentList.size();
//			if (currentSize == 0) {
//				isLastPage = true;
//				runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						toastLong("没有更多的数据");
//					}
//				});
//				setViewGone(loadMoreBtn, loadMoreProgressBar);
//				return;
//			} else {
//				setViewVisible(listView, loadMoreBtn);
//				setViewGone(loadMoreProgressBar);
//			}
//

				runOnUiThread(new Runnable() {
				@Override
				public void run() {
					alarmList.addAll(currentList);
					if (listView.getAdapter() == null) {
						listView.setAdapter(alarmAdapter);
					} else {
						alarmAdapter.notifyDataSetChanged();
					}
				}
			});
				
			} catch (Exception e) {
			}

		} else {
			setViewVisible(loadMoreBtn);
			setViewGone(loadMoreProgressBar);
			final String msg = response.getMessage();
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					toastLong(msg);
				}
			});
		}

	}

	private void formatCurrent(List<DeviceBean> currentList, List<DeviceBean> tmpList) {
		for (DeviceBean deviceBean : tmpList) {
			currentList.add(deviceBean);
			if (!CollectionUtil.isEmpty(deviceBean.getAlarmBeanList())) {
				for (AlarmBean alarmBean : deviceBean.getAlarmBeanList()) {
					DeviceBean bean = new DeviceBean();
					bean.setAlarmBean(alarmBean);
					currentList.add(bean);
				}
			}
		}
	}

	private class AlarmAdapter extends AbstractBaseAdapter {

		public AlarmAdapter(List<DeviceBean> catList) {
			super(catList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout holder = null;
			
			DeviceBean deviceBean = (DeviceBean) getItem(position);
			if(deviceBean == null) {
				return holder;
			}
			
			if (convertView == null) {
				if (deviceBean.isShowAlarmMsg()) {
					holder = new ViewHolderCell(AlarmActivity.this);
				} else {
					holder = new ViewHolderMain(AlarmActivity.this);
				}
			} else {
				holder = (LinearLayout) convertView;
			}

			
			
			if(deviceBean.isShowAlarmMsg()) {
				ViewHolderCell cell = (ViewHolderCell)holder;
//				cell.imageView.setImageDrawable(getDrawableByType(deviceBean.getAlarmBean().getType()));
				cell.imageView.setImageDrawable(getDrawableByType(deviceBean.getAlarmBean()));
				cell.mainTv.setText(deviceBean.getAlarmBean().getMsg());
				cell.timeTv.setText(deviceBean.getAlarmBean().getAlarmTime());
				return cell;
			} else {
				ViewHolderMain main = (ViewHolderMain)holder;
				main.mainTv.setText("设备名:" + deviceBean.getDevName());
				main.snaddrTv.setText(deviceBean.getSnaddr());
				return main;
			}
		}

	}

	private class ViewHolderCell extends LinearLayout {

//		public TextView nameTv;
//		public TextView timeTv;
//		public TextView reasonTv;
		
		public ImageView imageView;
		public TextView mainTv;
		public TextView timeTv;
		

		public ViewHolderCell(Context context) {
			super(context);
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			LinearLayout itemWrap = (LinearLayout) layoutInflater.inflate(R.layout.alarm_list_view_item, this);
//			nameTv = (TextView) itemWrap.findViewById(R.id.alarm_list_view_item_name_tv);
//			timeTv = (TextView) itemWrap.findViewById(R.id.alarm_list_view_time_tv);
//			reasonTv = (TextView) itemWrap.findViewById(R.id.alarm_list_view_reason_tv);
			
			LinearLayout itemWrap = (LinearLayout) layoutInflater.inflate(R.layout.alarm_list_view_item_cell, this);
			imageView = (ImageView) itemWrap.findViewById(R.id.alarm_list_view_item_cell_img);
			mainTv = (TextView) itemWrap.findViewById(R.id.alarm_list_view_item_cell_name_tv);
			timeTv = (TextView) itemWrap.findViewById(R.id.alarm_list_view_item_cell_time_tv);
		}
	}
	
	private class ViewHolderMain extends LinearLayout {

		public TextView mainTv;
		public TextView snaddrTv;

		public ViewHolderMain(Context context) {
			super(context);
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout itemWrap = (LinearLayout) layoutInflater.inflate(R.layout.alarm_list_view_item_main, this);
			mainTv = (TextView) itemWrap.findViewById(R.id.alarm_list_view_item_main_name_tv);
			snaddrTv = (TextView) itemWrap.findViewById(R.id.alarm_list_view_item_main_snaddr_tv);
		}
	}
	

	class ItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
													// click happened
				View arg1,// The view within the AdapterView that was clicked
				int arg2,// The position of the view in the adapter
				long arg3// The row id of the item that was clicked
		) {
			 final AlarmBean alarmMsgDO = (AlarmBean)arg0.getItemAtPosition(arg2);
			 // 删除待确认操作
			 confirm("确定要删除该报警信息？", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 执行删除操作 1.页面上删除，2. 提交到服务器端
//						remove(alarmList, alarmMsgDO);
					}

				}, null);
		}

	}
	
	private void remove(List<AlarmBean> alarmList, AlarmBean alarmMsgDO) {
		if(CollectionUtil.isEmpty(alarmList)) {
			return;
		}
//		for(AlarmMsgDO tmp : alarmList) {
//			if(tmp.getId() == alarmMsgDO.getId()) {
//				alarmList.remove(alarmMsgDO);
//				alarmAdapter.notifyDataSetChanged();
//				toastShort("删除成功");
//				// 提交到服务器端
//				deleteAlarm(alarmMsgDO.getId());
//				return;
//			}
//		}
		
	}

	private void deleteAlarm(long id) {
		RemoteManager remoteManager = RemoteManager.getFullFeatureRemoteManager();
		Request request = remoteManager.createQueryRequest(Config.getConfig().getProperty(Config.Names.ALARM_DELERE_URL));
		request.addParameter("userId", eewebApplication.getUserDO().getId());
		request.addParameter("psw", MD5.getMD5(eewebApplication.getUserDO().getPsw().getBytes()));
		request.addParameter("alarmId", id);
		eewebApplication.asyInvoke(new ThreadAid(new None(), request));
	}
	
	public void handleDeleteAll(View v) {
		alarmList.clear();
		alarmAdapter.notifyDataSetChanged();
		RemoteManager remoteManager = RemoteManager.getFullFeatureRemoteManager();
		Request request = remoteManager.createQueryRequest(Config.getConfig().getProperty(Config.Names.ALARM_DELERE_ALL_URL));
		request.addParameter("userId", eewebApplication.getUserDO().getId());
		request.addParameter("psw", MD5.getMD5(eewebApplication.getUserDO().getPsw().getBytes()));
		eewebApplication.asyInvoke(new ThreadAid(new None(), request));
	}
	
	
	private class None implements ThreadListener {
		@Override
		public void onPostExecute(Response response) {
			alert("aa");
		}
	}

}
