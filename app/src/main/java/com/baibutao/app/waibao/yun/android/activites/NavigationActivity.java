package com.baibutao.app.waibao.yun.android.activites;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.activites.common.NavigationTabIndexEnum;
import com.baibutao.app.waibao.yun.android.activites.common.TabFlushEnum;
import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;
import com.baibutao.app.waibao.yun.android.common.GlobalUtil;
import com.baibutao.app.waibao.yun.android.service.MessageService;
import com.baibutao.app.waibao.yun.android.util.ActionConstant;
import com.baidu.location.BDLocationListener;

import java.lang.reflect.Field;


public class NavigationActivity extends BaseActivity {
	
	// 默认选中tab的index
	private final int DEFAULT_CHECKED = 0;

	private TabHost tabs;
	
	private TabWidget tabWidget;

	private static final int NAVEIGATION[][] = {
//		{ R.drawable.foot_near_1, R.drawable.foot_near_2 }, 
//		{ R.drawable.foot_shopping_1, R.drawable.foot_shopping_2},
//		{ R.drawable.foot_more_1, R.drawable.foot_more_2 } 
		{ R.drawable.udata, R.drawable.data }, 
		{ R.drawable.ualarm, R.drawable.alarm},
		{ R.drawable.umore, R.drawable.more } 
	};

	protected static final Class<?>[] TAB_ITEMS = { InTimeActivity.class, AlarmActivity.class, SetupActivity.class };

	public BDLocationListener myListener;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.navigation);
		tabs = (TabHost) findViewById(android.R.id.tabhost);
		
		
		// 启动服务
		Intent intent = new Intent(this, MessageService.class);
		startService(intent);
		
		EewebApplication app = (EewebApplication) getApplication();
		app.setTabHost(tabs);

		tabWidget = (TabWidget) findViewById(android.R.id.tabs);
		int width = GlobalUtil.dipToPixel(50) * 2;
		int height = GlobalUtil.dipToPixel(50);

		tabs.setup(this.getLocalActivityManager());

		addNewsList(tabs);
		addShoppingList(tabs);
		addMore(tabs);

		tabs.setCurrentTab(DEFAULT_CHECKED);

		tabWidget.setPadding(0, 0, 0, 0);

		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			RelativeLayout relativeLayout = (RelativeLayout) tabWidget.getChildAt(i);
			relativeLayout.getLayoutParams().height = height;
			relativeLayout.getLayoutParams().width = width;
			// TODO lily
//			relativeLayout.setPadding(0, -3, 0, 0);
			relativeLayout.setPadding(0, 0, 0, 0);

			final TextView tv = (TextView) relativeLayout.findViewById(android.R.id.title);
			// TODO lily
//			tv.setPadding(tv.getPaddingLeft(), -2, tv.getPaddingRight(), 5);
			tv.setPadding(tv.getPaddingLeft(), 0, tv.getPaddingRight(), 5);
			tv.setTextColor(this.getResources().getColorStateList(android.R.color.white));
			tv.setTextSize(11);
			tv.setGravity(Gravity.CENTER);

			if (i == DEFAULT_CHECKED) {
				tv.setCompoundDrawablesWithIntrinsicBounds(0, NAVEIGATION[i][1], 0, 0);
				relativeLayout.setBackgroundResource(R.color.black);
			} else {
				tv.setCompoundDrawablesWithIntrinsicBounds(0, NAVEIGATION[i][0], 0, 0);
				relativeLayout.setBackgroundResource(R.drawable.nav_selector);
			}
		}

		/**
		 * 此方法是为了去掉系统默认的色白的底角
		 * 
		 * 在 TabWidget中mBottomLeftStrip、mBottomRightStrip 都是私有变量，但是我们可以通过反射来获取
		 * 
		 * 由于还不知道Android 2.2的接口是怎么样的，现在先加个判断好一些
		 */
		Field mBottomLeftStrip;
		Field mBottomRightStrip;
		if (Integer.parseInt(Build.VERSION.SDK) <= 7) {
			try {
				mBottomLeftStrip = tabWidget.getClass().getDeclaredField("mBottomLeftStrip");
				mBottomRightStrip = tabWidget.getClass().getDeclaredField("mBottomRightStrip");
				if (!mBottomLeftStrip.isAccessible()) {
					mBottomLeftStrip.setAccessible(true);
				}
				if (!mBottomRightStrip.isAccessible()) {
					mBottomRightStrip.setAccessible(true);
				}
				mBottomLeftStrip.set(tabWidget, getResources().getDrawable(R.drawable.tab_one_normal));
				mBottomRightStrip.set(tabWidget, getResources().getDrawable(R.drawable.tab_one_normal));
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// 去掉系统默认的色白的底角(太BT的方法了)
			tabs.setPadding(tabs.getPaddingLeft(), tabs.getPaddingTop(), tabs.getPaddingRight(), tabs.getPaddingBottom() - 5);
		}

		/**
		 * 当点击tab选项卡的时候，更换图片
		 */
		tabs.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				tabChange(Integer.parseInt(tabId));
			}
		});
//		notifyDoSomething();

	}
	
	public void tabChange(int tabIndex) {
		tabs.setCurrentTab(tabIndex);
		
		for (int i = 0; i < tabWidget.getChildCount(); i++) {
			RelativeLayout relativeLayout = (RelativeLayout) tabWidget.getChildAt(i);
			final TextView tv = (TextView) relativeLayout.findViewById(android.R.id.title);
			if (tabs.getCurrentTab() == i) {
				tv.setCompoundDrawablesWithIntrinsicBounds(0, NAVEIGATION[i][1], 0, 0);
				relativeLayout.setBackgroundResource(R.color.black);
			} else {
				tv.setCompoundDrawablesWithIntrinsicBounds(0, NAVEIGATION[i][0], 0, 0);
				relativeLayout.setBackgroundResource(R.drawable.nav_selector);
			}
		}
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		notifyDoSomething();
	}


	public void notifyDoSomething() {
		int flag = getIntent().getIntExtra(ActionConstant.NOTIFICATION_FLAG_KEY, 0);
		if (flag == ActionConstant.NOTIFICATION_FLAG_VALUE) {
			eewebApplication.setReflushAlarmActivity(true);
			tabChange(TabFlushEnum.ALARM.getIndex());
		}
	}

	private void addNewsList(TabHost tabs) {
		Intent intent = new Intent(NavigationActivity.this, InTimeActivity.class);
		TabSpec tabSpec = tabs.newTabSpec(NavigationTabIndexEnum.IN_TIME.getStringValue());
		tabSpec.setIndicator("实时", null);
		tabSpec.setContent(intent);
		tabs.addTab(tabSpec);
	}
	
	private void addShoppingList(TabHost tabs) {
		Intent intent = new Intent(NavigationActivity.this, AlarmActivity.class);
		TabSpec tabSpec = tabs.newTabSpec(NavigationTabIndexEnum.ALARM.getStringValue());
		tabSpec.setIndicator("报警", null);
		tabSpec.setContent(intent);
		tabs.addTab(tabSpec);
	}
	
	private void addMore(TabHost tabs) {
		Intent intent = new Intent(NavigationActivity.this, SetupActivity.class);
		TabSpec tabSpec = tabs.newTabSpec(NavigationTabIndexEnum.SET_UP.getStringValue());
		tabSpec.setIndicator("更多", null);
		tabSpec.setContent(intent);
		tabs.addTab(tabSpec);
	}
	
}

