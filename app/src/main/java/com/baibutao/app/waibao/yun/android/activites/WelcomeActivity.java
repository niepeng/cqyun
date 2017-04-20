package com.baibutao.app.waibao.yun.android.activites;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.tasks.AuthorUserTask;
import com.baibutao.app.waibao.yun.android.tasks.CheckUpdateTask;
import com.baibutao.app.waibao.yun.android.tasks.message.MessageHelper;
import com.baibutao.app.waibao.yun.android.tasks.taskgroup.TaskGroup;

public class WelcomeActivity extends BaseActivity {
	
	private Handler handler;
	
	private static final long waitTime = 3000;
	
	private final int[] picResourceId = {R.drawable.welcome_pic_480_800, R.drawable.welcome_pic_540_960, R.drawable.welcome_pic}; 
	
	private final double[] widthAndHeight = {0.6D, 0.5525D, 0.6667D};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.welcome);
		
		LinearLayout welcomelinear = (LinearLayout) findViewById(R.id.welcomelinear);
		welcomelinear.setBackgroundResource(getPicResourceId());
		
		handler = new Handler();
		final MessageHelper messageHelper = new MessageHelper(false);
		final TaskGroup taskGroup = new TaskGroup();
		
		Runnable navigationThread = new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(waitTime);
					// 如果已经弹出自动更新对话框，然用户选择后自己进去
					if (!messageHelper.set()) {
						startNaviagetionEventWhere();
					}
					
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					logError("", e);
				} finally {
					taskGroup.shutdown();
				}
			}
		};
		
		// 异步发送一次请求注册
//		eewebApplication.asyInvoke(new AuthorUserTask(eewebApplication));
		
		taskGroup.addMust(loadCatDataThread);
		taskGroup.addMust(navigationThread);
		
		taskGroup.addMay(new CheckUpdateTask(eewebApplication, this, handler, messageHelper));
		
		eewebApplication.asyCall(taskGroup);
		
	}
	
	private int getPicResourceId() {
		int index = 0;
		int width = eewebApplication.getMobileUserInfo().getWidth();
		int height = eewebApplication.getMobileUserInfo().getHeight();
		double currentWidthAndHeight = Double.valueOf(width).doubleValue() / Double.valueOf(height).doubleValue();
		double redouce = 0.d;
		for (int i = 0; i < widthAndHeight.length; i++) {
			double temp = Math.abs(currentWidthAndHeight - widthAndHeight[i]);
			if (i == 0) {
				redouce = temp;
				index = i;
				continue;
			}
			if (temp < redouce) {
				index = i;
				redouce = temp;
			}
		}
		return picResourceId[index];
	}
	
	
	Runnable loadCatDataThread = new Runnable() {
		@Override
		public void run() {
//			RemoteManager remoteManager = RemoteManager.getFullFeatureRemoteManager();
//			Request request = remoteManager.createQueryRequest(Config.getConfig().getProperty(Config.Names.SHOP_LIST_URL));
//			Response response = remoteManager.execute(request);
//			parseResponse(response);
//			write2LocalCat(cardApplication.getCatList());
		}
	};
	
	
	protected void parseResponse(Response response) {
		if(response == null || !response.isSuccess()) {
			return;
		}
//		JSONObject jsonObject = (JSONObject) response.getModel();
		/*List<CategoryDO> catList = JSONHelper.json2CatDOs(jsonObject);
		if(!CollectionUtil.isEmpty(catList)) {
			cardApplication.setCatList(catList);
		}*/
	}


	
	
	private void startNaviagetionEventWhere() {
		handler.post(new Runnable() {
			@Override
			public void run() {
				startNaviagetion();
			}
		});

	}

	private void startNaviagetion() {
		Intent intent = new Intent();
//		intent.setClass(this, NavigationActivity.class);
		intent.setClass(this, LoginActivity.class);
		this.startActivity(intent);
		this.finish();
	}
	

}