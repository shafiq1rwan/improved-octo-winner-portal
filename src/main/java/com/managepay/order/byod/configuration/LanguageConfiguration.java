package com.managepay.order.byod.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.managepay.order.byod.bean.LanguagePackCN;
import com.managepay.order.byod.bean.LanguagePackEN;

@Configuration
public class LanguageConfiguration {	
    @Bean
    public LanguagePackEN languagePackEN(){
        return new LanguagePackEN();
    }
    
    @Bean
    public LanguagePackCN languagePackCN(){
        return new LanguagePackCN();
    }
}
