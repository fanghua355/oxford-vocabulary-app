#!/bin/bash

# 检查是否安装了Maven
if command -v mvn &> /dev/null; then
    echo "使用Maven启动后端..."
    mvn spring-boot:run
else
    echo "Maven未安装，请先安装Maven。"
    echo "可以通过以下命令安装Maven："
    echo "  - macOS: brew install maven"
    echo "  - Ubuntu/Debian: sudo apt-get install maven"
    echo "  - CentOS/RHEL: sudo yum install maven"
    exit 1
fi 