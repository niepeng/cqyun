package com.baibutao.app.waibao.yun.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;

import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;
import com.baibutao.app.waibao.yun.android.biz.dataobject.SetupDO;
import com.baibutao.app.waibao.yun.android.receives.ServiceSyncReceiver;
import com.baibutao.app.waibao.yun.android.tasks.NotificationTask;
import com.baibutao.app.waibao.yun.android.util.ActionConstant;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageService extends Service {

	// Binding details
	private final IBinder mBinder = new LocalBinder();

	private EewebApplication eewebApplication;

	private int currentRepeatTime;

	private AtomicBoolean flag = new AtomicBoolean(false);

	@Override
	public void onCreate() {
		super.onCreate();
		// 提升优先级
//		setForeground(true);

//		SetupDO setup = SetupInfoHolder.getDO(this);
//		if(currentRepeatTime == 0) {
//			currentRepeatTime = setup.getAlarmtime() * 1000;
//		}
//		if(currentRepeatTime == 0) {
//			currentRepeatTime = ActionConstant.TIMES;
//		}

		if(eewebApplication == null) {
			eewebApplication = (EewebApplication)getApplication();
		}

		currentRepeatTime = ActionConstant.TIMES;
		startSystemAlarm(System.currentTimeMillis() + ActionConstant.TIMES, currentRepeatTime);
	}

	private  synchronized void  startSystemAlarm(long startTime, int repeatTime) {
		int flag = eewebApplication.getPrefs(EewebApplication.setAlarmOnOffKey, 1);
		if (flag == 1) {
			eewebApplication.startNotification(startTime, repeatTime);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		int result = START_REDELIVER_INTENT;
		if(eewebApplication == null) {
			return result;
		}
		if(flag.get()) {
			return result;
		}

		// 报警更新时间周期有用，默认是1分钟
		SetupDO setup = new SetupDO();
//		SetupDO setup = SetupInfoHolder.getDO(this);
		// 实现业务
		if(isNeedNotification()) {
			flag.set(true);
			NotificationTask task = new NotificationTask(eewebApplication);
			task.execute();
			SystemClock.sleep(3000);
		}
		flag.set(false);
		// 更新轮询时间
		if(currentRepeatTime != setup.getAlarmtime() * 1000) {
			currentRepeatTime = setup.getAlarmtime() * 1000;
			startSystemAlarm(System.currentTimeMillis() + ActionConstant.TIMES, currentRepeatTime);
		}
		return result;
	}

	/**
	 * 考虑某个时间段不通知用户: 00:00 ~ 8:00
	 * @return
	 */
	private boolean isNeedNotification() {
		// 获取用户
		Date date = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int hour = c.get(Calendar.HOUR_OF_DAY);
//			int min = c.get(Calendar.MINUTE);
		if(hour < 8) {
			return false;
		}

		return true;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}


	public class LocalBinder extends Binder {
		MessageService getService() {
			return MessageService.this;
		}
	}

}
