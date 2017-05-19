package com.baibutao.app.waibao.yun.android.activites.device;

import java.util.Map;
import java.util.concurrent.Future;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadHelper;
import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.http.HttpClientUtil;
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
public class DeviceSetupUpdateNameActivity extends BaseActivity {
	
	private EditText editText;
	private DeviceBean deviceBean;
	private Future<Response> responseFuture;
	private String newName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.device_setup_update_name);

		Bundle bundle = this.getIntent().getExtras();
		deviceBean = (DeviceBean) bundle.get("deviceBean");
		editText = (EditText) findViewById(R.id.device_setup_name_et);
		editText.setText(deviceBean.getDevName());
		setEditTextInhibitInputSpace(editText);
	}

	public void handleBack(View v) {
		this.finish();
	}

	/**
	 * 禁止EditText输入空格
	 * @param editText
	 */
	public static void setEditTextInhibitInputSpace(EditText editText){
		InputFilter filter=new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				if (" ".equals(source)) {
					return "";
				}
				return source;
			}
		};
		editText.setFilters(new InputFilter[]{filter});
	}

	
	public void handleSubmit(View v) {
		newName = editText.getText().toString();
		if(StringUtil.isBlank(newName)) {
			alert("当前设备名字不能为空");
			return;
		}

		if(newName.length() > 12) {
			alert("设备名称最多12个字");
			return;
		}
		
		if(deviceBean.getDevName().equals(newName)) {
			this.finish();
			return;
		}

//		try {
//			newName = new String(newName.getBytes(), "utf-8");
//		} catch(Exception e) {
//
//		}

		RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
		remoteManager.setResponseParser(new StringResponseParser());
		Request request = remoteManager.createPostRequest(Config.Values.URL);
		final Map<String, Object> map = CollectionUtil.newHashMap();
		map.put("snaddr", deviceBean.getSnaddr());
		map.put("devName", newName);
		map.put("user", eewebApplication.getUserDO().getUsername());
		request.setBody(JsonUtil.mapToJson(map));
		request.addHeader("type", "setDevName");

//		HttpClientUtil.
		
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
						deviceBean.setDevName(newName);
						Intent intent = DeviceSetupUpdateNameActivity.this.getIntent();
						intent.putExtra("deviceBean", deviceBean);
						DeviceSetupUpdateNameActivity.this.setResult(ACTIVITY_RESULT_CODE, intent);
						DeviceSetupUpdateNameActivity.this.finish();
					}
				});

				
			} catch (Exception e) {
				DeviceSetupUpdateNameActivity.this.logError(e.getMessage(), e);
			}

		}

	}


}
