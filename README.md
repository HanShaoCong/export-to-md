# export-db

#### 介绍
数据库字典导出到Markdown
支持MySql数据库，其他类型数据库可自行扩展

#### 使用说明

- 配置数据库 
> application.yml
```java
# 数据库信息
db:
  driver: com.mysql.cj.jdbc.Driver
  url: jdbc:mysql://localhost:3306/test?useSSL=true&verifyServerCertificate=false&serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=CONVERT_TO_NULL&allowMultiQueries=true
  username: root
  password: 123456
  # 数据库名
  database:
    - test

export:
  # 导出文件到文件夹，数据库名作为文件名
  dir: D:\project\idea\dev

```

- 执行程序
> 程序入口 ExportApplication.java 执行main函数
```java
public class ExportApplication {

    public static void main(String[] args) {
        DatabaseExporter exporter = new MySQLDatabaseExporter(ConfigUtil.loadConfig());
        exporter.export();
    }
}
```
