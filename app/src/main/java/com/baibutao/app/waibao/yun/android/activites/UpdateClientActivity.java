/**
 * 
 */
package com.baibutao.app.waibao.yun.android.activites;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
			installFile = File.createTempFile(genName(), ".apk");
		} catch (IOException e) {
			Log.e("update", e.getMessage(), e);
			this.alert(getString(R.string.update_client_sd_no_space_msg));
			return;
		}
		DownLoader downLoader = new DownLoader(lastAndroidClientUrl, installFile.getAbsolutePath(), new UpdateDownLoadCallback());
		downLoader.asyDownload((EewebApplication) this.getApplication());

	}

	private void startInstall(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			return;
		}
		String type = "application/vnd.android.package-archive";
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), type);
		startActivity(intent);
		endMe();
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
			startInstall(fileName);

			/*
			 * handler.post(new Runnable() {
			 * 
			 * @Override public void run() {
			 * UpdateClientActivity.this.alert("下载成功！"); } });
			 */

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
		return "eeweb_install_file_" + DateUtil.format(new Date(), "yyyy_MM_dd_HH_mm") + ".apk";
	}

}
