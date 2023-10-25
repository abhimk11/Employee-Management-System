package com.notificationservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
@EnableEurekaClient
public class NotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}
	@KafkaListener(topics = "notificationTopic")
	public void handleNotification(EmployeeEvent employeeEvent){
		//sending out log on console
		log.info("Received Notification for - {} {} {}",employeeEvent.getId(),
				employeeEvent.getFirstName(),
				employeeEvent.getLastName());
	}
}
