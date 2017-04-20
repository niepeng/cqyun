/**
 * 
 */
package com.baibutao.app.waibao.yun.android.activites;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.biz.dataobject.SetupDO;
import com.baibutao.app.waibao.yun.android.common.SetupInfoHolder;

/**
 * @author niepeng
 * 
 * @date 2012-9-13 下午1:12:28
 */
public class SetupUpdateDataTimeActivity extends BaseActivity {

//	private Spinner inTimeSpinner;
	
	private Spinner alarmSpinner;
	
//	private int currentChoiceIntime;
	
	private int currentChoiceAlarmTime;
	
	/*
	 * strngs.xml 最后：
	 * intime_labels
	 * 
	 * <item>1分钟</item>
        <item>2分钟</item>
        <item>3分钟</item>
        <item>5分钟</item>
        <item>10分钟</item>
        <item>半小时</item>
        <item>1小时</item>
        <item>2小时</item>
	 */
//	private static final int[] INTIME_TIMES = {60, 2 * 60, 3 *60, 5 * 60, 10 * 60, 
//		30 * 60, 60 * 60,  2 * 60 * 60};
	
	/**
	 * strings.xml 
	 *  <string-array name="alarm_labels">
        <item>1分钟</item>
        <item>5分钟</item>
        <item>10分钟</item>
        <item>15分钟</item>
        <item>20分钟</item>
        <item>25分钟</item>
        <item>30分钟</item>
        <item>60分钟</item>
    </string-array>
	 */
	private static final int[] ALARM_TIMES = { 60, 5 * 60, 10 * 60, 
		15 * 60, 20 * 60, 25 * 60,  30 * 60, 60 * 60};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.setup_update_time);

//		inTimeSpinner = (Spinner) findViewById(R.id.time_spinner);
		alarmSpinner = (Spinner) findViewById(R.id.alarm_spinner);

//		inTimeSpinner.setOnItemSelectedListener(new OnItemSelectedListenerImpl());
		alarmSpinner.setOnItemSelectedListener(new OnItemSelectedListenerImpl2());

		SetupDO setupDO = SetupInfoHolder.getDO(this);
//		inTimeSpinner.setSelection(getPostion(setupDO.getUpdateIntime(), INTIME_TIMES));
		alarmSpinner.setSelection(getPostion(setupDO.getAlarmtime(), ALARM_TIMES));
	}

	private int getPostion(int updateIntime, int[] intimeTimes) {
		for (int i = 0; i < intimeTimes.length; i++) {
			if (intimeTimes[i] == updateIntime) {
				return i;
			}
		}
		return 0;
	}

	public void handleBack(View v) {
		this.finish();
	}
	
	public void handleSubmit(View v) {
//		SetupDO setupDO = new SetupDO(currentChoiceIntime, currentChoiceAlarmTime);
		SetupDO setupDO = new SetupDO(0, currentChoiceAlarmTime);
		SetupInfoHolder.save(this, setupDO);
		toastLong("设置成功");
		this.finish();
	}

	private class OnItemSelectedListenerImpl implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//			String value = (String) parent.getItemAtPosition(position);// 得到选中的选项
//			currentChoiceIntime = INTIME_TIMES[position];
		}
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}
	
	private class OnItemSelectedListenerImpl2 implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//			String value = (String) parent.getItemAtPosition(position);// 得到选中的选项
			currentChoiceAlarmTime = ALARM_TIMES[position];
		}
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

}
