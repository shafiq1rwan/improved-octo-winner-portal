package my.com.byod.order.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import my.com.byod.order.bean.LanguagePackCN;
import my.com.byod.order.bean.LanguagePackEN;

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
