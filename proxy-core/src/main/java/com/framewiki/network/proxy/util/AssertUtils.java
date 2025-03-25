package com.framewiki.network.proxy.util;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.util
 * @ClassName: AssertUtils
 * @Description: 断言
 * @Author: frank tiger
 * @Date: 2025/1/2 13:59
 * @Version: 1.0
 */
public final class AssertUtils {

	/**
	 * 状态判断
	 *
	 * @param expression 判断条件
	 * @param message    错误信息
	 */
	public static void state(boolean expression, String message) {
		if (!expression) {
			throw new IllegalStateException(message);
		}
	}

	/**
	 * 是否为 true
	 *
	 * @param expression 判断条件
	 * @param message    错误信息
	 */
	public static void isTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * 是否为空
	 *
	 * @param object  判断条件
	 * @param message 错误信息
	 */
	public static void isNull(Object object, String message) {
		if (object != null) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * 是否非空
	 *
	 * @param object  判断条件
	 * @param message 错误信息
	 */
	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}
}
