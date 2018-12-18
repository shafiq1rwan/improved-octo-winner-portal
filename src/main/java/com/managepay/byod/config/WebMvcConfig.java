package com.managepay.byod.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer{
	
	@Value("${upload-path}")
	private String uploadPath;

	@Override
	 public void addResourceHandlers(ResourceHandlerRegistry registry) {
		 registry
		 	.addResourceHandler(uploadPath + "**","/byod" + uploadPath +"**")
		 	.addResourceLocations("file:///C:" + uploadPath);
	 }
}
