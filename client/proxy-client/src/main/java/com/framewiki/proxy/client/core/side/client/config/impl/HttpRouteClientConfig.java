package com.framewiki.proxy.client.core.side.client.config.impl;

import com.cdkjframework.util.log.LogUtils;
import com.framewiki.network.proxy.api.IHttpRouting;
import com.framewiki.network.proxy.api.socket.part.BaseSocketPart;
import com.framewiki.network.proxy.api.socket.part.HttpRouteSocketPart;
import com.framewiki.network.proxy.channel.impl.BaseSocketChannel;
import com.framewiki.network.proxy.model.HttpRoute;
import com.framewiki.network.proxy.model.InteractiveModel;
import com.framewiki.proxy.client.core.side.client.ClientControlThread;
import com.framewiki.proxy.client.core.side.client.adapter.ClientAdapter;
import com.framewiki.proxy.client.core.side.client.adapter.impl.InteractiveSimpleClientAdapter;
import com.framewiki.proxy.client.core.side.client.handler.impl.CommonReplyHandler;
import com.framewiki.proxy.client.core.side.client.handler.impl.ServerHeartHandler;
import com.framewiki.proxy.client.core.side.client.handler.impl.ServerWaitClientHandler;
import com.framewiki.proxy.client.core.side.client.heart.IClientHeartThread;
import com.framewiki.network.proxy.util.AssertUtils;
import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

import java.net.Socket;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.locks.StampedLock;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.client.core.side.client.config
 * @ClassName: HttpRouteClientConfig
 * @Description: http路由客户端配置
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */

public class HttpRouteClientConfig extends InteractiveClientConfig implements IHttpRouting {
  /**
   * 日志
   */
  private final LogUtils log = LogUtils.getLogger(HttpRouteClientConfig.class);

  /**
   * 基础配置
   */
  private final InteractiveClientConfig baseConfig;

  /**
   * 路由锁
   */
  private final StampedLock routeLock = new StampedLock();

  /**
   * 主路由
   */
  @Getter
  private HttpRoute masterRoute = null;
  /**
   * 不可对routeMap进行修改，只得以重新赋值方式重设
   */
  private Map<String, HttpRoute> routeMap = Collections.emptyMap();

  /**
   * 构造器
   */
  public HttpRouteClientConfig() {
    this.baseConfig = new InteractiveClientConfig();
  }

  /**
   * 构造器
   *
   * @param baseConfig 基础配置
   */
  public HttpRouteClientConfig(InteractiveClientConfig baseConfig) {
    this.baseConfig = baseConfig;
  }

  /**
   * 预设置
   *
   * @param masterRoute 主路由
   * @param routeMap    路由表
   */
  public void presetRoute(HttpRoute masterRoute, LinkedHashMap<String, HttpRoute> routeMap) {
    Objects.requireNonNull(masterRoute, "主路由不得为空");
    Objects.requireNonNull(routeMap, "路由表不得为null");

    LinkedHashMap<String, HttpRoute> routeMapTemp = new LinkedHashMap<>(routeMap);

    StampedLock routeLock = this.routeLock;
    long stamp = routeLock.writeLock();
    try {
      this.masterRoute = masterRoute;
      this.routeMap = routeMapTemp;
    } finally {
      routeLock.unlockWrite(stamp);
    }

  }

  /**
   * 获取路由表
   *
   * @return {@link Collections#unmodifiableMap(Map)} 不得对对象进行修改
   */
  public Map<String, HttpRoute> getRouteMap() {
    return Collections.unmodifiableMap(this.routeMap);
  }

  /**
   * 增加路由
   *
   * @param httpRoutes 路由
   */
  public void addRoute(HttpRoute... httpRoutes) {
    if (httpRoutes == null || httpRoutes.length < 1) {
      return;
    }

    StampedLock routeLock = this.routeLock;
    long stamp = routeLock.writeLock();
    try {
      if (Objects.isNull(this.masterRoute)) {
        this.masterRoute = httpRoutes[0];
      }

      LinkedHashMap<String, HttpRoute> routeMap = new LinkedHashMap<>(this.routeMap);
      for (HttpRoute model : httpRoutes) {
        routeMap.put(model.getHost(), model);
        if (model.isMaster()) {
          this.masterRoute = model;
        }
      }

      this.routeMap = routeMap;
    } finally {
      routeLock.unlockWrite(stamp);
    }
  }

  /**
   * 清理路由
   *
   * @param hosts 主路由
   */
  public void clearRoute(String... hosts) {
    if (Objects.isNull(hosts) || hosts.length < 1) {
      return;
    }

    StampedLock routeLock = this.routeLock;
    long stamp = routeLock.writeLock();
    try {
      LinkedHashMap<String, HttpRoute> routeMap = new LinkedHashMap<>(this.routeMap);
      HttpRoute masterRoute = this.masterRoute;

      String masterRouteHost = masterRoute.getHost();
      for (String host : hosts) {
        routeMap.remove(host);
        if (StringUtils.equals(masterRouteHost, host)) {
          masterRoute = null;

          // 减少string比较复杂度
          masterRouteHost = null;
        }
      }

      if (Objects.isNull(masterRoute)) {
        Iterator<HttpRoute> iterator = routeMap.values().iterator();
        if (iterator.hasNext()) {
          // 先将第一个设置为主路由，再遍历所有，如果是主标志则设置为主，以最后的主为准
          masterRoute = iterator.next();

          while (iterator.hasNext()) {
            HttpRoute model = iterator.next();
            if (model.isMaster()) {
              // 因使用的LinkedHashMap，就是让其符合初期定义，将后续加入有isMaster标志的路由设置为masterRoute
              masterRoute = model;
            }
          }

        } else {
          log.warn(String.format("{}:{} 路由是空的，若需要重新设置，请使用preset进行设置", this.getClientServiceIp(),
              this.getClientServicePort()));
        }
      }

      this.masterRoute = masterRoute;
      this.routeMap = routeMap;
    } finally {
      routeLock.unlockWrite(stamp);
    }

  }

