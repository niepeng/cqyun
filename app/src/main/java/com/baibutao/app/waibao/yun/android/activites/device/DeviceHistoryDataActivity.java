package com.baibutao.app.waibao.yun.android.activites.device;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.AbstractBaseAdapter;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceBean;
import com.baibutao.app.waibao.yun.android.biz.bean.DeviceDataBean;
import com.baibutao.app.waibao.yun.android.biz.bean.TmpHistoryBean;
import com.baibutao.app.waibao.yun.android.common.Tuple;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;

import org.w3c.dom.Text;

import java.util.List;

/**
 * @author niepeng
 *
 * @date 2012-9-13 下午1:12:28
 */
public class DeviceHistoryDataActivity extends BaseActivity {

	private TextView titleTv;

	private TextView startTimeTv;
	private TextView endTimeTv;
	private TextView maxTv;
	private TextView minTv;

	private Button changeBtn;

	// 列表
	private LinearLayout listTitleLayout;
	private ListView listView;

	// 曲线
	private LinearLayout charWrapLayout;
	private LinearLayout curBtnLayout;
	private WebView curWebView;

	// 温度和湿度
	private Button tempBtn;
	private Button humiBtn;


	private TmpHistoryBean tmpHistoryBean;
	private List<DeviceDataBean> dataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.device_history_data);

		Bundle bundle = this.getIntent().getExtras();

		titleTv = (TextView) findViewById(R.id.device_history_data_title);
		changeBtn = (Button) findViewById(R.id.btn_change);
		startTimeTv = (TextView) findViewById(R.id.device_history_data_start_time_tv);
		endTimeTv = (TextView) findViewById(R.id.device_history_data_end_time_tv);
		maxTv = (TextView) findViewById(R.id.device_history_data_max_tv);
		minTv = (TextView) findViewById(R.id.device_history_data_min_tv);

