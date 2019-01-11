package my.com.byod.order.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import my.com.byod.order.bean.ApplicationData;

@Configuration
public class OrderConfiguration {

    @Bean
    public ApplicationData applicationData(){
        return new ApplicationData();
    }
}
