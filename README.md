# FrameWiKi Network Proxy

Wiki Framework Internal Network Penetration Tool
*********************

## What does FrameWiKi Network Proxy do?

-Users are required to provide hardware support services, computers, and deployed intranet penetration tools on their
own.
-We can provide intranet penetration services for TCP protocol types, including but not limited to HTTP (s), database
connection, SSH, and other protocols.
-Support HTTPS and HTTP protocols and application interaction methods (HTTPS is recommended for greater security).
-Support unencrypted, control port encrypted interaction, and data encrypted interaction methods.
-The main service scenario requires opening up internal network applications to the public network, such as WeChat mini
program development and debugging, payment callbacks, etc.
-Support HTTP reverse proxy based on host; The target is still an internal network application, but the target
application can be selected based on the host field in the HTTP protocol header (note: it was only suggested by someone)
The issue of HTTP listening on a unified port and accessing it using a domain name, and the added value of this feature;
There is no load function, this is internal network penetration, not
nginx， It is not recommended to use it directly in production, and those that require load can be implemented by
themselves

# 1. Introduction

Wiki proxy is an open-source Java intranet penetration project based on Netty. Following the MIT license, you may copy,
modify, distribute, and use it for any personal or commercial activity.

# 2. Project Structure

- wiki-proxy Wiki Proxy Project
    - assets Framework resource file
    - client Agent
        - proxy-client Proxy client project
        - proxy-client-web Proxy client interface
    - proxy-admin Proxy monitoring project (developed based on Vue3+element plus)
    - proxy-core Proxy Core Library
    - proxy-util Proxy Tool Library
    - server Proxy server
        - proxy-server Proxy server business code
        - proxy-server-web Proxy server interface

# 3. Operation

# 4. Deployment method

# 5. Proxy schematic diagram

# 6. Contact us

- WeChat: wangnanfei-cn
- Gitee:  https://gitee.com/cdkjframework/wiki-proxy
- Github repository: https://github.com/cdkjframework/wiki-proxy

# 7. Participate and contribute

  <a href=" https://gitee.com/cdkjframework " target="_blank">
  <img src="assets/developer/wiki.png" width="11%"/>
  </a>