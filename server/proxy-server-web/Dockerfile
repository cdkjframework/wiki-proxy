# 使用官方OpenJDK镜像
FROM openjdk:17-jdk-oracle

# 定义构建参数（带默认值）
ARG SERVER_PORT=6666
ARG TCP_PORT=10010
ARG JAR_NAME=wiki-proxy-server.jar
ARG SPRING_PROFILE=test
# 默认配置为test

# 设置时区和工作目录的环境变量
ENV TZ=Asia/Shanghai \
    JAR_FILE=/usr/web/wiki-proxy/server/ \
    SPRING_PROFILES_ACTIVE=${SPRING_PROFILE}

# 创建目录（合并为单条RUN指令减少层数）
RUN mkdir -p ${JAR_FILE} && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

# 设置工作目录
WORKDIR ${JAR_FILE}

# 复制JAR文件到容器指定目录
COPY ${JAR_NAME} ${JAR_FILE}${JAR_NAME}
COPY wiki-proxy.jks ${JAR_FILE}wiki-proxy.jks

# 暴露端口（使用构建参数）
EXPOSE ${SERVER_PORT}
EXPOSE ${TCP_PORT}

# 使用 sh -c 执行启动命令，确保环境变量得到解析
ENTRYPOINT ["sh", "-c", "java -jar -Xmx256m -Xmn256m -Xss256k \
    -XX:+HeapDumpOnOutOfMemoryError ${JAR_FILE}wiki-proxy-server.jar \
    -Dspring.proxy.server.servicePort=${TCP_PORT} \
    -Dserver.port=${SERVER_PORT} \
    -Dspring.profiles.active=${SPRING_PROFILE}"]
