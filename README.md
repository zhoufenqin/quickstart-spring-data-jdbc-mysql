# Sample project for Spring Data JDBC with Azure Database for MySQL

This sample project is used in these documents.

1. [Use Spring Data JDBC with Azure Database for MySQL](https://docs.microsoft.com/azure/developer/java/spring-framework/configure-spring-data-jdbc-with-azure-mysql/?WT.mc_id=github-microsoftsamples-judubois).
2. [Deploy a Spring application to Azure Spring Apps with a passwordless connection to an Azure database](https://learn.microsoft.com/azure/developer/java/spring-framework/deploy-passwordless-spring-database-app?toc=%2Fazure%2Fspring-apps%2Ftoc.json&bc=%2Fazure%2Fspring-apps%2Fbreadcrumb%2Ftoc.json&tabs=mysql).

## Creating the infrastructure

We recommend you create an *env.sh* file to create the following environment variables:

```bash
#!/bin/sh

echo "Setting env variables"

export AZ_RESOURCE_GROUP=tmp-spring-jdbc-mysql
export AZ_DATABASE_SERVER_NAME=XXXXXX-tmp-spring-jdbc-mysql
export AZ_DATABASE_NAME=XXXXXX-tmp-spring-db
export AZ_LOCATION=eastus
export AZ_MYSQL_USERNAME=spring
export AZ_MYSQL_PASSWORD=XXXXXXXXXXXXXXXXXXX
export AZ_LOCAL_IP_ADDRESS=$(curl http://whatismyip.akamai.com/)

export SPRING_DATASOURCE_URL=jdbc:mysql://$AZ_DATABASE_SERVER_NAME.mysql.database.azure.com:3306/$AZ_DATABASE_NAME?serverTimezone=UTC
export SPRING_DATASOURCE_USERNAME=spring@$AZ_DATABASE_SERVER_NAME
export SPRING_DATASOURCE_PASSWORD=$AZ_MYSQL_PASSWORD
```

You will need to set up a unique `AZ_DATABASE_SERVER_NAME` as well as a correctly secured `AZ_MYSQL_PASSWORD`.

Once this file is created:

- Use `source env.sh` to set up those environment variables
- Use `./create-spring-data-jdbc-mysql.sh` to create your infrastructure
- Use `./destroy-spring-data-jdbc-mysql.sh` to delete your infrastructure

## Running the project

This is a standard Maven project, you can run it from your IDE, or using the provided Maven wrapper:

```bash
./mvnw spring-boot:run
```


## Running in ASA-E instance

- create a builder with java-native-image
- create an app and [configure service connector](https://learn.microsoft.com/en-us/azure/service-connector/tutorial-java-spring-mysql#create-an-azure-database-for-mysql-flexible-server)
- deploy app

```azure
az spring app deploy -n test2 --source-path . --build-env BP_JVM_VERSION=17

// native image
az spring app deploy -n native --source-path . --build-cpu 4 --build-memory 8Gi --builder native --build-env BP_NATIVE_IMAGE=true BP_JVM_VERSION=17 BP_NATIVE_IMAGE_BUILD_ARGUMENTS="--trace-object-instantiation=ch.qos.logback.classic.Logger --initialize-at-run-time=io.netty --no-fallback -H:+AddAllCharsets -H:ResourceConfigurationFiles=/workspace/META-INF/native-image/resource-config.json -H:ReflectionConfigurationFiles=/workspace/META-INF/native-image/reflect-config.json -H:+ReportExceptionStackTraces"
```

- record some error:

```bash
Error: No bin/java and no environment variable JAVA_HOME

-> add BP_NATIVE_IMAGE_BUILD_ARGUMENTS="--no-fallback"
```

```bash
https://github.com/spring-projects/spring-boot/issues/33632
https://www.graalvm.org/latest/reference-manual/native-image/dynamic-features/Resources/
-H:ReflectionConfigurationFiles=/workspace/META-INF/native-image/reflect-config.json

%PARSER_ERROR[d] %PARSER_ERROR[p] 1 --- [%PARSER_ERROR[t]] %PARSER_ERROR[logger] : %PARSER_ERROR[m]%PARSER_ERROR[n]%PARSER_ERROR[d] %PARSER_ERROR[p] 1 --- [%PARSER_ERROR[t]] %PARSER_ERROR[logger] : %PARSER_ERROR[m]%PARSER_ERROR[n]%PARSER_ERROR[d] %PARSER_ERROR[p] 1 --- [%PARSER_ERROR[t]] %PARSER_ERROR[logger] : %PARSER_ERROR[m]%PARSER_ERROR[n]
org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'todoController': Unsatisfied dependency expressed through constructor parameter 0: Error creating bean with name 'todoRepository': Unsatisfied dependency expressed through method 'setMappingContext' parameter 0: Error creating bean with name 'jdbcMappingContext': Unsatisfied dependency expressed through method 'jdbcMappingContext' parameter 1: Error creating bean with name 'jdbcDialect': Instantiation of supplied bean failed
...
...
Caused by: java.lang.ClassCastException: com.mysql.cj.exceptions.CJException cannot be cast to com.mysql.cj.exceptions.WrongArgumentException
        at com.mysql.cj.util.Util.getInstance(Util.java:193) ~[na:na]
        at com.mysql.cj.conf.ConnectionUrl$Type.getImplementingInstance(ConnectionUrl.java:241) ~[com.example.demo.DemoApplication:8.0.33]
        at com.mysql.cj.conf.ConnectionUrl$Type.getConnectionUrlInstance(ConnectionUrl.java:211) ~[com.example.demo.DemoApplication:8.0.33]
        at com.mysql.cj.conf.ConnectionUrl.getConnectionUrlInstance(ConnectionUrl.java:280) ~[com.example.demo.DemoApplication:8.0.33]
        at com.mysql.cj.jdbc.NonRegisteringDriver.connect(NonRegisteringDriver.java:185) ~[com.example.demo.DemoApplication:8.0.33]
        at com.zaxxer.hikari.util.DriverDataSource.getConnection(DriverDataSource.java:138) ~[na:na]
        at com.zaxxer.hikari.pool.PoolBase.newConnection(PoolBase.java:359) ~[com.example.demo.DemoApplication:na]
        at com.zaxxer.hikari.pool.PoolBase.newPoolEntry(PoolBase.java:201) ~[com.example.demo.DemoApplication:na]
        at com.zaxxer.hikari.pool.HikariPool.createPoolEntry(HikariPool.java:470) ~[na:na]
        at com.zaxxer.hikari.pool.HikariPool.checkFailFast(HikariPool.java:561) ~[na:na]
        ... 122 common frames omitted
```

```shell
https://github.com/micronaut-projects/micronaut-data/issues/123

Caused by: java.lang.ClassCastException: com.mysql.cj.exceptions.CJException cannot be cast to com.mysql.cj.exceptions.WrongArgumentException
        at com.mysql.cj.util.Util.getInstance(Util.java:193) ~[na:na]
        at com.mysql.cj.conf.ConnectionUrl$Type.getImplementingInstance(ConnectionUrl.java:241) ~[com.example.demo.DemoApplication:8.0.33]
        at com.mysql.cj.conf.ConnectionUrl$Type.getConnectionUrlInstance(ConnectionUrl.java:211) ~[com.example.demo.DemoApplication:8.0.33]
        at com.mysql.cj.conf.ConnectionUrl.getConnectionUrlInstance(ConnectionUrl.java:280) ~[com.example.demo.DemoApplication:8.0.33]
        at com.mysql.cj.jdbc.NonRegisteringDriver.connect(NonRegisteringDriver.java:185) ~[com.example.demo.DemoApplication:8.0.33]
        at com.zaxxer.hikari.util.DriverDataSource.getConnection(DriverDataSource.java:138) ~[na:na]
        at com.zaxxer.hikari.pool.PoolBase.newConnection(PoolBase.java:359) ~[com.example.demo.DemoApplication:na]
        at com.zaxxer.hikari.pool.PoolBase.newPoolEntry(PoolBase.java:201) ~[com.example.demo.DemoApplication:na]
        at com.zaxxer.hikari.pool.HikariPool.createPoolEntry(HikariPool.java:470) ~[na:na]
        at com.zaxxer.hikari.pool.HikariPool.checkFailFast(HikariPool.java:561) ~[na:na]
        ... 122 common frames omitted
```

How to solve this???????

https://github.com/micronaut-projects/micronaut-data/issues/123
https://bugs.mysql.com/bug.php?id=109508


```shell
java.sql.SQLNonTransientConnectionException: Cannot connect to MySQL server on mysqlf-server.mysql.database.azure.com:3,306.

Make sure that there is a MySQL server running on the machine/port you are trying to connect to and that the machine this software is running on is able to connect to this host/port (i.e. not firewalled). Also make sure that the server has not been started with the --skip-networking flag.
...
...
Caused by: java.lang.ClassCastException: com.mysql.cj.exceptions.CJException cannot be cast to com.mysql.cj.exceptions.UnableToConnectException
        at com.mysql.cj.jdbc.ConnectionImpl.createNewIO(ConnectionImpl.java:822) ~[na:na]
        at com.mysql.cj.jdbc.ConnectionImpl.<init>(ConnectionImpl.java:446) ~[na:na]
        ... 129 common frames omitted
```