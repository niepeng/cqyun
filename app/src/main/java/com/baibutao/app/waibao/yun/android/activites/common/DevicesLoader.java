package com.baibutao.app.waibao.yun.android.activites.common;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;
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
import com.baibutao.app.waibao.yun.android.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lily on 17/4/12.
 */

public class DevicesLoader extends AsyncTaskLoader<List<DeviceBean>> {

    List<DeviceBean> mDevices;
    private String mArea;

    public DevicesLoader(Context context, String area) {
        super(context);
        mArea = area;
    }

    @Override
    public List<DeviceBean> loadInBackground() {
        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        EewebApplication eewebApplication = (EewebApplication) getContext().getApplicationContext();
        map.put("user", eewebApplication.getUserDO().getUsername());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "getAllDevice");

        Response response = remoteManager.execute(request);

        if (response == null) {
            return null;
        }

//        JSONArray array = JsonUtil.getJsonArray(response.getModel());
        JSONObject mainJson =  JsonUtil.getJsonObject(response.getModel());
        JSONArray array = JsonUtil.getJsonArray(mainJson, "array");

        if (array == null) {
            return null;
        }

        try {

            List<DeviceBean> beanList = CollectionUtil.newArrayList();
            JSONObject jsonObject = null;
            DeviceBean bean = null;
            for (int i = 0, size = array.length(); i < size; i++) {
                jsonObject = array.getJSONObject(i);
                bean = JsonUtil.jsonToBean(jsonObject.toString(), DeviceBean.class);
                beanList.add(bean);
            }

            if (!CollectionUtil.isEmpty(beanList) && !StringUtil.isBlank(mArea)) {
                for (int i = 0; i < beanList.size(); ) {
                    if (mArea.equals(beanList.get(i).getArea())) {
                        i++;
                        continue;
                    }
                    beanList.remove(i);
                }
            }


            DeviceDataBean dataBean = null;
            for (DeviceBean deviceBean : beanList) {
                if (!StringUtil.isBlank(bean.getSnaddr())) {
                    dataBean = requestDeviceDataBean(deviceBean, eewebApplication.getUserDO().getUsername());
                    deviceBean.setDataBean(dataBean);
                }
            }

            Collections.sort(beanList, new DeviceSort());
            return beanList;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    private class DeviceSort implements Comparator<DeviceBean> {

        @Override
        public int compare(DeviceBean o1, DeviceBean o2) {

           try {
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

           }catch(Exception e) {
               e.printStackTrace();
               Log.e("ffffffff",e.toString());
           }
            return 1;
        }

    }

    private DeviceDataBean requestDeviceDataBean(DeviceBean bean, String user) {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("TYPE", "getRTData");
        Map<String, String> bodyMap = new HashMap<String, String>();
        bodyMap.put("snaddr", bean.getSnaddr());
        bodyMap.put("user", user);
        bodyMap.put("curve", bean.getCurve());
        String content = Httpclient.subPostForBody(Config.Values.URL, JsonUtil.mapToJson(bodyMap), Httpclient.DEFAULT_CHARSET, headerMap);

        JSONObject json1 = JsonUtil.getJsonObject(content);
        if (json1 == null || JsonUtil.getInt(json1,"code", -1) !=0) {
            return null;
        }
        JSONObject json = JsonUtil.getJSONObject(json1, "array");

        DeviceDataBean dataBean = new DeviceDataBean();
        dataBean.setAbnormal(JsonUtil.getString(json, "abnormal", null));
        dataBean.setTime(JsonUtil.getString(json, "time", null));
        JSONObject humiJson = JsonUtil.getJSONObject(json, "humi");
        dataBean.setHumi(JsonUtil.getString(humiJson, "value", null));
        dataBean.setHumiStatus(ChangeUtil.str2int(JsonUtil.getString(humiJson, "status", null)));

        JSONObject tempJson = JsonUtil.getJSONObject(json, "temp");
        dataBean.setTemp(JsonUtil.getString(tempJson, "value", null));
        dataBean.setTempStatus(ChangeUtil.str2int(JsonUtil.getString(tempJson, "status", null)));

        dataBean.setPow(JsonUtil.getString(json, "pow", null));
        dataBean.setWater(JsonUtil.getString(json, "water", null));
        dataBean.setSmoke(JsonUtil.getString(json, "smoke", null));
        dataBean.setDoor(JsonUtil.getString(json, "door", null));
        dataBean.setBat(JsonUtil.getString(json, "bat", null));

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
