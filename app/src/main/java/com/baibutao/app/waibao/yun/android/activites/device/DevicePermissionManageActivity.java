package com.baibutao.app.waibao.yun.android.activites.device;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.baibutao.app.waibao.yun.android.biz.bean.AlarmBean;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.biz.bean.UserBean;
import com.baibutao.app.waibao.yun.android.biz.dataobject.UserDO;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;


public class DevicePermissionManageActivity extends BaseActivity {

    private TextView snTv;
    private ListView listView;

    private DeviceBean deviceBean;
    private Future<Response> responseFuture;
    private Future<Response> optResponseFuture;
    private List<UserBean> accountList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.device_permission_manage);

        Bundle bundle = this.getIntent().getExtras();
        deviceBean = (DeviceBean) bundle.get("deviceBean");

        snTv = (TextView) findViewById(R.id.device_permission_manage_sn);
        listView = (ListView) findViewById(R.id.device_permission_manage_listview);
        initData();
        flush();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final UserBean currentUserBean = accountList.get(position);
                if(currentUserBean.hasAuth()) {
                    return;
                }

                if(!hasOptPermission()) {
                    alert("本账号暂未获取" + deviceBean.getSnaddr() + "的管理权限，无法执行修改阈值，设备名称，设置上传间隔等操作，亦不能主动获取管理权限。如需要进行对设备的修改操作，请登录管理账号执行。");
                    return;
                }

                // 确认框：执行权限转移操作
                String msg = "是否将设备管理权限转移至" + accountList.get(position).getUser();

                confirm(msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
                        remoteManager.setResponseParser(new StringResponseParser());
                        Request request = remoteManager.createPostRequest(Config.Values.URL);
                        final Map<String, Object> map = CollectionUtil.newHashMap();
                        map.put("snaddr", deviceBean.getSnaddr());
                        map.put("user", eewebApplication.getUserDO().getUsername());
                        map.put("newUser", currentUserBean.getUser());
                        request.setBody(JsonUtil.mapToJson(map));
                        request.addHeader("type", "handOverAuthority");
                        ProgressDialog progressDialog = showProgressDialog(R.string.app_up_data);
                        progressDialog.setOnDismissListener(new DevicePermissionManageActivity.OptData());
                        optResponseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
                    }
                }, null);

            }
        });

    }

    private void initData() {
        snTv.setText("当前账号:" + eewebApplication.getUserDO().getUsername());
        accountList = new ArrayList<UserBean>();
    }

    public void flush() {
        accountList.clear();
        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("snaddr", deviceBean.getSnaddr());
        map.put("accessToken", eewebApplication.getUserDO().getUsername());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "getDevAuthority");
        ProgressDialog progressDialog = showProgressDialog(R.string.app_read_data);
        progressDialog.setOnDismissListener(new DevicePermissionManageActivity.LoadData());
        responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
    }



    public void handleBack(View v) {
        Intent intent = DevicePermissionManageActivity.this.getIntent();
        intent.putExtra("deviceBean", deviceBean);
        DevicePermissionManageActivity.this.setResult(ACTIVITY_RESULT_CODE, intent);
        DevicePermissionManageActivity.this.finish();
    }


    public boolean hasOptPermission() {
        if (CollectionUtil.isEmpty(accountList)) {
            return false;
        }
        for (UserBean user : accountList) {
            if (eewebApplication.getUserDO().getUsername().equals(user.getUser()) && user.hasAuth()) {
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
                    return;
                }

                /**
                 * {
                 "snaddr": "G2100101",
                 "array": [{
                 "account": "cqy",
                 "authority": "0"
                 }, {
                 "account": "cqy222",
                 "authority": "1"
                 }],
                 "code": 0
                 }
                 */

                JSONObject jsonObject = JsonUtil.getJsonObject(response.getModel());
                int code = JsonUtil.getInt(jsonObject, "code", -1);
                if(code != 0) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            alert("获取关联账户失败，请稍后再试");
                        }
                    });
                    return;
                }

                JSONArray array = JsonUtil.getJsonArray(jsonObject, "array");
                JSONObject accountJsonObject = null;
                for (int i = 0; i < array.length(); i++) {
                    accountJsonObject = array.getJSONObject(i);
                    UserBean user = new UserBean();
                    user.setUser(JsonUtil.getString(accountJsonObject, "account", null));
                    user.setAuthority(JsonUtil.getString(accountJsonObject, "authority", null));
                    accountList.add(user);
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        DevicePermissionManageActivity.HistoryDataAdapter adDataAdapter = new DevicePermissionManageActivity.HistoryDataAdapter(accountList);
                        listView.setAdapter(adDataAdapter);
                    }
                });



            } catch (Exception e) {
                DevicePermissionManageActivity.this.logError(e.getMessage(), e);
            }

        }

    }


    private class OptData implements DialogInterface.OnDismissListener {

        @Override
        public void onDismiss(DialogInterface dialog) {
            if (optResponseFuture == null) {
                return;
            }

            try {

                Response response = optResponseFuture.get();
                if (!response.isDataSuccess()) {
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        flush();
                    }
                });
            } catch (Exception e) {
                DevicePermissionManageActivity.this.logError(e.getMessage(), e);
            }

        }

    }



    private class HistoryDataAdapter extends AbstractBaseAdapter {

        public HistoryDataAdapter(List<UserBean> deviceDataList) {
            super(deviceDataList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DevicePermissionManageActivity.ViewHolder holder;
            if (convertView == null) {
                holder = new DevicePermissionManageActivity.ViewHolder(DevicePermissionManageActivity.this);
            } else {
                holder = (DevicePermissionManageActivity.ViewHolder) convertView;
            }

            UserBean userBean = (UserBean) getItem(position);
            if (userBean != null) {
//                holder.imageView.setImageDrawable(getDrawableByType(deviceDataBean));
                if(userBean.hasAuth()) {
                    holder.mainTv.setTextColor(getResources().getColor(R.color.blue_deep));
                } else {
                    holder.mainTv.setTextColor(getResources().getColor(R.color.gray_deep));
                }
                holder.mainTv.setText(userBean.getUser());
                holder.timeTv.setText(userBean.hasAuth() ? "√" : "");
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

