package com.baibutao.app.waibao.yun.android.activites.device;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.AbstractBaseAdapter;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadHelper;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.biz.bean.UserBean;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;


public class DeviceSetupAlarmActivity extends BaseActivity {

    private ListView listView;

    private DeviceBean deviceBean;
    private Future<Response> responseFuture;
    private Future<Response> mobileResponseFuture;
    private Future<Response> optResponseFuture;
    private List<String> accountList;
    private List<String> mobileList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.device_setup_alarm);

        Bundle bundle = this.getIntent().getExtras();
        deviceBean = (DeviceBean) bundle.get("deviceBean");

        listView = (ListView) findViewById(R.id.device_setup_alarm_listview);
        initData();
        flush();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String account = accountList.get(position);
                final boolean addPermission = !hasPermission(account);

                String msg = null;

                if (addPermission) {
                    msg = "是否将设备与" + account + "绑定？";
                } else {
                    msg = "是否将设备与" + account + "解除绑定？";
                }

                confirm(msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
                        remoteManager.setResponseParser(new StringResponseParser());
                        Request request = remoteManager.createPostRequest(Config.Values.URL);
                        // user,snaddr,deviceMobileList
                        String content = "{\"user\":\""+eewebApplication.getUserDO().getUsername()+"\",\"snaddr\":[\""+deviceBean.getSnaddr()+"\"],\"deviceMobileList\":[\""+account+"\"]}";
                        request.setBody(content);
                        if (addPermission) {
                            request.addHeader("type", "addMobileToDevice");
                        } else {
                            request.addHeader("type", "delMobileToDevice");
                        }
                        ProgressDialog progressDialog = showProgressDialog(R.string.app_up_data);
                        progressDialog.setOnDismissListener(new DeviceSetupAlarmActivity.FlushData());
                        optResponseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
                    }
                }, null);

            }
        });

    }

    private void initData() {
        accountList = new ArrayList<String>();
        mobileList = new ArrayList<>();
    }

    public void flush() {
        accountList.clear();
        mobileList.clear();
        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", eewebApplication.getUserDO().getUsername());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "getAccountMobileList");
        ProgressDialog progressDialog = showProgressDialog(R.string.app_read_data);
        progressDialog.setOnDismissListener(new DeviceSetupAlarmActivity.LoadData());
        responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
    }



    public void handleBack(View v) {
        Intent intent = DeviceSetupAlarmActivity.this.getIntent();
        intent.putExtra("deviceBean", deviceBean);
        DeviceSetupAlarmActivity.this.setResult(ACTIVITY_RESULT_CODE, intent);
        DeviceSetupAlarmActivity.this.finish();
    }


    public boolean hasPermission(String account) {
        if (CollectionUtil.isEmpty(mobileList)) {
            return false;
        }

        for (String tmp : mobileList) {
            if (tmp.equals(account)) {
                return true;
            }
        }
        return false;
    }



    private class LoadData implements DialogInterface.OnDismissListener {

        @Override
        public void onDismiss(DialogInterface dialog) {
            if (responseFuture == null) {
                return;
            }

            try {

                Response response = responseFuture.get();
                if (!response.isDataSuccess()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            alert("获取信息，请稍后再试");
                            DeviceSetupAlarmActivity.this.finish();
                        }
                    });
                    return;
                }



                /**
                 *
                 * {
                 "msg": "success",
                 "mobileList": ["13958190387", "15372095699"],
                 "code": 0,
                 "user": "cqy222"
                 }
                 */

                JSONObject jsonObject = JsonUtil.getJsonObject(response.getModel());
                String user = JsonUtil.getString(jsonObject, "user", null);
                if(!eewebApplication.getUserDO().getUsername().equals(user)) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            alert("暂无权限获取该设备的报警关联信息");
                            DeviceSetupAlarmActivity.this.finish();
                        }
                    });
                }



                JSONArray array = JsonUtil.getJsonArray(jsonObject, "mobileList");
                JSONObject accountJsonObject = null;
                for (int i = 0; i < array.length(); i++) {
                    accountList.add(array.getString(i));
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
                        remoteManager.setResponseParser(new StringResponseParser());
                        Request request = remoteManager.createPostRequest(Config.Values.URL);
                        final Map<String, Object> map = CollectionUtil.newHashMap();
                        map.put("snaddr", deviceBean.getSnaddr());
                        map.put("user", eewebApplication.getUserDO().getUsername());
                        request.setBody(JsonUtil.mapToJson(map));
                        request.addHeader("type", "getDeviceMobileList");
                        ProgressDialog progressDialog = showProgressDialog(R.string.app_read_data);
                        progressDialog.setOnDismissListener(new DeviceSetupAlarmActivity.OptData());
                        mobileResponseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
                    }
                });

            } catch (Exception e) {
                DeviceSetupAlarmActivity.this.logError(e.getMessage(), e);
            }

        }

    }


    private class OptData implements DialogInterface.OnDismissListener {
        @Override
        public void onDismiss(DialogInterface dialog) {
            if (mobileResponseFuture == null) {
                return;
            }

            try {

                Response response = mobileResponseFuture.get();
                if (!response.isDataSuccess()) {
                    return;
                }

                /**
                 * {
                 "msg": "success",
                 "snaddr": "G2100101",
                 "code": 0,
                 "deviceMobileList": ["13958190387"]
                 }
                 */

                JSONObject jsonObject = JsonUtil.getJsonObject(response.getModel());
                JSONArray array = JsonUtil.getJsonArray(jsonObject, "deviceMobileList");
                for (int i = 0; i < array.length(); i++) {
                    mobileList.add(array.getString(i));
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        DeviceSetupAlarmActivity.HistoryDataAdapter adDataAdapter = new DeviceSetupAlarmActivity.HistoryDataAdapter(accountList);
                        listView.setAdapter(adDataAdapter);
                    }
                });

            } catch (Exception e) {
                DeviceSetupAlarmActivity.this.logError(e.getMessage(), e);
            }

        }

    }



    private class FlushData implements DialogInterface.OnDismissListener {
        @Override
        public void onDismiss(DialogInterface dialog) {

            try {
                Response response = optResponseFuture.get();

                runOnUiThread(new Runnable() {
                    public void run() {
                        flush();
                    }
                });

            } catch (Exception e) {
                DeviceSetupAlarmActivity.this.logError(e.getMessage(), e);
            }

        }

    }



    private class HistoryDataAdapter extends AbstractBaseAdapter {

        public HistoryDataAdapter(List<String> deviceDataList) {
            super(deviceDataList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DeviceSetupAlarmActivity.ViewHolder holder;
            if (convertView == null) {
                holder = new DeviceSetupAlarmActivity.ViewHolder(DeviceSetupAlarmActivity.this);
            } else {
                holder = (DeviceSetupAlarmActivity.ViewHolder) convertView;
            }

            String account = (String) getItem(position);
            if (!StringUtil.isBlank(account)) {
                holder.mainTv.setTextColor(getResources().getColor(R.color.blue_deep));
//                holder.mainTv.setTextColor(getResources().getColor(R.color.gray_deep));
                holder.mainTv.setText(account);
                holder.timeTv.setText(hasPermission(account) ? "√" : "");
            }
            return holder;
        }

    }

    private class ViewHolder extends LinearLayout {

        public ImageView imageView;
        public TextView mainTv;
        public TextView timeTv;

        public ViewHolder(Context context) {
            super(context);
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout itemWrap = (LinearLayout) layoutInflater.inflate(R.layout.device_permission_manage_list_view_item_cell, this);
            mainTv = (TextView) itemWrap.findViewById(R.id.alarm_list_view_item_cell_name_tv);
            timeTv = (TextView) itemWrap.findViewById(R.id.alarm_list_view_item_cell_time_tv);
        }
    }


}

