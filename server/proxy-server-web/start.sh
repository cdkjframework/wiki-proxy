#!/bin/bash
echo "开始构建"
#构建镜像
#分别输入 容器名称 和要构建的 版本号 eg test 111 生成版本号为 test:111
#contain_name=test
#version=1
contain_name=wiki-proxy-server
read -p "Please enter your contain name and version eg : v1.0: " version
echo "contain name and version is [${contain_name}:${version}]"
#输入要构建的端口号 或者自定义
#server_port=8001
read -p "Please enter your application server port in(1024到49151) " server_port
echo "you input server port is [${server_port}]"
#取消自定义命令
#contain_name=demo
docker rmi ${contain_name}:${version}
docker build -t  ${contain_name}:${version} .
#停用之前的服务
docker stop ${contain_name}
docker rm ${contain_name}
#启动容器
docker run -p ${server_port}:${server_port} -e server_port=${server_port} --name ${contain_name} -d ${contain_name}:${version}
echo "构建完成"