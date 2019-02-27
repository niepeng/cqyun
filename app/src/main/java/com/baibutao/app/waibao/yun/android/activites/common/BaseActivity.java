package com.baibutao.app.waibao.yun.android.activites.common;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.LoginActivity;
import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;
import com.baibutao.app.waibao.yun.android.biz.LoadImgDO;
import com.baibutao.app.waibao.yun.android.biz.bean.AlarmBean;
import com.baibutao.app.waibao.yun.android.biz.dataobject.UserDO;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.parser.StringResponseParser;
import com.baibutao.app.waibao.yun.android.util.AssetsUtil;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.DateUtil;
import com.baibutao.app.waibao.yun.android.util.JsonUtil;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import liuyongxiang.robert.com.testtime.wheelview.ScreenInfo;
import liuyongxiang.robert.com.testtime.wheelview.WheelMain;

/**
 * @author lsb
 * @date 2012-5-29 下午10:36:46
 */
public class BaseActivity extends ActivityGroup {

    protected EewebApplication eewebApplication;

    protected static Drawable defaultImage;

    protected static Drawable defaultImageSmall;

    protected final Handler handler = new Handler();

    protected WheelMain wheelMainDate;

    protected static final int ACTIVITY_RESULT_CODE = 1;
    protected static final int ACTIVITY_DEL_RESULT_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eewebApplication = (EewebApplication) this.getApplication();
        eewebApplication.addActivity(this);

        if (defaultImage == null) {
            defaultImage = getResources().getDrawable(R.drawable.default_img);
        }

        if (defaultImageSmall == null) {
            defaultImageSmall = getResources().getDrawable(R.drawable.default_img_small);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
//		StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//		StatService.onPause(this);
    }

    protected boolean hasNetWork() {
        ConnectivityManager nw = (ConnectivityManager) eewebApplication.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (nw == null) {
            return false;
        }
        NetworkInfo netinfo = nw.getActiveNetworkInfo();
        if (netinfo == null) {
            return false;
        }
        return netinfo.isAvailable();
    }

    protected void recordUserUmeng(String username, String umengDeviceToken) {
        RemoteManager remoteManager = RemoteManager.getRawRemoteManager();
        remoteManager.setResponseParser(new StringResponseParser());
        Request request = remoteManager.createPostRequest(Config.Values.URL);
        final Map<String, Object> map = CollectionUtil.newHashMap();
        map.put("user", username);
        map.put("token", umengDeviceToken);
        request.setBody(JsonUtil.mapToJson(map));
        request.addHeader("type", "setAndroidDevToken");
        eewebApplication.asyInvoke(new ThreadHelper(null, request, remoteManager));
    }




    protected Drawable getDrawableByType(AlarmBean alarmBean) {
        Resources resources = this.getResources();
        return resources.getDrawable(R.drawable.temp2);

//        String type = alarmBean.getType();
//        // 	// 1:温度过高;2:温度过低;3:湿度过高;4:湿度过低;5:开关报警;6:设备离线;7:传感器异常;8:传感器未连接
//        if ("1".equals(type) || "2".equals(type)) {
//            if ("0".equals(alarmBean.getBeginEndMark())) {
//                return resources.getDrawable(R.drawable.temp);
//            }
//            return resources.getDrawable(R.drawable.temp2);
//        }
//
//        if ("3".equals(type) || "4".equals(type)) {
//            if ("0".equals(alarmBean.getBeginEndMark())) {
//                return resources.getDrawable(R.drawable.humi);
//            }
//            return resources.getDrawable(R.drawable.humi2);
//        }
//
//        if ("6".equals(type)) {
//            if ("0".equals(alarmBean.getBeginEndMark())) {
//                return resources.getDrawable(R.drawable.off);
//            }
//            return resources.getDrawable(R.drawable.off2);
//        }
//
//        if ("7".equals(type) || "8".equals(type)) {
//            if ("0".equals(alarmBean.getBeginEndMark())) {
//                return resources.getDrawable(R.drawable.nosensor);
//            }
//            return resources.getDrawable(R.drawable.nosensor2);
//        }
//
//        return null;
    }

