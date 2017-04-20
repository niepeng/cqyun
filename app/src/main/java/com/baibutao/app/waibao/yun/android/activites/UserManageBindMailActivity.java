/**
 * 
 */
package com.baibutao.app.waibao.yun.android.activites;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

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

/**
 * @author niepeng
 * 
 * @date 2012-9-13 下午1:12:28
 */
public class UserManageBindMailActivity extends BaseActivity {

	private EditText mailEditText;
	
	private Future<Response> userInfoResponseFuture;

	private Future<Response> responseFuture;
	
	private String oldMail;
	
	 private static final String EMAIL_PATTEN = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";    
	 public static Pattern emailPatten = Pattern.compile(EMAIL_PATTEN);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.usermanage_bind_mail);
		
		mailEditText = (EditText) findViewById(R.id.usermanage_bind_mail_et);
		
		
		RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
		remoteManager.setResponseParser(new StringResponseParser());
		Request request = remoteManager.createPostRequest(Config.Values.URL);
		final Map<String, Object> map = CollectionUtil.newHashMap();
		map.put("user", eewebApplication.getUserDO().getUsername());
		request.setBody(JsonUtil.mapToJson(map));
		request.addHeader("type", "userInfo");

		ProgressDialog progressDialog = showProgressDialog(R.string.app_read_data);
		progressDialog.setOnDismissListener(new UserInfo());
		userInfoResponseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
		
	}

	public void handleBack(View v) {
		this.finish();
	}
	
	public void handleSubmit(View v) {
		String mail = mailEditText.getText().toString();
		
		if(StringUtil.isBlank(mail)) {
			toastLong(R.string.usermanage_bind_mail_empty);
			return;
		}
		
		Matcher matcher = emailPatten.matcher(mail);
		if (!matcher.matches()) {
			toastLong(R.string.usermanage_bind_mail_format_fail);
			return;
		}
		
		if(mail.equals(oldMail)) {
			toastLong(R.string.usermanage_bind_mail_same_oldmail);
			return;
		}
		
		RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
		remoteManager.setResponseParser(new StringResponseParser());
		Request request = remoteManager.createPostRequest(Config.Values.URL);
		final Map<String, Object> map = CollectionUtil.newHashMap();
		map.put("user", eewebApplication.getUserDO().getUsername());
		map.put("mail", mail);
		request.setBody(JsonUtil.mapToJson(map));
		request.addHeader("type", "bindMailbox");

		ProgressDialog progressDialog = showProgressDialog(R.string.app_up_data);
		progressDialog.setOnDismissListener(new BindMail());
		responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
	}
	
	private class UserInfo implements OnDismissListener {
		@Override
		public void onDismiss(DialogInterface dialog) {
			if (userInfoResponseFuture == null) {
				return;
			}
			try {
				Response response = userInfoResponseFuture.get();
				if (!response.isDataSuccess()) {
					return;
				}
				JSONObject jsonObject = JsonUtil.getJsonObject(response.getModel());
				oldMail = JsonUtil.getString(jsonObject, "mail", "");
				mailEditText.setText(oldMail);
			} catch (Exception e) {
				UserManageBindMailActivity.this.logError(e.getMessage(), e);
			}
		}

	}
	
	private class BindMail implements OnDismissListener {
		
		@Override
		public void onDismiss(DialogInterface dialog) {
			if (responseFuture == null) {
				return;
			}
			
			try {
				Response response = responseFuture.get();
				if (!response.isDataSuccess()) {
					UserManageBindMailActivity.this.toastLong(R.string.usermanage_bind_mail_fail);
					return;
				}
				
				toastLong(R.string.usermanage_bind_mail_success);
//				UserManageBindMailActivity.this.finish();
			} catch (Exception e) {
				UserManageBindMailActivity.this.logError(e.getMessage(), e);
			}
		}
		
	}

}
