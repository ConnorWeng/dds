# 构建程序包 #

工具：maven 3.3.9、jdk 1.8

从git上拉取DDS最新代码，在项目根目录下打开命令行窗口，运行命令`mvn clean package -Pdist`。命令执行完毕之后，在`distrubution/target/distribution-1.0-SNAPSHOT-distribution`目录下将看到DDS所有生成的程序包：

- dds-api-1.0-SNAPSHOT-distribution：                 接口程序包，隔离dds-rpc、dds-registry-client和dds-metrics之间的依赖
- dds-rpc-1.0-SNAPSHOT-distribution：                 REST RPC客户端
- dds-metrics-1.0-SNAPSHOT-distribution：             REST RPC统计模块
- dds-registry-client-1.0-SNAPSHOT-distribution：     注册中心客户端
- dds-registry-server-1.0-SNAPSHOT-distribution：     注册中心服务端
- spring-boot-starter-dds-1.0-SNAPSHOT-distribution： 利用SpringBoot快速搭建DDS REST服务端
- dds-message-client-1.0-SNAPSHOT-distribution：      消息服务客户端
- dds-message-proxy-1.0-SNAPSHOT-distribution：       消息服务代理

每一个程序包中都包含完整的依赖包。假设你需要利用`spring-boot-starter-dds`新建一个DDS REST服务端，那么只要将`spring-boot-starter-dds-1.0-SNAPSHOT-distribution`下的所有jar包放到项目的classpath下即可，不需要再单独拷贝dds-rpc、dds-registry-client等程序包。

# DDS用户指南 #

## Getting Started ##

DDS提供了一套服务的注册与发现机制。 通过注册中心，服务提供方和消费方可以解耦合。本教程利用DDS快速搭建一个简单服务提供方和服务消费方。完整实现代码可查看`dds-demo`模块。

### 准备工作 ###

在dds-registry-server模块根目录下通过maven命令`mvn package spring-boot:repackage`可直接打包出一个可运行的jar包。通过命令`java -jar dds-registry-server-1.0-SNAPSHOT.jar`就可以启动一个监听8761端口的注册中心服务端。

### 服务提供方 ###

服务提供方通过REST接口规范对外提供服务，并将服务实例的ip、端口等信息向注册中心注册，供服务消费方查询并调用。全新的服务提供方可使用DDS提供的`spring-boot-starter-dds`快速搭建出一个服务提供方。

首先新建一个java项目，将`spring-boot-starter-dds-1.0-SNAPSHOT-distribution`程序包中的所有jar包都放到项目的classpath目录下，在classpath根目录下提供一个配置文件`dds-client.conf`，内容如下:

```properties
# 注册中心地址以及园区信息
eureka_servers=localhost:8761
zone=A
# 注册服务名和端口
service_name=micro-service
port=8081
```

编写服务端主类和main函数：

```java
@SpringBootApplication
public class MicroServer {
    public static void main(String[] args) {
        SpringApplication.run(MicroServer.class, args);
    }
}
```

编写服务实现类：

```java
@DDSService
@Path("/")
public class MicroService {
    @GET
    @Path("hello")
    @Produces("text/plain")
    public String hello(@QueryParam("user") String user) {
        return String.format("hello, %s", user);
    }
}
```

运行main函数，可以通过浏览器访问注册中心 http://localhost:8761 看到micro-service服务实例，直接访问 http://localhost:8081/hello?user=tom ，页面会输出“hello, tom”。

### 服务消费方 ###

服务消费方通过`dds-rpc`程序包可以方便地调用服务，无需关心注册中心。

首先再新建一个java项目，将`dds-rpc-1.0-SNAPSHOT-distribution`和`dds-registry-client-1.0-SNAPSHOT-distribution`程序包下的所有jar包都放到项目classpath目录下（重复jar包可互相覆盖），在classpath根目录下提供一个配置文件`dds-client.conf`，内容如下:

```properties
# 注册中心地址以及园区信息
eureka_servers=localhost:8761
zone=A
```

编写服务调用代理类：

```java
public class MicroServiceSupport extends RestSupport {
    public String hello(String user) throws DDSRestRPCException {
        String response = this.getRestTemplate()
            .service("micro-service")
            .accept(MediaType.TEXT_PLAIN_TYPE)
            .path("/hello")
            .query("user", "tom")
            .get(String.class);
        return response;
    }
}
```

编写调用远程服务的业务类:

```java
public class ServiceConsumer {
    public static void main(String[] args) throws DDSRestRPCException {
        MicroServiceSupport microService = SupportFactory.getRestSupport(MicroServiceSupport.class);
        System.out.println(microService.hello("tom"));
    }
}
```

运行业务类控制台会输出: hello, tom，服务调用成功。

## 复杂场景示例 ##

在`dds-rpc`模块测试案例中覆盖了所有复杂场景。REST服务端示例代码请查看`src/test/java/com/icbc/dds/rpc/support/RestTestServices`，REST调用端示例代码请查看`src/test/java/com/icbc/dds/rpc/support/RestSupportIntegrationTest`。

# DDS开发指南 #

## maven命令 ##

- `mvn clean test`: 编译整个项目并运行所有测试
- `mvn clean package -Pdist`: 构建程序包用于分发
- 在dds-registry-server模块根目录下通过maven命令`mvn package spring-boot:repackage`可直接打包出一个可运行的jar包，通过命令`java -jar dds-registry-server-1.0-SNAPSHOT.jar`就可以启动一个监听8761端口的注册中心服务端（eureka server）。

## 项目模块 ##

### dds-api ###

接口层，定义了dds-metrics和dds-registry-client的接口。dds-rpc不直接依赖dds-metrics、dds-registry-client，而是依赖dds-api，通过JAVA SPI机制以插件的方式加载dds-metrics和dds-registry-client，如果没有找到这2个程序包的话，则用自己的默认实现替代。

### dds-rpc ###

封装jersey-client实现了基于REST的RPC调用。dds-rpc会通过JAVA SPI加载dds-registry-client，如果加载成功，dds-rpc就可以利用dds-registry-client向注册中心查询服务，然后发起RPC调用，同时能够透明重试，处理调用失败的情况。dds-rpc还会通过JAVA SPI加载dds-metrics，如果加载成功，dds-rpc就会通过dds-metrics收集服务调用数据，上送至监控平台。

### dds-metrics ###

见dds-rpc。

### dds-registry-client ###

见dds-rpc。

### dds-registry-server ###

通过spring-cloud-starter-eureka-server启动eureka server。在dds-registry-server模块根目录下通过maven命令`mvn package spring-boot:repackage`可直接打包出一个可运行的jar包。

### spring-boot-starter-dds ###

通过SpringBoot快速搭建起一个DDS REST服务端，特别适合于开发新的独立的微服务。

### dds-message-proxy ###

消息服务代理，向注册中心注册，提供消息读写服务。

### dds-message-client ###

消息服务客户端，用户使用该客户端与消息服务代理交互。

### dds-demo ###

示例代码，演示如何搭建服务提供方和消费方。

### distrubution ###

用于打包发版。
