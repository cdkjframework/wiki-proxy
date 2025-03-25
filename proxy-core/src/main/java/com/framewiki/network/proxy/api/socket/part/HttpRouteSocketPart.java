package com.framewiki.network.proxy.api.socket.part;

import com.framewiki.network.proxy.api.IBelongControl;
import com.framewiki.network.proxy.api.IHttpRouting;
import com.framewiki.network.proxy.api.passway.SimplePassway;
import com.framewiki.network.proxy.model.HttpRoute;
import com.framewiki.network.proxy.util.AssertUtils;
import com.framewiki.network.proxy.util.ToolsUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.api.socketpart
 * @ClassName: HttpRouteSocketPart
 * @Description: http路由socket对
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Slf4j
public class HttpRouteSocketPart extends SimpleSocketPart {

	/**
	 * http头字符集
	 */
	private static final Charset HTTP_CHARSET = StandardCharsets.ISO_8859_1;

	/**
	 * http路由
	 */
	private static final byte COLON_BYTE = ':';

	/**
	 * host匹配器
	 */
	private static final byte[] HOST_MATCHER = new byte[]{'h', 'o', 's', 't', COLON_BYTE};

	/**
	 * 冒号索引
	 */
	private static final int COLON_INDEX = HOST_MATCHER.length - 1;

	/**
	 * http路由
	 */
	private final IHttpRouting httpRouting;

	/**
	 * 构建函数
	 */
	public HttpRouteSocketPart(IBelongControl belongThread, IHttpRouting httpRouting) {
		super(belongThread);
		this.httpRouting = httpRouting;
	}

	/**
	 * 选择路由并连接至目标
	 *
	 * @throws Exception 抛出异常
	 */
	protected void routeHost() throws Exception {
		String host = null;
		BufferedInputStream inputStream = new BufferedInputStream(this.sendSocket.getInputStream());
		// 缓存数据，不能我们处理了就不给实际应用
		ByteArrayOutputStream headerBufferStream = new ByteArrayOutputStream(1024);
		// 临时输出列，用于读取一整行后进行字符串判断
		ByteArrayOutputStream lineBufferStream = new ByteArrayOutputStream();
		for (int flag = 0, lineCount = 0, matchFlag = 0; ; lineCount++) {
			// 依次读取
			int read = inputStream.read();
			lineBufferStream.write(read);
			if (read < 0) {
				break;
			}
			// 记录换行状态
			if (read == '\r' || read == '\n') {
				flag++;
			} else {
				flag = 0;
				boolean hostMatcher = HOST_MATCHER[matchFlag] == (read | 0x20);
				boolean colonIndexByte = (matchFlag != COLON_INDEX || COLON_BYTE == read);
				// 这里matchFlag与lineCount不相等的频次比例较大，先比较
				boolean value = matchFlag == lineCount && matchFlag < HOST_MATCHER.length;
				if (value
						// 大写转小写，如果是冒号的位置，需要完全相等
						&& hostMatcher && colonIndexByte
				) {
					matchFlag++;
				}
			}
			// 如果大于等于4则就表示http头结束了
			if (flag >= 4) {
				break;
			}
			// 等于2表示一行结束了，需要进行处理
			if (flag == 2) {
				boolean isHostLine = (matchFlag == HOST_MATCHER.length);
				// for循环特性，设置-1，营造line为0
				lineCount = -1;
				matchFlag = 0;
				// 省去一次toByteArray拷贝的可能
				lineBufferStream.writeTo(headerBufferStream);
				if (isHostLine) {
					byte[] byteArray = lineBufferStream.toByteArray();
					// 重置行输出流
					lineBufferStream.reset();
					int left, right;
					byte rightByte;
					for (left = right = HOST_MATCHER.length; right < byteArray.length; right++) {
						if (byteArray[left] == ' ') {
							// 左边先去掉空白，去除期间right不用判断
							left++;
						} else if (
								(rightByte = byteArray[right]) == COLON_BYTE
										//
										|| rightByte == ' ' || rightByte == '\r' || rightByte == '\n') {
							// right位置到left位置必有字符，遇到空白或 : 则停下，与left中间的组合为host地址
							break;
						}
					}
					// 将缓存中的数据进行字符串化，根据http标准，字符集为 ISO-8859-1
					host = new String(byteArray, left, right - left, HTTP_CHARSET);
					break;
				} else {
					// 重置临时输出流
					lineBufferStream.reset();
				}
			}
		}
		// 将最后残留的输出
		lineBufferStream.writeTo(headerBufferStream);
		Socket recvSocket = this.recvSocket;
		HttpRoute willConnect = this.httpRouting.pickEffectiveRoute(host);
		InetSocketAddress destAddress = new InetSocketAddress(willConnect.getDestIp(), willConnect.getDestPort());
		recvSocket.connect(destAddress);
		OutputStream outputStream = recvSocket.getOutputStream();
		headerBufferStream.writeTo(outputStream);
		// 用bufferedStream每次read不用单字节从硬件缓存里读呀，快了些呢，咋地了，不就是再拷贝一次嘛！
		ToolsUtils.streamCopy(inputStream, outputStream);
		// flush的原因，不排除这里全部读完了，导致缓存中没有数据，那即使创建了passway也不会主动flush而是挂在那里，防止遇到lazy的自动刷新特性
		outputStream.flush();
	}

	/**
	 * 创建隧道
	 * @return 是否创建成功
	 */
	@Override
	public boolean createPassWay() {
		AssertUtils.state(!this.canceled, "不得重启已退出的socketPart");
		if (this.isAlive) {
			return true;
		}
		this.isAlive = true;
		try {
			this.routeHost();
			SimplePassway outToInPassway = this.outToInPassway = new SimplePassway();
			outToInPassway.setBelongControl(this);
			outToInPassway.setSendSocket(this.sendSocket);
			outToInPassway.setRecvSocket(this.recvSocket);
			outToInPassway.setStreamCacheSize(this.getStreamCacheSize());

			SimplePassway inToOutPassway = this.inToOutPassway = new SimplePassway();
			inToOutPassway.setBelongControl(this);
			inToOutPassway.setSendSocket(this.recvSocket);
			inToOutPassway.setRecvSocket(this.sendSocket);
			inToOutPassway.setStreamCacheSize(this.getStreamCacheSize());

			outToInPassway.start();
			inToOutPassway.start();
		} catch (Exception e) {
			log.error("socketPart [" + this.socketPartKey + "] 隧道建立异常", e);
			this.stop();
			return false;
		}
		return true;
	}
}
