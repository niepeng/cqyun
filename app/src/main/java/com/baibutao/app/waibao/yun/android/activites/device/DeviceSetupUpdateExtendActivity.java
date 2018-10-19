package com.baibutao.app.waibao.yun.android.activites.device;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadHelper;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceExtendBean;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.util.ChangeUtil;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;

import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * <p>标题: </p>
 * <p>描述: </p>
 * <p>版权: lsb</p>
 * <p>创建时间: 2017年3月17日  下午3:57:47</p>
 * <p>作者：niepeng</p>
 */
public class DeviceSetupUpdateExtendActivity extends BaseActivity {

	private DeviceBean deviceBean;
	private Future<Response> responseFuture;
	private Future<Response> upResponseFuture;

	private EditText highTempEt;
	private EditText lowTempEt;
	private TextView distanceTempTv;

	private EditText highHumiEt;
	private EditText lowHumiEt;
	private TextView distanceHumiTv;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.device_setup_update_extend);

		Bundle bundle = this.getIntent().getExtras();
		deviceBean = (DeviceBean) bundle.get("deviceBean");

		highTempEt = (EditText) findViewById(R.id.device_update_extend_high_temp_et);
		lowTempEt = (EditText) findViewById(R.id.device_update_extend_low_temp_et);
		distanceTempTv = (TextView) findViewById(R.id.device_update_extend_distance_temp_et);

		highHumiEt = (EditText) findViewById(R.id.device_update_extend_high_humi_et);
		lowHumiEt = (EditText) findViewById(R.id.device_update_extend_low_humi_et);
		distanceHumiTv = (TextView) findViewById(R.id.device_update_extend_distance_humi_et);

		setEditTextInhibitInput(highTempEt);
		setEditTextInhibitInput(lowTempEt);


		if(!deviceBean.hasAuth()) {
			highTempEt.setFocusable(false);
			highTempEt.setEnabled(false);
			lowTempEt.setFocusable(false);
			lowTempEt.setEnabled(false);
			highHumiEt.setFocusable(false);
			highHumiEt.setEnabled(false);
			lowHumiEt.setFocusable(false);
			lowHumiEt.setEnabled(false);
		}

		RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
		remoteManager.setResponseParser(new StringResponseParser());
		Request request = remoteManager.createPostRequest(Config.Values.URL);
		final Map<String, Object> map = CollectionUtil.newHashMap();
		map.put("snaddr", deviceBean.getSnaddr());
		map.put("user", eewebApplication.getUserDO().getUsername());
		request.setBody(JsonUtil.mapToJson(map));
		request.addHeader("type", "getThreshold");

		ProgressDialog progressDialog = showProgressDialog(R.string.app_read_data);
		progressDialog.setOnDismissListener(new LoadData());
		responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));

	}

	/**
	 * 只能输入一个小数点，负号只能输入到最前面
	 *
	 * @param editText
	 */
	public static void setEditTextInhibitInput(final EditText editText){
		InputFilter filter=new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				if (".".equals(source)) {
					if (editText.getText().toString().indexOf(".") >=0) {
						return "";
					}
					return source;
				}
				if ("-".equals(source)) {
					if (("".equals(editText.getText().toString()) || editText.getText().toString().indexOf("-") < 0) && start == 0) {
						return source;
					}
					return "";
				}

				return source;
			}
		};
		editText.setFilters(new InputFilter[]{filter});
	}


	public void handleBack(View v) {
		this.finish();
	}


	public void handleSubmit(View v) {

		if(!deviceBean.hasAuth()) {
			alert("当前暂无权限阈值设置");
			return;
		}

		/**
		 * 允许设定阈值范围：
		 温度：-200℃ - 500℃
		 湿度：0-99.99%
		 */

		DeviceExtendBean tmpBean = new DeviceExtendBean();
		tmpBean.setSnaddr(deviceBean.getSnaddr());
		tmpBean.setMaxTemp(highTempEt.getText().toString());
		tmpBean.setMinTemp(lowTempEt.getText().toString());
		tmpBean.setTempHC(distanceTempTv.getText().toString());

		tmpBean.setMaxHumi(highHumiEt.getText().toString());
		tmpBean.setMinHumi(lowHumiEt.getText().toString());
		tmpBean.setHumiHC(distanceHumiTv.getText().toString());

		double highTempDouble = ChangeUtil.str2double(highTempEt.getText().toString());
		double lowTempDouble = ChangeUtil.str2double(lowTempEt.getText().toString());
		double highHumiDouble = ChangeUtil.str2double(highHumiEt.getText().toString());
		double lowHumiDouble = ChangeUtil.str2double(lowHumiEt.getText().toString());

		if(highTempDouble > 500) {
			toastLong("最高温度最大为500℃");
			return;
		}

		if(lowTempDouble < -200) {
			toastLong("最低温度最小为-200℃");
			return;
		}

		if(highTempDouble <= lowTempDouble) {
			toastLong("最高温度不能小于最低温度");
			return;
		}

		if(highHumiDouble > 99.99) {
			toastLong("最高湿度为99.99%");
			return;
		}

		if(lowHumiDouble < 0) {
			toastLong("最低湿度为0%");
			return;
		}

		if (highHumiDouble <= lowHumiDouble) {
			toastLong("最高湿度不能小于最低湿度");
			return;
		}



		if(!deviceBean.getDeviceExtendBean().isDataChange(tmpBean)) {
			this.finish();
			return;
		}

		RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
		remoteManager.setResponseParser(new StringResponseParser());
		Request request = remoteManager.createPostRequest(Config.Values.URL);
		final Map<String, Object> map = CollectionUtil.newHashMap();
		map.put("snaddr", tmpBean.getSnaddr());
		map.put("user", eewebApplication.getUserDO().getUsername());
		map.put("maxTemp", tmpBean.getMaxTemp());
		map.put("minTemp", tmpBean.getMinTemp());
		map.put("tempHC", tmpBean.getTempHC());

		map.put("maxHumi", tmpBean.getMaxHumi());
		map.put("minHumi", tmpBean.getMinHumi());
		map.put("humiHC", tmpBean.getHumiHC());

		request.setBody(JsonUtil.mapToJson(map));
		request.addHeader("type", "modifyTH");

		ProgressDialog progressDialog = showProgressDialog(R.string.app_up_data);
		progressDialog.setOnDismissListener(new UpData());
		upResponseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
	}

	public void initData() {
		DeviceExtendBean extendBean = deviceBean.getDeviceExtendBean();
		if(extendBean == null) {
			return;
		}

		highTempEt.setText(extendBean.getMaxTemp());
		lowTempEt.setText(extendBean.getMinTemp());
		distanceTempTv.setText(extendBean.getTempHC());

		highHumiEt.setText(extendBean.getMaxHumi());
		lowHumiEt.setText(extendBean.getMinHumi());
		distanceHumiTv.setText(extendBean.getTempHC());
	}

	private class LoadData implements OnDismissListener {
		@Override
		public void onDismiss(DialogInterface dialog) {
			if (responseFuture == null) {
				return;
			}

			try {

				Response response = responseFuture.get();
				JSONObject jsonObject = JsonUtil.getJsonObject(response.getModel());
				JSONObject json = JsonUtil.getJSONObject(jsonObject, "array");
				if(json == null) {
					toastLong(R.string.data_opt_fail);
					return;
				}
				DeviceExtendBean extendBean = JsonUtil.jsonToBean(json.toString() , DeviceExtendBean.class);
				if (extendBean == null || !deviceBean.getSnaddr().equals(extendBean.getSnaddr())) {
					toastLong(R.string.data_opt_fail);
					return;
				}
				deviceBean.setDeviceExtendBean(extendBean);

				runOnUiThread(new Runnable() {
					public void run() {
						initData();
					}
				});


			} catch (Exception e) {
				DeviceSetupUpdateExtendActivity.this.logError(e.getMessage(), e);
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
				if (response.isDataSuccess()) {
					toastLong(R.string.app_opt_success);
					DeviceSetupUpdateExtendActivity.this.finish();
				} else {
					toastLong(R.string.data_opt_fail);
				}
			} catch (Exception e) {
				DeviceSetupUpdateExtendActivity.this.logError(e.getMessage(), e);
			}

		}

	}


}
