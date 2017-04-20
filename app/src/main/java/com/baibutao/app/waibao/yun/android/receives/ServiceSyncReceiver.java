package com.baibutao.app.waibao.yun.android.receives;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.baibutao.app.waibao.yun.android.service.MessageService;

public class ServiceSyncReceiver extends BroadcastReceiver {

	// 与 mainfast中配置的一致
	public static final String ACTION = "com.baibutao.app.waibao.yun.android.receiver.ServiceSyncReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		final Intent service = new Intent(context, MessageService.class);
		context.startService(service);
	}

}
