package com.hmdp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.kafka.annotation.EnableKafka;

@EnableAspectJAutoProxy(exposeProxy = true) // 开启 AspectJ 自动代理，并暴露代理对象
@EnableKafka // 启用 Kafka 支持
@MapperScan("com.hmdp.mapper") // 扫描 Mapper 接口所在的包
@SpringBootApplication
public class HmDianPingApplication {

    public static void main(String[] args) {
        SpringApplication.run(HmDianPingApplication.class, args);
    }

}
