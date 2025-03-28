package com.framewiki.proxy.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.util
 * @ClassName: ToolsUtils
 * @Description: 无归类的工具集
 * @Author: frank tiger
 * @Date: 2025/1/2 13:59
 * @Version: 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ToolsUtils {

	/**
	 * integer 转换为 byte[]
	 *
	 * @param source int
	 * @return byte[]
	 */
	public static byte[] intToBytes(int source) {
		return new byte[]{(byte) ((source >> 24) & 0xFF), (byte) ((source >> 16) & 0xFF), (byte) ((source >> 8) & 0xFF),
				(byte) (source & 0xFF)};
	}

	/**
	 * byte[] 转 integer
	 *
	 * @param byteArr 源数据
	 * @return int
	 */
	public static int bytesToInt(byte[] byteArr) {
		int count = 0;

		for (int i = 0; i < 4; ++i) {
			count <<= 8;
			count |= byteArr[i] & 255;
		}

		return count;
	}

	/**
	 * 拷贝数据流
	 *
	 * @param inputStream  源数据
	 * @param outputStream 目标数据
	 * @return int
	 * @throws IOException IO异常
	 */
	public static int streamCopy(InputStream inputStream, OutputStream outputStream) throws IOException {
		byte[] temp = new byte[8192];
		int sum = 0;
		int len;
		while ((len = inputStream.read(temp)) > 0) {
			sum += len;
			outputStream.write(temp, 0, len);

			if (inputStream.available() <= 0) {
				break;
			}
		}
		return sum;
	}

	/**
	 * 将数据写入通道
	 *
	 * @param channel 通道
	 * @param buffer  数据
	 * @return int
	 * @throws IOException IO异常
	 */
	public static int channelWrite(WritableByteChannel channel, ByteBuffer buffer) throws IOException {
		int sum = 0;
		while (buffer.hasRemaining()) {
			sum += channel.write(buffer);
		}
		return sum;
	}

}
