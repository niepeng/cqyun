/**
 * 
 */
package com.baibutao.app.waibao.yun.android.activites;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;
import com.baibutao.app.waibao.yun.android.activites.common.DownLoader;
import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;
import com.baibutao.app.waibao.yun.android.common.ProgressCallback;
import com.baibutao.app.waibao.yun.android.util.DateUtil;

/**
 * @author niepeng
 *
 * @date 2012-9-13 下午2:01:18
 */
public class UpdateClientActivity extends BaseActivity {

	private ProgressBar progressBar;

	private Handler handler;

	private String lastAndroidClientUrl;

	private TextView downloadApkGrogress;

	private File installFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.update_client);
		handler = new Handler();
		lastAndroidClientUrl = this.getIntent().getExtras().getString("lastAndroidClientUrl");
		progressBar = (ProgressBar) this.findViewById(R.id.update_client_download_progress_bar);
		downloadApkGrogress = (TextView) this.findViewById(R.id.download_apk_grogress);
		startDownLoad();
	}

	private void startDownLoad() {
		try {
//			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//				alert("has sdcard." + Build.VERSION.SDK_INT);
//			} else {
//				alert("has not sdcard." + Build.VERSION.SDK_INT);
//			}

			installFile = File.createTempFile(genName(), ".apk");

			String folder = Environment.getExternalStorageDirectory().getAbsolutePath();
			File f = new File(folder);
			if (!f.exists()) {
				f.mkdir();
			}
			String apkPath = folder + File.separator + genName() + ".apk";
			installFile = new File(apkPath);
			//创建apk文件
			installFile.createNewFile();
			alert("installFile=" + installFile.getAbsolutePath());

//			toastLong(installFile.getAbsolutePath());
//			File f = Environment.getExternalStorageDirectory();//获取SD卡目录
//			installFile =  new File(f, genName());
		} catch (Exception e) {
			Log.e("update", e.getMessage(), e);
			this.alert(getString(R.string.update_client_sd_no_space_msg));
			return;
		}
		DownLoader downLoader = new DownLoader(lastAndroidClientUrl, installFile.getAbsolutePath(), new UpdateDownLoadCallback());
		downLoader.asyDownload((EewebApplication) this.getApplication());

	}

	private void startInstall(String fileName) {
		alert("fileName=" + fileName);
		File file = new File(fileName);
		if (!file.exists()) {
			alert("文件不存在");
			return;
		}
		alert("src=" + file.getAbsolutePath());


		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (Build.VERSION.SDK_INT >= 24) {
			//参数1 上下文；参数2 Provider主机地址 authorities 和配置文件中保持一致 ；参数3  共享的文件
			Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), "com.baibutao.app.waibao.yun.android.ins", file);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
		} else {
			intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		}

		startActivity(intent);
//		android.os.Process.killProcess(android.os.Process.myPid());



//		String type = "application/vnd.android.package-archive";
//		Intent intent = new Intent();
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setAction(android.content.Intent.ACTION_VIEW);
////		intent.addCategory(Intent.CATEGORY_DEFAULT);
//		intent.setDataAndType(Uri.fromFile(file), type);
//		startActivity(intent);
//		android.os.Process.killProcess(android.os.Process.myPid());
//		endMe();
	}

	private void endMe() {
		try {
			Log.d("update", "finishAllActivities!!");
			((EewebApplication)this.getApplication()).finishAllActivities();
			//Log.d("update", "killProcess!!");
			//android.os.Process.killProcess(android.os.Process.myPid());
			Log.d("update", "done!!");
		} catch (Throwable e) {
			Log.e("update", e.getMessage(), e);
		}
	}

	private class UpdateDownLoadCallback implements ProgressCallback {

		private int max;

		@Override
		public void onFinish() {
			final String fileName = installFile.getAbsolutePath();
			/*
			 * handler.post(new Runnable() {
			 * 
			 * @Override public void run() {
			 * UpdateClientActivity.this.alert("下载成功！"); } });
			 */
			handler.post(new Runnable() {

				@Override
				public void run() {
					startInstall(fileName);
				}
			});


		}

		@Override
		public void onSetMaxSize(int maxSize) {
			int targetMax = maxSize;
			if (maxSize < 0) {
				targetMax = 100;
			}
			this.max = targetMax;
			final int temp = targetMax;
			handler.post(new Runnable() {

				@Override
				public void run() {
					progressBar.setMax(temp);
					downloadApkGrogress.setText("0%");
				}
			});
		}

		@Override
		public void onProgress(final int downloadSize) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					progressBar.setProgress(downloadSize);
					if (max > 0) {
						int percent = (int) downloadSize * 100 / max;
						downloadApkGrogress.setText(percent + "%");
					}
				}
			});
		}

		@Override
		public void onException(Exception e) {
			alert(getString(R.string.app_label_conect_network_fail));
		}

	}
	
	private static String genName() {
//		return "eeweb_install_file_" + DateUtil.format(new Date(), "yyyy_MM_dd_HH_mm") + ".apk";
		return "cq_" + DateUtil.format(new Date(), "MM_dd_HH_mm");
	}

}
