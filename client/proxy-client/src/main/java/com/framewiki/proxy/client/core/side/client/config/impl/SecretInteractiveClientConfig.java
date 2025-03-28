package com.framewiki.proxy.client.core.side.client.config.impl;

import com.cdkjframework.exceptions.GlobalRuntimeException;
import com.cdkjframework.util.log.LogUtils;
import com.framewiki.proxy.core.channel.impl.SecretInteractiveChannelBase;
import com.framewiki.proxy.core.channel.impl.BaseSocketChannel;
import com.framewiki.proxy.core.model.InteractiveModel;
import com.framewiki.proxy.core.util.AesUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;


import java.io.IOException;
import java.net.Socket;
import java.security.Key;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.client.core.side.client.config
 * @ClassName: SecretInteractiveClientConfig
 * @Description: 交互加密的配置方案（AES加密）
 * @Author: frank tiger
 * @Date: 2024/12/30 17:10
 * @Version: 1.0
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class SecretInteractiveClientConfig extends InteractiveClientConfig {
  /**
   * 日志
   */
  private final LogUtils log = LogUtils.getLogger(SecretInteractiveClientConfig.class);

  /**
   * 交互密钥
   */
  private String tokenKey;

  /**
   * AES密钥
   */
  private Key aesKey;

  /**
   * 创建客户端通道
   *
   * @return 客户端通道
   */
  @Override
  public BaseSocketChannel<? extends InteractiveModel, ? super InteractiveModel> newClientChannel() {
    SecretInteractiveChannelBase channel = new SecretInteractiveChannelBase();

    channel.setCharset(this.getCharset());
    channel.setTokenKey(this.tokenKey);
    channel.setAesKey(this.aesKey);

    try {
      Socket socket = new Socket(this.getClientServiceIp(), this.getClientServicePort());
      channel.setSocket(socket);
    } catch (IOException e) {
      log.error("connect client service exception", e);
      throw new GlobalRuntimeException(e.getMessage());
    }
    return channel;
  }

  /**
   * 设置交互密钥
   *
   * @param aesKey AES密钥
   */
  public void setBaseAesKey(String aesKey) {
    this.aesKey = AesUtils.createKeyByBase64(aesKey);
  }

}
