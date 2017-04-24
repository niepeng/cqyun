package com.baibutao.app.waibao.yun.android.tasks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.MainActivity;
import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;
import com.baibutao.app.waibao.yun.android.biz.bean.AlarmBean;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.common.Constant;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.DateUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NotificationTask {

    private EewebApplication eewebApplication;

    public NotificationTask(EewebApplication eewebApplication) {
        this.eewebApplication = eewebApplication;
    }

    public void execute() {
        try {
            // 请求服务端，获取是否需要通知的更新
//			final RemoteManager remoteManager = RemoteManager.getFullFeatureRemoteManager();
//			final Request request = remoteManager.createQueryRequest(Config.getConfig().getProperty(Config.Names.ALARM_LIST_URL));
//			request.addParameter("userId", eewebApplication.getUserDO().getId());
//			request.addParameter("psw", MD5.getMD5(eewebApplication.getUserDO().getPsw().getBytes()));
//
            if(eewebApplication.getLastAlarmTime() == null) {
                eewebApplication.setLastAlarmTime(new Date());
            }

            final boolean requestAlarmFlag = true;
            final RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
//			remoteManager.setResponseParser(new StringResponseParser());
            final Request request = remoteManager.createQueryRequest(Config.Values.YUN_ALARM_URL);
            request.addParameter("user", eewebApplication.getUserDO().getUsername());
            if (requestAlarmFlag) {
//                Date requestDate = DateUtil.parse("2017-04-24 08:07:00", "yyyy-MM-dd HH:mm:ss");
                Date requestDate = DateUtil.changeSecond(eewebApplication.getLastAlarmTime(), 1);
				request.addParameter("requestTime", DateUtil.format(requestDate, DateUtil.DATE_FMT));
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response response = remoteManager.execute(request);
//						if (response.isSuccess()) {
//							Tuple.Tuple2<List<AlarmMsgDO>, Date> result = JSONHelper.json2AlarmList((JSONObject) response.getModel());
//							List<AlarmMsgDO> list = result._1();
//							Date currentTime = result._2();
//							if (currentTime != null) {
//								eewebApplication.setLastRequestAlarmTime(currentTime);
//							}
//							notifyMsg(list, requestAlarmFlag);
//						}

                        if (response.isSuccess()) {
                            optNotify((JSONObject) response.getModel(), requestAlarmFlag);
                        }
                    } catch (Exception e) {
                        Log.e(NotificationTask.class.getName(), "notfication task error", e);
                    }
                }
            }).start();
        } catch (Exception e) {
            Log.e(NotificationTask.class.getName(), "notfication task error", e);
        }
    }

    private void optNotify(JSONObject json, boolean requestAlarmFlag) {
        List<DeviceBean> beanList = CollectionUtil.newArrayList();
        DeviceBean bean = null;
        AlarmBean alarmBean = null;
        JSONObject aralmJson = null;
        JSONObject data = JsonUtil.getJSONObject(json, "data");
        String requsetTime = JsonUtil.getString(data, "requsetTime", null);
        JSONArray alarmArray = JsonUtil.getJsonArray(data, "deviceList");
        bean = new DeviceBean();

		/*
         * "snaddr": "W2000302",
		 * "devName": "3#-N-120",
		 * "area": "仓库2",
		 * "msg": "设备离线 开始报警",
		 * "alarmTime": "2017-04-04 16:31:00",
		 * "type": 6
		 */
        if (alarmArray != null) {
            for (int i = 0, size = alarmArray.length(); i < size; i++) {
                try {
                    aralmJson = alarmArray.getJSONObject(i);
                    bean.setSnaddr(JsonUtil.getString(aralmJson, "snaddr", null));
                    bean.setDevName(JsonUtil.getString(aralmJson, "devName", null));
                    bean.setArea(JsonUtil.getString(aralmJson, "area", null));

                    alarmBean = new AlarmBean();
                    alarmBean.setMsg(JsonUtil.getString(aralmJson, "msg", null));
                    alarmBean.setAlarmTime(JsonUtil.getString(aralmJson, "alarmTime", null));
                    alarmBean.setType(JsonUtil.getString(aralmJson, "type", null));
                    bean.setAlarmBean(alarmBean);
                    beanList.add(bean);
                } catch (Exception e) {
                }
            }

        }

        if (CollectionUtil.isEmpty(beanList)) {
            return;
        }
        eewebApplication.setLastAlarmTime(DateUtil.parseNoException(requsetTime));

        if (requestAlarmFlag) {
            notifyMsgNew(beanList);
        }
    }

    private void notifyMsgNew(List<DeviceBean> list) {
//		// 发送通知
//		NotificationManager mNotificationManager = (NotificationManager) eewebApplication.getSystemService(Context.NOTIFICATION_SERVICE);
//		// 定义通知栏展现的内容信息
//		int icon = R.drawable.ic_launcher;
//		CharSequence tickerText = "监控平台通知";
//		long when = System.currentTimeMillis() + 2000;
//		Notification notification = new Notification(icon, tickerText, when);
//
//		notification.defaults = Notification.DEFAULT_SOUND;
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//		// 定义下拉通知栏时要展现的内容信息
//		Context context = eewebApplication.getApplicationContext();
//		CharSequence contentTitle = "监控云平台-您的设备出现报警";
//		String content = "";
//		for (int i = 0; i < 3 && i < list.size(); i++) {
//			DeviceBean tmp = list.get(i);
//			content += (tmp.getArea() + "-" + tmp.getDevName() + "：" + tmp.getAlarmBean().getMsg() + "; \r\n ");
//		}
//		CharSequence contentText = content;
//		Intent notificationIntent = new Intent(context, NavigationActivity.class);
//		notificationIntent.setAction("sdfaf.dasfs.d.ds");
//		notificationIntent.putExtra(ActionConstant.NOTIFICATION_FLAG_KEY, ActionConstant.NOTIFICATION_FLAG_VALUE);
//		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
//		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
//		// 用mNotificationManager的notify方法通知用户生成标题栏消息通知
//		mNotificationManager.notify(995, notification);

        // TODO lily 更新到新版代码，还未测试
        long when = System.currentTimeMillis() + 2000;
        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = "监控平台通知";
        int idd = eewebApplication.notifyIdIncrementAndGet();
        Context context = eewebApplication.getApplicationContext();
//        CharSequence contentTitle = idd + "监控云平台-您的设备有新的报警信息";
        CharSequence contentTitle = "监控云平台-您的设备有新的报警信息";
        String content = "";
        for (int i = 0; i < 3 && i < list.size(); i++) {
            DeviceBean tmp = list.get(i);
            content += (tmp.getArea() + "-" + tmp.getDevName() + "：" + tmp.getAlarmBean().getMsg() + "; \r\n ");
        }
        CharSequence contentText = content;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.ic_launcher))
                .setTicker(tickerText)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setWhen(when);
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setAction("warning" + Calendar.getInstance().get(Calendar.MILLISECOND));
        resultIntent.putExtra(Constant.ARG_SELECTED_POSITION, Constant.TAB_WARNING_POSITION);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        mNotificationManager.notify(idd, notification);
    }


