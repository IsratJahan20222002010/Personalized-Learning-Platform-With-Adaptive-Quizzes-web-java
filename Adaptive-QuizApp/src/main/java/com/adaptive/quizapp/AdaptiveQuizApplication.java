package com.adaptive.quizapp;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication 
(exclude = { SecurityAutoConfiguration.class })
public class AdaptiveQuizApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdaptiveQuizApplication.class, args);
	}

}
