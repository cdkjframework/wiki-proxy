#指定以 openjdk:17-jdk-oracle 为基础镜像，来构建此镜像，可以理解为运行的需要基础环境
FROM openjdk:17-jdk-oracle
#创建目录
RUN mkdir -p /usr/web
RUN mkdir -p /usr/web/wiki-proxy
RUN mkdir -p /usr/web/wiki-proxy/client
#指定容器的工作目录
ENV jar_file /usr/web/wiki-proxy/client/
#添加时区指向
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
#WORKDIR指令用于指定容器的一个目录， 容器启动时执行的命令会在该目录下执行。
WORKDIR ${jar_file}
#将当前文件夹下面 demo.jar 复制到容器对应目录下
ENV jar_name wiki-proxy-client.jar
ADD ${jar_name} ${jar_file}${jar_name}
#jar文件端口 通过启动命令传入进来
ENV server_port ${server_port}
EXPOSE ${server_port}
#容器启动时执行的命令
CMD nohup java -jar \
-Xmx256m -Xmn256m  -Xss256k -XX:+HeapDumpOnOutOfMemoryError \
${jar_file}${jar_name} --server.port=${server_port}