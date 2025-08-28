package com.jzy.chatgptdata.app;

//import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @description 启动类
 */
@SpringBootApplication(scanBasePackages = "com.jzy.chatgptdata")
@Configurable
@EnableScheduling
@MapperScan("com.jzy.chatgptdata.infrastructure.dao")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

}
