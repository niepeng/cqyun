package com.baibutao.app.waibao.yun.android.activites.device;

/**
 * Created by lsb on 17/4/24.
 */


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadHelper;
import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.biz.dataobject.SetupDO;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.util.ActionConstant;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * 蜂鸣器开关设置
 * @author niepeng
 *
 * @date 2012-9-13 下午1:12:28
 */
public class DeviceSetupBeepStatusActivity extends BaseActivity {

    private ToggleButton toggleButton;

    // 0 关闭，1打开
    private int checkFlag;

    private DeviceBean deviceBean;

    private Future<Response> responseFuture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.device_setup_beep_status);

        toggleButton = (ToggleButton) findViewById(R.id.device_setup_beep_status_btn);
        Bundle bundle = this.getIntent().getExtras();
        deviceBean = (DeviceBean) bundle.get("deviceBean");
        if(deviceBean.isBeepOpen()) {
            checkFlag = 1;
            toggleButton.setChecked(true);
        } else {
            checkFlag = 0;
            toggleButton.setChecked(false);
        }
    }


    public void handleBack(View v) {
        this.finish();
    }

    public void handleSubmit(View v) {
        int newValue = toggleButton.isChecked() ? 1 : 0;
        if (newValue == checkFlag) {
            toastShort("当前未修改");
            return;
        }
        checkFlag = newValue;

        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", eewebApplication.getUserDO().getUsername());
        map.put("snaddr", deviceBean.getSnaddr());
        map.put("beepStatus", String.valueOf(checkFlag));
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "setRealAlarm");
        ProgressDialog progressDialog = showProgressDialog(R.string.app_up_data);
        progressDialog.setOnDismissListener(new DeviceSetupBeepStatusActivity.OptData());
        responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));

    }


    private class OptData implements DialogInterface.OnDismissListener {
        @Override
        public void onDismiss(DialogInterface dialog) {
            if ( responseFuture == null) {
                return;
            }

            try {

                Response response = responseFuture.get();
                if (!response.isDataSuccess()) {
                    return;
                }

                /**
                 * {"code" : 0，"msg": "success","beepStatus":"0或者1"｝
                 */

                final JSONObject jsonObject = JsonUtil.getJsonObject(response.getModel());
                deviceBean.setBeepStatus(String.valueOf(checkFlag));

                runOnUiThread(new Runnable() {
                    public void run() {
                        toastShort(JsonUtil.getString(jsonObject, "msg", null));
                        Intent intent = DeviceSetupBeepStatusActivity.this.getIntent();
                        intent.putExtra("deviceBean", deviceBean);
                        DeviceSetupBeepStatusActivity.this.setResult(ACTIVITY_RESULT_CODE, intent);
                        DeviceSetupBeepStatusActivity.this.finish();
                    }
                });

            } catch (Exception e) {
                DeviceSetupBeepStatusActivity.this.logError(e.getMessage(), e);
            }

        }

    }


}




