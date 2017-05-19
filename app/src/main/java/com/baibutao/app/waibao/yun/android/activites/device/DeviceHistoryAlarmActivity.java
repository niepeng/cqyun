package com.baibutao.app.waibao.yun.android.activites.device;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadHelper;
import com.baibutao.app.waibao.yun.android.biz.bean.AlarmBean;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceDataBean;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.DateUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author niepeng
 *
 * @date 2012-9-13 下午1:12:28
 */
public class DeviceHistoryAlarmActivity extends BaseActivity {

	private DeviceBean deviceBean;

	private TextView startTimeTv;

	private TextView endTimeTv;

	private Future<Response> responseFuture;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.device_history_alarm);

		Bundle bundle = this.getIntent().getExtras();
		deviceBean = (DeviceBean) bundle.get("deviceBean");

		startTimeTv = (TextView) findViewById(R.id.device_history_alarm_start_time);
		endTimeTv = (TextView) findViewById(R.id.device_history_alarm_end_time);

		Date now = new Date();
		startTimeTv.setText(DateUtil.format(DateUtil.changeDay(now, -1),DateUtil.DEFAULT_DATE_FMT));
		endTimeTv.setText(DateUtil.format(now, DateUtil.DEFAULT_DATE_FMT));

	}


	public void handleBack(View v) {
		this.finish();
	}

	public void handleSelectStart(View v) {
		showBottoPopupWindow(startTimeTv, endTimeTv);
	}

	public void handleSelectEnd(View v) {
		showBottoPopupWindow(endTimeTv, endTimeTv);
	}

	public void handleSubmit(View v) {
		String startTimeStr = startTimeTv.getText().toString();
		String endTimeStr = endTimeTv.getText().toString();

		Date startTime = DateUtil.parse(startTimeStr, DateUtil.DEFAULT_DATE_FMT);
		Date endTime = DateUtil.parse(endTimeStr, DateUtil.DEFAULT_DATE_FMT);

		if(startTime == null || endTime == null) {
			toastLong("开始时间和结束时间不能为空");
			return;
		}

		if(DateUtil.after(startTime, endTime)) {
			toastLong("开始时间不能大于结束时间");
			return;
		}

		if(DateUtil.distanceDay(startTime, endTime) > 7) {
			toastLong("移动端只能查询七天以内的数据");
			return;
		}

		RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
		remoteManager.setResponseParser(new StringResponseParser());
		Request request = remoteManager.createPostRequest(Config.Values.URL);
		final Map<String, Object> map = CollectionUtil.newHashMap();
		map.put("snaddr", deviceBean.getSnaddr());
		map.put("user", eewebApplication.getUserDO().getUsername());
		map.put("startTime", DateUtil.formatDefault(startTime));
		map.put("endTime", DateUtil.formatDefault(endTime));
		request.setBody(JsonUtil.mapToJson(map));
		request.addHeader("type", "getDeviceErr");


		ProgressDialog progressDialog = showProgressDialog(R.string.app_read_data);
		progressDialog.setOnDismissListener(new LoadData());
		responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));

	}

	private class LoadData implements OnDismissListener {

		@Override
		public void onDismiss(DialogInterface dialog) {
			if (responseFuture == null) {
				return;
			}

			try {
				Response response = responseFuture.get();
				List<DeviceDataBean> dataList = null;

				JSONObject mainJson = JsonUtil.getJsonObject(response.getModel());
				JSONObject json = JsonUtil.getJSONObject(mainJson, "array");

//				JSONObject json = JsonUtil.getJsonObject(response.getModel());

				DeviceBean bean = null;
				if (json != null) {
					bean = new DeviceBean();
					bean.setSnaddr(JsonUtil.getString(json, "snaddr", null));
					bean.setDevName(JsonUtil.getString(json, "devName", null));
					bean.setArea(JsonUtil.getString(json, "area", null));
					JSONArray alarmArray = JsonUtil.getJsonArray(json, "detail");
					List<AlarmBean> alarmBeanList = CollectionUtil.newArrayList();
					AlarmBean alarmBean = null;
					if (alarmArray != null) {
						for (int j = 0, alarmLenth = alarmArray.length(); j < alarmLenth; j++) {
							alarmBean = JsonUtil.jsonToBean(alarmArray.getJSONObject(j).toString(), AlarmBean.class);
							alarmBeanList.add(alarmBean);
						}
					}
					bean.setAlarmBeanList(alarmBeanList);
				}

				if(bean == null || StringUtil.isBlank(bean.getSnaddr()) || CollectionUtil.isEmpty(bean.getAlarmBeanList())) {
					toastLong(R.string.app_no_data);
					return;
				}

				// 跳转到新的页面展示数据
				Intent intent = new Intent(DeviceHistoryAlarmActivity.this, DeviceHistoryAlarmDataActivity.class);
				Bundle bundle = new Bundle();
				bean.setDevName(deviceBean.getDevName());
				bundle.putSerializable("deviceBean", bean);
				intent.putExtras(bundle);
				startActivity(intent);

			} catch (Exception e) {
				DeviceHistoryAlarmActivity.this.logError(e.getMessage(), e);
			}


		}

	}

}
