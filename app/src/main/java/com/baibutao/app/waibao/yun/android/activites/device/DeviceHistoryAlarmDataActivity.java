package com.baibutao.app.waibao.yun.android.activites.device;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.AbstractBaseAdapter;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.biz.bean.AlarmBean;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;

/**
 * @author niepeng
 * 
 * @date 2012-9-13 下午1:12:28
 */
public class DeviceHistoryAlarmDataActivity extends BaseActivity {
	
	private TextView titleTv;
	
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.device_history_alarm_data);

		Bundle bundle = this.getIntent().getExtras();

		titleTv = (TextView) findViewById(R.id.device_history_alarm_data_title);
		DeviceBean deviceBean = (DeviceBean) bundle.getSerializable("deviceBean");

		listView = (ListView) findViewById(R.id.device_history_alarm_listview);
		titleTv.setText(deviceBean.getDevName() + "\n" + deviceBean.getArea());

		if (!CollectionUtil.isEmpty(deviceBean.getAlarmBeanList())) {
			HistoryDataAdapter adDataAdapter = new HistoryDataAdapter(deviceBean.getAlarmBeanList());
			listView.setAdapter(adDataAdapter);
		}

	}

	public void handleBack(View v) {
		this.finish();
	}
	
	private class HistoryDataAdapter extends AbstractBaseAdapter {

		public HistoryDataAdapter(List<AlarmBean> deviceDataList) {
			super(deviceDataList);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder(DeviceHistoryAlarmDataActivity.this);
			} else {
				holder = (ViewHolder) convertView;
			}

			AlarmBean deviceDataBean = (AlarmBean) getItem(position);
			if (deviceDataBean != null) {
				holder.imageView.setImageDrawable(getDrawableByType(deviceDataBean));
				holder.mainTv.setText(deviceDataBean.getMsg());
				holder.timeTv.setText(deviceDataBean.getAlarmTime());
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
			LinearLayout itemWrap = (LinearLayout) layoutInflater.inflate(R.layout.alarm_list_view_item_cell, this);
			imageView = (ImageView) itemWrap.findViewById(R.id.alarm_list_view_item_cell_img);
            mainTv = (TextView) itemWrap.findViewById(R.id.alarm_list_view_item_cell_name_tv);
            timeTv = (TextView) itemWrap.findViewById(R.id.alarm_list_view_item_cell_time_tv);
		}
	}
	
	

}
