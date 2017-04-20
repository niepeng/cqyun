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
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

/**
 * @author niepeng
 * 
 */
public class DeviceAddBySeriActivity extends BaseActivity {
	
	private EditText snaddrEt;
	private EditText acEt;
	private Future<Response> responseFuture;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.device_add_by_seri);

		snaddrEt = (EditText) findViewById(R.id.device_add_by_seri_snaddr);
		acEt = (EditText) findViewById(R.id.device_add_by_seri_ac);

	}

	public void handleBack(View v) {
		this.finish();
	}
	
	public void handleSubmit(View v) {
		String snaddr = snaddrEt.getText().toString();
		String ac = acEt.getText().toString();
		
		if(StringUtil.isBlank(snaddr)) {
			toastLong("SN码不能为空");
			return;
		}
		
		if(StringUtil.isBlank(ac)) {
			toastLong("AC码不能为空");
			return;
		}
		
		RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
		remoteManager.setResponseParser(new StringResponseParser());
		Request request = remoteManager.createPostRequest(Config.Values.URL);
		final Map<String, Object> map = CollectionUtil.newHashMap();
		map.put("snaddr", snaddr);
		map.put("ac", ac);
		map.put("devName", "");
		map.put("user", eewebApplication.getUserDO().getUsername());
		request.setBody(JsonUtil.mapToJson(map));
		request.addHeader("type", "addDeviceBySN");
		
		ProgressDialog progressDialog = showProgressDialog(R.string.app_up_data);
		progressDialog.setOnDismissListener(new UpData());
		responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
	
	}
	
	private class UpData implements OnDismissListener {
		@Override
		public void onDismiss(DialogInterface dialog) {
			if (responseFuture == null) {
				return;
			}

			try {
				Response response = responseFuture.get();
				if (!response.isDataSuccess()) {
					toastLong("添加设备失败,当前设备已经存在 或 SN和AC码对应不正确");
					return;
				}
				
				toastLong(R.string.app_opt_success);
				Intent intent = DeviceAddBySeriActivity.this.getIntent();
				DeviceAddBySeriActivity.this.setResult(ACTIVITY_RESULT_CODE, intent);
				DeviceAddBySeriActivity.this.finish();
				
			} catch (Exception e) {
				DeviceAddBySeriActivity.this.logError(e.getMessage(), e);
			}

		}

	}
	
}
