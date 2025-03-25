package com.framewiki.proxy.server.core.side.server.listen;

import com.framewiki.proxy.server.core.side.server.listen.config.ListenServerConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.listen
 * @ClassName: ListenServerControl
 * @Description: 转发监听服务控制类
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ListenServerControl {

	/**
	 * 监听服务进程map
	 */
	private static final ConcurrentHashMap<Integer, ServerListenThread> SERVER_LISTEN_MAP = new ConcurrentHashMap<>();

	/**
	 * 加入新的监听服务进程
	 *
	 * @param serverListen 服务监听
	 * @return
	 */
	public static boolean add(ServerListenThread serverListen) {
		if (serverListen == null) {
			return false;
		}

		Integer listenPort = serverListen.getListenPort();
		ServerListenThread serverListenThread = SERVER_LISTEN_MAP.get(listenPort);
		if (serverListenThread != null) {
			// 必须要先remove掉才能add，讲道理如果原先的存在应该直接报错才对，也就是参数为null，所以这里不自动remove
			return false;
		}

		SERVER_LISTEN_MAP.put(listenPort, serverListen);
		return true;
	}

	/**
	 * 去除指定端口的监听服务端口
	 *
	 * @param listenPort 监听端口
	 * @return
	 */
	public static boolean remove(Integer listenPort) {
		ServerListenThread serverListenThread = SERVER_LISTEN_MAP.remove(listenPort);
		if (Objects.nonNull(serverListenThread)) {
			serverListenThread.cancel();
		}
		return true;
	}

	/**
	 * 根据端口获取监听服务端口
	 *
	 * @param listenPort 监听端口
	 * @return
	 */
	public static ServerListenThread get(Integer listenPort) {
		return SERVER_LISTEN_MAP.get(listenPort);
	}

	/**
	 * 获取全部监听服务
	 *
	 * @return 返回全部服务
	 */
	public static List<ServerListenThread> getAll() {
		List<ServerListenThread> list = new LinkedList<>();
		SERVER_LISTEN_MAP.forEach((key, value) -> list.add(value));
		return list;
	}

	/**
	 * 关闭所有监听服务
	 */
	public static void closeAll() {
		Integer[] array = SERVER_LISTEN_MAP.keySet().toArray(new Integer[0]);
		for (Integer key : array) {
			ListenServerControl.remove(key);
		}
	}

	/**
	 * 创建新的监听服务
	 *
	 * @param config 配置
	 * @return
	 */
	public static ServerListenThread createNewListenServer(ListenServerConfig config) {
		ServerListenThread serverListenThread;
		try {
			serverListenThread = new ServerListenThread(config);
		} catch (Exception e) {
			log.warn("创建监听服务 [" + config.getListenPort() + "] 失败", e);
			return null;
		}
		// 若没有报错则说明没有监听该端口的线程，即不可正常使用原有端口，所以先进行强行remove，再进行add
		ListenServerControl.remove(config.getListenPort());
		ListenServerControl.add(serverListenThread);
		return serverListenThread;
	}
}
