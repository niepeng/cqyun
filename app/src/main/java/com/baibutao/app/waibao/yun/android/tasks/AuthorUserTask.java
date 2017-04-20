package com.baibutao.app.waibao.yun.android.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;

import com.baibutao.app.waibao.yun.android.androidext.EewebApplication;
import com.baibutao.app.waibao.yun.android.common.MessageCodes;
import com.baibutao.app.waibao.yun.android.config.Config;
import com.baibutao.app.waibao.yun.android.localcache.FileCache;
import com.baibutao.app.waibao.yun.android.remote.RemoteManager;
import com.baibutao.app.waibao.yun.android.remote.Request;
import com.baibutao.app.waibao.yun.android.remote.Response;
import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

public class AuthorUserTask implements Callable<String> {

	private EewebApplication huiApplication;
	
	private boolean success;
	
	static final Pattern pattern = Pattern.compile("baibutao-(\\d+).apk");
	
	public AuthorUserTask(EewebApplication huiApplication) {
		super();
		this.huiApplication = huiApplication;
	}

	@Override
	public String call() throws Exception {
		try {
			RemoteManager remoteManager = RemoteManager.getSecurityRemoteManager();
			Response loginResponse = doLogin(remoteManager);
			if (!loginResponse.isSuccess()) {
				Log.d("author task", "unkown result code:" + loginResponse.getCode());
			}
			success = loginResponse.isSuccess();
			if (success) {
				deleteOldAPKfile();
				// keep login
//				JSONObject jsonObject = (JSONObject)loginResponse.getModel();
			} 
			return null;
		} catch (Exception e) {
			Log.e("author task", "author use failed" , e);
			success = false;
			return null;
		}
	}
	
	private void deleteOldAPKfile() {
		try {
			List<File> appFiles = getAppFiles(pattern);
			if (!CollectionUtil.isEmpty(appFiles)) {
				for (File file : appFiles) {
					file.delete();
				}
			}
		} catch (Exception e) {
			Log.d("AuthorUserTask", "delete old apk file error", e);
		}
	}

	private Request createLoginRequest(RemoteManager remoteManager, EewebApplication huiApplication) {
		String loginUrl = Config.getConfig().getProperty(Config.Names.USER_LOGIN_URL);
		Request request = remoteManager.createPostRequest(loginUrl);
//		String phone = childayApplication.getPhone();
//		String ssid = childayApplication.getSsid();
//		request.addParameter("phone", phone);
//		request.addParameter("ssid", ssid);
		String uid = getUid();
		request.addParameter("uid", uid);
		return request;
	}
	
	public String getUid() {
		String uid = huiApplication.getUid();
		if (StringUtil.isNotBlank(uid)) {
			return uid;
		}

		// 1.本地获取
		uid = FileCache.getUser();
		if (StringUtil.isNotBlank(uid)) {
			huiApplication.setUid(uid);
			return uid;
		}

		// 2.生成唯一标识， 写到本地  4fb542d96c3ba84
		uid = Secure.getString(huiApplication.getBaseContext().getContentResolver(), Secure.ANDROID_ID);
		if (StringUtil.isBlank(uid)) {
			uid = String.valueOf(System.currentTimeMillis());
		}
		FileCache.putUser(uid);
		huiApplication.setUid(uid);
		return uid;
	}
	
	private Response doLogin(RemoteManager remoteManager) {
		Request request = createLoginRequest(remoteManager, huiApplication);
		
		
		
		Response response = null;
		int tryTimes = 1;
		while (true) {
			response = remoteManager.execute(request);
			if (response.isSuccess()) {
				return response;
			}
			if (response.getCode() != MessageCodes.CONNECT_FAILED) {
				return response;
			}
			if (tryTimes >= 10) {
				// 如果尝试了10次都没成功，那就放弃吧
				Log.w("author task", "网络连接失败，尝试了"+tryTimes+"次都没成功，放弃了");
				return response;
			}
			Log.w("author task", "网络连接失败，3秒后重新进行第" + tryTimes + "次重新连接...");
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			++tryTimes;
		}
	}
	
	

	private static List<File> getAppFiles(final Pattern pattern) {
		List<File> result = new ArrayList<File>();
		try {
			File sdDir = Environment.getExternalStorageDirectory();
			
			/*File[] appFiles = sdDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					return pattern.matcher(filename).find();
				}
			});*/
		
			List<File> folders = new ArrayList<File>();
			for(File tempFile : sdDir.listFiles()) {
				if(tempFile.isDirectory() && tempFile.getName().indexOf("download") > -1 ) {
					folders.add(tempFile);
				} else if(pattern.matcher(tempFile.getName()).find()) {
					result.add(tempFile);
				}
			}
			
			for(File folder : folders) {
				for(File file : folder.listFiles()) {
					if(file.isFile() && pattern.matcher(file.getName()).find() ) {
						result.add(file);
					}
				}
			}
		} catch(Exception e) {
			Log.e(AuthorUserTask.class.getSimpleName(), "get apk files error", e);
		}
		return result;
	}
	
	
	public boolean isSuccess() {
		return success;
	}


}