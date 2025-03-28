#!/bin/bash

contain_name="wiki-proxy-client"

# 检查并停止容器
if docker ps -q -f name=${contain_name}; then
  echo "停止容器 ${contain_name} ..."
  docker stop ${contain_name}
else
  echo "容器 ${contain_name} 未运行或不存在！"
fi