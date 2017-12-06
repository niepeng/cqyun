package com.baibutao.app.waibao.yun.android.activites;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadHelper;
import com.baibutao.app.waibao.yun.android.activites.device.DeviceSetupUpdateExtendActivity;
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
import com.baibutao.app.waibao.yun.android.util.MD5;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.Future;


public class RegisterActivity extends BaseActivity {

    private Future<Response> upResponseFuture;

    private EditText accountEt;
    private EditText pswEt;
    private TextView pswConfirmEt;
    private EditText nickEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.register);

        accountEt = (EditText) findViewById(R.id.register_account);
        pswEt = (EditText) findViewById(R.id.register_psw);
        pswConfirmEt = (TextView) findViewById(R.id.register_psw_confirm);
        nickEt = (EditText) findViewById(R.id.register_nick);
    }

    public void handleBack(View v) {
        this.finish();
    }


    public void handleSubmit(View v) {
        int minLength = 6;
        int maxLength = 20;

        String account = accountEt.getText().toString();
        String psw = pswEt.getText().toString();
        String pswConfrim = pswConfirmEt.getText().toString();
        String nick = nickEt.getText().toString().trim();

        if(StringUtil.isBlank(account)) {
            toastLong("账号不能为空");
            return;
        }
        if(account.length() < minLength) {
            toastLong("账号不能小于" + minLength + "位");
            return;
        }
        if(account.length() > maxLength) {
            toastLong("账号不能大于" + maxLength + "位");
            return;
        }

        if(StringUtil.isBlank(psw)) {
            toastLong("密码不能为空");
            return;
        }
        if(psw.length() < minLength) {
            toastLong("密码不能小于" + minLength + "位");
            return;
        }
        if(psw.length() > maxLength) {
            toastLong("密码不能大于" + maxLength + "位");
            return;
        }

        if(StringUtil.isBlank(pswConfrim)) {
            toastLong("确认密码不能为空");
            return;
        }
        if(pswConfrim.length() < minLength) {
            toastLong("确认密码不能小于" + minLength + "位");
            return;
        }
        if(pswConfrim.length() > maxLength) {
            toastLong("确认密码不能大于" + maxLength + "位");
            return;
        }

        if(!pswConfrim.equals(psw)) {
            toastLong("密码与确认密码不同");
            return;
        }

        if(StringUtil.isBlank(nick)) {
            toastLong("昵称不能为空");
            return;
        }
        if(nick.length() > maxLength) {
            toastLong("昵称不能大于" + maxLength + "位");
            return;
        }

        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", account);
        map.put("password", MD5.encrypt(psw));
        map.put("name", nick);
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "register");
        ProgressDialog progressDialog = showProgressDialog(R.string.app_up_data);
        progressDialog.setOnDismissListener(new RegisterActivity.UpData());
        upResponseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
    }

    public void initData() {
    }

    private class UpData implements DialogInterface.OnDismissListener {
        @Override
        public void onDismiss(DialogInterface dialog) {
            if (upResponseFuture == null) {
                return;
            }

            try {
                Response response = upResponseFuture.get();
                if (response.isDataSuccess()) {
                    toastLong("注册成功，管理员正在审核");
                    RegisterActivity.this.finish();
                } else {
                    JSONObject jsonObject = JsonUtil.getJsonObject(response.getModel());
                    String msg = JsonUtil.getString(jsonObject, "msg", null);
                    if (StringUtil.isBlank(msg)) {
                        msg = "注册失败";
                    }
                    toastLong(msg);
                }
            } catch (Exception e) {
                RegisterActivity.this.logError(e.getMessage(), e);
            }

        }

    }

}
