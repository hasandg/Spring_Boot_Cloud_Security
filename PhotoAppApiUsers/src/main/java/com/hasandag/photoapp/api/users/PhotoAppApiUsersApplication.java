package com.hasandag.photoapp.api.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import feign.Logger;

import static java.lang.Thread.sleep;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class PhotoAppApiUsersApplication implements CommandLineRunner {

    @Autowired
    Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(PhotoAppApiUsersApplication.class, args);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    @Profile("production")
    Logger.Level feignLoggerLevel() {
        return Logger.Level.NONE;
    }

    @Bean
    @Profile("!production")
    Logger.Level feignDefaultLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    @Profile("production")
    public String createProductionBean() {
        System.out.println("Production bean created. myapplication.environment = " + environment.getProperty("myapplication.environment"));
        return "Production bean";
    }

    @Bean
    @Profile("!production")
    public String createNotProductionBean() {
        System.out.println("Not Production bean created. myapplication.environment = " + environment.getProperty("myapplication.environment"));
        return "Not production bean";
    }

    @Bean
    @Profile("default")
    public String createDevelopmentBean() {
        System.out.println("Development bean created. myapplication.environment = " + environment.getProperty("myapplication.environment"));
        return "Development bean";
    }
	
	/*
	@Bean
	public FeignErrorDecoder getFeignErrorDecoder()
	{
		return new FeignErrorDecoder();
	} */

    @Override
    public void run(String... args) throws Exception {
        var myThread = new Thread(this::logEnvironmets);
        myThread.start();
    }

    private void logEnvironmets() {
        do {
            try {
                System.out.println("myexcercise.environment: *********** " + environment.getProperty("myexcercise.environment"));
                System.out.println("allowed.ip.addresses: *********** " + environment.getProperty("allowed.ip.addresses"));

                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (environment.getProperty("log.environments.infinite-loop", Boolean.class, false));
    }
}
