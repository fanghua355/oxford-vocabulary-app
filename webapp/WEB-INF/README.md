# Oxford 3000 Vocabulary App Backend

这是一个基于Spring Boot和MyBatis的后端应用程序，用于管理Oxford 3000词汇表。

## 技术栈

- Java 11
- Spring Boot 2.7.0
- MyBatis
- MySQL 8.0
- Maven

## 数据库设置

在运行应用程序之前，您需要创建一个MySQL数据库：

```sql
CREATE DATABASE oxford_vocabulary CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 配置

应用程序配置在`src/main/resources/application.yml`文件中。默认配置如下：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/oxford_vocabulary?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC
    username: root
    password: root
```

如果您的MySQL配置不同，请相应地修改这些值。

## 构建和运行

### 使用Maven

```bash
cd backend
./mvnw clean package
java -jar target/vocabulary-0.0.1-SNAPSHOT.jar
```

### 使用提供的脚本

```bash
cd backend
./start.sh
```

## API端点

- `GET /api/vocabulary` - 获取所有词汇
- `GET /api/vocabulary/{word}` - 通过单词获取词汇
- `GET /api/vocabulary/level/{level}` - 通过级别获取词汇
- `POST /api/vocabulary/import?fileName=oxford3000.csv` - 从CSV文件导入词汇

## 数据导入

应用程序启动时会自动从`src/main/resources/data/oxford3000.csv`文件导入数据。如果您想手动触发导入，可以使用以下API：

```
POST /api/vocabulary/import
```

您可以通过`fileName`参数指定不同的CSV文件：

```
POST /api/vocabulary/import?fileName=custom_vocabulary.csv
```

CSV文件应该放在`src/main/resources/data/`目录下，并且应该具有以下格式：

```
word,phonetic,translation,level,example,partOfSpeech,definition,synonyms,antonyms
birth,/bɜːθ/,出生 ,B1,It was a difficult birth.,noun,the time when a baby or young animal comes out of its mother's body:,give birth,
``` 