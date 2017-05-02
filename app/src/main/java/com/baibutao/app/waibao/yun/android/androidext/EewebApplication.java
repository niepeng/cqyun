/**
 * 
 */
package com.baibutao.app.waibao.yun.android.androidext;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.TabHost;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.activites.common.TabFlushEnum;
import com.baibutao.app.waibao.yun.android.as.AsynchronizedInvoke;
import com.baibutao.app.waibao.yun.android.biz.LoadImgDO;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceDataBean;
import com.baibutao.app.waibao.yun.android.biz.dataobject.UserDO;
import com.baibutao.app.waibao.yun.android.common.GlobalUtil;
import com.baibutao.app.waibao.yun.android.common.MobileUseInfo;
import com.baibutao.app.waibao.yun.android.common.UserInfoHolder;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.localcache.FileCache;
import com.baibutao.app.waibao.yun.android.localcache.ImageCache;
import com.baibutao.app.waibao.yun.android.receives.ServiceSyncReceiver;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.tasks.CheckUpdateTask;
import com.baibutao.app.waibao.yun.android.tasks.message.MessageHelper;
import com.baibutao.app.waibao.yun.android.tasks.taskgroup.TaskGroup;
import com.baibutao.app.waibao.yun.android.util.ActionConstant;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.DateUtil;
import com.baidu.location.LocationClient;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author niepeng
 *
 * @date 2012-9-8 下午5:14:39
 */
public class EewebApplication extends Application {
	
	private AsynchronizedInvoke asynchronizedInvoke;
	
	private TabHost tabHost;
	
	public TabFlushEnum[] tabFlushEnums = { TabFlushEnum.IN_TIME, TabFlushEnum.ALARM, TabFlushEnum.MORE };

	private List<Activity> activities = CollectionUtil.newArrayList();
	
	private String uid;
	
	public LocationClient mLocationClient;
	
	private double longitude;
	
	private double latitude;
	
//	private Date lastRequestAlarmTime;

	private Date lastAlarmTime;
	
	private boolean reflushAlarmActivity;
	
	private UserDO userDO;

	private List<DeviceDataBean> tmpList;

	private AtomicInteger notifyId =  null;

	private int clearStartNotifyId = 1;

	public static final String setAlarmOnOffKey = "setAlarmOnOff";


