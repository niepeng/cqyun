package com.baibutao.app.waibao.yun.android.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseFragment;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadAid;
import com.baibutao.app.waibao.yun.android.activites.common.ThreadListener;
import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;
import com.baibutao.app.waibao.yun.android.androidext.SimpleDividerItemDecoration;
import com.baibutao.app.waibao.yun.android.biz.bean.AlarmBean;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.DateUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmFragment extends BaseFragment implements ThreadListener,View.OnClickListener {

    protected EewebApplication eewebApplication;

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;

    private FrameLayout largeLoadFramelayout;

    private int page;

    private Button loadMoreBtn;

    private ProgressBar loadMoreProgressBar;

    private boolean isLastPage;

    private TextView lastUpdateTimeTv;

    public AlarmFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AlarmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlarmFragment newInstance() {
        AlarmFragment fragment = new AlarmFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        eewebApplication = (EewebApplication) getActivity().getApplication();

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_alarm, container, false);
        if(eewebApplication.isReflushAlarmActivity()) {
            eewebApplication.setReflushAlarmActivity(false);
        }

        Button reflushBtn = (Button) root.findViewById(R.id.btn_reflush);
        reflushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleReflush(v);
            }
        });

        lastUpdateTimeTv = (TextView) root.findViewById(R.id.intime_gmtmodified_tv);
        largeLoadFramelayout = (FrameLayout) root.findViewById(R.id.large_more_process_framelayout);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);
        loadMoreBtn = (Button) root.findViewById(R.id.load_more_btn);
        loadMoreProgressBar = (ProgressBar) root.findViewById(R.id.load_more_progress_bar);

        initData();
        setViewContent();

        request();
        return root;
    }


    private void initData() {
        page = 1;
        isLastPage = false;
        mAdapter.setData(null);
    }

    private void request() {
        if (isLastPage) {
            return;
        }

        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", eewebApplication.getUserDO().getUsername());
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "getUnresolvedError");

        eewebApplication.asyInvoke(new ThreadAid(this, request, remoteManager));

        setUpdateTime(null);
    }

    private void setUpdateTime(Date date) {
        if (date == null) {
            date = new Date();
        }
        String text = "最近更新时间：" + DateUtil.format(date, DateUtil.DATE_MMddHHmm);
        lastUpdateTimeTv.setText(text);
    }

    private void setViewContent() {
    }

    public synchronized void handleReflush(View v) {
        initData();
        setViewContent();
        setViewVisiableBySynchronization(largeLoadFramelayout);
        request();
    }

    @Override
    public void onClick(View v) {
        AlarmBean alarmBean = (AlarmBean)v.getTag();
        String type = (String)v.getTag(R.id.tag_first);

        if (!alarmBean.isAlarmEnd()) {
            alert("操作失败，报警未结束！", "提示");
            return;
        }

        if("ignore".equals(type)) {
            // dealwithTheError 空内容提交，提示:报警已确认！
            submitMsg(alarmBean, "");
            return;
        }

        if("confirm".equals(type)) {
            // 弹出框输入内容，提交；
            alertEdit(alarmBean);
            return;
        }
    }

    public void alertEdit(final AlarmBean alarmBean) {
        final EditText et = new EditText(getActivity());
        new AlertDialog.Builder(getActivity()).setTitle("请输入备注信息")
//                .setIcon(android.R.drawable.icla)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件
                        submitMsg(alarmBean, et.getText().toString());
                    }
                }).setNegativeButton("取消", null).show();
    }


    private void submitMsg(AlarmBean alarmBean, String msg) {
        setViewVisiableBySynchronization(largeLoadFramelayout);

        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", eewebApplication.getUserDO().getUsername());
        map.put("snaddr", alarmBean.getSnaddr());
        map.put("alarmId", alarmBean.getAlarmId());
        map.put("additionInfo", msg);
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "dealwithTheError");

        eewebApplication.asyInvoke(new ThreadAid(new SubmitMsg(), request, remoteManager));
    }

    class SubmitMsg implements ThreadListener {
        @Override
        public void onPostExecute(Response response) {
            setViewGone(largeLoadFramelayout);
            JSONObject mainJson = JsonUtil.getJsonObject(response.getModel());
            boolean isOptSuccess = false;
            try {
                isOptSuccess = mainJson != null && mainJson.has("code") && mainJson.getInt("code") == 0;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final boolean flag = isOptSuccess;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (flag) {
                        toastLong("报警已确认！");
                    } else {
                        toastLong("操作失败，报警未结束！");
                    }
                }
            });
        }
    }

    @Override
    public void onPostExecute(Response response) {
        setViewGone(largeLoadFramelayout);

        if (response == null) {
            return;
        }
        setViewGone(loadMoreBtn, loadMoreProgressBar);

        JSONObject mainJson = JsonUtil.getJsonObject(response.getModel());
        JSONArray array = JsonUtil.getJsonArray(mainJson, "array");

        final List<AlarmBean> currentList = CollectionUtil.newArrayList();

        if (array != null) {
            try {
                JSONObject json = null;
                AlarmBean alarmBean = null;
                for (int i = 0, size = array.length(); i < size; i++) {
                    json = array.getJSONObject(i);
                    if (json == null) {
                        continue;
                    }
                    alarmBean = JsonUtil.jsonToBean(json.toString(), AlarmBean.class);
                    currentList.add(alarmBean);
                }


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addData(currentList);
                    }
                });

            } catch (Exception e) {
            }

        } else {
            setViewVisible(loadMoreBtn);
            setViewGone(loadMoreProgressBar);
            final String msg = response.getMessage();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toastLong(msg);
                }
            });
        }

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

    public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int ITEM_VIEW_TYPE_FOR_CELL = 1;
        private static final int ITEM_VIEW_TYPE_FOR_MAIN = 2;

        private List<AlarmBean> mDevices;

        public void setData(List<AlarmBean> devices) {
            mDevices = devices;
            notifyDataSetChanged();
        }

        public void addData(List<AlarmBean> devices) {
            if (mDevices == null) {
                mDevices = devices;
            } else {
                mDevices.addAll(devices);
            }
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            if (viewType == ITEM_VIEW_TYPE_FOR_CELL) {
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.alarm_list_view_item_cell, parent, false);
                return new ViewHolderCell(v);
            } else {
//                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_list_view_item_main, parent, false);
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_list_view_item_new, parent, false);
                return new ViewHolderMain(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int type = getItemViewType(position);
            AlarmBean alarmBean = mDevices.get(position);
            if (type == ITEM_VIEW_TYPE_FOR_CELL) {
                ViewHolderCell cell = (ViewHolderCell) holder;
//                cell.imageView.setImageDrawable(getDrawableByType(deviceBean.getAlarmBean()));
//                cell.mainTv.setText(deviceBean.getAlarmBean().getMsg());
//                cell.timeTv.setText(deviceBean.getAlarmBean().getAlarmTime());

//                cell.alarmIdTv.setText("报警ID：" + alarmBean.getAlarmId());
//                cell.infoTv.setText("报警详情：" + alarmBean.getInfo());
//                cell.startTimeTv.setText("报警开始时间：" + alarmBean.getStartTime());
//                cell.endTimeTv.setText("报警结束时间：" + alarmBean.getEndTime());
//                cell.handleUserTv.setText("备注填写人：" + alarmBean.getHandleUser());
//                cell.additionInfoTv.setText("报警备注：" + alarmBean.getAdditionInfo());
            } else {
                ViewHolderMain main = (ViewHolderMain) holder;
//                main.mainTv.setText("设备名:" + deviceBean.getDevName());
//                main.snaddrTv.setText(deviceBean.getSnaddr());

                main.alarmIdTv.setText("报警ID：" + alarmBean.getAlarmId());
                main.deviceTv.setText(alarmBean.getDevName() + "(" + alarmBean.getSnaddr() + ")");
                main.infoTv.setText(alarmBean.getInfo());


                if(alarmBean.isAlarmEnd()) {
                    main.statusTv.setText("报警结束");
                    main.statusTv.setTextColor(getResources().getColor(R.color.green));
                    main.distanceTv.setText("持续：" +  DateUtil.calcTimeDistance(DateUtil.parse(alarmBean.getStartTime()), DateUtil.parse(alarmBean.getEndTime())));
                    main.timeTv.setText("开始报警时间：" + alarmBean.getStartTime() + "\n结束报警时间：" + alarmBean.getEndTime());
                } else {
                    main.statusTv.setText("正在报警");
                    main.statusTv.setTextColor(getResources().getColor(R.color.red));
                    main.distanceTv.setText("");
                    main.timeTv.setText("开始报警时间：" + alarmBean.getStartTime());
                }

                main.ignoreBtn.setTag(alarmBean);
                main.ignoreBtn.setTag(R.id.tag_first, "ignore");
                main.confirmBtn.setTag(alarmBean);
                main.confirmBtn.setTag(R.id.tag_first, "confirm");


                main.ignoreBtn.setOnClickListener(AlarmFragment.this);
                main.confirmBtn.setOnClickListener(AlarmFragment.this);
            }

        }

        @Override
        public int getItemCount() {
            if (mDevices == null) {
                return 0;
            }
            return mDevices.size();
        }

        @Override
        public int getItemViewType(int position) {
//            if (mDevices.get(position).isShowAlarmMsg()) {
//                return ITEM_VIEW_TYPE_FOR_CELL;
//            }
//            return ITEM_VIEW_TYPE_FOR_MAIN;

             return ITEM_VIEW_TYPE_FOR_MAIN;
        }

        private class ViewHolderCell extends RecyclerView.ViewHolder {

//            public ImageView imageView;
//            public TextView mainTv;
//            public TextView timeTv;

            public TextView alarmIdTv;
            public TextView infoTv;
            public TextView startTimeTv;
            public TextView endTimeTv;
            public TextView handleUserTv;
            public TextView additionInfoTv;



            public ViewHolderCell(View itemView) {
                super(itemView);
//                imageView = (ImageView) itemView.findViewById(R.id.alarm_list_view_item_cell_img);
//                mainTv = (TextView) itemView.findViewById(R.id.alarm_list_view_item_cell_name_tv);
//                timeTv = (TextView) itemView.findViewById(R.id.alarm_list_view_item_cell_time_tv);
                alarmIdTv = (TextView) itemView.findViewById(R.id.device_history_alarm_listview_cell_aramid);
                infoTv = (TextView) itemView.findViewById(R.id.device_history_alarm_listview_cell_info);
                startTimeTv = (TextView) itemView.findViewById(R.id.device_history_alarm_listview_cell_starttime);
                endTimeTv = (TextView) itemView.findViewById(R.id.device_history_alarm_listview_cell_endtime);
                handleUserTv = (TextView) itemView.findViewById(R.id.device_history_alarm_listview_cell_handleuser);
                additionInfoTv = (TextView) itemView.findViewById(R.id.device_history_alarm_listview_cell_additioninfo);
            }
        }

        private class ViewHolderMain extends RecyclerView.ViewHolder {

//            public TextView mainTv;
//            public TextView snaddrTv;

            public TextView alarmIdTv;
            public TextView statusTv;
            public TextView deviceTv;
            public TextView infoTv;
            public TextView distanceTv;
            public TextView timeTv;
            public Button ignoreBtn;
            public Button confirmBtn;

            public ViewHolderMain(View itemView) {
                super(itemView);
//                mainTv = (TextView) itemView.findViewById(R.id.alarm_list_view_item_main_name_tv);
//                snaddrTv = (TextView) itemView.findViewById(R.id.alarm_list_view_item_main_snaddr_tv);

                alarmIdTv = (TextView) itemView.findViewById(R.id.alarm_list_view_item_new_alarm_id_tv);
                statusTv = (TextView) itemView.findViewById(R.id.alarm_list_view_item_new_alarm_status_tv);
                deviceTv = (TextView) itemView.findViewById(R.id.alarm_list_view_item_new_alarm_device_tv);
                infoTv = (TextView) itemView.findViewById(R.id.alarm_list_view_item_new_alarm_info_tv);
                distanceTv = (TextView) itemView.findViewById(R.id.alarm_list_view_item_new_alarm_distance_tv);
                timeTv = (TextView) itemView.findViewById(R.id.alarm_list_view_item_new_alarm_time_tv);
                ignoreBtn = (Button) itemView.findViewById(R.id.alarm_list_view_item_new_ignore_btn);
                confirmBtn = (Button) itemView.findViewById(R.id.alarm_list_view_item_new_confirm_btn);
            }
        }
    }

    protected String getMessageBoxTitle() {
        return getActivity().getTitle().toString();
    }


}
