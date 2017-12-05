package com.baibutao.app.waibao.yun.android.activites;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadHelper;
import com.baibutao.app.waibao.yun.android.activites.device.DeviceSetupUpdateNameActivity;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

import java.util.Map;
import java.util.concurrent.Future;

public class AlarmSetupPhoneAddActivity extends BaseActivity {

    private EditText editText;
    private Future<Response> responseFuture;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.alarm_setup_phone_add);

        Bundle bundle = this.getIntent().getExtras();
        editText = (EditText) findViewById(R.id.device_setup_name_et);
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
        phone = editText.getText().toString();
        if(StringUtil.isBlank(phone)) {
            alert("当前号码不能为空");
            return;
        }

        if(phone.length() > 11) {
            alert("号码最多11位");
            return;
        }

//        if(deviceBean.getDevName().equals(newName)) {
//            this.finish();
//            return;
//        }

        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("mobile", phone);
        map.put("user", eewebApplication.getUserDO().getUsername());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "addMobileToAccount");

        ProgressDialog progressDialog = showProgressDialog(R.string.app_up_data);
        progressDialog.setOnDismissListener(new AlarmSetupPhoneAddActivity.LoadData());
        responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
    }

    private class LoadData implements DialogInterface.OnDismissListener {

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
                        Intent intent = AlarmSetupPhoneAddActivity.this.getIntent();
                        AlarmSetupPhoneAddActivity.this.setResult(ACTIVITY_RESULT_CODE, intent);
                        AlarmSetupPhoneAddActivity.this.finish();
                    }
                });


            } catch (Exception e) {
                AlarmSetupPhoneAddActivity.this.logError(e.getMessage(), e);
            }

        }

    }


}

