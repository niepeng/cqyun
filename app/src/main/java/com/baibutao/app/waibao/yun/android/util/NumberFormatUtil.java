package com.baibutao.app.waibao.yun.android.util;

import java.text.NumberFormat;

public class NumberFormatUtil {

	/**
	 * 保留小数点后面几位，并以字符串输出
	 * 
	 * @param doubleStr
	 * @param number
	 * @return
	 */
	public static String format(String doubleStr, int number) {
		try {
			NumberFormat myformat = NumberFormat.getInstance();
			myformat.setMinimumFractionDigits(number);
			myformat.setMaximumFractionDigits(number);
			return myformat.format(Double.valueOf(doubleStr));
		} catch (Exception e) {
			e.printStackTrace();
			return doubleStr;
		}
	}
	
	public static String formatByInt(int intNumber, int number) {
		try {
			NumberFormat myformat = NumberFormat.getInstance();
			myformat.setMinimumFractionDigits(number);
			myformat.setMaximumFractionDigits(number);
			return myformat.format((double)intNumber/100.0);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static int str2int(String doubleStr) {
		try {
			double doubleNum = format(doubleStr);
			return (int) (doubleNum * 100);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static String discount(int oldPirce, int newPrice) {
		try {
			NumberFormat myformat = NumberFormat.getInstance();
			myformat.setMinimumFractionDigits(1);
			myformat.setMaximumFractionDigits(1);
			return myformat.format(newPrice * 10  /(double)oldPirce);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static double format(String doubleStr){
		return Double.valueOf(doubleStr);
	}

}
