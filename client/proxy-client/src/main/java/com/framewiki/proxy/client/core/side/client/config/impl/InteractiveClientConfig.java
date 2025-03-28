package com.framewiki.proxy.client.core.side.client.config.impl;

import com.cdkjframework.exceptions.GlobalRuntimeException;
import com.cdkjframework.util.log.LogUtils;
import com.framewiki.proxy.core.api.socket.part.BaseSocketPart;
import com.framewiki.proxy.core.api.socket.part.SimpleSocketPart;
import com.framewiki.proxy.core.channel.impl.InteractiveChannelBase;
import com.framewiki.proxy.core.channel.impl.BaseSocketChannel;
import com.framewiki.proxy.core.model.InteractiveModel;
import com.framewiki.proxy.client.core.side.client.ClientControlThread;
import com.framewiki.proxy.client.core.side.client.adapter.ClientAdapter;
import com.framewiki.proxy.client.core.side.client.adapter.impl.InteractiveSimpleClientAdapter;
import com.framewiki.proxy.client.core.side.client.config.ClientConfig;
import com.framewiki.proxy.client.core.side.client.handler.impl.CommonReplyHandler;
import com.framewiki.proxy.client.core.side.client.handler.impl.ServerHeartHandler;
import com.framewiki.proxy.client.core.side.client.handler.impl.ServerWaitClientHandler;
import com.framewiki.proxy.client.core.side.client.heart.IClientHeartThread;
import com.framewiki.proxy.client.core.side.client.heart.impl.ClientHeartThread;
import lombok.Data;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.client.core.side.client.config
 * @ClassName: InteractiveClientConfig
 * @Description: 简单的以InteractiveModel为交互模型的配置
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */

@Data
public class InteractiveClientConfig implements ClientConfig<InteractiveModel, InteractiveModel> {
  /**
   * 日志
   */
  private final LogUtils log = LogUtils.getLogger(InteractiveClientConfig.class);

  /**
   * 客户端服务端IP
   */
  private String clientServiceIp;

  /**
   * 客户端服务端端口
   */
  private Integer clientServicePort;

  /**
   * 客户端监听端口
   */
  private Integer listenServerPort;

  /**
   * 目标IP
   */
  private String destIp;

  /**
   * 目标端口
   */
  private Integer destPort;

  /**
   * 字符集
   */
  private Charset charset = StandardCharsets.UTF_8;

  /**
   * 缓存大小
   */
  private int streamCacheSize = 8196;

  /**
   * 心跳检测间隔（s）
   */
  private long heartIntervalSeconds = 10L;
  /**
   * 尝试重连次数，若超过则中断链接
   */
  private int tryRecipientCount = 10;

  @Override
  public void setDestIpPort(String destIp, Integer destPort) {
    this.destIp = destIp;
    this.destPort = destPort;
  }

  @Override
  public IClientHeartThread newClientHeartThread(ClientControlThread clientControlThread) {
    ClientHeartThread clientHeartThread = new ClientHeartThread(clientControlThread);
    clientHeartThread.setHeartIntervalSeconds(this.heartIntervalSeconds);
    clientHeartThread.setTryRecipientCount(this.tryRecipientCount);
    return clientHeartThread;
  }

  @Override
  public ClientAdapter<InteractiveModel, InteractiveModel> newCreateControlAdapter(
      ClientControlThread clientControlThread) {
    InteractiveSimpleClientAdapter simpleClientAdapter = new InteractiveSimpleClientAdapter(clientControlThread,
        this);
    simpleClientAdapter.addMessageHandler(CommonReplyHandler.INSTANCE);
    simpleClientAdapter.addMessageHandler(ServerHeartHandler.INSTANCE);
    simpleClientAdapter.addMessageHandler(ServerWaitClientHandler.INSTANCE);
    return simpleClientAdapter;
  }

  @Override
  public BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> newClientChannel() {
    InteractiveChannelBase interactiveChannel = new InteractiveChannelBase();
    try {
      java.nio.channels.SocketChannel openSocketChannel = SelectorProvider.provider().openSocketChannel();
      openSocketChannel.connect(new InetSocketAddress(this.getClientServiceIp(), this.getClientServicePort()));
      Socket socket = openSocketChannel.socket();
      interactiveChannel.setSocket(socket);
    } catch (IOException e) {
      log.error("connect client service exception", e);
      throw new GlobalRuntimeException(e.getMessage());
    }

    interactiveChannel.setCharset(this.charset);

    return interactiveChannel;
  }

  /**
   * 新建SocketPart
   *
   * @param clientControlThread 控制线程
   * @return
   */
  @Override
  public BaseSocketPart newSocketPart(ClientControlThread clientControlThread) {
    SimpleSocketPart simpleSocketPart = new SimpleSocketPart(clientControlThread);
    simpleSocketPart.setStreamCacheSize(this.getStreamCacheSize());
    return simpleSocketPart;
  }

}
