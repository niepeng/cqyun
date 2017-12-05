package com.baibutao.app.waibao.yun.android.activites;

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
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
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


public class AlarmSetupPhoneActivity extends BaseActivity {

    private TextView snTv;
    private ListView listView;

    private DeviceBean deviceBean;
    private Future<Response> responseFuture;
    private Future<Response> optResponseFuture;
    private List<String> phoneList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.alarm_setup_phone);

        snTv = (TextView) findViewById(R.id.alarm_setup_phone_sn);
        listView = (ListView) findViewById(R.id.alarm_setup_phone_listview);
        initData();
        flush();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String phone = phoneList.get(position);

                // 确认框：执行操作
                String msg = "确定要去掉 " + phone + " 号码?";

                confirm(msg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
                        remoteManager.setResponseParser(new StringResponseParser());
                        Request request = remoteManager.createPostRequest(Config.Values.URL);
                        final Map<String, Object> map = CollectionUtil.newHashMap();
                        map.put("mobile", phone);
                        map.put("user", eewebApplication.getUserDO().getUsername());
                        request.setBody(JsonUtil.mapToJson(map));
                        request.addHeader("type", "delMobileToAccount");
                        ProgressDialog progressDialog = showProgressDialog(R.string.app_up_data);
                        progressDialog.setOnDismissListener(new AlarmSetupPhoneActivity.OptData());
                        optResponseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
                    }
                }, null);

            }
        });

    }

    private void initData() {
        snTv.setText("当前账号:" + eewebApplication.getUserDO().getUsername());
        phoneList = new ArrayList<String>();
    }

    public void flush() {
        phoneList.clear();
        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", eewebApplication.getUserDO().getUsername());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "getAccountMobileList");
        ProgressDialog progressDialog = showProgressDialog(R.string.app_read_data);
        progressDialog.setOnDismissListener(new AlarmSetupPhoneActivity.LoadData());
        responseFuture = eewebApplication.asyInvoke(new ThreadHelper(progressDialog, request, remoteManager));
    }



    public void handleBack(View v) {
        Intent intent = AlarmSetupPhoneActivity.this.getIntent();
        intent.putExtra("deviceBean", deviceBean);
        AlarmSetupPhoneActivity.this.setResult(ACTIVITY_RESULT_CODE, intent);
        AlarmSetupPhoneActivity.this.finish();
    }

    /**
     * 添加号码
     * @param v
     */
    public void handleAddPhone(View v) {
        Intent intent = new Intent(this, AlarmSetupPhoneAddActivity.class);
        startActivityForResult(intent, ACTIVITY_RESULT_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ACTIVITY_RESULT_CODE) {
            flush();
        }
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
                            alert("获取关联手机失败，请稍后再试");
                        }
                    });
                    return;
                }

                JSONArray array = JsonUtil.getJsonArray(jsonObject, "mobileList");
                JSONObject accountJsonObject = null;
                for (int i = 0; i < array.length(); i++) {
                    phoneList.add(array.getString(i));
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        AlarmSetupPhoneActivity.HistoryDataAdapter adDataAdapter = new AlarmSetupPhoneActivity.HistoryDataAdapter(phoneList);
                        listView.setAdapter(adDataAdapter);
                    }
                });



            } catch (Exception e) {
                AlarmSetupPhoneActivity.this.logError(e.getMessage(), e);
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
                AlarmSetupPhoneActivity.this.logError(e.getMessage(), e);
            }

        }

    }



    private class HistoryDataAdapter extends AbstractBaseAdapter {

        public HistoryDataAdapter(List<String> deviceDataList) {
            super(deviceDataList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AlarmSetupPhoneActivity.ViewHolder holder;
            if (convertView == null) {
                holder = new AlarmSetupPhoneActivity.ViewHolder(AlarmSetupPhoneActivity.this);
            } else {
                holder = (AlarmSetupPhoneActivity.ViewHolder) convertView;
            }

            String phone = (String) getItem(position);
            if (phone != null) {
//                holder.imageView.setImageDrawable(getDrawableByType(deviceDataBean));
                holder.mainTv.setTextColor(getResources().getColor(R.color.black));
                holder.mainTv.setText(phone);
//                holder.timeTv.setText(userBean.hasAuth() ? "√" : "");
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

