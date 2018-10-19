/**
 * 
 */
package com.baibutao.app.waibao.yun.android.activites;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
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

import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author niepeng
 * 
 * @date 2012-9-13 下午1:12:28
 */
public class ForgotPswActivity extends BaseActivity {

	private EditText accountEditText;
	
	private Future<Response> responseFuture;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.forgot_psw);
		accountEditText = (EditText) findViewById(R.id.forgot_psw_et);
	}

	public void handleBack(View v) {
		this.finish();
	}
	
	public void handleSubmit(View v) {
		String account = accountEditText.getText().toString();
		
		if(StringUtil.isBlank(account)) {
			toastLong("账号不能为空");
			return;
		}
		
		RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
		remoteManager.setResponseParser(new StringResponseParser());
		Request request = remoteManager.createPostRequest(Config.Values.URL);
		final Map<String, Object> map = CollectionUtil.newHashMap();
		map.put("user", account);
		request.setBody(JsonUtil.mapToJson(map));
		request.addHeader("type", "retrievePass");

		ProgressDialog progressDialog = showProgressDialog(R.string.app_up_data);
		progressDialog.setOnDismissListener(new ForgotPsw());
		responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
	}

	private class ForgotPsw implements OnDismissListener {

		@Override
		public void onDismiss(DialogInterface dialog) {
			if (responseFuture == null) {
				return;
			}

			try {
				Response response = responseFuture.get();
				if (!response.isDataSuccess()) {
					JSONObject jsonObject = JsonUtil.getJsonObject(response.getModel());
					String msg = JsonUtil.getString(jsonObject, "msg", "操作失败，确保先绑定邮箱才能找回密码。");
					ForgotPswActivity.this.toastLong(msg);
					return;
				}
				toastLong("请求成功，系统将在10秒内发送至您的绑定邮箱，请登录邮箱取回密码。");
			} catch (Exception e) {
				ForgotPswActivity.this.logError(e.getMessage(), e);
			}
		}

	}

}
