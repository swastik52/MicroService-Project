package com.microservice.configuratonserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfiguratonServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfiguratonServerApplication.class, args);
	}

}