  /**
   * 设置目标IP和端口
   *
   * @param destIp   目标IP
   * @param destPort 目标端口
   */
  @Override
  public void setDestIpPort(String destIp, Integer destPort) {
    // do nothing
  }

  /**
   * 获取客户端心跳线程
   *
   * @param clientControlThread 控制线程
   * @return 心跳线程
   */
  @Override
  public IClientHeartThread newClientHeartThread(ClientControlThread clientControlThread) {
    return this.baseConfig.newClientHeartThread(clientControlThread);
  }

  /**
   * 创建控制适配器
   *
   * @param clientControlThread 控制线程
   * @return 控制适配器
   */
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

  /**
   * 创建客户端通道
   *
   * @return 客户端通道
   */
  @Override
  public BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> newClientChannel() {
    return this.baseConfig.newClientChannel();
  }

  /**
   * 创建socket部分
   *
   * @param clientControlThread 控制线程
   * @return socket部分
   */
  @Override
  public BaseSocketPart newSocketPart(ClientControlThread clientControlThread) {
    HttpRouteSocketPart httpRouteSocketPart = new HttpRouteSocketPart(clientControlThread, this);

    httpRouteSocketPart.setStreamCacheSize(this.getStreamCacheSize());

    return httpRouteSocketPart;
  }

  /**
   * 获取有效的路由
   *
   * @param host 主机
   * @return 路由
   */
  @Override
  public HttpRoute pickEffectiveRoute(String host) {
    StampedLock routeLock = this.routeLock;
    long stamp = routeLock.tryOptimisticRead();
    Map<String, HttpRoute> routeMap = this.routeMap;
    HttpRoute masterRoute = this.masterRoute;
    if (!routeLock.validate(stamp)) {
      stamp = routeLock.readLock();
      try {
        routeMap = this.routeMap;
        masterRoute = this.masterRoute;
      } finally {
        routeLock.unlockRead(stamp);
      }
    }
    HttpRoute httpRoute = routeMap.get(host);
    if (Objects.isNull(httpRoute)) {
      httpRoute = masterRoute;
    }
    AssertUtils.state(Objects.nonNull(httpRoute), "未能获取有效的路由");
    return httpRoute;
  }

  /**
   * 创建目标Socket
   *
   * @return Socket
   * @throws Exception 创建失败
   */
  @Override
  public Socket newDestSocket() throws Exception {
    java.nio.channels.SocketChannel openSocketChannel = SelectorProvider.provider().openSocketChannel();
    return openSocketChannel.socket();
  }

  /**
   * 获取客户端
   *
   * @return 客户端
   */
  @Override
  public String getClientServiceIp() {
    return this.baseConfig.getClientServiceIp();
  }

  /**
   * 设置客户端
   *
   * @param clientServiceIp 客户端
   */
  @Override
  public void setClientServiceIp(String clientServiceIp) {
    this.baseConfig.setClientServiceIp(clientServiceIp);
  }

  /**
   * 获取客户端端口
   *
   * @return 客户端端口
   */
  @Override
  public Integer getClientServicePort() {
    return this.baseConfig.getClientServicePort();
  }

  /**
   * 设置客户端端口
   *
   * @param clientServicePort 客户端端口
   */
  @Override
  public void setClientServicePort(Integer clientServicePort) {
    this.baseConfig.setClientServicePort(clientServicePort);
  }

  /**
   * 获取监听端口
   *
   * @return 监听端口
   */
  @Override
  public Integer getListenServerPort() {
    return this.baseConfig.getListenServerPort();
  }

  /**
   * 设置监听端口
   *
   * @param listenServerPort 监听端口
   */
  @Override
  public void setListenServerPort(Integer listenServerPort) {
    this.baseConfig.setListenServerPort(listenServerPort);
  }

  /**
   * 获取目标IP
   *
   * @return 目标IP
   */
  @Override
  public String getDestIp() {
    return null;
  }

  /**
   * 设置目标IP
   *
   * @param destIp 目标IP
   */
  @Override
  public void setDestIp(String destIp) {
    // do nothing
  }

  /**
   * 获取目标端口
   *
   * @return 目标端口
   */
  @Override
  public Integer getDestPort() {
    return null;
  }

  /**
   * 设置目标端口
   *
   * @param destPort 目标端口
   */
  @Override
  public void setDestPort(Integer destPort) {
    // do nothing
  }

  /**
   * 获取字符集
   *
   * @return 字符集
   */
  @Override
  public Charset getCharset() {
    return this.baseConfig.getCharset();
  }

  /**
   * 设置字符集
   *
   * @param charset 字符集
   */
  @Override
  public void setCharset(Charset charset) {
    this.baseConfig.setCharset(charset);
  }

  /**
   * 获取缓存大小
   *
   * @return 缓存大小
   */
  @Override
  public int getStreamCacheSize() {
    return this.baseConfig.getStreamCacheSize();
  }

  /**
   * 设置缓存大小
   *
   * @param streamCacheSize 缓存大小
   */
  @Override
  public void setStreamCacheSize(int streamCacheSize) {
    this.baseConfig.setStreamCacheSize(streamCacheSize);
  }
}