    protected ProgressDialog showProgressDialog(int message) {
        String title = getString(R.string.app_name);
        String messageString = getString(message);
        return ProgressDialog.show(this, title, messageString);
    }

    public void showBottoPopupWindow(final TextView currentTv, TextView locationTv) {
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        View menuView = LayoutInflater.from(this).inflate(R.layout.show_popup_window, null);
        final PopupWindow mPopupWindow = new PopupWindow(menuView, (int) (width * 0.8), LinearLayout.LayoutParams.WRAP_CONTENT);
        ScreenInfo screenInfoDate = new ScreenInfo(this);
        wheelMainDate = new WheelMain(menuView, true);
        wheelMainDate.screenheight = screenInfoDate.getHeight();

        String dateStr = currentTv.getText().toString();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.parse(dateStr, DateUtil.DEFAULT_DATE_FMT));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelMainDate.initDateTimePicker(year, month, day, hours, minute);
        // mPopupWindow.setAnimationStyle(R.style.AnimationPreview);

        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(locationTv, Gravity.CENTER, 0, 0);
        mPopupWindow.setOnDismissListener(new poponDismissListener());
        // backgroundAlpha(0.6f);
        TextView tv_cancle = (TextView) menuView.findViewById(R.id.tv_cancle);
        TextView tv_ensure = (TextView) menuView.findViewById(R.id.tv_ensure);
        TextView tv_pop_title = (TextView) menuView.findViewById(R.id.tv_pop_title);
        tv_pop_title.setText("选择时间");
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
                // backgroundAlpha(1f);
            }
        });

        tv_ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Date selectDate = wheelMainDate.getTimeDate();
                currentTv.setText(DateUtil.format(selectDate, DateUtil.DEFAULT_DATE_FMT));
                mPopupWindow.dismiss();
                // backgroundAlpha(1f);
            }
        });
    }

    class poponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