//		ArrayList dataList = bundle.getParcelableArrayList("dataList");
		dataList = eewebApplication.getTmpList();
		tmpHistoryBean = eewebApplication.getTmpHistoryBean();
		eewebApplication.setTmpList(null);
		eewebApplication.setTmpHistoryBean(null);

		DeviceBean deviceBean = (DeviceBean) bundle.getSerializable("deviceBean");

		listTitleLayout = (LinearLayout) findViewById(R.id.device_history_data_list_title);
		listView = (ListView) findViewById(R.id.device_history_listview);

		charWrapLayout = (LinearLayout) findViewById(R.id.device_history_data_char_wrap);
		curBtnLayout = (LinearLayout) findViewById(R.id.device_history_data_cur_layout);
		curWebView = (WebView) findViewById(R.id.device_history_data_cur_webview);

		tempBtn = (Button) findViewById(R.id.device_history_data_cur_temp);
		humiBtn = (Button) findViewById(R.id.device_history_data_cur_humi);

		titleTv.setText(deviceBean.getDevName() + "\n" + deviceBean.getArea());
		startTimeTv.setText("开始时间：" + tmpHistoryBean.getStartTime());
		endTimeTv.setText("结束时间：" + tmpHistoryBean.getEndTime() + "				" + "间隔：" + tmpHistoryBean.getDistance());
		maxTv.setText("最高温度：" + tmpHistoryBean.getTempMax() + "℃		" + "最高湿度：" + tmpHistoryBean.getHumiMax() + "%");
		minTv.setText("最低温度：" + tmpHistoryBean.getTempMin() + "℃		" + "最低湿度：" + tmpHistoryBean.getHumiMin()+ "%");
		if (!CollectionUtil.isEmpty(dataList)) {
			HistoryDataAdapter adDataAdapter = new HistoryDataAdapter(dataList);
			listView.setAdapter(adDataAdapter);
		}

		init();
	}

	private void init() {

		// 默认展示曲线，曲线中，默认展示温度
		// 开启本地文件读取（默认即为true）
		curWebView.getSettings().setAllowContentAccess(true);
		curWebView.getSettings().setJavaScriptEnabled(true);
		curWebView.loadUrl("file:///android_asset/cur_history_grid.html");

		changeBtn.setText("列表");
		listTitleLayout.setVisibility(View.GONE);
		listView.setVisibility(View.GONE);

		new Handler().postDelayed(new Runnable(){
			public void run() {
				handleTemp(null);
			}
		}, 1000);
	}

	public void handleBack(View v) {
		this.finish();
	}

	public void handleChange(View v) {
		String text = changeBtn.getText().toString();
		if("曲线".equals(text)) {
			changeBtn.setText("列表");
			charWrapLayout.setVisibility(View.VISIBLE);
			listTitleLayout.setVisibility(View.GONE);
			listView.setVisibility(View.GONE);
		}
		if("列表".equals(text)) {
			changeBtn.setText("曲线");
			charWrapLayout.setVisibility(View.GONE);
			listTitleLayout.setVisibility(View.VISIBLE);
			listView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 温度按钮
     */
	public void handleTemp(View v) {
//		curWebView.loadUrl("javascript:doCreatChart('line','温度',['a1', 'b', 'c','d','e11','series'], [89.1,78,77,66,44,55], ['#c23531']);");
		String loadData = "javascript:doCreatChart('line','温度',%s, %s, ['#c23531']);";
		Tuple.Tuple2<String, String> tempTuple = assembleData(dataList, "temp");
		String xAxis = tempTuple._1();
		String yAxis = tempTuple._2();
		curWebView.loadUrl(String.format(loadData, xAxis, yAxis));

		tempBtn.setBackgroundResource(R.drawable.btn_background_left);
		tempBtn.setTextColor(getResources().getColor(R.color.white));

		humiBtn.setBackgroundResource(R.drawable.btn_background_right2);
		humiBtn.setTextColor(getResources().getColor(R.color.yun_title));

	}

	/**
	 * 湿度按钮
	 */
	public void handleHumi(View v) {
//		curWebView.loadUrl("javascript:doCreatChart('line','湿度',['a2', 'b2', 'c2','d2','e11','series'], [89.2,78,77,66,44,55], ['#2f4554']);");
		String loadData = "javascript:doCreatChart('line','湿度',%s, %s, ['#2f4554']);";
		Tuple.Tuple2<String, String> tempTuple = assembleData(dataList, "humi");
		String xAxis = tempTuple._1();
		String yAxis = tempTuple._2();
		String url = String.format(loadData, xAxis, yAxis);
		curWebView.loadUrl(url);

		tempBtn.setBackgroundResource(R.drawable.btn_background_left2);
		tempBtn.setTextColor(getResources().getColor(R.color.yun_title));

		humiBtn.setBackgroundResource(R.drawable.btn_background_right);
		humiBtn.setTextColor(getResources().getColor(R.color.white));

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
				holder.tempTv.setText(deviceDataBean.getTemp() + "℃");
				holder.humiTv.setText(deviceDataBean.getHumi() + "%");
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


	private Tuple.Tuple2<String,String> assembleData(List<DeviceDataBean> dataList, String type) {
		if (dataList == null || dataList.size() == 0) {
			return Tuple.tuple("[]", "[]");
		}
		// ['a11', 'b', 'c','d','e11','series'], [89.1,78,77,66,44,55]
		StringBuilder xBuilder = new StringBuilder();
		StringBuilder yBuilder = new StringBuilder();
		boolean isHumi = "humi".equals(type);
		DeviceDataBean tmpBean;
		for (int i = 0, size = dataList.size(); i < size; i++) {
			tmpBean = dataList.get(i);
			if (i > 0) {
				xBuilder.append(",");
				yBuilder.append(",");
			}
			xBuilder.append("'");
			xBuilder.append(tmpBean.getTime());
			xBuilder.append("'");

			yBuilder.append(isHumi ? tmpBean.getHumi() : tmpBean.getTemp());

		}
		return Tuple.tuple("[" + xBuilder.toString() + "]", "[" + yBuilder.toString() + "]");
	}



}
