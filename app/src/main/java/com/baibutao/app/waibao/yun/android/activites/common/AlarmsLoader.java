package com.baibutao.app.waibao.yun.android.activites.common;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;
import com.baibutao.app.waibao.yun.android.biz.bean.AlarmBean;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceDataBean;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.remotesimple.Httpclient;
import com.baibutao.app.waibao.yun.android.util.ChangeUtil;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lily on 17/4/12.
 */

public class AlarmsLoader extends AsyncTaskLoader<List<DeviceBean>> {

    List<DeviceBean> mDevices;
    private String mArea;

    public AlarmsLoader(Context context, String area) {
        super(context);
        mArea = area;
    }

    @Override
    public List<DeviceBean> loadInBackground() {

        EewebApplication eewebApplication = (EewebApplication) getContext().getApplicationContext();

        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", eewebApplication.getUserDO().getUsername());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "getAccountErr");

        Response response = remoteManager.execute(request);

        JSONArray array = JsonUtil.getJsonArray(response.getModel());
        final List<DeviceBean> currentList = CollectionUtil.newArrayList();

        if (array != null) {
            try {
                List<DeviceBean> tmpList = CollectionUtil.newArrayList();
                JSONObject json = null;
                DeviceBean bean = null;
                JSONArray alarmArray = null;
                AlarmBean alarmBean = null;
                List<AlarmBean> alarmBeanList = null;
                for (int i = 0, size = array.length(); i < size; i++) {
                    json = array.getJSONObject(i);
                    if (json == null) {
                        continue;
                    }
                    bean = new DeviceBean();
                    bean.setSnaddr(JsonUtil.getString(json, "snaddr", null));
                    bean.setDevName(JsonUtil.getString(json, "devName", null));
                    bean.setArea(JsonUtil.getString(json, "area", null));
                    alarmArray = json.getJSONArray("detail");
                    alarmBeanList = CollectionUtil.newArrayList();
                    for (int j = 0, alarmLenth = alarmArray.length(); j < alarmLenth; j++) {
                        alarmBean = JsonUtil.jsonToBean(alarmArray.getJSONObject(j).toString(), AlarmBean.class);
                        alarmBeanList.add(alarmBean);
                    }
                    bean.setAlarmBeanList(alarmBeanList);
                    tmpList.add(bean);
                }

                formatCurrent(currentList, tmpList);

            } catch (Exception e) {
            }

        }

        return null;
    }

    private void formatCurrent(List<DeviceBean> currentList, List<DeviceBean> tmpList) {
        for (DeviceBean deviceBean : tmpList) {
            currentList.add(deviceBean);
            if (!CollectionUtil.isEmpty(deviceBean.getAlarmBeanList())) {
                for (AlarmBean alarmBean : deviceBean.getAlarmBeanList()) {
                    DeviceBean bean = new DeviceBean();
                    bean.setAlarmBean(alarmBean);
                    currentList.add(bean);
                }
            }
        }
    }


    private class DeviceSort implements Comparator<DeviceBean> {

        @Override
        public int compare(DeviceBean o1, DeviceBean o2) {

            if (o1.getDataBean() == null) {
                return 1;
            }

            if (o2.getDataBean() == null) {
                return -1;
            }

            if (o1.getDataBean().getAbnormal().equals(o2.getDataBean().getAbnormal())) {
                return o1.getSnaddr().compareTo(o2.getSnaddr());
            }

            if (o1.getDataBean().isSuccess()) {
                return -1;
            }

            if (o2.getDataBean().isSuccess()) {
                return 1;
            }
            return o1.getSnaddr().compareTo(o2.getSnaddr());

        }

    }

    private DeviceDataBean requestDeviceDataBean(DeviceBean bean) {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("TYPE", "getRTData");
        Map<String, String> bodyMap = new HashMap<String, String>();
        bodyMap.put("snaddr", bean.getSnaddr());
        bodyMap.put("curve", bean.getCurve());
        String content = Httpclient.subPostForBody(Config.Values.URL, JsonUtil.mapToJson(bodyMap), Httpclient.DEFAULT_CHARSET, headerMap);

        JSONObject json = JsonUtil.getJsonObject(content);
        if (json == null) {
            return null;
        }
        DeviceDataBean dataBean = new DeviceDataBean();
        dataBean.setAbnormal(JsonUtil.getString(json, "abnormal", null));
        dataBean.setTime(JsonUtil.getString(json, "time", null));
        JSONObject humiJson = JsonUtil.getJSONObject(json, "humi");
        dataBean.setHumi(JsonUtil.getString(humiJson, "value", null));
        dataBean.setHumiStatus(ChangeUtil.str2int(JsonUtil.getString(humiJson, "status", null)));

        JSONObject tempJson = JsonUtil.getJSONObject(json, "temp");
        dataBean.setTemp(JsonUtil.getString(tempJson, "value", null));
        dataBean.setTempStatus(ChangeUtil.str2int(JsonUtil.getString(tempJson, "status", null)));

        return dataBean;
    }

    @Override
    public void deliverResult(List<DeviceBean> devices) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (devices != null) {
                onReleaseResources(devices);
            }
        }
        List<DeviceBean> oldDevices = mDevices;
        mDevices = devices;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(devices);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldDevices != null) {
            onReleaseResources(oldDevices);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mDevices != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mDevices);
        }


        if (takeContentChanged() || mDevices == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(List<DeviceBean> mDevices) {
        super.onCanceled(mDevices);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(mDevices);
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mDevices != null) {
            onReleaseResources(mDevices);
            mDevices = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<DeviceBean> mDevices) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}
