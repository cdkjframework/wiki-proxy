package com.framewiki.proxy.core.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.common
 * @ClassName: CommonConstants
 * @Description: 公共参数
 * @Author: frank tiger
 * @Date: 2025/1/2 13:59
 * @Version: 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstants {

	/**
	 * 服务端地址，支持IP或域名，这个根据服务端放的网络位置进行设置
	 */
	public static String serviceIp = "127.0.0.1";
	/**
	 * 客户端服务的端口
	 */
	public static int servicePort = 10010;
	/**
	 * 交互密钥 AES
	 */
	public static String aesKey = "8AUWlb+IWD+Fhbs0xnXCCg==";
	/**
	 * 交互签名key
	 */
	public static String tokenKey = "tokenKey";

	/**
	 * 映射对
	 */
	public static List<ListenDest> listenDestArray = new ArrayList<>();

	/**
	 * 监听、映射对
	 */
	public static class ListenDest {
		/**
		 * 服务端监听的端口，外网访问服务端IP:listingPort即可进行穿透
		 */
		public int listenPort;
		/**
		 * 穿透的目标，即要暴露在外网的内网IP
		 */
		public String destIp;
		/**
		 * 要暴露的内网端口
		 */
		public int destPort;

		/**
		 * @param listenPort 服务端监听的端口，外网访问服务端IP:listingPort即可进行穿透
		 * @param destIp     穿透的目标，即要暴露在外网的内网IP
		 * @param destPort   要暴露的内网端口
		 * @return 监听、映射对
		 */
		public static ListenDest of(int listenPort, String destIp, int destPort) {
			ListenDest model = new ListenDest();
			model.listenPort = listenPort;
			model.destIp = destIp;
			model.destPort = destPort;
			return model;
		}
	}

}
