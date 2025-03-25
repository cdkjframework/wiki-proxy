package com.framewiki.proxy.server.core.side.server.listen.clear;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.network.proxy.side.server.listen
 * @ClassName: IClearInvalidSocketPartThread
 * @Description: 清理无效端口 线程
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */
public interface IClearInvalidSocketPartThread extends Runnable {

    /**
     * 启动
     */
    void start();

    /**
     * 退出
     */
    void cancel();

}
