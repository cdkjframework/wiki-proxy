package com.framewiki.proxy.client.core;

import com.framewiki.proxy.client.core.config.ProxyConfig;
import com.framewiki.network.proxy.common.CommonConstants;
import com.framewiki.network.proxy.model.HttpRoute;
import com.framewiki.proxy.client.core.side.client.ClientControlThread;
import com.framewiki.proxy.client.core.side.client.config.impl.AllSecretInteractiveClientConfig;
import com.framewiki.proxy.client.core.side.client.config.impl.HttpRouteClientConfig;
import com.framewiki.proxy.client.core.side.client.config.impl.InteractiveClientConfig;
import com.framewiki.proxy.client.core.side.client.config.impl.SecretInteractiveClientConfig;

import java.util.List;

import static com.framewiki.network.proxy.common.CommonConstants.ListenDest;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.client.core
 * @ClassName: NetworkAgentClient
 * @Description: 网络代理客户端
 * @Author: xiaLin
 * @Date: 2025/1/3 17:10
 * @Version: 1.0
 */
public class WikiProxyClient {

  /**
   * 配置
   */
  private final ProxyConfig proxyConfig;

  /**
   * 构造方法
   */
  public WikiProxyClient(ProxyConfig proxyConfig) {
    this.proxyConfig = proxyConfig;
  }

  /**
   * 线程启动
   */
  public void start() throws Exception {
    // 构建监听目标
    buildListenDest();
    // 交互加密、数据不加密的策略
    secret();
  }

  /**
   * http路由
   * <p>
   * 默认使用交互加密、数据不加密的策略
   */
  public static void secretHttpRoute() throws Exception {
    HttpRoute[] routes = new HttpRoute[]{
        //
        HttpRoute.of("localhost", "127.0.0.1", 8080),
        //
        HttpRoute.of(true, "127.0.0.1", "127.0.0.1", 8080),
        //
    };

    for (ListenDest model : CommonConstants.listenDestArray) {
      SecretInteractiveClientConfig baseConfig = new SecretInteractiveClientConfig();

      // 设置服务端IP和端口
      baseConfig.setClientServiceIp(CommonConstants.serviceIp);
      baseConfig.setClientServicePort(CommonConstants.servicePort);
      // 设置对外暴露的端口，该端口的启动在服务端，这里只是表明要跟服务端的那个监听服务对接
      baseConfig.setListenServerPort(model.listenPort);

      // 设置交互密钥和签名key
      baseConfig.setBaseAesKey(CommonConstants.aesKey);
      baseConfig.setTokenKey(CommonConstants.tokenKey);

      HttpRouteClientConfig config = new HttpRouteClientConfig(baseConfig);
      config.addRoute(routes);

      new ClientControlThread(config).createControl();
    }
  }

  /**
   * 交互、隧道都进行加密
   */
  public static void secretAll() throws Exception {
    for (ListenDest model : CommonConstants.listenDestArray) {
      AllSecretInteractiveClientConfig config = new AllSecretInteractiveClientConfig();

      // 设置服务端IP和端口
      config.setClientServiceIp(CommonConstants.serviceIp);
      config.setClientServicePort(CommonConstants.servicePort);
      // 设置对外暴露的端口，该端口的启动在服务端，这里只是表明要跟服务端的那个监听服务对接
      config.setListenServerPort(model.listenPort);
      // 设置要暴露的目标IP和端口
      config.setDestIp(model.destIp);
      config.setDestPort(model.destPort);

      // 设置交互密钥和签名key
      config.setBaseAesKey(CommonConstants.aesKey);
      config.setTokenKey(CommonConstants.tokenKey);
      // 设置隧道交互密钥
      config.setBasePasswayKey(CommonConstants.aesKey);

      new ClientControlThread(config).createControl();
    }
  }

  /**
   * 交互加密，即交互验证
   */
  public static void secret() throws Exception {
    for (ListenDest model : CommonConstants.listenDestArray) {
      SecretInteractiveClientConfig config = new SecretInteractiveClientConfig();

      // 设置服务端IP和端口
      config.setClientServiceIp(CommonConstants.serviceIp);
      config.setClientServicePort(CommonConstants.servicePort);
      // 设置对外暴露的端口，该端口的启动在服务端，这里只是表明要跟服务端的那个监听服务对接
      config.setListenServerPort(model.listenPort);
      // 设置要暴露的目标IP和端口
      config.setDestIp(model.destIp);
      config.setDestPort(model.destPort);

      // 设置交互密钥和签名key
      config.setBaseAesKey(CommonConstants.aesKey);
      config.setTokenKey(CommonConstants.tokenKey);

      new ClientControlThread(config).createControl();
    }
  }

  /**
   * 无加密、无验证
   */
  public static void simple() throws Exception {
    for (ListenDest model : CommonConstants.listenDestArray) {
      InteractiveClientConfig config = new InteractiveClientConfig();

      // 设置服务端IP和端口
      config.setClientServiceIp(CommonConstants.serviceIp);
      config.setClientServicePort(CommonConstants.servicePort);
      // 设置对外暴露的端口，该端口的启动在服务端，这里只是表明要跟服务端的那个监听服务对接
      config.setListenServerPort(model.listenPort);
      // 设置要暴露的目标IP和端口
      config.setDestIp(model.destIp);
      config.setDestPort(model.destPort);

      new ClientControlThread(config).createControl();
    }
  }

  /**
   * 构建监听目标
   *
   * @throws Exception 异常信息
   */
  private void buildListenDest() throws Exception {
    List<Integer> destPort = proxyConfig.getDestPort();
    List<Integer> ports = proxyConfig.getPort();
    List<String> ips = proxyConfig.getIp();
    for (int i = 0; i < destPort.size(); i++) {
      CommonConstants.listenDestArray.add(ListenDest.of(ports.get(i), ips.get(i), destPort.get(i)));
    }
  }
}
