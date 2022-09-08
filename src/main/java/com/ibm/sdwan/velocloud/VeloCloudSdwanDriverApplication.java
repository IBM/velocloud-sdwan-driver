package com.ibm.sdwan.velocloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.ibm.sdwan.velocloud.config.SDWDriverProperties;
import com.ibm.sdwan.velocloud.driver.SDWResponseErrorHandler;

@SpringBootApplication
@EnableConfigurationProperties
public class VeloCloudSdwanDriverApplication {
	private final static Logger logger = LoggerFactory.getLogger(VeloCloudSdwanDriverApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(VeloCloudSdwanDriverApplication.class, args);
	}   
	@Autowired
	SDWDriverProperties sDWDriverProperties;
	@Bean
	public RestTemplate getRestTemplate(RestTemplateBuilder restTemplateBuilder, SDWResponseErrorHandler sDWResponseErrorHandler) {
		return restTemplateBuilder
		        .errorHandler(sDWResponseErrorHandler)
				.setConnectTimeout(sDWDriverProperties.getRestConnectTimeout())
				.setReadTimeout(sDWDriverProperties.getRestReadTimeout())
				.build();
	}
	
}
