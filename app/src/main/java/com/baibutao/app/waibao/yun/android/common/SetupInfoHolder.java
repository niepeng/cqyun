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

import com.baibutao.app.waibao.yun.android.biz.dataobject.SetupDO;
import com.baibutao.app.waibao.yun.android.util.IoUtil;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

public class SetupInfoHolder {

	private static final String ATTRIBUTE_SPLIT = "&&&&&&&&";

	public static SetupDO getDO(Context context) {
		SetupDO setupDO = new SetupDO();
		try {
			InputStream is = context.openFileInput(FileNames.SETUP_INFO_FILE);
			StringWriter writer = new StringWriter();
			IoUtil.ioAndClose(new InputStreamReader(is), writer);
			String content = writer.toString();
			if (StringUtil.isEmpty(content)) {
				return null;
			}
			String[] attrs = content.split(ATTRIBUTE_SPLIT);
			if (attrs.length != 2) {
				return setupDO;
			}
			
			setupDO.setUpdateIntime(Integer.valueOf(attrs[0]));
			setupDO.setAlarmtime(Integer.valueOf(attrs[1]));
			return setupDO;
		} catch (FileNotFoundException e) {
			return setupDO;
		} catch (Exception e) {
			Log.e("SetupInfoHolder", "io error", e);
			return setupDO;
		}
	}
	
	public static void save(Context context, SetupDO setupDO) {
		if(setupDO == null) {
			return;
		}
		try {
			OutputStream os = context.openFileOutput(FileNames.SETUP_INFO_FILE, Context.MODE_PRIVATE);
			String info = setupDO.getUpdateIntime() + ATTRIBUTE_SPLIT + setupDO.getAlarmtime();
			IoUtil.ioAndClose(new StringReader(info),  new OutputStreamWriter(os));
		} catch (FileNotFoundException e) {
			Log.e("SetupInfoHolder", "io error", e);
		} catch (Exception e) {
			Log.e("SetupInfoHolder", "io error", e);
		}
	}

}
