/**
 * 
 */
package com.baibutao.app.waibao.yun.android.activites;

import java.util.Map;
import java.util.concurrent.Future;

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
import com.baibutao.app.waibao.yun.android.util.MD5;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

/**
 * @author niepeng
 * 
 * @date 2012-9-13 下午1:12:28
 */
public class UserManageUpdatePswActivity extends BaseActivity {

	private EditText oldPswEditText;
	
	private EditText newPsw1EditText;
	
	private EditText newPsw2EditText;
	
	private Future<Response> responseFuture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.usermanage_update_psw);
		
		oldPswEditText = (EditText) findViewById(R.id.usermanage_update_psw_old);
		newPsw1EditText = (EditText) findViewById(R.id.usermanage_update_psw_new1);
		newPsw2EditText = (EditText) findViewById(R.id.usermanage_update_psw_new2);
	}

	public void handleBack(View v) {
		this.finish();
	}
	
	public void handleSubmit(View v) {
		String oldPsw = oldPswEditText.getText().toString();
		String newPsw1 = newPsw1EditText.getText().toString();
		String newPsw2 = newPsw2EditText.getText().toString();
		
		if(StringUtil.isBlank(oldPsw)) {
			toastLong(R.string.usermanage_update_psw_old_empty);
			return;
		}
		
		if(StringUtil.isBlank(newPsw1)) {
			toastLong(R.string.usermanage_update_psw_new1_empty);
			return;
		}
		
		if(StringUtil.isBlank(newPsw2)) {
			toastLong(R.string.usermanage_update_psw_new2_empty);
			return;
		}
		
		if(!newPsw1.equals(newPsw2)) {
			toastLong(R.string.usermanage_update_psw_new_not_same);
			return;
		}
		
		if(newPsw1.length() < 6) {
			toastLong(R.string.usermanage_update_psw_new_length_limit);
			return;
		}
		
		if(!oldPsw.equals(eewebApplication.getUserDO().getPsw())) {
			toastLong(R.string.usermanage_update_psw_old_wrong);
			return;
		}
		
		RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
		remoteManager.setResponseParser(new StringResponseParser());
		Request request = remoteManager.createPostRequest(Config.Values.URL);
		final Map<String, Object> map = CollectionUtil.newHashMap();
		map.put("user", eewebApplication.getUserDO().getUsername());
		map.put("oldPass", MD5.encrypt(oldPsw));
		map.put("newPass", MD5.encrypt(newPsw1));
		request.setBody(JsonUtil.mapToJson(map));
		request.addHeader("type", "modifyPass");
		
		
		ProgressDialog progressDialog = showProgressDialog(R.string.app_up_data);
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
					UserManageUpdatePswActivity.this.toastLong(R.string.usermanage_update_psw_bind_mail_first);
					return;
				}
				
				toastLong(R.string.usermanage_update_psw_success);
				eewebApplication.getUserDO().setPsw(newPsw1EditText.getText().toString());
				eewebApplication.setUserDO(eewebApplication.getUserDO());
				UserManageUpdatePswActivity.this.finish();
				
			} catch (Exception e) {
				UserManageUpdatePswActivity.this.logError(e.getMessage(), e);
			}
			

		}

	}

}
