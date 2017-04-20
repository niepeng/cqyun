package com.baibutao.app.waibao.yun.android.activites;

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
import com.baibutao.app.waibao.yun.android.biz.dataobject.UserDO;
import com.baibutao.app.waibao.yun.android.common.UserInfoHolder;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;
import com.baibutao.app.waibao.yun.android.util.MD5;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

import java.util.Map;
import java.util.concurrent.Future;

public class LoginActivity extends BaseActivity {

	private EditText usernameText;

	private EditText pswText;

	private Future<Response> responseFuture;

	private static final int LIMIT_WORD = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login);

		usernameText = (EditText) findViewById(R.id.login_username);
		pswText = (EditText) findViewById(R.id.login_password);

		UserDO userDO = UserInfoHolder.getUserDO(this);
		if(userDO != null) {
			usernameText.setText(userDO.getUsername());
			pswText.setText(userDO.getPsw());
		}
	}
	
	public void handleLogin(View v) {
//		Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
//		startActivity(intent);
		String username = usernameText.getText().toString();
		String psw = pswText.getText().toString();

		if (StringUtil.isEmpty(username)) {
			toastLong(R.string.login_username_empty);
			return;
		}

		if (username.length() >= LIMIT_WORD) {
			toastLong(R.string.login_username_length_limit);
			return;
		}

		if (StringUtil.isEmpty(psw)) {
			toastLong(R.string.login_psw_empty);
			return;
		}

		if (psw.length() >= LIMIT_WORD) {
			toastLong(R.string.login_psw_length_limit);
			return;
		}

		
		RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
//		Request request = remoteManager.createPostRequest(Config.getConfig().getProperty(Config.Names.USER_LOGIN_URL));
//		request.addParameter("userName", username);
//		request.addParameter("psw", MD5.getMD5(psw.getBytes()));
		remoteManager.setResponseParser(new StringResponseParser());
		Request request = remoteManager.createPostRequest(Config.Values.URL);
//		request.setBody("{\"user\":\"cqy\",\"password\":\""+MD5.encrypt("123456")+"\"}");
		final Map<String, Object> map = CollectionUtil.newHashMap();
		map.put("user", username);
		map.put("password", MD5.encrypt(psw));
		request.setBody(JsonUtil.mapToJson(map));
		request.addHeader("type", "login");
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				
			}
		}).start();
		
		ProgressDialog progressDialog = showProgressDialog(R.string.app_read_data);
		progressDialog.setOnDismissListener(new Login());
		responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
	}

	private class Login implements OnDismissListener {

		@Override
		public void onDismiss(DialogInterface dialog) {
			if (responseFuture == null) {
				return;
			}

			try {
				Response response = responseFuture.get();
				if (!response.isDataSuccess()) {
					LoginActivity.this.toastLong(R.string.login_username_psw_wrong);
					return;
				}
//				JSONObject jsonObject = JsonUtil.getJsonObject(response.getModel());
				UserDO userDO = new UserDO();
				userDO.setUsername(usernameText.getText().toString());
				userDO.setPsw(pswText.getText().toString());
				toastLong("登录成功,加载数据中...");
				eewebApplication.setUserDO(userDO);
				
//				UserInfoHolder.saveUser(LoginActivity.this, userDO);
				
//				Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);

			} catch (Exception e) {
				LoginActivity.this.logError(e.getMessage(), e);
			}

		}

	}
}
