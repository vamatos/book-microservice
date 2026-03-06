package br.com.vandre;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

//DEPRECATED substituido por kubernetes
@SpringBootApplication
@EnableEurekaServer
public class NamingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NamingServiceApplication.class, args);
	}

}
