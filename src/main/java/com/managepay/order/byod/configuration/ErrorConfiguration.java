package com.managepay.order.byod.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.managepay.order.byod.bean.ErrorData;

@Configuration
public class ErrorConfiguration {

    @Bean
    public ErrorData errorData(){
        return new ErrorData();
    }
}
