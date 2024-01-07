没啥可说的，课设用的微服务

# 配置：

请手动给每个服务添加application.yaml

## admin-server

```
server:
  port: 8084

spring:
  application:
    name: admin-server

  servlet:
    multipart:
      max-request-size: 10MB
      max-file-size: 10MB

  datasource:
    url: jdbc:mysql://{ip}:3306/db_shop_admin?useSSL=false
    username: {username}
    password: {password}
    driver-class-name: com.mysql.cj.jdbc.Driver

  redis:
    host: {ip}
    port: 6379
    password: {password}
    lettuce:
      pool:
        max-active: 10
        max-idle: 10
        min-idle: 1
        time-between-eviction-runs: 10s
    database: 1

  cloud:
    nacos:
      discovery:
        server-addr: {ip}:8848 #nacos地址

  rabbitmq:
    host: {ip}
    port: 5672
    virtual-host: /
    username: {username}
    password: {password}


mybatis-plus:
  type-aliases-package: com.mata.pojo

feign:
  client:
    config:
      default:
        logger-level: BASIC
  httpclient:
    enabled: true #开启feign对httpclient的支持
    max-connections: 200 #最大连接数
    max-connections-per-route: 50 #每个路径的最大连接数
  hystrix:
    enabled: true
  circuitbreaker:
    enabled: true
```

## email-code-server

```
server:
  port: 8082

spring:
  application:
    name: email-code-server

  redis:
    host: {ip}
    port: 6379
    password: {password}
    lettuce:
      pool:
        max-active: 10
        max-idle: 10
        min-idle: 1
        time-between-eviction-runs: 10s
    database: 0

  cloud:
    nacos:
      discovery:
        server-addr: {ip}:8848 #nacos地址


  mail:
    host: smtp.163.com
    port: 465
    username: {email}
    password: {password}
    protocol: smtps
    properties:
      mail:
        debug: true
        smtp:
          from: {email} #默认发送者邮箱
          ssl:
            enabled: true
            socketFactory:
              fallback: false
              class: javax.net.ssl.SSLSocketFactory
          auth: true
          starttls:
            enabled: true
            required: true
    default-encoding: utf-8
```

## gateway

```
server:
  port: 10010
spring:
  application:
    name: gateway

  cloud:
    nacos:
      server-addr: {ip}:8848
    gateway:
      routes: #网关路由配置
        - id: user-server #路由id，自定义，只要唯一即可
          uri: lb://user-server #路由的目标位置
          predicates: # 路由断言，也就是判断请求是否符合路由规则的条件
            - Path=/user/**,/shop-cart/** # 路径断言 这个是按照路径匹配，只要以/user/开头就符合要求
        - id: email-code-server #路由id，自定义，只要唯一即可
          uri: lb://email-code-server #路由的目标位置
          predicates:
            - Path=/send-email-code/login-code/**,/send-email-code/change-password-code/**
        - id: admin-server
          uri: lb://admin-server
          predicates:
            - Path=/manage-good/**,/admin/**
        - id: goods-server
          uri: lb://goods-server
          predicates:
            - Path=/goods/search/**
        - id: order-server
          uri: lb://order-server
          predicates:
            - Path=/order/**
      globalcors: # 全局的跨域处理
        add-to-simple-url-handler-mapping: true # 解决options请求被拦截问题
        corsConfigurations:
          '[/**]':
            allowedOrigins: # 允许哪些网站的跨域请求
              - "*"
            allowedMethods: # 允许的跨域ajax的请求方式
              - "GET"
              - "POST"
              - "DELETE"
              - "PUT"
              - "OPTIONS"
            allowedHeaders: "*" # 允许在请求中携带的头信息
            allowCredentials: true # 是否允许携带cookie
            maxAge: 360000 # 这次跨域检测的有效期
```

## goods-server

```
server:
  port: 8083

spring:
  application:
    name: goods-server

  servlet:
    multipart:
      max-request-size: 10MB
      max-file-size: 10MB

  redis:
    host: {ip}
    port: 6379
    password: {password}
    lettuce:
      pool:
        max-active: 10
        max-idle: 10
        min-idle: 1
        time-between-eviction-runs: 10s
    database: 2

  cloud:
    nacos:
      discovery:
        server-addr: {ip}:8848 #nacos地址

  rabbitmq:
    host: {ip}
    port: 5672
    virtual-host: /
    username: {username}
    password: {password}



es:
  host: {ip}:9200

tx-client:
  secretId: {secretId}  #  secretId
  secretKey: {secretKey} #  secretKey
  regionName: ap-nanjing #地域简称
  bucketName: {bucketName} #桶名称
  fileMkdir: testMkdir #文件夹

feign:
  client:
    config:
      default:
        logger-level: BASIC
  httpclient:
    enabled: true #开启feign对httpclient的支持
    max-connections: 200 #最大连接数
    max-connections-per-route: 50 #每个路径的最大连接数
  hystrix:
    enabled: true
  circuitbreaker:
    enabled: true


```

## order-server

```
server:
  port: 8085

spring:
  application:
    name: order-server

  datasource:
    url: jdbc:mysql://{ip}:3306/db_shop_order?useSSL=false
    username: {username}
    password: {password}
    driver-class-name: com.mysql.cj.jdbc.Driver

  redis:
    host: {ip}
    port: 6379
    password: {password}
    lettuce:
      pool:
        max-active: 10
        max-idle: 10
        min-idle: 1
        time-between-eviction-runs: 10s
    database: 3

  cloud:
    nacos:
      discovery:
        server-addr: {ip}:8848 #nacos地址

  rabbitmq:
    host: {ip}
    port: 5672
    virtual-host: /
    username: {username}
    password: {password}



mybatis-plus:
  type-aliases-package: com.mata.pojo

feign:
  client:
    config:
      default:
        logger-level: BASIC
  httpclient:
    enabled: true #开启feign对httpclient的支持
    max-connections: 200 #最大连接数
    max-connections-per-route: 50 #每个路径的最大连接数
  hystrix:
    enabled: true
  circuitbreaker:
    enabled: true
```

## user-server

```
server:
  port: 8081

spring:
  application:
    name: user-server

  datasource:
    url: jdbc:mysql://{ip}:3306/db_shop_user?useSSL=false
    username: {username}
    password: {password}
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource

  data:
    mongodb:
      host: {ip}
      port: 27017
      database: shop
      username: root
      password: {password}
      authentication-database: admin



  redis:
    host: {ip}
    port: 6379
    password: {password}
    lettuce:
      pool:
        max-active: 10
        max-idle: 10
        min-idle: 1
        time-between-eviction-runs: 10s
    database: 0

  cloud:
    nacos:
      discovery:
        server-addr: {ip}:8848 #nacos地址

  rabbitmq:
    host: {ip}
    port: 5672
    virtual-host: /
    username: {username}
    password: {password}


mybatis-plus:
  type-aliases-package: com.mata.pojo


feign:
  client:
    config:
      default:
        logger-level: BASIC
  httpclient:
    enabled: true #开启feign对httpclient的支持
    max-connections: 200 #最大连接数
    max-connections-per-route: 50 #每个路径的最大连接数
  hystrix:
    enabled: true
  circuitbreaker:
    enabled: true

```

