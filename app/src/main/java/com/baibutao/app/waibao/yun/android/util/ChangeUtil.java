package com.baibutao.app.waibao.yun.android.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class ChangeUtil {
	
	public static long str2long(String str) {
		try {
			if (str == null) {
				return 0L;
			}
			return Long.valueOf(str);
		} catch (Exception e) {
		}
		return 0L;
	}

	public static int str2int(String str) {
		try {
			if (str == null) {
				return 0;
			}
			return Integer.valueOf(str);
		} catch (Exception e) {
		}
		return 0;
	}

	public static double str2double(String str) {
		try {
			if (str == null) {
				return 0.d;
			}
			return Double.parseDouble(str);
		} catch (Exception e) {
		}
		return 0.d;
	}

	public static double chu100(int price) {
		BigDecimal b1 = new BigDecimal(Double.toString(price));
		BigDecimal b2 = new BigDecimal(Double.toString(100));
		return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 保留2为小数
	 * @param d
	 * @return
	 */
	public static String contain2bit(double d) {
		DecimalFormat df = new DecimalFormat("######0.00");
		return df.format(d);
	}

}