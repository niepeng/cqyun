package com.baibutao.app.waibao.yun.android.activites;

/**
 * Created by lsb on 17/4/24.
 */


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadHelper;
import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;
import com.baibutao.app.waibao.yun.android.biz.dataobject.SetupDO;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.util.ActionConstant;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;
import com.baibutao.app.waibao.yun.android.util.MD5;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author niepeng
 *
 * @date 2012-9-13 下午1:12:28
 */
public class UserManagerSetAlarmInfoActivity extends BaseActivity {

    private ToggleButton toggleButton;

    // 0 关闭，1打开
    private int checkFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.usermanage_set_alarm);

        toggleButton = (ToggleButton) findViewById(R.id.usermanage_set_alarm);
        checkFlag = eewebApplication.getPrefs(EewebApplication.setAlarmOnOffKey, 1);
        if(checkFlag == 1) {
            toggleButton.setChecked(true);
        } else {
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
        eewebApplication.savePrefs(EewebApplication.setAlarmOnOffKey, newValue);
        toastShort("修改成功");
        if (checkFlag == 1) {
            SetupDO setup = new SetupDO();
            eewebApplication.startNotification(System.currentTimeMillis() + ActionConstant.TIMES, setup.getAlarmtime() * 1000);
        } else {
            eewebApplication.cancelNotificationAlarm();
        }
    }


}

