package com.baibutao.app.waibao.yun.android.activites;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.AreasLoader;
import com.baibutao.app.waibao.yun.android.activites.device.DeviceReadAddActivity;
import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;
import com.baibutao.app.waibao.yun.android.common.Constant;
import com.baibutao.app.waibao.yun.android.fragments.TabFragment;
import com.baibutao.app.waibao.yun.android.service.MessageService;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<List<String>>, TabFragment.OnTabChangeListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private Toolbar mToolbar;

    private int mShowType;
    private String mSelectedArea = null;

    private List<String> mAreas;

    private int mSelectedTabPosition = 0;

    protected static final int ACTIVITY_RESULT_CODE = 1;
    protected static final int ACTIVITY_DEL_RESULT_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MessageService.class);
        startService(intent);

        EewebApplication eewebApplication = (EewebApplication) getApplication();
        eewebApplication.addActivity(this);

        setContentView(R.layout.activity_main);

        EewebApplication application = (EewebApplication) getApplication();
        mShowType = application.getPrefs(getString(R.string.prefs_key_intime_show_type), Constant.SHOW_TYPE_LINEAR);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        mFragmentManager = getSupportFragmentManager();


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);

        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        setFirstItemNavigationView();

        setAccountView();

        getSupportLoaderManager().initLoader(0, null, this);

        setContentMain(mShowType, mSelectedArea);
        onTabSelected(mSelectedTabPosition);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null) {
            int position = intent.getIntExtra(Constant.ARG_SELECTED_POSITION, -1);
            if (position > -1) {
                NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                nMgr.cancelAll();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent() != null) {
            int position = getIntent().getIntExtra(Constant.ARG_SELECTED_POSITION, -1);
            if (position > -1) {
                mSelectedTabPosition = position;
                setContentMain(mShowType, mSelectedArea);
                onTabSelected(mSelectedTabPosition);
            }
        }
    }

    private void setFirstItemNavigationView() {
        mNavigationView.setCheckedItem(R.id.nav_all_equipment);
        getSupportActionBar().setTitle(getText(R.string.menu_item_all_equipment));
    }

    private void setAccountView() {
        View header = mNavigationView.getHeaderView(0);
        TextView accountView = (TextView) header.findViewById(R.id.account_name);
        EewebApplication app = (EewebApplication) getApplication();
        accountView.setText(app.getUserDO().getUsername());
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            // TODO
            Intent intent = new Intent(this, DeviceReadAddActivity.class);
            startActivityForResult(intent, ACTIVITY_RESULT_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all_equipment) {
            mSelectedArea = null;
        } else {
            CharSequence title = item.getTitle();
            if (item.getGroupId() == R.id.custom_area) {
                mSelectedArea = title.toString();
                item.setCheckable(true);
                item.setChecked(true);
            }
            // TODO 区域管理
        }
        getSupportActionBar().setTitle(item.getTitle());
        mDrawerLayout.closeDrawer(GravityCompat.START);
        setContentMain(mShowType, mSelectedArea);
        return true;
    }

    public void onListModeClick(View v) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mShowType = Constant.SHOW_TYPE_LINEAR;
        setContentMain(mShowType, mSelectedArea);
        saveIntimeShowType(mShowType);
    }

    public void onGridModeClick(View v) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mShowType = Constant.SHOW_TYPE_GRID;
        setContentMain(mShowType, mSelectedArea);
        saveIntimeShowType(mShowType);
    }

    private void saveIntimeShowType(int type) {
        EewebApplication application = (EewebApplication) getApplication();
        application.savePrefs(getString(R.string.prefs_key_intime_show_type), type);
    }

    public void setContentMain(int showType, String area) {
        Bundle args = new Bundle();
        args.putInt(Constant.ARG_SHOW_TYPE, showType);
        args.putString(Constant.ARG_AREA, area);
        args.putInt(Constant.ARG_SELECTED_POSITION, mSelectedTabPosition);
        TabFragment tabFragment = new TabFragment();
        tabFragment.setArguments(args);
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_main, tabFragment).commit();
    }

    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        return new AreasLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
        mAreas = data;
        Menu menu = mNavigationView.getMenu();
        for (String item : data) {
            menu.add(R.id.custom_area, Menu.NONE, Menu.NONE, item);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {

    }

    @Override
    public void onTabSelected(int position) {
        mSelectedTabPosition = position;
        if (position == 0) {
            mToolbar.setVisibility(View.VISIBLE);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else if (position == 1) {
            mToolbar.setVisibility(View.GONE);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else if (position == 2) {
            mToolbar.setVisibility(View.GONE);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ACTIVITY_DEL_RESULT_CODE) {
            setContentMain(mShowType, mSelectedArea);
            return;
        }

        if (data != null) {
            boolean refush = data.getExtras().getBoolean("needRefulsh");
            if (refush) {
                setContentMain(mShowType, mSelectedArea);
                return;
            }
        }
    }
}
