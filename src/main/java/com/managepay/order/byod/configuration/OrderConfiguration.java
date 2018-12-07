package com.managepay.order.byod.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.managepay.order.byod.bean.ApplicationData;

@Configuration
public class OrderConfiguration {

    @Bean
    public ApplicationData applicationData(){
        return new ApplicationData();
    }
}