//	private void notifyMsg(List<AlarmMsgDO> list, boolean flag) {
//		if (CollectionUtil.isEmpty(list) || !flag) {
//			return;
//		}
//
//		// 发送通知
//		NotificationManager mNotificationManager = (NotificationManager) eewebApplication.getSystemService(Context.NOTIFICATION_SERVICE);
//		// 定义通知栏展现的内容信息
//		int icon = R.drawable.ic_launcher;
//		CharSequence tickerText = "监控云平台通知";
//		long when = System.currentTimeMillis() + 2000;
//		Notification notification = new Notification(icon, tickerText, when);
//
//		notification.defaults = Notification.DEFAULT_SOUND;
//		notification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//		// 定义下拉通知栏时要展现的内容信息
//		Context context = eewebApplication.getApplicationContext();
//		CharSequence contentTitle = "监控云平台-您的设备出现报警";
//		String content = "";
//		for (int i = 0; i < 3 && i < list.size(); i++) {
//			AlarmMsgDO tmp = list.get(i);
//			content += (tmp.getAreaName() + "-" + tmp.getShowName() + "：" + tmp.getReason() + "; \r\n ");
//		}
//		CharSequence contentText = content;
//		Intent notificationIntent = new Intent(context, NavigationActivity.class);
//		notificationIntent.setAction("sdfaf.dasfs.d.ds");
//		notificationIntent.putExtra(ActionConstant.NOTIFICATION_FLAG_KEY, ActionConstant.NOTIFICATION_FLAG_VALUE);
//		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
//		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
//		// 用mNotificationManager的notify方法通知用户生成标题栏消息通知
//		mNotificationManager.notify(995, notification);
//	}

}
