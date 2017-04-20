package com.baibutao.app.waibao.yun.android.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.baibutao.app.waibao.yun.android.remote.http.HttpClientUtil;

/**
 * @author lsb
 *
 * @date 2012-5-29 下午11:29:14
 */
public class IoUtil {
	private static final int BUF_SIZE = 8 * 1024;
	
	public static String getContent(Reader reader) throws IOException {
		StringWriter writer = new StringWriter();
		ioAndClose(reader, writer);
		return writer.toString();
	}
	
	public static String getContent(InputStream is, String charset) throws IOException {
		return getContent(new InputStreamReader(is, charset));
	}
	
	public static void io(Reader reader, Writer writer)  throws IOException {
		
		char[] buf = new char[BUF_SIZE];
		while (true) {
			int len = reader.read(buf);
			if (len < 0) {
				break;
			}
			writer.write(buf, 0, len);
		}
	}
	
	public static void ioAndClose(Reader reader, Writer writer) throws IOException {
		io(reader, writer);
		reader.close();
		writer.close();
	}
	
	public static void io(InputStream is, OutputStream os) throws IOException {
		byte[] buf = new byte[BUF_SIZE];
		while (true) {
			int len = is.read(buf);
			if (len < 0) {
				break;
			}
			os.write(buf, 0, len);
		}
	}
	
	public static byte[] readAll(InputStream is) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		io(is, bos);
		byte[] ret = bos.toByteArray();
		return ret;
	}
	
	public static void ioAndClose(InputStream is, OutputStream os) throws IOException {
		io(is, os);
		is.close();
		os.close();
	}
	
/*	public static byte[] readFromUrl(URL url) throws IOException {
		if (url == null) {
			return null;
		}
		try {
			Log.d("url", url.toString());
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			InputStream is = conn.getInputStream();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ioAndClose(is, bos);
			return bos.toByteArray();
		} catch(Exception e) {
			Log.e("io error", "can not connect url:" + url, e);
			return null;
		}
	}*/
	
	public static byte[] readFromUrlByHttpClient(URL url) throws IOException {
		if (url == null) {
			return null;
		}
		try {
			Log.d("url", "url: " + url);
			HttpGet httpRequest = new HttpGet(url.toURI());
			return HttpClientUtil.getHttpData(httpRequest);
		} catch(IOException e) {
			Log.e("io error", "can not connect url:" + url, e);
			throw e;
		} catch (URISyntaxException e) {
			Log.e("io error", "can not connect url:" + url, e);
			throw new RuntimeException(e);
		}
	}
	
	public static void closeQuietly(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
	
	public static void delete(File file) {
		if (file.isFile()) {
			file.delete();
			Log.d("clean cache", "delete file: " +file.getAbsolutePath());
		} else if (file.isDirectory()){
			File[] files = file.listFiles();
			for (File childFile : files) {
				delete(childFile);
			}
			file.delete();
		}
	}
	
}
