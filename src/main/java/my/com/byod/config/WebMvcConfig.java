package my.com.byod.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer{
	
	@Value("${access-drive}")
	private String accessDrive;
	
	@Value("${upload-path}")
	private String uploadPath;
	
	@Value("${menu-path}")
	private String menuPath;

	@Override
	 public void addResourceHandlers(ResourceHandlerRegistry registry) {
		 registry
		 	.addResourceHandler(uploadPath + "**","/byod/" + uploadPath +"**")
		 	.addResourceLocations("file:///" + accessDrive + ":" + uploadPath);
		 
		 registry
		 	.addResourceHandler(menuPath + "**","/byod/" + menuPath +"**")
		 	.addResourceLocations("file:///"+ accessDrive + ":" + menuPath);
	 }
}
