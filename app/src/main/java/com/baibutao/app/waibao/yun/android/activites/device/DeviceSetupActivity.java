package com.baibutao.app.waibao.yun.android.activites.device;

import java.util.Map;
import java.util.concurrent.Future;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

/**
 * @author niepeng
 * 
 */
public class DeviceSetupActivity extends BaseActivity {
	
	private TextView snTv;
	private TextView nameTv;
	private TextView areaTv;
	private TextView distaceTimeTv;
	
	private DeviceBean deviceBean;
	private Future<Response> responseFuture;
	private Future<Response> delResponseFuture;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.device_setup);

		Bundle bundle = this.getIntent().getExtras();
		deviceBean = (DeviceBean) bundle.get("deviceBean");

		snTv = (TextView) findViewById(R.id.device_setup_sn);
		nameTv = (TextView) findViewById(R.id.devcie_setup_name);
		areaTv = (TextView) findViewById(R.id.device_setup_area);
		distaceTimeTv = (TextView) findViewById(R.id.device_setup_time);
		
		initData();
		
		RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
		remoteManager.setResponseParser(new StringResponseParser());
		Request request = remoteManager.createPostRequest(Config.Values.URL);
		final Map<String, Object> map = CollectionUtil.newHashMap();
		map.put("snaddr", deviceBean.getSnaddr());
		request.setBody(JsonUtil.mapToJson(map));
		request.addHeader("type", "getDevInfo");
		
		ProgressDialog progressDialog = showProgressDialog(R.string.app_read_data);
		progressDialog.setOnDismissListener(new LoadData());
		responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
	}

	private void initData() {
		snTv.setText("SN:" + deviceBean.getSnaddr());
		nameTv.setText(deviceBean.getDevName());
		areaTv.setText(deviceBean.getArea());
		distaceTimeTv.setText(deviceBean.getDevGap());
	}

	public void handleBack(View v) {
		Intent intent = DeviceSetupActivity.this.getIntent();
		intent.putExtra("deviceBean", deviceBean);
		DeviceSetupActivity.this.setResult(ACTIVITY_RESULT_CODE, intent);
		DeviceSetupActivity.this.finish();
	}
	
	public void handleSetupDevName(View v) {
		Intent intent = new Intent(DeviceSetupActivity.this, DeviceSetupUpdateNameActivity.class);
		intent.putExtra("deviceBean", deviceBean);
		startActivityForResult(intent, ACTIVITY_RESULT_CODE);
	}
	
	public void handleSetupArea(View v) {
		Intent intent = new Intent(DeviceSetupActivity.this, DeviceSetupUpdateAreaActivity.class);
		intent.putExtra("deviceBean", deviceBean);
		startActivityForResult(intent, ACTIVITY_RESULT_CODE);
	
	}
	
	public void handleSetupDevGap(View v) {
		Intent intent = new Intent(DeviceSetupActivity.this, DeviceSetupUpdateExtendActivity.class);
		intent.putExtra("deviceBean", deviceBean);
		startActivity(intent);
	}
	
	public void handleSetupDistanceTime(View v) {
		Intent intent = new Intent(DeviceSetupActivity.this, DeviceSetupUpdateDistanceTimeActivity.class);
		intent.putExtra("deviceBean", deviceBean);
		startActivityForResult(intent, ACTIVITY_RESULT_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == ACTIVITY_RESULT_CODE) {
			Bundle bundle = data.getExtras();
			DeviceBean tmp = (DeviceBean) bundle.get("deviceBean");
			if (tmp != null) {
				deviceBean = tmp;
				initData();
			}
		}
	}
	
	/**
	 * 删除操作
	 */
	public void handleSubmit(View v) {
		confirm("确定要删除该设备吗？", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				optDelte();
			}
		}, null);
	}
	
	protected void optDelte() {
		RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
		remoteManager.setResponseParser(new StringResponseParser());
		Request request = remoteManager.createPostRequest(Config.Values.URL);
		final Map<String, Object> map = CollectionUtil.newHashMap();
		map.put("snaddr", deviceBean.getSnaddr());
		map.put("user", eewebApplication.getUserDO().getUsername());
		request.setBody(JsonUtil.mapToJson(map));
		request.addHeader("type", "delDevice");
		
		ProgressDialog progressDialog = showProgressDialog(R.string.app_up_data);
		progressDialog.setOnDismissListener(new DeleteData());
		delResponseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
	}

	private class LoadData implements OnDismissListener {

		@Override
		public void onDismiss(DialogInterface dialog) {
			if (responseFuture == null) {
				return;
			}

			try {

				Response response = responseFuture.get();
				if (!response.isDataSuccess()) {
					return;
				}

				JSONObject jsonObject = JsonUtil.getJsonObject(response.getModel());
				deviceBean.setDevGap(JsonUtil.getString(jsonObject, "devGap", null));
				runOnUiThread(new Runnable() {
					public void run() {
						distaceTimeTv.setText(deviceBean.getDevGap());
					}
				});
			} catch (Exception e) {
				DeviceSetupActivity.this.logError(e.getMessage(), e);
			}

		}

	}
	
	private class DeleteData implements OnDismissListener {
		
		@Override
		public void onDismiss(DialogInterface dialog) {
			if (delResponseFuture == null) {
				return;
			}
			
			try {
				
				Response response = delResponseFuture.get();
				if (!response.isDataSuccess()) {
					toastLong(R.string.data_opt_fail);
					return;
				}
				toastLong(R.string.app_opt_success);
				Intent intent = DeviceSetupActivity.this.getIntent();
				DeviceSetupActivity.this.setResult(ACTIVITY_DEL_RESULT_CODE, intent);
				DeviceSetupActivity.this.finish();
				
			} catch (Exception e) {
				DeviceSetupActivity.this.logError(e.getMessage(), e);
			}
			
		}
		
	}


}
