package com.framewiki.proxy.client.core.side.client.adapter.impl;

import com.cdkjframework.exceptions.GlobalRuntimeException;
import com.cdkjframework.util.log.LogUtils;
import com.framewiki.proxy.core.api.socket.part.BaseSocketPart;
import com.framewiki.proxy.core.channel.impl.BaseSocketChannel;
import com.framewiki.proxy.core.executor.FrameExecutor;
import com.framewiki.proxy.core.model.FrameResultModel;
import com.framewiki.proxy.core.model.InteractiveModel;
import com.framewiki.proxy.core.model.enums.FrameResultEnum;
import com.framewiki.proxy.core.model.enums.InteractiveTypeEnum;
import com.framewiki.proxy.core.model.interactive.ClientControlModel;
import com.framewiki.proxy.core.model.interactive.ServerWaitModel;
import com.framewiki.proxy.client.core.side.client.ClientControlThread;
import com.framewiki.proxy.client.core.side.client.adapter.ClientAdapter;
import com.framewiki.proxy.client.core.side.client.config.ClientConfig;
import com.framewiki.proxy.client.core.side.client.handler.IClientHandler;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.client.core.side.client.adapter
 * @ClassName: InteractiveSimpleClientAdapter
 * @Description: 基于InteractiveModel的客户端适配器
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */

public class InteractiveSimpleClientAdapter implements ClientAdapter<InteractiveModel, InteractiveModel> {

  /**
   * 日志
   */
  private final LogUtils log = LogUtils.getLogger(InteractiveSimpleClientAdapter.class);

  /**
   * 所属的客户端线程
   */
  private final ClientControlThread clientControlThread;
  /**
   * 客户端设置
   */
  private final ClientConfig<InteractiveModel, InteractiveModel> config;
  /**
   * 客户端消息接收处理链
   */
  protected List<IClientHandler<? super InteractiveModel, ? extends InteractiveModel>> messageHandlerList = new LinkedList<>();
  /**
   * 适配器通道
   */
  private BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> baseSocketChannel;
  private LocalDateTime serverHeartLastRecvTime = LocalDateTime.now();

  public InteractiveSimpleClientAdapter(ClientControlThread clientControlThread,
                                        ClientConfig<InteractiveModel, InteractiveModel> clientConfig) {
    this.clientControlThread = clientControlThread;
    this.config = clientConfig;
  }

  /**
   * 创建客户端通道
   *
   * @return
   */
  protected BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> newClientChannel() {
    return this.config.newClientChannel();
  }

  /**
   * 向穿透目标socket
   *
   * @return
   * @throws Exception
   */
  protected Socket newDestSocket() throws Exception {
    return this.config.newDestSocket();
  }

  /**
   * 向服务端和暴露目标创建socketPart
   *
   * @return
   */
  protected BaseSocketPart newSocketPart() {
    return this.config.newSocketPart(this.clientControlThread);
  }

  @Override
  public boolean createControlChannel() throws Exception {
    BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> baseSocketChannel = this.newClientChannel();
    if (baseSocketChannel == null) {
      log.error("向服务端[{}:{}]建立控制通道失败", this.config.getClientServiceIp(),
          this.config.getClientServicePort());
      return false;
    }

    InteractiveModel interactiveModel = InteractiveModel.of(InteractiveTypeEnum.CLIENT_CONTROL,
        new ClientControlModel(this.config.getListenServerPort()));

    baseSocketChannel.writeAndFlush(interactiveModel);

    InteractiveModel recv = baseSocketChannel.read();
    log.info("建立控制端口回复：{}", recv);

    FrameResultModel javaObject = recv.getData().toJavaObject(FrameResultModel.class);

    if (FrameResultEnum.SUCCESS.getCode().equals(javaObject.getCode())) {
      // 使用相同的
      this.baseSocketChannel = baseSocketChannel;

      this.resetServerHeartLastRecvTime();

      return true;
    }
    return false;
  }

