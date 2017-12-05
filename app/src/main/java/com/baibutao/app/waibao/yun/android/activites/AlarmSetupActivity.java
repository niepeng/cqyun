package com.baibutao.app.waibao.yun.android.activites;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadHelper;
import com.baibutao.app.waibao.yun.android.biz.bean.UserBean;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;


public class AlarmSetupActivity extends BaseActivity {

    private ToggleButton totalSwitch;
    private View totalView;

    private ToggleButton exceptionSms;
    private ToggleButton exceptionPhone;
    private ToggleButton exceptionApp;
    private ToggleButton exceptionEmail;

    private ToggleButton offSms;
    private ToggleButton offPhone;
    private ToggleButton offApp;
    private ToggleButton offEmail;

    private UserBean userBean;

    private Future<Response> responseFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.alarm_setup);

        userBean = new UserBean();
        totalSwitch = (ToggleButton)findViewById(R.id.alarm_setup_total_switch);
        totalView = (View) findViewById(R.id.alarm_setup_total_switch_view);

        exceptionSms = (ToggleButton)findViewById(R.id.alarm_setup_exception_sms);
        exceptionPhone = (ToggleButton)findViewById(R.id.alarm_setup_exception_phone);
        exceptionApp = (ToggleButton)findViewById(R.id.alarm_setup_exception_app);
        exceptionEmail = (ToggleButton)findViewById(R.id.alarm_setup_exception_email);

        offSms = (ToggleButton)findViewById(R.id.alarm_setup_off_sms);
        offPhone = (ToggleButton)findViewById(R.id.alarm_setup_off_phone);
        offApp = (ToggleButton)findViewById(R.id.alarm_setup_off_app);
        offEmail = (ToggleButton)findViewById(R.id.alarm_setup_off_email);



        initData();

        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", eewebApplication.getUserDO().getUsername());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "getAllAlarmStatus");

        ProgressDialog progressDialog = showProgressDialog(R.string.app_read_data);
        progressDialog.setOnDismissListener(new AlarmSetupActivity.LoadData());
        responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
    }

    private void initData() {

    }


    public void handleBack(View v) {
        AlarmSetupActivity.this.finish();
    }

    /**
     * 手机号管理
     * @param v
     */
    public void handlePhoneManage(View v) {
        Intent intent = new Intent(AlarmSetupActivity.this, AlarmSetupPhoneActivity.class);
        startActivity(intent);
        return;
    }

    /**
     * 报警总开关操作
     * @param v
     */
    public void handleTotalSwitch(View v) {
        userBean.setAllAlarmStatus(((ToggleButton)v).isChecked() ? "1" : "0");
        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", eewebApplication.getUserDO().getUsername());
        map.put("allAlarmStatus", userBean.getAllAlarmStatus());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "switchAllAlarm");
        eewebApplication.asyInvoke(new ThreadHelper(null, request, remoteManager));
        refushView();
    }


    /**
     * 异常短信
     * @param v
     */
    public void handleExceptionSms(View v) {
        userBean.setData_msm(((ToggleButton)v).isChecked() ? "1" : "0");
        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", eewebApplication.getUserDO().getUsername());
        map.put("channel", "data_msm");
        map.put("alarmStatus", userBean.getData_msm());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "switchAlarmChannel");
        eewebApplication.asyInvoke(new ThreadHelper(null, request, remoteManager));
        refushView();
    }

    /**
     * 异常电话
     * @param v
     */
    public void handleExceptionPhone(View v) {
        userBean.setData_call(((ToggleButton)v).isChecked() ? "1" : "0");
        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", eewebApplication.getUserDO().getUsername());
        map.put("channel", "data_call");
        map.put("alarmStatus", userBean.getData_call());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "switchAlarmChannel");
        eewebApplication.asyInvoke(new ThreadHelper(null, request, remoteManager));
        refushView();
    }



    /**
     * 异常app推送
     * @param v
     */
    public void handleExceptionApp(View v) {
        userBean.setData_push(((ToggleButton)v).isChecked() ? "1" : "0");
        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", eewebApplication.getUserDO().getUsername());
        map.put("channel", "data_push");
        map.put("alarmStatus", userBean.getData_push());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "switchAlarmChannel");
        eewebApplication.asyInvoke(new ThreadHelper(null, request, remoteManager));
        refushView();
    }

    /**
     * 异常邮件
     * @param v
     */
    public void handleExceptionEmail(View v) {
        userBean.setData_mail(((ToggleButton)v).isChecked() ? "1" : "0");
        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", eewebApplication.getUserDO().getUsername());
        map.put("channel", "data_mail");
        map.put("alarmStatus", userBean.getData_mail());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "switchAlarmChannel");
        eewebApplication.asyInvoke(new ThreadHelper(null, request, remoteManager));
        refushView();
    }




    /**
     * 离线短信
     * @param v
     */
    public void handleOffSms(View v) {
        userBean.setState_msm(((ToggleButton)v).isChecked() ? "1" : "0");
        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", eewebApplication.getUserDO().getUsername());
        map.put("channel", "state_msm");
        map.put("alarmStatus", userBean.getState_msm());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "switchAlarmChannel");
        eewebApplication.asyInvoke(new ThreadHelper(null, request, remoteManager));
        refushView();
    }

    /**
     * 离线电话
     * @param v
     */
    public void handleOffPhone(View v) {
        userBean.setState_call(((ToggleButton)v).isChecked() ? "1" : "0");
        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", eewebApplication.getUserDO().getUsername());
        map.put("channel", "state_call");
        map.put("alarmStatus", userBean.getState_call());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "switchAlarmChannel");
        eewebApplication.asyInvoke(new ThreadHelper(null, request, remoteManager));
        refushView();
    }



    /**
     * 离线app推送
     * @param v
     */
    public void handleOffApp(View v) {
        userBean.setState_push(((ToggleButton)v).isChecked() ? "1" : "0");
        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", eewebApplication.getUserDO().getUsername());
        map.put("channel", "state_push");
        map.put("alarmStatus", userBean.getState_push());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "switchAlarmChannel");
        eewebApplication.asyInvoke(new ThreadHelper(null, request, remoteManager));
        refushView();
    }


    /**
     * 离线邮件
     * @param v
     */
    public void handleOffEmail(View v) {
        userBean.setState_mail(((ToggleButton)v).isChecked() ? "1" : "0");
        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", eewebApplication.getUserDO().getUsername());
        map.put("channel", "state_mail");
        map.put("alarmStatus", userBean.getState_mail());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "switchAlarmChannel");
        eewebApplication.asyInvoke(new ThreadHelper(null, request, remoteManager));
        refushView();
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

                JSONObject jsonObject = JsonUtil.getJsonObject(response.getModel());
                userBean.setAllAlarmStatus(JsonUtil.getString(jsonObject, "allAlarmStatus", null));
                JSONArray array = JsonUtil.getJsonArray(jsonObject, "alarmChannel");
                List<String> alarmChannelList = new ArrayList<String>();
                if(array != null) {
                    for(int i = 0; i < array.length(); i++ ) {
                        alarmChannelList.add(array.getString(i));
                    }
                }

                userBean.setData_push(alarmChannelList.contains("data_push") ? "1" : "0");
                userBean.setData_msm(alarmChannelList.contains("data_msm") ? "1" : "0");
                userBean.setData_call(alarmChannelList.contains("data_call") ? "1" : "0");
                userBean.setData_mail(alarmChannelList.contains("data_mail") ? "1" : "0");

                userBean.setState_push(alarmChannelList.contains("state_push") ? "1" : "0");
                userBean.setState_msm(alarmChannelList.contains("state_msm") ? "1" : "0");
                userBean.setState_call(alarmChannelList.contains("state_call") ? "1" : "0");
                userBean.setState_mail(alarmChannelList.contains("state_mail") ? "1" : "0");

                refushView();
            } catch (Exception e) {
                AlarmSetupActivity.this.logError(e.getMessage(), e);
            }
        }

    }

    private void refushView() {
        runOnUiThread(new Runnable() {
            public void run() {
                totalSwitch.setChecked("1".equals(userBean.getAllAlarmStatus()));
                if(totalSwitch.isChecked()) {
                    setViewVisible(totalView);
                } else {
                    setViewGone(totalView);
                }
                exceptionSms.setChecked("1".equals(userBean.getData_msm()));
                exceptionPhone.setChecked("1".equals(userBean.getData_call()));
                exceptionApp.setChecked("1".equals(userBean.getData_push()));
                exceptionEmail.setChecked("1".equals(userBean.getData_mail()));

                offSms.setChecked("1".equals(userBean.getState_msm()));
                offPhone.setChecked("1".equals(userBean.getState_call()));
                offApp.setChecked("1".equals(userBean.getState_push()));
                offEmail.setChecked("1".equals(userBean.getState_mail()));
            }
        });
    }

}
