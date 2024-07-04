package com.appsdeveloperblog.photoapp.api.albums;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableDiscoveryClient
public class PhotoAppApiAlbumsApplication { // implements CommandLineRunner {

	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(PhotoAppApiAlbumsApplication.class, args);
	}

	/*@Override
	public void run(String... args) throws Exception {

		System.out.println("myexcercise.environment: *********** "+environment.getProperty("myexcercise.environment"));
		System.out.println("myexcercise.environment: *********** "+environment.getProperty("myexcercise.environment"));

	}*/
}

