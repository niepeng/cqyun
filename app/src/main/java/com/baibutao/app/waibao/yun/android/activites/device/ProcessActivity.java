package com.baibutao.app.waibao.yun.android.activites.device;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.view.View;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.androidext.CircleProgressView;


/**
 * <p>标题: </p>
 * <p>描述: </p>
 * <p>版权: lsb</p>
 * <p>创建时间: 2017年3月17日  下午8:11:22</p>
 * <p>作者：niepeng</p>
 */
public class ProcessActivity extends BaseActivity {

	private CircleProgressView mCircleBar;

	private final int second = 60;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.process);
		initViews();
	}

	private String getShowSecond(int i) {
		return (second-(int)((second * 1.d)/100*(100-i)))+ "秒";
	}


	private void initViews() {
		mCircleBar = (CircleProgressView) findViewById(R.id.circleProgressbar);
//		mCircleBar.setProgress(0);
		mCircleBar.setProgressAndText(100, second + "秒");

	}

	
	public void handleBack(View v) {
		Intent intent = this.getIntent();
		this.setResult(ACTIVITY_RESULT_CODE, intent);
		this.finish();
	}

	public void handleByTest(View v) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int total = 100;
				for(int i=100;i>=0;i--) {
					SystemClock.sleep(second * 10);
					updateProcess(i, getShowSecond(i));
				}
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						handleProcess();
					}
				});
			}
		}).start();
	}


	public void updateProcess(final int flag, final String text) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				mCircleBar.setProgress(flag);
				mCircleBar.setProgressAndText(flag, text);
			}
		});
	}

	public void handleProcess() {
		alert("hello");
	}



	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == ACTIVITY_RESULT_CODE) {
			return;
		}
	}
}
