# FrameWiKi Network Proxy

维基框架内网穿透工具

*********************

## FrameWiKi Network Proxy 是做什么的？

- 需要用户自行提供硬件支持的服务或电脑、部署的内网穿透工具。
- 可提供TCP协议类型的内网穿透服务，包括但不限于 http(s)、数据库连接、ssh等协议。
- 支持https 与 http协议与应用交互方式（推荐使用https更安全）。
- 支持无加密、控制端口加密交互、数据加密交互方式。
- 主要服务场景，需要将内网的应用开放到公网，如微信小程序开发调试、支付回调等。
- 支持 http 根据 host 进行反向代理；目标依然是内网应用，只是可以根据 http 协议 header 中的 host 字段区分选择目标应用（注意：只是有人提出来了
  http 监听统一端口并用域名访问的问题，并且有做的价值才补充的该功能；没做负载功能，这个是内网穿透，不是
  nginx，更不建议直接用在生产上，需要负载的可以自己去实现）

# 1、简介

维基代理（wiki-proxy）是一个基于Netty的、开源的java内网穿透项目。遵循MIT许可，因此您可以对它进行复制、修改、传播并用于任何个人或商业行为。

# 2、项目结构

- wiki-proxy 维基代理项目
    - assets 框架资源文件
    - client 代理客户端
        - proxy-client 代理客户端项目
        - proxy-client-web 代理客户端项目
    - proxy-admin 代理监控项目（基于vue3 + element-plus开发）
    - proxy-core 代理核心库
    - proxy-util 代理工具库
    - server 代理服务端
        - proxy-server 代理服务端业务代码
        - proxy-server-web 代理服务端接口

# 3、运行

# 4、部署方式

# 5、代理示意图

# 6、联系我们

- 微信: wangnanfei-cn
- Gitee: https://gitee.com/cdkjframework/wiki-proxy
- Github仓库：https://github.com/cdkjframework/wiki-proxy

# 7、参与贡献

<a href="https://gitee.com/cdkjframework" target="_blank">
<img src="assets/developer/wiki.png" width="11%"/>
</a>
