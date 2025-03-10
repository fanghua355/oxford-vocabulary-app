#!/bin/bash

echo "开始导入数据..."
./mvnw spring-boot:run -Dspring-boot.run.profiles=import

if [ $? -eq 0 ]; then
    echo "数据导入完成。"
else
    echo "数据导入失败，请检查日志。"
    exit 1
fi 