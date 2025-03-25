package com.framewiki.network.proxy.channel;

import java.io.Closeable;
import java.nio.charset.Charset;

/**
 * @param <R> 读取返回的类型
 * @param <W> 写入的类型
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.channel
 * @ClassName: StringChannelBase
 * @Description: 读写通道
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface Channel<R, W> extends Closeable {

	/**
	 * 简单的读取方式
	 *
	 * @return 读取的数据
	 * @throws Exception 读取异常
	 */
	R read() throws Exception;

	/**
	 * 简单的写入
	 *
	 * @param value 待写入的数据
	 * @throws Exception 写入异常
	 */
	void write(W value) throws Exception;

	/**
	 * 刷新
	 *
	 * @throws Exception 刷新异常
	 */
	void flush() throws Exception;

	/**
	 * 简单的写入并刷新
	 *
	 * @param value 待写入的数据
	 * @throws Exception 写入并刷新异常
	 */
	void writeAndFlush(W value) throws Exception;

	/**
	 * 设置交互编码
	 *
	 * @param charset 编码
	 */
	default void setCharset(Charset charset) {
		throw new UnsupportedOperationException("不支持的操作");
	}
}
