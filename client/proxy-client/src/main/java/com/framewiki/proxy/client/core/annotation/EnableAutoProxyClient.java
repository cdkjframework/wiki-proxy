package com.framewiki.proxy.client.core.annotation;

import com.framewiki.proxy.client.core.config.ProxyMarkerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.client.core.annotation
 * @ClassName: EnableAutoClientAgent
 * @Description: 自动启用客户端代理
 * @Author: xiaLin
 * @Date: 2025/1/3 17:08
 * @Version: 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ProxyMarkerConfiguration.class})
public @interface EnableAutoProxyClient {
}
