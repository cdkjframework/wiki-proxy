#!/bin/bash

contain_name="wiki-proxy-server"
default_port=6666
default_tcp_port=10010
default_profile="test"

# 输入镜像版本
read -p "请输入镜像版本号（例如 v1.0.0）: " version
if [[ -z "$version" ]]; then
  echo "错误：版本号不能为空！"
  exit 1
fi

# 输入应用端口（校验范围）
while true; do
  read -p "请输入应用端口号（1024-49151，默认${default_port}）: " server_port
  server_port=${server_port:-$default_port}
  if [[ "$server_port" =~ ^[0-9]+$ ]] && [ "$server_port" -ge 1024 ] && [ "$server_port" -le 49151 ]; then
    break
  else
    echo "错误：端口号必须是1024到49151之间的数字！"
  fi
done

# 输入应用TCP端口（校验范围）
while true; do
  read -p "请输入应用TCP端口号（1024-49151，默认${default_tcp_port}）: " tcp_port
  tcp_port=${tcp_port:-$default_tcp_port}
  if [[ "$tcp_port" =~ ^[0-9]+$ ]] && [ "$tcp_port" -ge 1024 ] && [ "$tcp_port" -le 49151 ]; then
    break
  else
    echo "错误：端口号必须是1024到49151之间的数字！"
  fi
done

# 输入 Spring 配置文件（例如 test, dev, prod）
read -p "请输入 Spring 配置文件（默认${default_profile}）: " profile
profile=${profile:-$default_profile}

echo "------------------------------"
echo "镜像名称与版本: ${contain_name}:${version}"
echo "应用端口号: ${server_port}"
echo "应用TCP端口号: ${tcp_port}"
echo "使用的 Spring 配置文件: ${profile}"
echo "------------------------------"

# 清理旧镜像和容器（提供确认提示）
read -p "是否清理旧的容器和镜像？（y/n）: " confirm
if [[ "$confirm" == "y" || "$confirm" == "Y" ]]; then
  echo "正在停止并删除旧的容器..."
  docker stop ${contain_name} 2>/dev/null
  docker rm ${contain_name} 2>/dev/null
  docker rmi ${contain_name}:${version} 2>/dev/null
else
  echo "跳过清理旧容器和镜像。"
fi

# 构建镜像（传递构建参数）
echo "正在构建镜像..."
docker build --build-arg SERVER_PORT=${server_port} --build-arg TCP_PORT=${tcp_port} --build-arg SPRING_PROFILE=${profile} -t ${contain_name}:${version} .
if [ $? -ne 0 ]; then
  echo "镜像构建失败，退出！"
  exit 1
fi

# 运行容器（传递环境变量）
echo "正在启动容器..."
docker run -d \
  --restart always \
  -p ${server_port}:${server_port} \
  -p ${tcp_port}:${tcp_port} \
  --name ${contain_name} \
  -e SERVER_PORT=${server_port} \
  -e TCP_PORT=${tcp_port} \
  -e SPRING_PROFILE=${profile} \
  ${contain_name}:${version}

if [ $? -eq 0 ]; then
  echo "构建完成！"
  echo "访问地址：http://localhost:${server_port}"
else
  echo "容器启动失败！"
  exit 1
fi
