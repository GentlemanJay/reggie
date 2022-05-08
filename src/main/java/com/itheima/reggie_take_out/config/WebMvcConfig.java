package com.itheima.reggie_take_out.config;

import com.itheima.reggie_take_out.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 配置springMvc静态资源映射
 * @author xushengjie
 * @create 2022/4/26 7:38 PM
 */
@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

	@Override
	protected void addResourceHandlers(ResourceHandlerRegistry registry) {
		log.info("===开始进行静态资源映射");
		registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
		registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");

		//swagger-ui静态页面映射
		registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
		registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

	}

	/**
	 * 扩展mvc框架的消息转换器
	 * @param converters
	 */
	@Override
	protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		log.info("扩展消息转换器...");
		//创建消息转换器对象
		MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
		//设置对象转换器，底层使用Jackson将Java对象转为json
		messageConverter.setObjectMapper(new JacksonObjectMapper());
		//将上面的消息转换器对象追加到mvc框架的转换器集合中
		converters.add(0,messageConverter);
	}
}
