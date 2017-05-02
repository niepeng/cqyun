package com.baibutao.app.waibao.yun.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

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
		Log.e("notificationAlarm", "onCreateonCreateonCreate");
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

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			Log.e("notificationAlarm", "rrrrrrrrrrrrrrrrr");
			eewebApplication.startNotification(System.currentTimeMillis(), currentRepeatTime);
		}

//		Log.e("notificationAlarm", "111111111111");
		int result = START_REDELIVER_INTENT;
		if(eewebApplication == null) {
			return result;
		}
//		Log.e("notificationAlarm", "2222222222");
		if(flag.get()) {
			return result;
		}
//		Log.e("notificationAlarm", "3333333333333");
		// 报警更新时间周期有用，默认是1分钟
		SetupDO setup = new SetupDO();
//		SetupDO setup = SetupInfoHolder.getDO(this);
		// 实现业务
		if(isNeedNotification()) {
//			Log.e("notificationAlarm", "4444444444444");
			flag.set(true);
			NotificationTask task = new NotificationTask(eewebApplication);
			task.execute();
			SystemClock.sleep(3000);
		}
		flag.set(false);
		// 更新轮询时间
//		Log.e("notificationAlarm", "5555555555555555");
		if(currentRepeatTime != setup.getAlarmtime() * 1000) {
//			Log.e("notificationAlarm", "ffffffffffffffffffffff");
			currentRepeatTime = setup.getAlarmtime() * 1000;
			startSystemAlarm(System.currentTimeMillis() + ActionConstant.TIMES, currentRepeatTime);
		}
//		Log.e("notificationAlarm", "666666666666666");



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
