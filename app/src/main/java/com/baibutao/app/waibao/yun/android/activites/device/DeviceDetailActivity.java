package com.baibutao.app.waibao.yun.android.activites.device;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceDataBean;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remotesimple.Httpclient;
import com.baibutao.app.waibao.yun.android.util.ChangeUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <p>标题: </p>
 * <p>描述: </p>
 * <p>版权: lsb</p>
 * <p>创建时间: 2017年3月16日  下午4:08:15</p>
 * <p>作者：niepeng</p>
 */
public class DeviceDetailActivity extends BaseActivity {

    private DeviceBean deviceBean;

    private TextView titleTv;
    private TextView tempTv;
    private TextView humiTv;
    private TextView statusTv;
    private TextView timeTv;

    private Timer mTimer;

    private static final int REFRESH_TIME = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.device_detail);

        Bundle bundle = this.getIntent().getExtras();
        deviceBean = (DeviceBean) bundle.get("deviceBean");

        titleTv = (TextView) findViewById(R.id.device_detail_title);
        tempTv = (TextView) findViewById(R.id.device_detail_tmp_tv);
        humiTv = (TextView) findViewById(R.id.device_detail_humi_tv);
        statusTv = (TextView) findViewById(R.id.device_detail_status_tv);
        timeTv = (TextView) findViewById(R.id.device_detail_time_tv);
        initData();

        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleReflush();
                    }
                });
            }
        }, REFRESH_TIME, REFRESH_TIME);
    }

    @Override
    protected void onDestroy() {
        mTimer.cancel();
        super.onDestroy();
    }

    public void initData() {
        titleTv.setText(deviceBean.getDevName() + "\n" + deviceBean.getArea());
        if (deviceBean.getDataBean().isSuccess()) {
            tempTv.setText(deviceBean.getDataBean().getTemp());
            humiTv.setText(deviceBean.getDataBean().getHumi());
            statusTv.setText(deviceBean.getDataBean().showStatus());
            timeTv.setText("最后上传时间：" + deviceBean.getDataBean().getTime());
        } else {
            tempTv.setText("--.--");
            humiTv.setText("--.--");
            statusTv.setTextColor(getResources().getColor(R.color.black));
            statusTv.setText(getResources().getString(R.string.app_device_offline));
            timeTv.setText("");
        }
    }

    public void handleBack(View v) {
        this.finish();
    }

    public void handleHistoryData(View v) {
        Intent intent = new Intent(DeviceDetailActivity.this, DeviceHistoryActivity.class);
        intent.putExtra("deviceBean", deviceBean);
        startActivity(intent);
    }

    public void handleHistoryAlarm(View v) {
        Intent intent = new Intent(DeviceDetailActivity.this, DeviceHistoryAlarmActivity.class);
        intent.putExtra("deviceBean", deviceBean);
        startActivity(intent);
    }

    public void handleSetupInfo(View v) {
        Intent intent = new Intent(DeviceDetailActivity.this, DeviceSetupActivity.class);
        intent.putExtra("deviceBean", deviceBean);
        startActivityForResult(intent, ACTIVITY_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ACTIVITY_RESULT_CODE) {
            Bundle bundle = data.getExtras();
            DeviceBean tmp = (DeviceBean) bundle.get("deviceBean");
            if (tmp != null) {
                deviceBean = tmp;
                initData();
            }
            return;
        }

        if (resultCode == ACTIVITY_DEL_RESULT_CODE) {
            Intent intent = DeviceDetailActivity.this.getIntent();
            DeviceDetailActivity.this.setResult(ACTIVITY_DEL_RESULT_CODE, intent);
            DeviceDetailActivity.this.finish();
            return;
        }
    }

    private void handleReflush() {
        new GetDeviceTask().execute(deviceBean, null, null);
    }

    private class GetDeviceTask extends AsyncTask<DeviceBean, Void, Void> {
        protected Void doInBackground(DeviceBean... devicebeans) {
            if (devicebeans != null) {
                DeviceDataBean dataBean = requestDeviceDataBean(devicebeans[0]);
                deviceBean.setDataBean(dataBean);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            initData();
        }
    }

    private DeviceDataBean requestDeviceDataBean(DeviceBean bean) {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("TYPE", "getRTData");
        Map<String, String> bodyMap = new HashMap<String, String>();
        bodyMap.put("snaddr", bean.getSnaddr());
        bodyMap.put("curve", bean.getCurve());
        String content = Httpclient.subPostForBody(Config.Values.URL, JsonUtil.mapToJson(bodyMap), Httpclient.DEFAULT_CHARSET, headerMap);

        JSONObject json = JsonUtil.getJsonObject(content);
        if (json == null) {
            return null;
        }
        DeviceDataBean dataBean = new DeviceDataBean();
        dataBean.setAbnormal(JsonUtil.getString(json, "abnormal", null));
        dataBean.setTime(JsonUtil.getString(json, "time", null));
        JSONObject humiJson = JsonUtil.getJSONObject(json, "humi");
        dataBean.setHumi(JsonUtil.getString(humiJson, "value", null));
        dataBean.setHumiStatus(ChangeUtil.str2int(JsonUtil.getString(humiJson, "status", null)));

        JSONObject tempJson = JsonUtil.getJSONObject(json, "temp");
        dataBean.setTemp(JsonUtil.getString(tempJson, "value", null));
        dataBean.setTempStatus(ChangeUtil.str2int(JsonUtil.getString(tempJson, "status", null)));

        return dataBean;
    }

}
