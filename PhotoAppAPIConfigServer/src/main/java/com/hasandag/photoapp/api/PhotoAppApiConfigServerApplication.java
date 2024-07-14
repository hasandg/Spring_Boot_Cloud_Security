package com.hasandag.photoapp.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.core.env.Environment;

import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.Thread.sleep;

@SpringBootApplication
@EnableConfigServer
public class PhotoAppApiConfigServerApplication implements CommandLineRunner {
//public class PhotoAppApiConfigServerApplication {

	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(PhotoAppApiConfigServerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var myThread = new Thread(this::logEnvironmets);
		myThread.start();
	}

	private void logEnvironmets() {
		do {
			try {
				System.out.println("myexcercise.environment: *********** "+environment.getProperty("myexcercise.environment"));
				System.out.println("spring.security.user.name: *********** "+environment.getProperty("spring.security.user.name"));
				System.out.println("spring.security.user.password: *********** "+environment.getProperty("spring.security.user.password"));
				System.out.println("GIT_TOKEN from Env Variables: *********** "+environment.getProperty("GIT_TOKEN"));

				sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (environment.getProperty("log.environments.infinite-loop", Boolean.class, false));
	}

}
