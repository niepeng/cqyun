package com.baibutao.app.waibao.yun.android.common;

import java.text.NumberFormat;
import java.text.ParseException;

import android.content.Context;

import com.baibutao.app.waibao.yun.android.R;

public class PriceUtil {

private static final double FACTOR = 100.0;
	
	private static final int FRACTION_DIGITS = 2;
	
	/**
	 * 格式化价格，单位：分
	 * 12341 => 123.41
	 * @param price
	 * @return
	 */
	public static String formatPrice(int price) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(FRACTION_DIGITS);
//		numberFormat.setMinimumFractionDigits(FRACTION_DIGITS);
		numberFormat.setGroupingUsed(false);
		return numberFormat.format(price / FACTOR);
	}
	
	protected static String getStringById(Context context, int resourceId) {
		return context.getResources().getString(resourceId);
	}
	
	/**
	 * 格式化价格，单位：分
	 * 12341 => 123.41元
	 * @param price
	 * @return
	 */
	public static String getPriceString(int price, Context context) {
		String unitYuan = getStringById(context, R.string.app_unit_yuan);
		return formatPrice(price) + unitYuan;
	}	
	
	/**
	 * 解析价格 
	 * "123.41" => 12341
	 * "123.410" => 12341
	 * "123.411" => 12341
	 * "123.415" => 12341
	 * "123.418" => 12341
	 * "abc" => defaultValue
	 * @param price
	 * @return
	 */
	public static long parsePrice(String price, long defaultValue) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(FRACTION_DIGITS);
		numberFormat.setMinimumFractionDigits(FRACTION_DIGITS);
		numberFormat.setGroupingUsed(false);
		try {
			return (int)(numberFormat.parse(price).doubleValue() * FACTOR);
		} catch (ParseException e) {
			return defaultValue;
		}
	}
	
	/**
	 * 解析价格 
	 * "123.41" => 12341
	 * "123.410" => 12341
	 * "123.411" => 12341
	 * "123.415" => 12341
	 * "123.418" => 12341
	 * "abc" => 0
	 * @param price
	 * @return
	 */
	public static long parsePrice(String price) {
		return parsePrice(price, 0);
	}
}
