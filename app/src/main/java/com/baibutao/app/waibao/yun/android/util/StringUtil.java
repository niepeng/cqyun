package com.baibutao.app.waibao.yun.android.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lsb
 *
 * @date 2012-5-29 下午10:55:51
 */
public class StringUtil {
	public static final String EMPTY_STRING = "";

	public static final String POINTS = "...";
	
	public static String leftNumberByEmpty(String input, int size) {
		if (StringUtil.isEmpty(input)) {
			return EMPTY_STRING;
		}
		String key = null;
		int sourceLength = input.length();
		if (sourceLength >= size) {
			key = input.substring(0, size);
		} else {
			key = input;
			for (int i = sourceLength; i < size; i++) {
				key += " ";
			}
		}
		return key;
	}

	public static String left(String input, int size) {
		if (StringUtil.isEmpty(input)) {
			return EMPTY_STRING;
		}
		if (size >= input.length()) {
			return input;
		}
		return input.substring(0, size);
	}

	/**
	 * 类似于String.split功能，但是会把字符的空白去掉，如果分割后的内容全部为空白， 将会忽略该部分内容
	 * 
	 * @param input
	 * @param regex
	 * @return
	 */
	public static List<String> splitTrim(String input, String regex) {
		List<String> ret = new ArrayList<String>();
		String[] parts = input.split(regex);
		for (String part : parts) {
			String trimmedPart = StringUtil.trimToEmpty(part);
			if (StringUtil.isEmpty(trimmedPart)) {
				continue;
			}
			ret.add(trimmedPart);
		}
		return ret;
	}

	public static String toString(Object obj) {
		if (obj == null) {
			return null;
		}
		return obj.toString();
	}

	public static boolean equals(String str1, String str2) {
		if (str1 == null && str2 == null) {
			return true;
		}
		if (str1 == null || str2 == null) {
			return false;
		}
		return str1.equals(str2);
	}

	public static int getLength(String input) {
		if (input == null) {
			return 0;
		}
		return input.length();
	}

	/**
	 * 出去最后一个及其之后的字符， 如果不存在此字符串，则返回原字符串
	 * 
	 * @param input
	 * @param token
	 * @return
	 */
	public static String trimLastSelfAndAfter(String input, String token) {
		if (isEmpty(input)) {
			return input;
		}
		int pos = input.lastIndexOf(token);
		if (pos < 0) {
			return input;
		}
		return input.substring(0, pos);
	}

	/**
	 * 获取某个最后字符串之后的字符串 如果不存在此字符串，则返回原字符串
	 * 
	 * @param input
	 * @param token
	 * @return
	 */
	public static String getLastAfter(String input, String token) {
		if (isEmpty(input)) {
			return input;
		}
		int pos = input.lastIndexOf(token);
		if (pos < 0) {
			return input;
		}
		return input.substring(pos + token.length());
	}

	/**
	 * 获取某个符号之前的内容
	 * 
	 * @param input
	 * @param token
	 * @return
	 */
	public static String getFirstBefore(String input, String token) {
		if (isEmpty(input)) {
			return input;
		}
		int pos = input.indexOf(token);
		if (pos < 0) {
			return input;
		}
		return input.substring(0, pos);
	}

	/**
	 * 获取某个最后字符串之前的字符串 如果不存在此字符串，则返回原字符串
	 * 
	 * @param input
	 * @param input
	 * @param token
	 * @return
	 */
	public static String getLastBefore(String input, String token) {
		if (isEmpty(input)) {
			return input;
		}
		int pos = input.lastIndexOf(token);
		if (pos < 0) {
			return input;
		}
		return input.substring(0, pos);
	}

