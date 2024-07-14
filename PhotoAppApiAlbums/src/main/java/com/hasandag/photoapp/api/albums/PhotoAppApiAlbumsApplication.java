package com.hasandag.photoapp.api.albums;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.core.env.Environment;

import static java.lang.Thread.sleep;

@SpringBootApplication
@EnableDiscoveryClient
public class PhotoAppApiAlbumsApplication implements CommandLineRunner {

	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(PhotoAppApiAlbumsApplication.class, args);
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

				sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (environment.getProperty("log.environments.infinite-loop", Boolean.class, false));
	}

}

