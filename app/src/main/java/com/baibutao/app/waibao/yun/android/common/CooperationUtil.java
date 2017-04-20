/**
 * 
 */
package com.baibutao.app.waibao.yun.android.common;

import java.util.Map;

import com.baibutao.app.waibao.yun.android.util.CollectionUtil;
import com.baibutao.app.waibao.yun.android.util.StringUtil;

/**
 * @author niepeng
 *
 * @date 2012-10-18 下午3:42:50
 */
public class CooperationUtil {

	public static final Map<String, String> cooperaters = CollectionUtil.newTreeMap();
	static {
		cooperaters.put("douban", "豆瓣");
		cooperaters.put("juhuasuan", "聚划算");
		cooperaters.put("weibo", "新浪微博");
	}

	public static String format(String name) {
		if (StringUtil.isBlank(name)) {
			return "合作伙伴：其他";
		}
		String result = cooperaters.get(name);
		if (StringUtil.isBlank(result)) {
			return "合作伙伴：其他";
		}
		return "合作伙伴：" + result;
	}
}