	public boolean startNotification;

	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}
	
	private void init() {

		createSharedPrefs();
		
		Config config = Config.getConfig();
		config.init(this);

		GlobalUtil.init(this);
		
		MobileUseInfo mobileUseInfo = MobileUseInfo.getMobileUseInfo();
    	mobileUseInfo.init(this);
    	
		RemoteManager.init(this);
		ImageCache.init(this);
		FileCache.init(this);
		
		asynchronizedInvoke = new AsynchronizedInvoke();
		asynchronizedInvoke.init();
		
		// 删除本地图片
//		ImageCache.getLocalCacheManager().clearLocalCache();
	}

	private void createSharedPrefs() {
		try{
			Context context = createPackageContext(getString(R.string.prefs_file_name),  Context.CONTEXT_IGNORE_SECURITY);
		} catch (PackageManager.NameNotFoundException e){
			e.printStackTrace();
		}
	}
	

	@Override
	public void onTerminate() {
		try {
			asynchronizedInvoke.cleanup();
			mLocationClient.stop();
		} catch (Exception e) {
		}
		super.onTerminate();
	}

	public void addLoadImage(LoadImgDO loadImgDO) {
		asynchronizedInvoke.addLoadImage(loadImgDO);
	}

	public <V> Future<V> asyInvoke(Callable<V> callable) {
		return asynchronizedInvoke.invoke(callable);
	}

	public void asyCall(Runnable runnable) {
		asynchronizedInvoke.call(runnable);
	}

	public int getVersionCode() {
		return getPackageInfo().versionCode;
	}
	
	private PackageInfo getPackageInfo() {
		try {
			PackageManager packageManager = getPackageManager();
			return packageManager.getPackageInfo(getPackageName(), 0);
		} catch (Exception e) {
		}
		return null;
	} 
	
	public String getVersionName() {
		return getPackageInfo().versionName;
	}

	public void finishAllActivities() {
		synchronized (activities) {
			for (Activity a : activities) {
				a.finish();
			}
			activities.clear();
		}
	}

	public int notifyIdIncrementAndGet() {
		if(notifyId == null) {
			notifyId =  new AtomicInteger(randomInt(1, 1000000));
			clearStartNotifyId = notifyId.get();
		}
		return notifyId.incrementAndGet();
	}

	public int randomInt(int start, int end) {
		int value = (int) (Math.random() * (end - start)) + start;
		return value;
	}

	

	public void addActivity(Activity activity) {
		if (activity == null) {
			return;
		}
		synchronized (activities) {
			activities.add(activity);
		}
	}
	
	public MobileUseInfo getMobileUserInfo() {
		return MobileUseInfo.getMobileUseInfo();
	}
	

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	public TabHost getTabHost() {
		return tabHost;
	}

	public void setTabHost(TabHost tabHost) {
		this.tabHost = tabHost;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

//	public Date getLastRequestAlarmTime() {
//		return lastRequestAlarmTime;
//	}
//
//	public void setLastRequestAlarmTime(Date lastRequestAlarmTime) {
//		this.lastRequestAlarmTime = lastRequestAlarmTime;
//	}


	public Date getLastAlarmTime() {
		return lastAlarmTime;
	}

	public void setLastAlarmTime(Date lastAlarmTime) {
		this.lastAlarmTime = lastAlarmTime;
	}

	public boolean isReflushAlarmActivity() {
		return reflushAlarmActivity;
	}

	public void setReflushAlarmActivity(boolean reflushAlarmActivity) {
		this.reflushAlarmActivity = reflushAlarmActivity;
	}

	public UserDO getUserDO() {
		if(userDO == null) {
			userDO = UserInfoHolder.getUserDO(this);
		}
		return userDO;
	}

	public void setUserDO(UserDO userDO) {
		UserInfoHolder.saveUser(this, userDO);
		this.userDO = userDO;
	}

	PendingIntent pi = null;
	public void startNotification(long startTime, int repeatTime) {
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		long now = new Date().getTime();

//		Log.e("eewebApplication", "startNotification11111111");
		Log.e("eewebApplication", "now=" + now +",s=" + startTime + "("+(startTime-now)+")" +  ",r=" + repeatTime + ",d=" + DateUtil.format(new Date()));
		if (pi != null) {
//			Log.e("eewebApplication", "startNotification22222222");
			alarmManager.cancel(pi);
		}
//		Log.e("eewebApplication", "startNotification333333333333");
		Intent intent = new Intent(ServiceSyncReceiver.ACTION);
		pi = PendingIntent.getBroadcast(getApplicationContext(), ActionConstant.REQUEST_CODE_FLAG_VALUE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, repeatTime, pi);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + repeatTime, pi);
		} else {
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, repeatTime, pi);
		}

	}



	public void cancelNotificationAlarm() {
		try {
			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			if (pi != null) {
				am.cancel(pi);
				NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				nMgr.cancelAll();
			}
			Intent intent = new Intent(ServiceSyncReceiver.ACTION);
			PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE);
			am.cancel(sender);
			NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			nMgr.cancelAll();

		} catch(Exception e) {

		}
	}

	public void checkUpdateApp(final Activity baseActivity, Handler handler) {
		final MessageHelper messageHelper = new MessageHelper(false);
		final TaskGroup taskGroup = new TaskGroup();
		taskGroup.addMust(new CheckUpdateTask(this, baseActivity, handler, messageHelper));
		asyCall(taskGroup);
	}




	public void savePrefs(String key, int value) {
		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.prefs_file_name), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public int getPrefs(String key, int defaultValue) {
		SharedPreferences sharedPref = getSharedPreferences(getString(R.string.prefs_file_name), Context.MODE_PRIVATE);
		return sharedPref.getInt(key, defaultValue);
	}

	public List<DeviceDataBean> getTmpList() {
		return tmpList;
	}

	public void setTmpList(List<DeviceDataBean> tmpList) {
		this.tmpList = tmpList;
	}

	public int getClearStartNotifyId() {
		return clearStartNotifyId;
	}

	public void setClearStartNotifyId(int clearStartNotifyId) {
		this.clearStartNotifyId = clearStartNotifyId;
	}

	public AtomicInteger getNotifyId() {
		return notifyId;
	}
}