package my.com.byod.order.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import my.com.byod.order.bean.ErrorData;

@Configuration
public class ErrorConfiguration {

    @Bean
    public ErrorData errorData(){
        return new ErrorData();
    }
}
