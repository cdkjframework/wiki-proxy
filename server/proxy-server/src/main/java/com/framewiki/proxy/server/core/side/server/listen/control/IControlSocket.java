package com.framewiki.proxy.server.core.side.server.listen.control;

import com.framewiki.proxy.server.core.side.server.listen.ServerListen;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.side.server.listen.control
 * @ClassName: IControlSocket
 * @Description: 控制端口接口
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface IControlSocket {

	/**
	 * 是否有效
	 *
	 * @return
	 */
	boolean isValid();

	/**
	 * 发送隧道等待状态
	 *
	 * @param socketPartKey 隧道标识
	 * @return
	 */
	boolean sendClientWait(String socketPartKey);

	/**
	 * 关闭
	 */
	void close();

	/**
	 * 替换关闭
	 * 适配多客户端模式，若是替换关闭则可能不进行关闭 <br>
	 * 若是传统 1 对 1 模式，则等价调用 {@link #close()}
	 */
	default void replaceClose() {
		this.close();
	}

	/**
	 * 开启接收线程 现的类需要自己进行幂等性处理
	 */
	void startRecv();

	/**
	 * 设置控制的监听线程
	 *
	 * @param serverListen 监听线程
	 */
	void setServerListen(ServerListen serverListen);

}
