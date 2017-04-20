package com.baibutao.app.waibao.yun.android.activites.device;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadHelper;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

/**
 * <p>标题: </p>
 * <p>描述: </p>
 * <p>版权: lsb</p>
 * <p>创建时间: 2017年3月17日  下午3:57:47</p>
 * <p>作者：niepeng</p>
 */
public class DeviceSetupUpdateAreaActivity extends BaseActivity {
	
	private Spinner areaSpinner;
	private DeviceBean deviceBean;
	private Future<Response> responseFuture;
	private Future<Response> upResponseFuture;
	private String newValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.device_setup_update_area);

		Bundle bundle = this.getIntent().getExtras();
		deviceBean = (DeviceBean) bundle.get("deviceBean");
		areaSpinner = (Spinner) findViewById(R.id.device_update_areas);
		
		RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
		remoteManager.setResponseParser(new StringResponseParser());
		Request request = remoteManager.createPostRequest(Config.Values.URL);
		final Map<String, Object> map = CollectionUtil.newHashMap();
		map.put("user", eewebApplication.getUserDO().getUsername());
		request.setBody(JsonUtil.mapToJson(map));
		request.addHeader("type", "getAreaInfo");
		
		ProgressDialog progressDialog = showProgressDialog(R.string.app_read_data);
		progressDialog.setOnDismissListener(new LoadData());
		responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
		
	}

	public void handleBack(View v) {
		this.finish();
	}

	
	public void handleSubmit(View v) {
		newValue = areaSpinner.getSelectedItem().toString();
		if(StringUtil.isBlank(newValue)) {
			alert("当前设备名字不能为空");
			return;
		}
		
		if(deviceBean.getArea().equals(newValue)) {
			this.finish();
			return;
		}
		
		RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
		remoteManager.setResponseParser(new StringResponseParser());
		Request request = remoteManager.createPostRequest(Config.Values.URL);
		final Map<String, Object> map = CollectionUtil.newHashMap();
		map.put("snaddr", deviceBean.getSnaddr());
		map.put("area", newValue);
		request.setBody(JsonUtil.mapToJson(map));
		request.addHeader("type", "setDevArea");
		
		ProgressDialog progressDialog = showProgressDialog(R.string.app_up_data);
		progressDialog.setOnDismissListener(new UpData());
		upResponseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
	}
	
	private class LoadData implements OnDismissListener {

		@Override
		public void onDismiss(DialogInterface dialog) {
			if (responseFuture == null) {
				return;
			}

			try {
				Response response = responseFuture.get();
				JSONArray array = JsonUtil.getJsonArray(response.getModel());
				final List<String> result = CollectionUtil.newArrayList();
				try {
					if (array != null) {
						for (int i = 0, size = array.length(); i < size; i++) {
							result.add(array.getString(i));
						}
					}
				} catch (JSONException e) {
				}

				runOnUiThread(new Runnable() {
					public void run() {
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(DeviceSetupUpdateAreaActivity.this, R.layout.simple_spinner_item, result);
						areaSpinner.setAdapter(adapter);
					}
				});

			} catch (Exception e) {
				DeviceSetupUpdateAreaActivity.this.logError(e.getMessage(), e);
			}

		}

	}

	
	private class UpData implements OnDismissListener {
		
		@Override
		public void onDismiss(DialogInterface dialog) {
			if (upResponseFuture == null) {
				return;
			}
			
			try {
				Response response = upResponseFuture.get();
				if(!response.isDataSuccess()) {
					toastLong(R.string.data_opt_fail);
					return;
				} 
				
				toastLong(R.string.app_opt_success);
				deviceBean.setArea(newValue);
				Intent intent = DeviceSetupUpdateAreaActivity.this.getIntent();
				intent.putExtra("deviceBean", deviceBean);
				DeviceSetupUpdateAreaActivity.this.setResult(ACTIVITY_RESULT_CODE, intent);
				DeviceSetupUpdateAreaActivity.this.finish();
				
			} catch (Exception e) {
				DeviceSetupUpdateAreaActivity.this.logError(e.getMessage(), e);
			}
			
		}
		
	}


}
