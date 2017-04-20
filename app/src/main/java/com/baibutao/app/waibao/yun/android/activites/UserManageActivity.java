/**
 * 
 */
package com.baibutao.app.waibao.yun.android.activites;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;

/**
 * @author niepeng
 * 
 * @date 2012-9-12
 */
public class UserManageActivity extends BaseActivity {
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usermanage);
		
	}
	
	public void handleBack(View v) {
		this.finish();
	}
	
	
	public void handleBindMail(View v) {
		Intent intent = new Intent(this, UserManageBindMailActivity.class);
		startActivity(intent);
	}
	
	public void handleUpdatePsw(View v) {
		Intent intent = new Intent(this, UserManageUpdatePswActivity.class);
		startActivity(intent);
	}
	
	public void handeExit(View v) {
		exit();
	}
	
	

}
