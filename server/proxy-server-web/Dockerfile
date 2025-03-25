FROM openjdk:17-jdk-oracle
#同步时间
#RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories && \
#    apk update && apk add wget unzip vim && apk add -U tzdata && \
#    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone
# 设置时区为北京时间
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN mkdir -p /user/web
RUN mkdir -p /user/web/wiki-proxy
RUN mkdir -p /user/web/wiki-proxy/config
WORKDIR  /user/web/wiki-proxy
COPY ./target/wiki-proxy-server.jar /user/web/wiki-proxy/wiki-proxy-server.jar
COPY ./src/main/resources/application.yml /root/wiki-proxy/config/application.yml
#VOLUME ["/user/web/wiki-proxy"]
ENTRYPOINT ["java","-jar","wiki-proxy-server.jar","config=./config/application.yml"]