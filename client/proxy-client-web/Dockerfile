# 使用官方OpenJDK镜像
FROM openjdk:17-jdk-oracle

# 定义构建参数（带默认值）
ARG SERVER_PORT=8888
ARG TCP_PORT=10010
ARG TCP_IP=127.0.0.1
ARG SPRING_PROFILE=test
ARG JAR_NAME=wiki-proxy-client.jar

# 设置时区和工作目录的环境变量
ENV TZ=Asia/Shanghai \
    JAR_FILE=/usr/web/wiki-proxy/client/ \
    SPRING_PROFILES_ACTIVE=${SPRING_PROFILE} \
    SPRING_PROXY_CLIENT_SERVICE_IP=${TCP_IP} \
    SPRING_PROXY_CLIENT_SERVICE_PORT=${TCP_PORT}

# 创建目录（合并为单条RUN指令减少层数）
RUN mkdir -p ${JAR_FILE} && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

# 设置工作目录
WORKDIR ${JAR_FILE}

# 复制JAR文件到容器指定目录
COPY ${JAR_NAME} ${JAR_FILE}${JAR_NAME}

# 暴露端口（使用构建参数）
EXPOSE ${SERVER_PORT}

# 使用 sh -c 执行启动命令，确保环境变量得到解析
ENTRYPOINT ["sh", "-c", "java -jar -Xmx256m -Xmn256m -Xss256k \
    -XX:+HeapDumpOnOutOfMemoryError ${JAR_FILE}wiki-proxy-client.jar \
    -Dspring.proxy.client.serviceIp=${TCP_IP} \
    -Dspring.proxy.client.servicePort=${TCP_PORT} \
    -Dserver.port=${SERVER_PORT} \
    -Dspring.profiles.active=${SPRING_PROFILE}"]