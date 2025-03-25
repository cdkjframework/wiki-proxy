package com.framewiki.network.proxy.common;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.common
 * @ClassName: Optional
 * @Description: 操作对象，主要是让值能够通过引用进行传递
 * @Author: frank tiger
 * @Date: 2025/1/2 13:59
 * @Version: 1.0
 */
public class Optional<T> {

	/**
	 * 值
	 */
	private T value;

	/**
	 * 构造函数
	 *
	 * @param value 值
	 */
	public Optional(T value) {
		this.value = value;
	}

	/**
	 * 创建一个Optional对象
	 *
	 * @param value 值
	 * @param <T>   值类型
	 * @return Optional对象
	 */
	public static <T> Optional<T> of(T value) {
		return new Optional<>(value);
	}

	/**
	 * 获取值
	 *
	 * @return 值
	 */
	public T getValue() {
		return value;
	}

	/**
	 * 设置值
	 *
	 * @param value 值
	 */
	public void setValue(T value) {
		this.value = value;
	}

}
