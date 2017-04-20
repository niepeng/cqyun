package com.baibutao.app.waibao.yun.android.activites.common;

import java.util.concurrent.Callable;

import android.app.ProgressDialog;
import android.util.Log;

import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;

/**
 * @author lsb
 *
 * @date 2012-6-4 下午12:10:50
 */
public class ThreadHelper implements Callable<Response> {
	
	private ProgressDialog progressDialog;

	private Request request;
	
	private RemoteManager remoteManager;
	
	
	public ThreadHelper(ProgressDialog progressDialog, Request request) {
		super();
		this.progressDialog = progressDialog;
		this.request = request;
	}
	
	public ThreadHelper(ProgressDialog progressDialog, Request request, RemoteManager remoteManager) {
		this(progressDialog, request);
		this.remoteManager = remoteManager;
	}

	@Override
	public Response call() throws Exception {
		try {
			if(remoteManager == null) {
				remoteManager = RemoteManager.getFullFeatureRemoteManager();
			}
			Response response = remoteManager.execute(request);
			Log.d("response", String.valueOf(response));
			if(progressDialog != null) {
				progressDialog.setProgress(100);
			}
			Thread.sleep(200);
			return response;
		} catch (Exception e) {
			Log.e("ThreadHelper", e.getMessage(), e);
			throw new RuntimeException(e);
		} finally {
			if(progressDialog != null) {
				progressDialog.dismiss();
			}
		}
	}


}

