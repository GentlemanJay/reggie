package com.itheima.reggie_take_out;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReggieTakeOutApplicationTests {

    @Test
    public void contextLoads() {

        //e10adc3949ba59abbe56e057f20f883e
        String mad5str = "123456";
        System.out.println("====data: " + SecureUtil.md5(mad5str));

        //生成随机数
        System.out.println("===randomNumber: " + RandomUtil.randomNumbers(6));

    }

}
