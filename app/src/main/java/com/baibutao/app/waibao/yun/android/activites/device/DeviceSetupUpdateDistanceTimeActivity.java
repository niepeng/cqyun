package com.baibutao.app.waibao.yun.android.activites.device;

import java.util.Map;
import java.util.concurrent.Future;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadHelper;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.util.ChangeUtil;
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
public class DeviceSetupUpdateDistanceTimeActivity extends BaseActivity {
	
	private EditText editText;
	private DeviceBean deviceBean;
	private Future<Response> responseFuture;
	private String newValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.device_setup_update_distance_time);

		Bundle bundle = this.getIntent().getExtras();
		deviceBean = (DeviceBean) bundle.get("deviceBean");
		editText = (EditText) findViewById(R.id.device_setup_distance_time_et);
		editText.setText(deviceBean.getDevGap());
	}

	public void handleBack(View v) {
		this.finish();
	}

	
	public void handleSubmit(View v) {
		newValue = editText.getText().toString();
		if(StringUtil.isBlank(newValue)) {
			alert("当前上传间隔不能为空");
			return;
		}
		
		int intValue = ChangeUtil.str2int(newValue);
		if(intValue == 0) {
			alert("上传间隔必须是大于0的整数");
			return;
		}
		
		if(intValue > 300) {
			alert("上传间隔最大300秒");
			return;
		}
		
		if(deviceBean.getDevGap().equals(newValue)) {
			this.finish();
			return;
		}
		
		RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
		remoteManager.setResponseParser(new StringResponseParser());
		Request request = remoteManager.createPostRequest(Config.Values.URL);
		final Map<String, Object> map = CollectionUtil.newHashMap();
		map.put("snaddr", deviceBean.getSnaddr());
		map.put("devGap", newValue);
		request.setBody(JsonUtil.mapToJson(map));
		request.addHeader("type", "modifyDeviceGap");
		
		ProgressDialog progressDialog = showProgressDialog(R.string.app_up_data);
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
				if (!response.isDataSuccess()) {
					return;
				}
				
				runOnUiThread(new Runnable() {
					public void run() {
						toastLong(R.string.app_opt_success);
						deviceBean.setDevGap(newValue);
						Intent intent = DeviceSetupUpdateDistanceTimeActivity.this.getIntent();
						intent.putExtra("deviceBean", deviceBean);
						DeviceSetupUpdateDistanceTimeActivity.this.setResult(ACTIVITY_RESULT_CODE, intent);
						DeviceSetupUpdateDistanceTimeActivity.this.finish();
					}
				});

				
			} catch (Exception e) {
				DeviceSetupUpdateDistanceTimeActivity.this.logError(e.getMessage(), e);
			}

		}

	}


}