//            backgroundAlpha(1f);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isTabActivity()) {
                exit();
                return true;
            } else {
                this.finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void exit() {
        confirm(this.getResources().getString(R.string.app_exit_confirm), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eewebApplication.finishAllActivities();
                UserDO userDO = eewebApplication
                        .getUserDO();
                userDO.setPsw("");
                eewebApplication.setUserDO(userDO);
                toLoginPage();
            }
        }, null);
    }

    protected void toLoginPage() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    private boolean isTabActivity() {
        for (TabFlushEnum tabFlushEnum : eewebApplication.tabFlushEnums) {
            if (this.getClass().getName().equals(tabFlushEnum.getTabActivity().getName())) {
                return true;
            }
        }
        return false;
    }


    protected String loadContent(String pathFile, Map<String, String> internVars) {
        String content = AssetsUtil.getAssertAsString(this, pathFile);
        if (!StringUtil.isEmpty(content) && internVars != null && !internVars.isEmpty()) {
            for (Map.Entry<String, String> entry : internVars.entrySet()) {
                content = content.replaceAll(entry.getKey(), entry.getValue());
            }

        }
        return content.replaceAll("\r\n", "\n");
    }


    public void alert(CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(getMessageBoxTitle());
        alertDialog.setMessage(message);
        String buttonLabel = getString(R.string.app_btn_label_ok);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, buttonLabel, (Message) null);
        alertDialog.show();
    }

    protected void confirm(CharSequence message, OnClickListener onYesButton, OnClickListener onNoButton) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        alertDialog.setTitle(getMessageBoxTitle());
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.app_btn_label_ok), onYesButton);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.app_btn_label_canel), onNoButton);
        alertDialog.show();
    }

    public String getUid() {
        return eewebApplication.getUid();
    }

    public void setViewVisible(final View... views) {
        setView(true, views);
    }


    public void setViewGone(final View... views) {
        setView(false, views);
    }

    private void setView(final boolean isShow, final View... views) {
        if (views == null || views.length == 0) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (View view : views) {
                    if (view == null) {
                        continue;
                    }
                    int v = view.getVisibility();
                    if (isShow && v == View.VISIBLE) {
                        continue;
                    }
                    if (!isShow && v == View.GONE) {
                        continue;
                    }
                    if (isShow) {
                        view.setVisibility(View.VISIBLE);
                    } else {
                        view.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public void setViewGoneBySynchronization(final View... views) {
        setViewBySynchronization(false, views);
    }

    public void setViewVisiableBySynchronization(final View... views) {
        setViewBySynchronization(true, views);
    }


    private void setViewBySynchronization(final boolean isShow, final View... views) {
        if (views == null || views.length == 0) {
            return;
        }
        for (View view : views) {
            if (view == null) {
                continue;
            }
            int v = view.getVisibility();
            if (isShow && v == View.VISIBLE) {
                continue;
            }
            if (!isShow && v == View.GONE) {
                continue;
            }
            if (isShow) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }


    /**
     * 隐藏键盘
     */
    public void hiddenBoard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    public void asyLoadImages(final ImageView imageView, final String fileName, final String picUrl) {
        LoadImgDO loadImgDO = new LoadImgDO();
        loadImgDO.setHandler(handler);
        loadImgDO.setImageView(imageView);
        loadImgDO.setFileName(fileName);
        loadImgDO.setPicUrl(picUrl);
        eewebApplication.addLoadImage(loadImgDO);
    }

    public void asyLoadImages(final ImageView imageView, final String fileName, final String picUrl, int newWidth) {
        LoadImgDO loadImgDO = new LoadImgDO();
        loadImgDO.setHandler(handler);
        loadImgDO.setImageView(imageView);
        loadImgDO.setFileName(fileName);
        loadImgDO.setPicUrl(picUrl);
        loadImgDO.setNewWidth(newWidth);
        eewebApplication.addLoadImage(loadImgDO);
    }

    public void setImageViewSize(final ImageView imageView, final Bitmap bitmap, final int newWidth) {
        if (imageView == null) {
            return;
        }
        imageView.getLayoutParams().width = newWidth;
        imageView.getLayoutParams().height = (int) (newWidth * bitmap.getHeight() / Double.valueOf(bitmap.getWidth()).doubleValue());
    }


    protected boolean lengthValidator(String content, int min, int max) {
        if (min > max || content == null) {
            return true;
        }

        int length = content.getBytes().length;
        if (length >= min && length <= max) {
            return true;
        }
        return false;
    }


    public CharSequence getTimes(Date startTime, Date endTime) {
        if (startTime == null) {
            return null;
        }
        if (endTime == null) {
            return DateUtil.format(startTime, DateUtil.DATE_MMddHHmm);
        }
        return DateUtil.format(startTime, DateUtil.DATE_MMddHHmm) + " ~ " + DateUtil.format(endTime, DateUtil.DATE_MMddHHmm);
    }


    public String getNameByUrl(String url) {
        if (StringUtil.isEmpty(url)) {
            return url;
        }
        int index = url.lastIndexOf("/");
        if (index < 1) {
            return url;
        }
        return url.substring(index + 1);
    }


    protected String getMessageBoxTitle() {
        return this.getTitle().toString();
    }

    protected void toastShort(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void toastShort(int message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void toastLong(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    protected void toastLong(int message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    protected String getLogTag() {
        return getClass().getSimpleName();
    }

    protected final void logError(String msg, Throwable e) {
        Log.e(getLogTag(), msg, e);
    }

    protected final void logError(String msg) {
        Log.e(getLogTag(), msg);
    }

    protected final void logWarn(String msg) {
        Log.w(getLogTag(), msg);
    }

    protected final void logInfo(String msg) {
        Log.i(getLogTag(), msg);
    }

    protected final void logDebug(String msg) {
        Log.d(getLogTag(), msg);
    }


    public EewebApplication getHuiApplication() {
        return eewebApplication;
    }


    public Handler getHandler() {
        return handler;
    }

}
