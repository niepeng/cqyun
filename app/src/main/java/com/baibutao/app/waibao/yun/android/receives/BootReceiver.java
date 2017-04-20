package com.baibutao.app.waibao.yun.android.receives;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;

public class BootReceiver extends BroadcastReceiver {
	
	private EewebApplication eewebApplication;
	
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            // 重新计算检查报警时间
        	Context app = context.getApplicationContext();
        	if (app instanceof EewebApplication) {
//				cardApplication = (CardApplication) app;
//				NotificationTask task = new NotificationTask(cardApplication);
//				task.execute();
			}
        }
    }
    
	
}