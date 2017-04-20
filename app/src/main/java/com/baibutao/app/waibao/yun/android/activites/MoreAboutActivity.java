/**
 * 
 */
package com.baibutao.app.waibao.yun.android.activites;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;

/**
 * @author niepeng
 *
 * @date 2012-9-13 下午1:12:28
 */
public class MoreAboutActivity extends BaseActivity {

	private TextView versionTv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.more_about);
		
		((TextView) findViewById(R.id.more_about_title)).getPaint().setFakeBoldText(true);
		versionTv = (TextView) findViewById(R.id.more_other_about_version);
		versionTv.setText("软件版本：" + eewebApplication.getVersionName());
		
		
	}
	
	public void handleBack(View v) {
		this.finish();
	}
}
