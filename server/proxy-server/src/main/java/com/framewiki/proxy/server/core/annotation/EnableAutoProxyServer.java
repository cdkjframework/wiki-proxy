package com.framewiki.proxy.server.core.annotation;

import com.framewiki.proxy.server.core.config.ProxyMarkerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @ProjectName: wiki-proxy
 * @Package: com.framewiki.proxy.server.core.annotation
 * @ClassName: EnableAutoServerAgent
 * @Description: 自动启用服务器代理
 * @Author: frank tiger
 * @Date: 2025/1/3 16:06
 * @Version: 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ProxyMarkerConfiguration.class})
public @interface EnableAutoProxyServer {
}
