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
import com.baibutao.app.waibao.yun.android.biz.bean.TmpHistoryBean;
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


import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

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

	private TmpHistoryBean tmpHistoryBean;

	private AtomicInteger notifyId =  null;

	private int clearStartNotifyId = 1;

	public static final String setAlarmOnOffKey = "setAlarmOnOff";


	public boolean startNotification;

	// 友盟推送的设备id，需要跟当前用户作关联
	private String umengDeviceToken;

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

		// 友盟
		initUmeng();
		// 删除本地图片
//		ImageCache.getLocalCacheManager().clearLocalCache();
	}

	private void initUmeng() {

		/**
		 * 友盟文档：https://developer.umeng.com/docs/66632/detail/98581
		 *
		 */
		// 在此处调用基础组件包提供的初始化函数 相应信息可在应用管理 -> 应用信息 中找到 http://message.umeng.com/list/apps
		// 参数一：当前上下文context；
		// 参数二：应用申请的Appkey（需替换）；
		// 参数三：渠道名称；
		// 参数四：设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机；
		// 参数五：Push推送业务的secret 填充Umeng Message Secret对应信息（需替换）
		UMConfigure.init(this, "5c398662b465f566b5001405", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "fc5b535b0e83376e4c3692487de2499d");

		//获取消息推送代理示例
		PushAgent mPushAgent = PushAgent.getInstance(this);
		//注册推送服务，每次调用register方法都会回调该接口
		mPushAgent.register(new IUmengRegisterCallback() {

			@Override
			public void onSuccess(String deviceToken) {
				//注册成功会返回deviceToken deviceToken是推送消息的唯一标志
//				Log.i(TAG,"注册成功：deviceToken：-------->  " + deviceToken);
				Log.e("ffffffffffff", "注册成功：deviceToken：-------->  " + deviceToken);
				umengDeviceToken = deviceToken;
			}

			@Override
			public void onFailure(String s, String s1) {
				Log.e("ffffffffffff","注册失败：-------->  " + "s:" + s + ",s1:" + s1);
			}
		});
//
//		UmengMessageHandler messageHandler = new UmengMessageHandler() {
//
//			/**
//			 * 自定义通知栏样式的回调方法
//			 */
//			@Override
//			public Notification getNotification(Context context, UMessage msg) {
//				switch (msg.builder_id) {
//					case 1:
//						Notification.Builder builder = new Notification.Builder(context);
//						RemoteViews myNotificationView = new RemoteViews(context.getPackageName(),
//								R.layout.notification_view);
//						myNotificationView.setTextViewText(R.id.notification_title, msg.title);
//						myNotificationView.setTextViewText(R.id.notification_text, msg.text);
//						myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
//						myNotificationView.setImageViewResource(R.id.notification_small_icon,
//								getSmallIconId(context, msg));
//						builder.setContent(myNotificationView)
//								.setSmallIcon(getSmallIconId(context, msg))
//								.setTicker(msg.ticker)
//								.setAutoCancel(true);
//
//						return builder.getNotification();
//					default:
//						//默认为0，若填写的builder_id并不存在，也使用默认。
//						return super.getNotification(context, msg);
//				}
//			}
//		};
//		mPushAgent.setMessageHandler(messageHandler);
//
//		UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
//
//			@Override
//			public void dealWithCustomAction(Context context, UMessage msg){
//				Log.e(TAG,"click");
//			}
//
//		};
//
//		mPushAgent.setNotificationClickHandler(notificationClickHandler);
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

	public TmpHistoryBean getTmpHistoryBean() {
		return tmpHistoryBean;
	}

	public void setTmpHistoryBean(TmpHistoryBean tmpHistoryBean) {
		this.tmpHistoryBean = tmpHistoryBean;
	}

	public String getUmengDeviceToken() {
		return umengDeviceToken;
	}
}