package com.baibutao.app.waibao.yun.android.activites.device;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.AbstractBaseAdapter;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceDataBean;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;

import java.util.List;

/**
 * @author niepeng
 *
 * @date 2012-9-13 下午1:12:28
 */
public class DeviceHistoryDataActivity extends BaseActivity {

	private TextView titleTv;

	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.device_history_data);

		Bundle bundle = this.getIntent().getExtras();

		titleTv = (TextView) findViewById(R.id.device_history_data_title);
//		ArrayList dataList = bundle.getParcelableArrayList("dataList");
		List<DeviceDataBean> dataList = eewebApplication.getTmpList();
		eewebApplication.setTmpList(null);

		DeviceBean deviceBean = (DeviceBean) bundle.getSerializable("deviceBean");

		listView = (ListView) findViewById(R.id.device_history_listview);
		titleTv.setText(deviceBean.getDevName() + "\n" + deviceBean.getArea());

		if (!CollectionUtil.isEmpty(dataList)) {
			HistoryDataAdapter adDataAdapter = new HistoryDataAdapter(dataList);
			listView.setAdapter(adDataAdapter);
		}

	}

	public void handleBack(View v) {
		this.finish();
	}

	private class HistoryDataAdapter extends AbstractBaseAdapter {

		public HistoryDataAdapter(List<DeviceDataBean> deviceDataList) {
			super(deviceDataList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder(DeviceHistoryDataActivity.this);
			} else {
				holder = (ViewHolder) convertView;
			}

			DeviceDataBean deviceDataBean = (DeviceDataBean) getItem(position);
			if (deviceDataBean != null) {
				holder.tempTv.setText("温度："+deviceDataBean.getTemp() + "℃");
				holder.humiTv.setText("湿度："+deviceDataBean.getHumi() + "%");
				holder.timeTv.setText(deviceDataBean.getTime());
			}

			return holder;
		}

	}

	private class ViewHolder extends LinearLayout {

		public TextView tempTv;
		public TextView humiTv;
		public TextView timeTv;

		public ViewHolder(Context context) {
			super(context);
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout itemWrap = (LinearLayout) layoutInflater.inflate(R.layout.history_data_list_view_item, this);
			tempTv = (TextView) itemWrap.findViewById(R.id.history_data_list_view_item_temp_tv);
			humiTv = (TextView) itemWrap.findViewById(R.id.history_data_list_view_item_humi_tv);
			timeTv = (TextView) itemWrap.findViewById(R.id.history_data_list_view_item_time_tv);
		}
	}



}