	public static boolean isEmpty(String input) {
		if (input == null || input.length() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isNotEmpty(String input) {
		return !isEmpty(input);
	}

	public static boolean isBlank(String input) {
		if (input == null) {
			return true;
		}
		return input.trim().length() == 0;
	}

	public static boolean isNotBlank(String input) {
		return !isBlank(input);
	}

	public static String trimToEmpty(String input) {
		if (input == null) {
			return EMPTY_STRING;
		}
		return input.trim();
	}

	public static boolean endsWith(String input, String postfix) {
		boolean inputEmpty = StringUtil.isEmpty(input);
		if (inputEmpty && StringUtil.isEmpty(postfix)) {
			return true;
		} else if (inputEmpty) {
			return false;
		} else {
			return input.endsWith(postfix);
		}
	}

	public static boolean startsWith(String input, String prefix) {
		boolean inputEmpty = StringUtil.isEmpty(input);
		if (inputEmpty && StringUtil.isEmpty(prefix)) {
			return true;
		} else if (inputEmpty) {
			return false;
		} else {
			return input.startsWith(prefix);
		}
	}

	/**
	 * null => "" "	" => "" "abc" => "abc" "aBc" => "a_bc" "helloWord" =>
	 * "hello_word" "hi hao are   you" => "hi hao are   you" "helloWord_iAmHsl"
	 * => "hello_word_i_am_hsl"
	 * 
	 * @return
	 */
	public static String camelToUnderLineString(String str) {
		return camelToFixedString(str, "_");
	}

	/**
	 * null => "" "	" => "" "abc" => "abc" "a_bc" => "aBc" "the_first_name_" =>
	 * "theFirstName" "hello_word" => "helloWord" "hi hao are   you" =>
	 * "hi hao are   you" "hello_word_i_am_hsl" => "helloWordIAmHsl"
	 * 
	 * @return
	 */
	public static String underLineStringToCamel(String str) {
		return fixedCharToCamel(str, '_');
	}

	/**
	 * 把字符串的第一个字母转成大写
	 * 
	 * @param str
	 * @return
	 */
	public static String uppercaseFirstLetter(String str) {
		if (StringUtil.isEmpty(str)) {
			return str;
		}
		char firstLetter = str.charAt(0);
		firstLetter = Character.toUpperCase(firstLetter);
		return firstLetter + str.substring(1);
	}

	/**
	 * 把字符串的第一个字母转成小写
	 * 
	 * @param str
	 * @return
	 */
	public static String lowercaseFirstLetter(String str) {
		if (StringUtil.isEmpty(str)) {
			return str;
		}
		char firstLetter = str.charAt(0);
		firstLetter = Character.toLowerCase(firstLetter);
		return firstLetter + str.substring(1);
	}

	/**
	 * null => "" "	" => "" "abc" => "Abc" "a_bc" => "ABc" "the_first_name_" =>
	 * "TheFirstName" "hello_word" => "HelloWord" "hi hao are   you" =>
	 * "Hi hao are   you" "hello_word_i_am_hsl" => "HelloWordIAmHsl"
	 * 
	 * @return
	 */
	public static String underLineStringToCamelUppercaseFirst(String str) {
		String ret = underLineStringToCamel(str);
		ret = uppercaseFirstLetter(ret);
		return ret;
	}

	private static String fixedCharToCamel(String str, Set<Character> fixed) {
		str = trimToEmpty(str);
		if (isEmpty(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder();
		final int len = str.length();
		for (int i = 0; i < len; ++i) {
			char c = str.charAt(i);
			if (fixed.contains(c)) {
				++i;
				if (i != len) {
					c = Character.toUpperCase(str.charAt(i));
					sb.append(c);
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String fixedCharToCamel(String str, char[] fixed) {
		return fixedCharToCamel(str, asCharSet(fixed));
	}

	public static String fixedCharToCamel(String str, char fixed) {
		str = trimToEmpty(str);
		if (isEmpty(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder();
		final int len = str.length();
		for (int i = 0; i < len; ++i) {
			char c = str.charAt(i);
			if (fixed == c) {
				// ignore this char
				// upper next char
				++i;
				if (i != len) {
					c = Character.toUpperCase(str.charAt(i));
					sb.append(c);
				}
			} else {
				sb.append(c);
			}

		}
		return sb.toString();
	}

	public static String camelToFixedString(String str, String fixed) {
		str = trimToEmpty(str);
		if (isEmpty(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); ++i) {
			char c = str.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i != 0) {
					sb.append(fixed);
				}
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String toUpperCase(String input) {
		if (StringUtil.isEmpty(input)) {
			return input;
		}
		return input.toUpperCase();
	}

	public static String toLowerCase(String input) {
		if (StringUtil.isEmpty(input)) {
			return input;
		}
		return input.toLowerCase();
	}

	public static String replaceAll(String target, char oldChar, String newValue) {
		if (StringUtil.isEmpty(target)) {
			return target;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < target.length(); ++i) {
			char c = target.charAt(i);
			if (c == oldChar) {
				sb.append(newValue);
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	static Set<Character> asCharSet(char[] input) {
		Set<Character> ret = new HashSet<Character>();
		if (input == null) {
			return ret;
		}
		for (char c : input) {
			ret.add(c);
		}
		return ret;
	}

	public static String substring(String arg, int number) {
		if (isEmpty(arg)) {
			return arg;
		}

		int length = arg.length();
		if (length < number) {
			return arg;
		}

		return arg.substring(0, number) + POINTS;
	}
}
