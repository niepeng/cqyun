package com.baibutao.app.waibao.yun.android.fragments;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.DevicesLoader;
import com.baibutao.app.waibao.yun.android.activites.device.DeviceDetailActivity;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceDataBean;
import com.baibutao.app.waibao.yun.android.biz.dataobject.SetupDO;
import com.baibutao.app.waibao.yun.android.common.Constant;
import com.baibutao.app.waibao.yun.android.common.SetupInfoHolder;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.baibutao.app.waibao.yun.android.common.Constant.ARG_AREA;
import static com.baibutao.app.waibao.yun.android.common.Constant.ARG_SHOW_TYPE;
import static com.baibutao.app.waibao.yun.android.common.Constant.SHOW_TYPE_LINEAR;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InTimeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InTimeFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<DeviceBean>> {

    private int mShowType = Constant.SHOW_TYPE_LINEAR;
    private String mArea;

    private RecyclerView mRecyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView areaTv;
    private TextView successNumTv;
    private TextView notConnectionNumTv;
    private TextView normalNumTv;
    private TextView kaiguanAlarmNumTv;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog mProgress;

    private Timer mTimer;

    protected static final int ACTIVITY_RESULT_CODE = 1;
    protected static final int ACTIVITY_DEL_RESULT_CODE = 2;

    public InTimeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param showType Parameter 1.
     * @return A new instance of fragment InTimeFragment.
     */
    public static InTimeFragment newInstance(int showType, String area) {
        InTimeFragment fragment = new InTimeFragment();
        Bundle args = new Bundle();
        args.putInt(Constant.ARG_SHOW_TYPE, showType);
        args.putString(Constant.ARG_AREA, area);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mShowType = getArguments().getInt(ARG_SHOW_TYPE);
            mArea = getArguments().getString(ARG_AREA, null);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("数据加载中...");
        mProgress.show();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_in_time, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        areaTv = (TextView) view.findViewById(R.id.in_time_area_tv);
        successNumTv = (TextView) view.findViewById(R.id.in_time_total_success_num_tv);
        notConnectionNumTv = (TextView) view.findViewById(R.id.in_time_total_not_connection_num_tv);
        normalNumTv = (TextView) view.findViewById(R.id.in_time_total_normal_tv);
        kaiguanAlarmNumTv = (TextView) view.findViewById(R.id.in_time_total_kaiguan_alarm_num_tv);

        if (mShowType == SHOW_TYPE_LINEAR) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        }

        mAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mAdapter);

        SetupDO setupDO = SetupInfoHolder.getDO(getActivity());
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask(){

            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleReflush();
                    }
                });
            }
        }, 0, setupDO.getUpdateIntime()*1000);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handleReflush();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        super.onDestroyView();
    }

    @Override
    public Loader<List<DeviceBean>> onCreateLoader(int id, Bundle args) {
        return new DevicesLoader(getActivity(), mArea);
    }

    protected boolean hasNetWork() {
        ConnectivityManager nw = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (nw == null) {
            return false;
        }
        NetworkInfo netinfo = nw.getActiveNetworkInfo();
        if (netinfo == null) {
            return false;
        }
        return netinfo.isAvailable();
    }

    @Override
    public void onLoadFinished(Loader<List<DeviceBean>> loader, List<DeviceBean> data) {
        mAdapter.setData(data);
        if (data == null || data.size() == 0) {
            if (!hasNetWork()) {
                Toast.makeText(getActivity(), "网络异常，请检查网络", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "暂无设备数据", Toast.LENGTH_LONG).show();
            }
        }
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        // To dismiss the dialog
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }

        // 修改统计数量
        calcTotalNumAndShow(data);
    }

    private void calcTotalNumAndShow(List<DeviceBean> list) {
        int successNum = 0, notConnectionNum = 0, normalNum = 0, kaiguanAlarmNum = 0;
        if (list != null) {
            DeviceDataBean dataBean = null;
            for (DeviceBean tmp : list) {
                if (tmp == null || (dataBean = tmp.getDataBean()) == null) {
                    continue;
                }
                if (dataBean.isSuccess()) {
                    successNum++;
                    continue;
                }
                if (dataBean.isNotConnection()) {
                    notConnectionNum++;
                    continue;
                }
                if (dataBean.isKaiguanAlarm()) {
                    kaiguanAlarmNum++;
                    continue;
                }
                normalNum++;
            }
        }

        successNumTv.setText(String.valueOf(successNum));
        notConnectionNumTv.setText(String.valueOf(notConnectionNum));
        normalNumTv.setText(String.valueOf(normalNum));
        kaiguanAlarmNumTv.setText(String.valueOf(kaiguanAlarmNum));
        if(StringUtil.isBlank(mArea)) {
            areaTv.setText(getResources().getString(R.string.menu_item_all_equipment));
        } else {
            areaTv.setText(mArea);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<DeviceBean>> loader) {
        mAdapter.setData(null);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private static final int ITEM_VIEW_TYPE_FOR_LINEAR = 1;
        private static final int ITEM_VIEW_TYPE_FOR_GRID = 2;

        private List<DeviceBean> mDevices;

        public void setData(List<DeviceBean> devices) {
            mDevices = devices;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            if (viewType == ITEM_VIEW_TYPE_FOR_LINEAR) {
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_in_time_item_for_linear, parent, false);
            } else {
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_in_time_item_for_grid, parent, false);
            }
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            DeviceBean device = mDevices.get(position);
            holder.mEquipmentNameView.setText(device.getDevName());

            if (device.getDataBean() != null) {
                setDeviceStatus(holder, device);
                if (device.getDataBean().isOffline()) {
                    holder.mTemperatureView.setText("设备离线");
                    holder.mTemperatureStatusView.setVisibility(View.GONE);
                    holder.mHumidityView.setText("设备离线");
                    holder.mHumidityStatusView.setVisibility(View.GONE);
                    holder.mTimeView.setText("");
                }else if (device.getDataBean().isNotConnection()) {
                    holder.mTemperatureView.setText("传感器未连接");
                    holder.mTemperatureStatusView.setVisibility(View.GONE);
                    holder.mHumidityView.setText("传感器未连接");
                    holder.mHumidityStatusView.setVisibility(View.GONE);
//                    holder.mTimeView.setText("传感器未连接");
                    holder.mTimeView.setText("-- ");
                } else {
                    setTemperature(holder, device);
                    setHumidity(holder, device);
                    holder.mTimeView.setText(device.getDataBean().getTime());
                }
            } else {
                holder.mTemperatureView.setText("-- ");
                holder.mHumidityView.setText("-- ");
                holder.mTimeView.setText("-- ");
                holder.mTemperatureStatusView.setVisibility(View.GONE);
                holder.mHumidityStatusView.setVisibility(View.GONE);
            }

            holder.itemView.setTag(device);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DeviceBean bean = (DeviceBean) v.getTag();
                    Intent intent = new Intent(getActivity(), DeviceDetailActivity.class);
                    intent.putExtra("deviceBean", bean);
                    startActivityForResult(intent, ACTIVITY_RESULT_CODE);
                }
            });

        }

        private void setHumidity(ViewHolder holder, DeviceBean device) {
            if (!StringUtil.isBlank(device.getDataBean().getHumi())) {
                holder.mHumidityView.setText(device.getDataBean().getHumi() + "%");
                if (device.getDataBean().getHumiStatus() > 0) {
                    holder.mHumidityStatusView.setVisibility(View.VISIBLE);
                    holder.mHumidityStatusView.setText("↑");
                } else if (device.getDataBean().getHumiStatus() < 0) {
                    holder.mHumidityStatusView.setVisibility(View.VISIBLE);
                    holder.mHumidityStatusView.setText("↓");
                } else {
                    holder.mHumidityStatusView.setVisibility(View.GONE);
                }
            } else {
                holder.mHumidityView.setText("-- ");
                holder.mHumidityStatusView.setVisibility(View.GONE);
            }
        }

        private void setTemperature(ViewHolder holder, DeviceBean device) {
            if (!StringUtil.isBlank(device.getDataBean().getTemp())) {
                holder.mTemperatureView.setText(device.getDataBean().getTemp() + "℃");
                if (device.getDataBean().getTempStatus() > 0) {
                    holder.mTemperatureStatusView.setVisibility(View.VISIBLE);
                    holder.mTemperatureStatusView.setText("↑");
                } else if (device.getDataBean().getTempStatus() < 0) {
                    holder.mTemperatureStatusView.setVisibility(View.VISIBLE);
                    holder.mTemperatureStatusView.setText("↓");
                } else {
                    holder.mTemperatureStatusView.setVisibility(View.GONE);
                }
            } else {
                holder.mTemperatureView.setText("-- ");
                holder.mTemperatureStatusView.setVisibility(View.GONE);
            }
        }

        private void setDeviceStatus(ViewHolder holder, DeviceBean device) {
            if (device.getDataBean().isSuccess()) {
                holder.mEquipmentStatusView.setImageResource(R.drawable.device_status_success);
            } else if (device.getDataBean().isNotConnection()) {
                holder.mEquipmentStatusView.setImageResource(R.drawable.device_status_no_connection);
            }  else if (device.getDataBean().isKaiguanAlarm()) {
                holder.mEquipmentStatusView.setImageResource(R.drawable.device_status_kaiguan_alarm);
            } else {
                holder.mEquipmentStatusView.setImageResource(R.drawable.device_status_normal);
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
            if (mShowType == Constant.SHOW_TYPE_GRID) {
                return ITEM_VIEW_TYPE_FOR_GRID;
            }
            return ITEM_VIEW_TYPE_FOR_LINEAR;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView mEquipmentNameView;
            ImageView mEquipmentStatusView;
            TextView mTemperatureView;
            TextView mTemperatureStatusView;
            TextView mHumidityView;
            TextView mHumidityStatusView;
            TextView mTimeView;

            public ViewHolder(View itemView) {
                super(itemView);
                mEquipmentNameView = (TextView) itemView.findViewById(R.id.equipment_name);
                mEquipmentStatusView = (ImageView) itemView.findViewById(R.id.equipment_status);
                mTemperatureView = (TextView) itemView.findViewById(R.id.temperature);
                mTemperatureStatusView = (TextView) itemView.findViewById(R.id.temperature_status);
                mHumidityView = (TextView) itemView.findViewById(R.id.humidity);
                mHumidityStatusView = (TextView) itemView.findViewById(R.id.humidity_status);
                mTimeView = (TextView) itemView.findViewById(R.id.time);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == ACTIVITY_DEL_RESULT_CODE) {
            handleReflush();
            return;
        }

        if(data != null) {
            boolean refush = data.getExtras().getBoolean("needRefulsh");
            if (refush) {
                handleReflush();
                return;
            }
        }
    }

    private void handleReflush() {
        getLoaderManager().restartLoader(0, null, this);
    }
}
