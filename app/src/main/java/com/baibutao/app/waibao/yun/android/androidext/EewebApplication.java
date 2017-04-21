/**
 * 
 */
package com.baibutao.app.waibao.yun.android.androidext;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.TabHost;

import com.baibutao.app.waibao.yun.android.R;
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
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
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

	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}
	
	private void init() {
		
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

}