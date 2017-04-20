/**
 * 
 */
package com.baibutao.app.waibao.yun.android.activites;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadHelper;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

/**
 * @author niepeng
 * 
 * @date 2012-9-13 下午1:40:03
 */
public class FeedbackActivity extends BaseActivity implements DialogInterface.OnDismissListener {

	private TextView feedbackWordTextView;

	private EditText contentEditText;

	// 限制字数
	private final int LIMIT_WORD = 200;

	private Future<Response> responseFuture;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_feedback);

		((TextView) findViewById(R.id.setup_title)).getPaint().setFakeBoldText(true);

		feedbackWordTextView = (TextView) findViewById(R.id.feedback_word);
		contentEditText = (EditText) findViewById(R.id.feedback_content);

		initData();
	}

	
	private void initData() {
		setWord(0);
		contentEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				setWord(s.length());
			}
		});
		
	}
	
	private void setWord(int word) {
		int remainder = LIMIT_WORD - word;
		if (remainder < 0) {
			String msg = "您输入的内容已经超过" + (-remainder) + "个字了,请修改";
			toastLong(msg);
			feedbackWordTextView.setText(msg);
			return;
		}
		feedbackWordTextView.setText(String.format(getString(R.string.more_feedback_word_tv_label), remainder));
	}
	
	
	public void handleBack(View btn) {
		this.finish();
	}
	public void handleSubmit(View btn) {
		/*String email = emailText.getText().toString();
		if(StringUtil.isEmpty(email)) {
			toastLong(R.string.more_feed_back_email_empty);
			return;
		}
		String name = nickText.getText().toString();*/
		
		String content = contentEditText.getText().toString();
		if (StringUtil.isEmpty(content)) {
			toastLong(R.string.more_feed_back_content_empty);
			return;
		}
		
		if (content.length() > LIMIT_WORD) {
			toastLong(String.format(getString(R.string.more_feed_back_content_range), LIMIT_WORD));
			return;
		}
		
		RemoteManager remoteManager = RemoteManager.getPostOnceRemoteManager();
		Request request = remoteManager.createPostRequest(Config.getConfig().getProperty(Config.Names.SUGGESTION_URL));
		
//		request.addParameter("email", email);
//		request.addParameter("name", name);
		/*UserDO userDO = UserDOHolder.get(this);
		if(userDO != null) {
			request.addParameter("uid", userDO.getUid());
		}*/
		
		request.addParameter("content", content);
		
		request.addParameter("actionTarget", "feedBackAction");
		request.addParameter("actionEvent","addFeedBack");
		request.addParameter("refer","10");
//		parameters.put("visitorId",  ); getUid()
		request.addParameter("visitorId",  0);
		request.addParameter("content", content);
		
		ProgressDialog progressDialog = showProgressDialog(R.string.app_up_data);
		progressDialog.setOnDismissListener(this);
		responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request));
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		Response response = null;
		try {
			if (responseFuture == null) {
				return;
			}
			response = responseFuture.get();
		} catch (InterruptedException ex) {
			FeedbackActivity.this.logError(ex.toString(), ex);
		} catch (ExecutionException ex) {
			FeedbackActivity.this.logError(ex.toString(), ex);
		}

		if (response == null) {
			return;
		}
		
		if (response.isSuccess()) {
			toastLong("反馈成功");
			FeedbackActivity.this.finish();
		} else {
			toastLong(response.getMessage());
		}
		
	}
	
}
