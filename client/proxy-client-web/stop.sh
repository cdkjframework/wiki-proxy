#vi stopAndRemoveContain.sh
#!/bin/bash
contain_name=wiki-proxy-client
#停止容器
docker stop ${contain_name}
#删除容器
docker rm ${contain_name}
#删除容器镜像
docker rmi ${contain_name}