package com.itheima.reggie_take_out.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 获取腾讯云配置类信息
 * @author xushengjie
 * @create 2022/5/6 11:18 AM
 */
@PropertySource("classpath:/application.yml")
@ConfigurationProperties("tencent.cloud")
@Configuration
@Data
public class TencentCloudParamConfig {

	@Value("${sdkAppId}")
	private String sdkAppId;

	@Value("${signName}")
	private String signName;

	@Value("${templateId}")
	private String templateId;
}
