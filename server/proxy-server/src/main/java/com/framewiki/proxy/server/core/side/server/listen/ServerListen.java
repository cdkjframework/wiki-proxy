package com.framewiki.proxy.server.core.side.server.listen;

import com.framewiki.proxy.server.core.side.server.listen.control.IControlSocket;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.listen
 * @ClassName: ServerListen
 * @Description: 监听转发服务进程
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface ServerListen {

	/**
	 * 格式化信息
	 *
	 * @return
	 */
	String formatInfo();

	/**
	 * 控制端口通知关闭
	 *
	 * @param controlSocket
	 */
	void controlCloseNotice(IControlSocket controlSocket);

}
