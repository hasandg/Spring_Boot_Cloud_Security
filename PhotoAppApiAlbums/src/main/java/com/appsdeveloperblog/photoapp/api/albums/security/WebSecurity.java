package com.appsdeveloperblog.photoapp.api.albums.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@EnableMethodSecurity(prePostEnabled=true)
@EnableWebSecurity
@Configuration
public class WebSecurity {
	
	Environment environment;
	
	public WebSecurity(Environment environment) {
		this.environment = environment;
	}
	
    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
    	
        // Configure AuthenticationManagerBuilder
      AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
 
      // Get AuthenticationManager
      AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
    	
        http
        .csrf((csrf) -> csrf.disable())
         .authorizeHttpRequests((authz) -> authz
        .anyRequest().authenticated())
        .addFilter(new AuthorizationFilter(authenticationManager, environment))
        .authenticationManager(authenticationManager)
        .sessionManagement((session) -> session
        	        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
 
        
        return http.build();
    }

}

