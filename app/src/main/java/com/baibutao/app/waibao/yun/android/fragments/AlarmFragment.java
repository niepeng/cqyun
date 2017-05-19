package com.baibutao.app.waibao.yun.android.fragments;


import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmFragment extends BaseFragment implements ThreadListener {

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
        request.addHeader("type", "getAccountErr");

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
    public void onPostExecute(Response response) {
        setViewGone(largeLoadFramelayout);

        if (response == null) {
            return;
        }
        setViewGone(loadMoreBtn, loadMoreProgressBar);

        JSONObject mainJson = JsonUtil.getJsonObject(response.getModel());
        JSONArray array = JsonUtil.getJsonArray(mainJson, "array");

//        JSONArray array = JsonUtil.getJsonArray(response.getModel());
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

        private List<DeviceBean> mDevices;

        public void setData(List<DeviceBean> devices) {
            mDevices = devices;
            notifyDataSetChanged();
        }

        public void addData(List<DeviceBean> devices) {
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
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.alarm_list_view_item_main, parent, false);
                return new ViewHolderMain(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int type = getItemViewType(position);
            DeviceBean deviceBean = mDevices.get(position);
            if (type == ITEM_VIEW_TYPE_FOR_CELL) {
                ViewHolderCell cell = (ViewHolderCell) holder;
                cell.imageView.setImageDrawable(getDrawableByType(deviceBean.getAlarmBean()));
                cell.mainTv.setText(deviceBean.getAlarmBean().getMsg());
                cell.timeTv.setText(deviceBean.getAlarmBean().getAlarmTime());
            } else {
                ViewHolderMain main = (ViewHolderMain) holder;
                main.mainTv.setText("设备名:" + deviceBean.getDevName());
                main.snaddrTv.setText(deviceBean.getSnaddr());
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
            if (mDevices.get(position).isShowAlarmMsg()) {
                return ITEM_VIEW_TYPE_FOR_CELL;
            }
            return ITEM_VIEW_TYPE_FOR_MAIN;
        }

        private class ViewHolderCell extends RecyclerView.ViewHolder {

            public ImageView imageView;
            public TextView mainTv;
            public TextView timeTv;


            public ViewHolderCell(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.alarm_list_view_item_cell_img);
                mainTv = (TextView) itemView.findViewById(R.id.alarm_list_view_item_cell_name_tv);
                timeTv = (TextView) itemView.findViewById(R.id.alarm_list_view_item_cell_time_tv);
            }
        }

        private class ViewHolderMain extends RecyclerView.ViewHolder {

            public TextView mainTv;
            public TextView snaddrTv;

            public ViewHolderMain(View itemView) {
                super(itemView);
                mainTv = (TextView) itemView.findViewById(R.id.alarm_list_view_item_main_name_tv);
                snaddrTv = (TextView) itemView.findViewById(R.id.alarm_list_view_item_main_snaddr_tv);
            }
        }
    }

    public void alert(CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(getMessageBoxTitle());
        alertDialog.setMessage(message);
        String buttonLabel = getString(R.string.app_btn_label_ok);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, buttonLabel, (Message) null);
        alertDialog.show();
    }

    protected String getMessageBoxTitle() {
        return getActivity().getTitle().toString();
    }


}
