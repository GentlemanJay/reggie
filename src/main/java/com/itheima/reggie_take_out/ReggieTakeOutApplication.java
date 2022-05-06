package com.itheima.reggie_take_out;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author xushengjie
 */
@SpringBootApplication
@MapperScan("com.itheima.reggie_take_out.mapper")
//扫描WebFilter注解的过滤器
@ServletComponentScan
public class ReggieTakeOutApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieTakeOutApplication.class, args);
    }

}
