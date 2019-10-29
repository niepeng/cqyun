package com.baibutao.app.waibao.yun.android.androidext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadHelper;
import com.baibutao.app.waibao.yun.android.activites.device.DeviceAddResultListActivity;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;
import com.baibutao.app.waibao.yun.android.util.StringUtil;
import com.hiflying.smartlink.AbstractSmartLinkerActivity;
import com.hiflying.smartlink.ISmartLinker;
import com.hiflying.smartlink.R1;
import com.hiflying.smartlink.SmartLinkedModule;
import com.hiflying.smartlink.v7.MulticastSmartLinker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;


public class YunMulticastSmartLinkerActivityOld extends AbstractSmartLinkerActivity {

    private EewebApplication eewebApplication;
    private Future<Response> responseFuture;
    private ArrayList<String> snList;
    private LinearLayout circleProgressbarLayout;
    private CircleProgressView mCircleBar;
    private final int second = 60;
    private Dialog dialog;
    protected static final int ACTIVITY_RESULT_CODE = 1;



    public YunMulticastSmartLinkerActivityOld() {
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eewebApplication = (EewebApplication) this.getApplication();

//        circleProgressbarLayout = (LinearLayout) findViewById(R.id.circleProgressbarLayout);
//        mCircleBar = (CircleProgressView) findViewById(R.id.circleProgressbar);

        this.mStartButton.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View v) {
                String psw = YunMulticastSmartLinkerActivityOld.this.mPasswordEditText.getText().toString().trim();
                if(StringUtil.isBlank(psw)) {
                    toastShort("密码不能为空");
                    return;
                }

                dialog = new Dialog(YunMulticastSmartLinkerActivityOld.this, R.style.dialog_countdown);
                String sid = YunMulticastSmartLinkerActivityOld.this.mSsidEditText.getText().toString();
                LayoutInflater inflater = YunMulticastSmartLinkerActivityOld.this.getLayoutInflater();
                circleProgressbarLayout = (LinearLayout)inflater.inflate(R.layout.layout_countdown, null);
                mCircleBar =  (CircleProgressView)circleProgressbarLayout.findViewById(R.id.circleProgressbar);
                dialog.setContentView(circleProgressbarLayout);
                dialog.setCancelable(false);
                dialog.show();

//                if(!YunMulticastSmartLinkerActivityOld.this.mIsConncting) {
                try {
                    YunMulticastSmartLinkerActivityOld.this.mSmartLinker.setOnSmartLinkListener(YunMulticastSmartLinkerActivityOld.this);
                    YunMulticastSmartLinkerActivityOld.this.mSmartLinker.start(YunMulticastSmartLinkerActivityOld.this.getApplicationContext(), YunMulticastSmartLinkerActivityOld.this.mPasswordEditText.getText().toString().trim(), new String[]{YunMulticastSmartLinkerActivityOld.this.mSsidEditText.getText().toString().trim()});
//                        YunMulticastSmartLinkerActivityOld.this.mIsConncting = true;
//                        YunMulticastSmartLinkerActivityOld.this.mWaitingDialog.show();

                    mCircleBar.setProgressAndText(100, second + "秒");
//                        circleProgressbarLayout.setVisibility(View.VISIBLE);
                    startChangeCircle();
                } catch (Exception var3) {
                    var3.printStackTrace();
                }
//                }

            }
        });
    }

    private void startChangeCircle() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=99;i>=1;i--) {
                    updateProcess(i, getShowSecond(i));
                    SystemClock.sleep(second * 10);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }
        }).start();
    }

    public void updateProcess(final int flag, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//				mCircleBar.setProgress(flag);
                mCircleBar.setProgressAndText(flag, text);
            }
        });
    }

    private String getShowSecond(int i) {
        return (second-(int)((second * 1.d)/100*(100-i)))+ "秒";
    }


    @Override
    public void onLinked(final SmartLinkedModule module) {
        Log.e(TAG, "onLinkedsub"+ module.getMac());
        this.mViewHandler.post(new Runnable() {
            public void run() {
                addDevice(module.getMac());
//                alert(module.getMac());
//                toastShort(module.getMac()+ "...ip=" + module.getModuleIP());
//                Toast.makeText(YunMulticastSmartLinkerActivity.this.getApplicationContext(), YunMulticastSmartLinkerActivity.this.getString(R1.string("hiflying_smartlinker_new_module_found"), new Object[]{module.getMac(), module.getModuleIP()}), 0).show();
            }
        });
    }

    @Override
    public void onCompleted() {
        Log.w(TAG, "onCompleted");
        this.mViewHandler.post(new Runnable() {
            public void run() {
                dialog.dismiss();
//                Toast.makeText(YunMulticastSmartLinkerActivity.this.getApplicationContext(), YunMulticastSmartLinkerActivity.this.getString(R1.string("hiflying_smartlinker_completed")), 0).show();
                YunMulticastSmartLinkerActivityOld.this.mWaitingDialog.dismiss();
//                YunMulticastSmartLinkerActivity.this.mIsConncting = false;
                Intent intent = new Intent(YunMulticastSmartLinkerActivityOld.this, DeviceAddResultListActivity.class);
                intent.putStringArrayListExtra("snList", snList);
//                startActivityForResult(intent, ACTIVITY_RESULT_CODE);
                startActivityForResult(intent,ACTIVITY_RESULT_CODE);
                YunMulticastSmartLinkerActivityOld.this.finish();

            }
        });
//        alert(CollectionUtil.join(snList, ","));
    }

    public ISmartLinker setupSmartLinker() {
        return MulticastSmartLinker.getInstance();
    }

    public void handleBack(View view) {
        this.finish();
    }

    protected void toastShort(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void alert(CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(getMessageBoxTitle());
        alertDialog.setMessage(message);
        String buttonLabel = getString(R.string.app_btn_label_ok);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, buttonLabel, (Message) null);
        alertDialog.show();
    }

    protected String getMessageBoxTitle() {
        return this.getTitle().toString();
    }

    protected ProgressDialog showProgressDialog(int message) {
        String title = getString(R.string.app_name);
        String messageString = getString(message);
        return ProgressDialog.show(this, title, messageString);
    }


    public void addDevice(String mac) {
        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("mac", mac);
        map.put("user", eewebApplication.getUserDO().getUsername());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "addDevice");

        ProgressDialog progressDialog = showProgressDialog(R.string.app_up_data);
        progressDialog.setOnDismissListener(new UpData());
        responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
    }

    public synchronized void addValue(String sn) {
        if(snList == null) {
            snList = CollectionUtil.newArrayList();
        }
        if(!StringUtil.isBlank(sn)) {
            snList.add(sn);
        }
    }


    private class UpData implements DialogInterface.OnDismissListener {
        @Override
        public void onDismiss(DialogInterface dialog) {
            if (responseFuture == null) {
                return;
            }

            try {
                Response response = responseFuture.get();
                if (!response.isDataSuccess()) {
                    toastShort("添加设备失败,当前设备已经存在 或 MAC地址不正确");
                    return;
                }

                Log.e("SmartLinkerActivity", String.valueOf(response.getModel()));
                JSONObject jsonObject = JsonUtil.getJsonObject(response.getModel());
                JSONArray array = JsonUtil.getJsonArray(jsonObject, "snList");
                if(array != null) {
                    for (int i = 0, size = array.length(); i < size; i++) {
                        addValue(array.getString(i));
                    }
                }

//                toastShort(getString(com.baibutao.app.waibao.yun.android.R.string.app_opt_success));
//                Intent intent = DeviceAddBySeriActivity.this.getIntent();
//                DeviceAddBySeriActivity.this.setResult(ACTIVITY_RESULT_CODE, intent);
//                DeviceAddBySeriActivity.this.finish();

            } catch (Exception e) {
//                DeviceAddBySeriActivity.this.logError(e.getMessage(), e);
            }

        }

    }

}
