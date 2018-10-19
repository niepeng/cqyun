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

import org.w3c.dom.Text;

/**
 * @author niepeng
 * 
 * @date 2012-9-13 下午1:12:28
 */
public class DeviceHistoryAlarmDataActivity extends BaseActivity {
	
	private TextView titleTv;

	private TextView devNameTv;
	private TextView snTv;
	private TextView timeTv;
	
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.device_history_alarm_data);

		Bundle bundle = this.getIntent().getExtras();

		titleTv = (TextView) findViewById(R.id.device_history_alarm_data_title);
		devNameTv = (TextView) findViewById(R.id.device_history_alarm_devname);
		snTv = (TextView) findViewById(R.id.device_history_alarm_sn);
		timeTv = (TextView) findViewById(R.id.device_history_alarm_time);
		listView = (ListView) findViewById(R.id.device_history_alarm_listview);

		DeviceBean deviceBean = (DeviceBean) bundle.getSerializable("deviceBean");
		titleTv.setText("报警记录");
		devNameTv.setText("设备名："+deviceBean.getDevName());
		snTv.setText("SN号："+deviceBean.getSnaddr());
		timeTv.setText("时间段：" + bundle.getString("startTimeStr") + " 至 " + bundle.getString("endTimeStr"));

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
//				holder.imageView.setImageDrawable(getDrawableByType(deviceDataBean));
//				holder.mainTv.setText(deviceDataBean.getInfo());
//				holder.timeTv.setText(deviceDataBean.getStartTime());

				holder.alarmIdTv.setText("报警ID：" + deviceDataBean.getAlarmId());
				holder.infoTv.setText("报警详情：" + deviceDataBean.getInfo());
				holder.startTimeTv.setText("报警开始时间：" + deviceDataBean.getStartTime());
				holder.endTimeTv.setText("报警结束时间：" + deviceDataBean.getEndTime());
				holder.handleUserTv.setText("备注填写人：" + deviceDataBean.getHandleUser());
				holder.additionInfoTv.setText("报警备注：" + deviceDataBean.getAdditionInfo());
			}
			return holder;
		}
	}

	private class ViewHolder extends LinearLayout {

//		public ImageView imageView;
//        public TextView mainTv;
//        public TextView timeTv;

		public TextView alarmIdTv;
		public TextView infoTv;
		public TextView startTimeTv;
		public TextView endTimeTv;
		public TextView handleUserTv;
		public TextView additionInfoTv;

		public ViewHolder(Context context) {
			super(context);
			LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout itemWrap = (LinearLayout) layoutInflater.inflate(R.layout.alarm_list_view_item_cell, this);
//			imageView = (ImageView) itemWrap.findViewById(R.id.alarm_list_view_item_cell_img);
//            mainTv = (TextView) itemWrap.findViewById(R.id.alarm_list_view_item_cell_name_tv);
//            timeTv = (TextView) itemWrap.findViewById(R.id.alarm_list_view_item_cell_time_tv);

			alarmIdTv = (TextView) itemWrap.findViewById(R.id.device_history_alarm_listview_cell_aramid);
			infoTv = (TextView) itemWrap.findViewById(R.id.device_history_alarm_listview_cell_info);
			startTimeTv = (TextView) itemWrap.findViewById(R.id.device_history_alarm_listview_cell_starttime);
			endTimeTv = (TextView) itemWrap.findViewById(R.id.device_history_alarm_listview_cell_endtime);
			handleUserTv = (TextView) itemWrap.findViewById(R.id.device_history_alarm_listview_cell_handleuser);
			additionInfoTv = (TextView) itemWrap.findViewById(R.id.device_history_alarm_listview_cell_additioninfo);



		}
	}
	
	

}
