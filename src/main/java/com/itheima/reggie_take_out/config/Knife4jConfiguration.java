package com.itheima.reggie_take_out.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @author xushengjie
 * @create 2022/5/8 10:27 PM
 */
@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfiguration {

	@Bean(value = "defaultApi2")
	public Docket defaultApi2() {
		Docket docket=new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(new ApiInfoBuilder()
						.title("reggie外卖")
						.description("reggie外卖接口文档")
						.termsOfServiceUrl("http://localhost:8080/backend/page/login/login.html")
						.version("1.0")
						.build())
				//分组名称
				.groupName("1.0版本")
				.select()
				//这里指定Controller扫描包路径
				.apis(RequestHandlerSelectors.basePackage("com.itheima.reggie_take_out.controller"))
				.paths(PathSelectors.any())
				.build();
		return docket;
	}
}
