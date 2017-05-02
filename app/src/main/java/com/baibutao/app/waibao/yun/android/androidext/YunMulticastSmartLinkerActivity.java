package com.baibutao.app.waibao.yun.android.androidext;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.util.StringUtil;
import com.hiflying.smartlink.AbstractSmartLinkerActivity;
import com.hiflying.smartlink.ISmartLinker;
import com.hiflying.smartlink.v7.MulticastSmartLinker;
import com.hiflying.smartlink.v7.MulticastSmartLinkerActivity;


public class YunMulticastSmartLinkerActivity extends AbstractSmartLinkerActivity {
    public YunMulticastSmartLinkerActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mStartButton.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(View v) {
                String psw = YunMulticastSmartLinkerActivity.this.mPasswordEditText.getText().toString().trim();
                if(StringUtil.isBlank(psw)) {
                    toastShort("密码不能为空");
                    return;
                }

//                if(!YunMulticastSmartLinkerActivity.this.mIsConncting) {
                    try {
                        YunMulticastSmartLinkerActivity.this.mSmartLinker.setOnSmartLinkListener(YunMulticastSmartLinkerActivity.this);
                        YunMulticastSmartLinkerActivity.this.mSmartLinker.start(YunMulticastSmartLinkerActivity.this.getApplicationContext(), YunMulticastSmartLinkerActivity.this.mPasswordEditText.getText().toString().trim(), new String[]{YunMulticastSmartLinkerActivity.this.mSsidEditText.getText().toString().trim()});
//                        YunMulticastSmartLinkerActivity.this.mIsConncting = true;
                        YunMulticastSmartLinkerActivity.this.mWaitingDialog.show();
                    } catch (Exception var3) {
                        var3.printStackTrace();
                    }
//                }

            }
        });
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
}
