package com.baibutao.app.waibao.yun.android.common;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;

import android.content.Context;
import android.util.Log;

import com.baibutao.app.waibao.yun.android.biz.dataobject.UserDO;
import com.baibutao.app.waibao.yun.android.util.IoUtil;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

public class UserInfoHolder {

	private static final String ATTRIBUTE_SPLIT = "&&&&&&&&";

	public static UserDO getUserDO(Context context) {
		try {
			InputStream is = context.openFileInput(FileNames.USER_INFO_FILE);
			StringWriter writer = new StringWriter();
			IoUtil.ioAndClose(new InputStreamReader(is), writer);
			String content = writer.toString();
			if (StringUtil.isEmpty(content)) {
				return null;
			}
			String[] attrs = content.split(ATTRIBUTE_SPLIT);
			if (attrs.length != 3) {
				return null;
			}
			UserDO userDO = new UserDO();
			userDO.setUsername(attrs[0]);
			userDO.setPsw(attrs[1]);
			userDO.setId(Long.valueOf(attrs[2]));
			return userDO;
		} catch (FileNotFoundException e) {
			return null;
		} catch (Exception e) {
			Log.e("UserInfoHolder", "io error", e);
			return null;
		}
	}
	
	public static void saveUser(Context context, UserDO userDO) {
		if(userDO == null) {
			return;
		}
		try {
			OutputStream os = context.openFileOutput(FileNames.USER_INFO_FILE, Context.MODE_PRIVATE);
			String userInfo = userDO.getUsername() + ATTRIBUTE_SPLIT + userDO.getPsw() +  ATTRIBUTE_SPLIT + userDO.getId();
			IoUtil.ioAndClose(new StringReader(userInfo),  new OutputStreamWriter(os));
		} catch (FileNotFoundException e) {
			Log.e("UserInfoHolder", "io error", e);
		} catch (Exception e) {
			Log.e("UserInfoHolder", "io error", e);
		}
	}
}