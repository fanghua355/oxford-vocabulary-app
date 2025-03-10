#!/bin/bash

# 检查MySQL服务是否运行
echo "检查MySQL服务..."
if ! pgrep -x "mysqld" > /dev/null; then
    echo "MySQL服务未运行，请先启动MySQL服务"
    exit 1
fi

# 检查数据库是否存在，如果不存在则创建
echo "检查数据库是否存在..."
mysql -h 124.223.76.88 -u henry -phenryfang -e "CREATE DATABASE IF NOT EXISTS oxford_vocabulary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 构建项目
echo "构建项目..."
./mvnw clean package -DskipTests

# 运行应用
echo "启动应用..."
java -jar target/vocabulary-0.0.1-SNAPSHOT.jar 