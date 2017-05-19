package com.baibutao.app.waibao.yun.android.activites.common;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remotesimple.Httpclient;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lily on 17/4/12.
 */

public class AreasLoader extends AsyncTaskLoader<List<String>> {

    private List<String> mAreas;

    public AreasLoader(Context context) {
        super(context);
    }

    @Override
    public List<String> loadInBackground() {
        EewebApplication eewebApplication = (EewebApplication) getContext().getApplicationContext();
        List<String> result = CollectionUtil.newArrayList();
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("TYPE", "getAreaInfo");
        final Map<String, Object> bodyMap = CollectionUtil.newHashMap();
        bodyMap.put("user", eewebApplication.getUserDO().getUsername());
        String content = Httpclient.subPostForBody(Config.Values.URL, JsonUtil.mapToJson(bodyMap), Httpclient.DEFAULT_CHARSET, headerMap);
        JSONObject mainJson =  JsonUtil.getJsonObject(content);
        JSONArray array = JsonUtil.getJsonArray(mainJson, "array");
//        JSONArray array = JsonUtil.getJsonArray(content);
        try {
            if (array != null) {
                for (int i = 0, size = array.length(); i < size; i++) {
                    result.add(array.getString(i));
                }
            }
        } catch (JSONException e) {
        }
        return result;
    }

    @Override
    public void deliverResult(List<String> areas) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (areas != null) {
                onReleaseResources(areas);
            }
        }
        List<String> oldDevices = mAreas;
        mAreas = areas;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(areas);
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
        if (mAreas != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mAreas);
        }


        if (takeContentChanged() || mAreas == null) {
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
    public void onCanceled(List<String> areas) {
        super.onCanceled(areas);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(areas);
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mAreas != null) {
            onReleaseResources(mAreas);
            mAreas = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<String> areas) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}
