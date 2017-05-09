package com.baibutao.app.waibao.yun.android.activites.device;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.androidext.YunMulticastSmartLinkerActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>标题: </p>
 * <p>描述: </p>
 * <p>版权: lsb</p>
 * <p>创建时间: 2017年3月17日  下午8:11:22</p>
 * <p>作者：niepeng</p>
 */
public class DeviceAddResultListActivity extends BaseActivity {
	
	private boolean returnNeedRefulsh;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.device_add_result_list);

		ListView listView = (ListView) findViewById(R.id.listView);
		ArrayList<String> list = getIntent().getExtras().getStringArrayList("snList");
		returnNeedRefulsh = list != null && list.size() > 0;
        /*
         * 第一个参数 this 代表的是当前上下文，可以理解为你当前所处的activity
         * 第二个参数 getData() 一个包含了数据的List,注意这个List里存放的必须是map对象。simpleAdapter中的限制是这样的List<? extends Map<String, ?>> data
         * 第三个参数 R.layout.device_add_result_cell 展示信息的组件
         * 第四个参数 一个string数组，数组内存放的是你存放数据的map里面的key。
         * 第五个参数：一个int数组，数组内存放的是你展示信息组件中，每个数据的具体展示位置，与第四个参数一一对应
         * */
		SimpleAdapter adapter = new SimpleAdapter(this, getData(list), R.layout.device_add_result_cell,
				new String[]{"name"}, new int[]{R.id.device_add_result_cell_tv});
		listView.setAdapter(adapter);
	}

	private ArrayList<HashMap<String, Object>> getData(ArrayList<String> list){
		ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String,Object>>();

//		for(int i=0;i<10;i++){
//			HashMap<String, Object> tempHashMap = new HashMap<String, Object>();
//			tempHashMap.put("image", R.drawable.icon);
//			tempHashMap.put("userName", "用户"+i);
//			tempHashMap.put("userAge", 30-i);
//			arrayList.add(tempHashMap);
//
//		}

		if(list != null) {
			for(String s : list) {
				HashMap<String, Object> tempHashMap = new HashMap<String, Object>();
				tempHashMap.put("name", s);
				arrayList.add(tempHashMap);
			}
		}

		return arrayList;
	}


	public void handleBack(View v) {
		Intent intent = this.getIntent();
		intent.putExtra("needRefulsh", returnNeedRefulsh);
		this.setResult(ACTIVITY_RESULT_CODE, intent);
		this.finish();
	}

	public void handleConfirm(View v) {
		handleBack(v);
	}


//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode == ACTIVITY_RESULT_CODE) {
//			returnNeedRefulsh = true;
//			return;
//		}
//	}
}
