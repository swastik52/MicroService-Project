package com.microservice.notificationservices;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@SpringBootApplication
@EnableEurekaClient
public class NotificationServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServicesApplication.class, args);
	}

	@Bean
	public Consumer<Message<String>> notificationEventSupplier(){
		return message->{
			new EmailSender().sendEmail(message.getPayload());
		};
	}
}
