package com.baibutao.app.waibao.yun.android.androidext;

import android.app.Activity;
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
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hiflying.smartlink.ISmartLinker;
import com.hiflying.smartlink.OnSmartLinkListener;
import com.hiflying.smartlink.R1;
import com.hiflying.smartlink.SmartLinkedModule;

import java.util.List;

/**
 * Created by lsb on 2019/10/29.
 * 反编译自 com.hiflying.smartlink.AbstractSmartLinkerActivity 这个文件，
 * 添加了获取ssid的兼容android9的获取方式，改造方法：private String getSSid()内部加了 getWIFISSID这个方法的调用。
 *
 */

public abstract class AbstractSmartLinkerActivity2 extends Activity implements OnSmartLinkListener {
    protected static String TAG = "SmartLinkerActivity";
    protected EditText mSsidEditText;
    protected EditText mPasswordEditText;
    protected Button mStartButton;
    protected ISmartLinker mSmartLinker;
    private boolean mIsConncting = false;
    protected Handler mViewHandler = new Handler();
    protected ProgressDialog mWaitingDialog;
    private BroadcastReceiver mWifiChangedReceiver;

    public AbstractSmartLinkerActivity2() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        R1.initContext(this.getApplicationContext());
        this.mSmartLinker = this.setupSmartLinker();
        this.mWaitingDialog = new ProgressDialog(this);
        this.mWaitingDialog.setMessage(this.getString(R1.string("hiflying_smartlinker_waiting")));
//        this.mWaitingDialog.setButton(-2, this.getString(17039360), new DialogInterface.OnClickListener() {
            this.mWaitingDialog.setButton(-2, "确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        this.mWaitingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                AbstractSmartLinkerActivity2.this.mSmartLinker.setOnSmartLinkListener((OnSmartLinkListener)null);
                AbstractSmartLinkerActivity2.this.mSmartLinker.stop();
                AbstractSmartLinkerActivity2.this.mIsConncting = false;
            }
        });
        this.setContentView(R1.layout("activity_hiflying_sniffer_smart_linker"));
        this.mSsidEditText = (EditText)this.findViewById(R1.id("editText_hiflying_smartlinker_ssid"));
        this.mPasswordEditText = (EditText)this.findViewById(R1.id("editText_hiflying_smartlinker_password"));
        this.mStartButton = (Button)this.findViewById(R1.id("button_hiflying_smartlinker_start"));
        this.mSsidEditText.setText(this.getSSid());
        this.mStartButton.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View v) {
                if(!AbstractSmartLinkerActivity2.this.mIsConncting) {
                    try {
                        AbstractSmartLinkerActivity2.this.mSmartLinker.setOnSmartLinkListener(AbstractSmartLinkerActivity2.this);
                        AbstractSmartLinkerActivity2.this.mSmartLinker.start(AbstractSmartLinkerActivity2.this.getApplicationContext(), AbstractSmartLinkerActivity2.this.mPasswordEditText.getText().toString().trim(), new String[]{AbstractSmartLinkerActivity2.this.mSsidEditText.getText().toString().trim()});
                        AbstractSmartLinkerActivity2.this.mIsConncting = true;
                        AbstractSmartLinkerActivity2.this.mWaitingDialog.show();
                    } catch (Exception var3) {
                        var3.printStackTrace();
                    }
                }

            }
        });
        this.mWifiChangedReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectivityManager = (ConnectivityManager)AbstractSmartLinkerActivity2.this.getSystemService("connectivity");
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(1);
                if(networkInfo != null && networkInfo.isConnected()) {
                    AbstractSmartLinkerActivity2.this.mSsidEditText.setText(AbstractSmartLinkerActivity2.this.getSSid());
                    AbstractSmartLinkerActivity2.this.mPasswordEditText.requestFocus();
                    AbstractSmartLinkerActivity2.this.mStartButton.setEnabled(true);
                } else {
                    AbstractSmartLinkerActivity2.this.mSsidEditText.setText(AbstractSmartLinkerActivity2.this.getString(R1.string("hiflying_smartlinker_no_wifi_connectivity")));
                    AbstractSmartLinkerActivity2.this.mSsidEditText.requestFocus();
                    AbstractSmartLinkerActivity2.this.mStartButton.setEnabled(false);
                    if(AbstractSmartLinkerActivity2.this.mWaitingDialog.isShowing()) {
                        AbstractSmartLinkerActivity2.this.mWaitingDialog.dismiss();
                    }
                }

            }
        };
        this.registerReceiver(this.mWifiChangedReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    protected void onDestroy() {
        super.onDestroy();
        this.mSmartLinker.setOnSmartLinkListener((OnSmartLinkListener)null);

        try {
            this.unregisterReceiver(this.mWifiChangedReceiver);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public void onLinked(final SmartLinkedModule module) {
        Log.w(TAG, "onLinked");
        this.mViewHandler.post(new Runnable() {
            public void run() {
                Toast.makeText(AbstractSmartLinkerActivity2.this.getApplicationContext(), AbstractSmartLinkerActivity2.this.getString(R1.string("hiflying_smartlinker_new_module_found"), new Object[]{module.getMac(), module.getModuleIP()}), 0).show();
            }
        });
    }

    public void onCompleted() {
        Log.w(TAG, "onCompleted");
        this.mViewHandler.post(new Runnable() {
            public void run() {
                Toast.makeText(AbstractSmartLinkerActivity2.this.getApplicationContext(), AbstractSmartLinkerActivity2.this.getString(R1.string("hiflying_smartlinker_completed")), 0).show();
                AbstractSmartLinkerActivity2.this.mWaitingDialog.dismiss();
                AbstractSmartLinkerActivity2.this.mIsConncting = false;
            }
        });
    }

    public void onTimeOut() {
        Log.w(TAG, "onTimeOut");
        this.mViewHandler.post(new Runnable() {
            public void run() {
                Toast.makeText(AbstractSmartLinkerActivity2.this.getApplicationContext(), AbstractSmartLinkerActivity2.this.getString(R1.string("hiflying_smartlinker_timeout")), 0).show();
                AbstractSmartLinkerActivity2.this.mWaitingDialog.dismiss();
                AbstractSmartLinkerActivity2.this.mIsConncting = false;
            }
        });
    }

    private String getSSid() {
        WifiManager wm = (WifiManager)this.getSystemService("wifi");
        if(wm != null) {
            WifiInfo wi = wm.getConnectionInfo();
            if(wi != null) {
                String ssid = wi.getSSID();
                if(ssid.length() > 2 && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                    return ssid.substring(1, ssid.length() - 1);
                }

                /**
                 * 兼容android9的获取方式,该类的核心改造点
                 */
                if(ssid != null && (ssid.indexOf("unknown") >=0  || ssid.indexOf("ssid") >=0) ) {
                    return getWIFISSID();
                }
                return ssid;
            }
        }


        return "";
    }

    /**
     * android9 以后无法获取sid的值，这里使用新的方式获取ssid
     * 获取SSID
     * @return  WIFI 的SSID
     */
    private String getWIFISSID() {
        String ssid = "";
        WifiManager my_wifiManager = ((WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE));
        assert my_wifiManager != null;
        WifiInfo wifiInfo = my_wifiManager.getConnectionInfo();
        ssid = wifiInfo.getSSID();
        int networkId = wifiInfo.getNetworkId();
        List<WifiConfiguration> configuredNetworks = my_wifiManager.getConfiguredNetworks();
        for (WifiConfiguration wifiConfiguration : configuredNetworks) {
            if (wifiConfiguration.networkId == networkId) {
                ssid = wifiConfiguration.SSID;
                break;
            }
        }

        if (ssid == null) {
            return "";
        }

        if (ssid.startsWith("\"")) {
            ssid = ssid.substring(1, ssid.length());
        }
        if (ssid.endsWith("\"")) {
            ssid = ssid.substring(0, ssid.length() - 1);
        }

        return ssid;
    }

    public abstract ISmartLinker setupSmartLinker();
}

