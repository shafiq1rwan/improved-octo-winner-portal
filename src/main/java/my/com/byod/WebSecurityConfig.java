package my.com.byod;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import my.com.byod.login.domain.ApplicationUser;
import my.com.byod.login.repository.ApplicationUserRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	DataSource dataSource;
	 
	@Autowired
	private BCryptPasswordEncoder passwordencoder;
	
	@Autowired
	private ApplicationUserRepository applicationUserRepo;
	
	private static String USERS_BY_USERNAME_QUERY = "SELECT username,password, enabled FROM users WHERE username=?";
	private static String AUTHORITIES_BY_USERNAME_QUERY = "SELECT u.username, a.authority FROM authorities a "
			+ "INNER JOIN users u ON a.user_id = u.id "
			+ "WHERE u.username=?";
 
	 @Autowired
	 public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		 auth.jdbcAuthentication()
	   .dataSource(dataSource)
       .usersByUsernameQuery(USERS_BY_USERNAME_QUERY)
       .authoritiesByUsernameQuery(AUTHORITIES_BY_USERNAME_QUERY)
       .passwordEncoder(passwordencoder());		 
	 }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http 
		.csrf().disable()
		.authorizeRequests()
				.antMatchers("/user/signin/**","/order/**","/api/device/**","/byod-menu/**","/forget-password/**","/byod-img/**","/reset-password/view*").permitAll()
				.antMatchers("/byod/byod-panel/**").access("hasAnyRole('ROLE_SUPER_ADMIN','ROLE_SUPER_GROUP_ADMIN','ROLE_ADMIN','ROLE_USER')")
				.antMatchers("/reset-password/reset*").hasAuthority("CHANGE_PASSWORD_PRIVILEGE")
				.anyRequest().authenticated()
	          .and()
	          .formLogin().loginPage("/user/signin")
	           	  .loginProcessingUrl("/perform-login")
		          .usernameParameter("username")
		          .passwordParameter("password")
		          .successHandler((request, response, authentication) -> {
		        	  User user = (User) authentication.getPrincipal();
		        	  String role = user.getAuthorities().iterator().next().toString();
		        	  
		        	  if(user!=null && role !=null) {
		        		  if(role.equals("ROLE_SUPER_ADMIN") || role.equals("ROLE_SUPER_GROUP_ADMIN") ||role.equals("ROLE_ADMIN") || role.equals("ROLE_USER"))
		        		  {
		        				HttpSession session = request.getSession();
		        		    	session.setAttribute("role", role);
		        		  }
		        		  response.sendRedirect(request.getContextPath()+"/byod/byod-panel");
		        	  }
		          })
		          .failureHandler((request, response, exception) -> {
		  			String username = request.getParameter("username");
					ApplicationUser user = applicationUserRepo.findUserByUsername(username);	        	  
		        	if(user!=null) {
		        		response.sendRedirect(request.getContextPath()+"/user/signin/error/*");
		        	} else {
		        		response.sendRedirect(request.getContextPath()+"/user/signin/error/not-exist");
		        	}
		          })
		          .permitAll()
	          .and()
	          .logout()
	          .clearAuthentication(true)
	          .invalidateHttpSession(true)
	          .and()
	          .exceptionHandling().accessDeniedPage("/user/403");
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/assets/**", "/*/imgS_190220191.png");
	}

	@Bean
	public BCryptPasswordEncoder passwordencoder() {
		return new BCryptPasswordEncoder();
	}

}