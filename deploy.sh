#!/bin/bash

# 设置变量
SSH_KEY="/Users/henry/Downloads/tencentCloud.pem"
SERVER="ubuntu@124.223.76.88"
DEPLOY_TYPE=$1  # 接受参数：frontend 或 backend 或 all

# 部署前端
deploy_frontend() {
    echo "开始部署前端..."
    npm run build
    ssh -i $SSH_KEY $SERVER "sudo rm -rf /var/www/html/* && sudo chown -R ubuntu:ubuntu /var/www/html"
    scp -i $SSH_KEY -r dist/* $SERVER:/var/www/html/
    ssh -i $SSH_KEY $SERVER "sudo chown -R www-data:www-data /var/www/html"
    echo "前端部署完成！"
}

# 部署后端
deploy_backend() {
    echo "开始部署后端..."
    cd backend
    mvn clean package -DskipTests
    scp -i $SSH_KEY target/oxford-vocabulary.war $SERVER:/tmp/
    ssh -i $SSH_KEY $SERVER "sudo mv /tmp/oxford-vocabulary.war /var/lib/tomcat9/webapps/ && sudo systemctl restart tomcat9"
    cd ..
    echo "后端部署完成！"
}

# 根据参数执行部署
case $DEPLOY_TYPE in
    "frontend")
        deploy_frontend
        ;;
    "backend")
        deploy_backend
        ;;
    "all")
        deploy_frontend
        deploy_backend
        ;;
    *)
        echo "请指定部署类型：frontend 或 backend 或 all"
        exit 1
        ;;
esac

# 检查服务状态
echo "检查服务状态..."
ssh -i $SSH_KEY $SERVER "sudo systemctl status nginx | grep Active; sudo systemctl status tomcat9 | grep Active" 