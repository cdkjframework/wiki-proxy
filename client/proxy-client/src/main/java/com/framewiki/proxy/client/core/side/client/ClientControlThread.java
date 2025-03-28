package com.framewiki.proxy.client.core.side.client;

import com.cdkjframework.util.log.LogUtils;
import com.framewiki.proxy.core.api.IBelongControl;
import com.framewiki.proxy.core.api.socket.part.BaseSocketPart;
import com.framewiki.proxy.client.core.side.client.adapter.ClientAdapter;
import com.framewiki.proxy.client.core.side.client.config.ClientConfig;
import com.framewiki.proxy.client.core.side.client.heart.IClientHeartThread;
import lombok.Getter;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.core.nio
 * @ClassName: FrameExecutor
 * @Description: 客户端控制服务
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */

public final class ClientControlThread implements Runnable, IBelongControl {
  /**
   * 日志
   */
  private final LogUtils log = LogUtils.getLogger(ClientControlThread.class);

  /**
   * 隧道集合
   */
  private final Map<String, BaseSocketPart> socketPartMap = new ConcurrentHashMap<>();

  /**
   * 配置
   */
  private final ClientConfig<?, ?> config;

  /**
   * 线程
   */
  private volatile Thread myThread = null;

  /**
   * 是否还活着
   */
  private volatile boolean isAlive = false;

  /**
   * 是否取消
   */
  @Getter
  private volatile boolean cancelled = false;

  /**
   * 心跳线程
   */
  private volatile IClientHeartThread clientHeartThread;

  /**
   * 适配器
   */
  private volatile ClientAdapter<?, ?> clientAdapter;

  /**
   * 构建函数
   */
  public ClientControlThread(ClientConfig<?, ?> config) {
    this.config = config;
  }

  /**
   * 触发控制服务
   *
   * @return
   * @throws Exception
   */
  public boolean createControl() throws Exception {
    this.stopClient();

    if (this.clientAdapter == null) {
      this.clientAdapter = this.config.newCreateControlAdapter(this);
    }

    boolean flag = this.clientAdapter.createControlChannel();

    if (!flag) {
      return false;
    }

    this.start();
    return true;
  }

  /**
   * 运行
   */
  @Override
  public void run() {
    while (this.isAlive) {
      try {
        // 使用适配器代理执行
        this.clientAdapter.waitMessage();
      } catch (Exception e) {
        log.warn(String.format("client control [{}] to server is exception,will stopClient",
            this.config.getListenServerPort()));
        this.stopClient();
      }
    }
  }

  /**
   * 停止隧道
   *
   * @param socketPartKey 隧道标识
   * @return
   */
  @Override
  public boolean stopSocketPart(String socketPartKey) {
    log.debug("stopSocketPart[{}]", socketPartKey);

    BaseSocketPart socketPart = this.socketPartMap.remove(socketPartKey);
    if (socketPart == null) {
      return false;
    }
    socketPart.cancel();
    return true;
  }

  /**
   * * 启动
   */
  private void start() {
    this.isAlive = true;
    this.cancelled = false;

    Thread myThread = this.myThread;
    if (Objects.isNull(myThread) || !myThread.isAlive()) {

      IClientHeartThread clientHeartThread = this.clientHeartThread;
      if (Objects.isNull(clientHeartThread) || !clientHeartThread.isAlive()) {
        clientHeartThread = this.clientHeartThread = this.config.newClientHeartThread(this);
        if (Objects.nonNull(clientHeartThread)) {
          clientHeartThread.start();
        }
      }

      myThread = this.myThread = new Thread(this);
      myThread.setName("client-" + this.formatInfo());
      myThread.start();
    }
  }

  /**
   * * 停止客户端监听
   */
  public void stopClient() {
    this.isAlive = false;

    Thread myThread = this.myThread;
    if (myThread != null) {
      this.myThread = null;
      myThread.interrupt();
    }

    ClientAdapter<?, ?> clientAdapter = this.clientAdapter;
    if (Objects.nonNull(clientAdapter)) {
      try {
        clientAdapter.close();
      } catch (Exception e) {
        // do nothing
      }
    }
  }

  /**
   * 全部退出
   */
  public void cancel() {
    if (this.cancelled) {
      return;
    }
    this.cancelled = true;

    this.stopClient();

    IClientHeartThread clientHeartThread;
    if ((clientHeartThread = this.clientHeartThread) != null) {
      this.clientHeartThread = null;
      try {
        clientHeartThread.cancel();
      } catch (Exception e) {
        // do no thing
      }
    }

    ClientAdapter<?, ?> clientAdapter;
    if ((clientAdapter = this.clientAdapter) != null) {
      this.clientAdapter = null;
      try {
        clientAdapter.close();
      } catch (Exception e) {
        // do no thing
      }
    }

    String[] array = this.socketPartMap.keySet().toArray(new String[0]);

    for (String key : array) {
      this.stopSocketPart(key);
    }

  }

  /**
   * 服务端监听的端口
   *
   * @return
   */
  public Integer getListenServerPort() {
    return this.config.getListenServerPort();
  }

  /**
   * 重设目标端口
   *
   * @param destIp
   * @param destPort
   */
  public void setDestIpPort(String destIp, Integer destPort) {
    this.config.setDestIpPort(destIp, destPort);
  }

  /**
   * 检测是否还活着
   *
   * @return
   */
  public boolean isAlive() {
    return this.isAlive;
  }

  /**
   * 发送心跳测试
   *
   * @throws Exception
   */
  public void sendHeartTest() throws Exception {
    // 无需判空，空指针异常也是异常
    this.clientAdapter.sendHeartTest();
  }

  /**
   * 获取服务端上次心跳测试成功时间
   *
   * @return
   */
  public LocalDateTime obtainServerHeartLastRecvTime() {
    return this.clientAdapter.obtainServerHeartLastRecvTime();
  }

  /**
   * 设置隧道伙伴
   *
   * @param socketPartKey 隧道标识
   * @param socketPart
   */
  public void putSocketPart(String socketPartKey, BaseSocketPart socketPart) {
    this.socketPartMap.put(socketPartKey, socketPart);
  }

  /**
   * 格式化信息
   *
   * @return
   */
  public String formatInfo() {
    return String.valueOf(this.getListenServerPort());
  }

}