  /**
   * 建立连接
   *
   * @param serverWaitModel 等待连接的socketPart
   */
  @Override
  public boolean clientConnect(ServerWaitModel serverWaitModel) {
    // 首先向暴露目标建立socket
    Socket destSocket;
    try {
      destSocket = this.newDestSocket();
    } catch (Exception e) {
      log.error("向目标建立连接失败 {}:{}", this.config.getDestIp(), this.config.getDestPort());
      return false;
    }

    BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> passwayClientChannel = null;
    try {
      // 向服务端请求建立隧道
      passwayClientChannel = this.newClientChannel();

      InteractiveModel model = InteractiveModel.of(InteractiveTypeEnum.CLIENT_CONNECT,
          new ServerWaitModel(serverWaitModel.getSocketPartKey()));

      passwayClientChannel.writeAndFlush(model);

      InteractiveModel recv = passwayClientChannel.read();
      log.info("建立隧道回复：{}", recv);

      FrameResultModel javaObject = recv.getData().toJavaObject(FrameResultModel.class);

      if (!FrameResultEnum.SUCCESS.getCode().equals(javaObject.getCode())) {
        throw new GlobalRuntimeException("绑定失败");
      }

    } catch (Exception e) {
      log.error(
          "打通隧道发生异常 " + this.config.getClientServiceIp() + ":" + this.config.getClientServicePort() +
              "<->" + this.config.getDestIp() + ":" + this.config.getDestPort(), e);
      try {
        destSocket.close();
      } catch (IOException e1) {
        // do nothing
      }

      if (passwayClientChannel != null) {
        try {
          passwayClientChannel.closeSocket();
        } catch (IOException e1) {
          // do nothing
        }
      }
      return false;
    }

    // 将两个socket建立伙伴关系
    BaseSocketPart socketPart = this.newSocketPart();
    socketPart.setSocketPartKey(serverWaitModel.getSocketPartKey());
    socketPart.setSendSocket(passwayClientChannel.getSocket());
    socketPart.setRecvSocket(destSocket);
    // 尝试打通隧道
    boolean createPassWay = socketPart.createPassWay();
    if (!createPassWay) {
      socketPart.cancel();
      return false;
    }

    // 将socket伙伴放入客户端线程进行统一管理
    this.clientControlThread.putSocketPart(serverWaitModel.getSocketPartKey(), socketPart);
    return true;
  }

  @Override
  public void waitMessage() throws Exception {
    InteractiveModel read = this.baseSocketChannel.read();

    // 只要有有效信息推送过来，则重置心跳时间
    this.resetServerHeartLastRecvTime();
    FrameExecutor.executeClientMessageProc(() -> this.procMethod(read));
  }

  /**
   * 消息处理方法
   *
   * @param interactiveModel 接收到的消息
   */
  protected void procMethod(InteractiveModel interactiveModel) {
    log.info("接收到新的指令: {}", interactiveModel);
    try {
      boolean proceedFlag = false;

      for (IClientHandler<? super InteractiveModel, ? extends InteractiveModel> handler : this.messageHandlerList) {
        proceedFlag = handler.proc(interactiveModel, this);
        if (proceedFlag) {
          break;
        }
      }

      if (!proceedFlag) {
        log.info("无处理方法的信息：[{}]", interactiveModel.toString());
        InteractiveModel result = InteractiveModel.of(interactiveModel.getInteractiveSeq(),
            InteractiveTypeEnum.COMMON_REPLY, FrameResultEnum.UNKNOWN_INTERACTIVE_TYPE.toResultModel());
        this.getSocketChannel().writeAndFlush(result);
      }

    } catch (Exception e) {
      // 只记录，其他的交给心跳之类的去把控
      log.error("读取或写入异常", e);
    }
  }

  @Override
  public BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> getSocketChannel() {
    return this.baseSocketChannel;
  }

  /**
   * 添加消息处理器
   *
   * @param handler
   * @return
   */
  public InteractiveSimpleClientAdapter addMessageHandler(
      IClientHandler<? super InteractiveModel, ? extends InteractiveModel> handler) {
    this.messageHandlerList.add(handler);
    return this;
  }

  @Override
  public void close() throws Exception {
    BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> baseSocketChannel = this.baseSocketChannel;
    if (Objects.nonNull(baseSocketChannel)) {
      this.baseSocketChannel = null;
      baseSocketChannel.closeSocket();
    }
  }

  @Override
  public void sendHeartTest() throws Exception {
    InteractiveModel interactiveModel = InteractiveModel.of(InteractiveTypeEnum.HEART_TEST, null);
    this.baseSocketChannel.writeAndFlush(interactiveModel);
  }

  @Override
  public LocalDateTime obtainServerHeartLastRecvTime() {
    return this.serverHeartLastRecvTime;
  }

  @Override
  public LocalDateTime resetServerHeartLastRecvTime(LocalDateTime time) {
    LocalDateTime tempTime = this.serverHeartLastRecvTime;
    this.serverHeartLastRecvTime = time;
    return tempTime;
  }

}
