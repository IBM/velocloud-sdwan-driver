package com.ibm.sdwan.velocloud.driver;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
public class SdwanResponseErrorHandlerTest {
	
	@Mock
	private ObjectMapper mapper;
	SdwanResponseErrorHandler errorHandler;
	
	@BeforeEach
	public void setUp() {
		errorHandler = new SDWResponseErrorHandler(mapper);
	}
	
	@Test
	public void handleErrorTest() throws IOException {
		ClientHttpResponse clientHttpResponse = new MockClientHttpResponse("error".getBytes(),HttpStatus.BAD_REQUEST);
		assertThrows(SdwanResponseException.class, ()->{
			errorHandler.handleError(clientHttpResponse);
		});
		
	}
	
	@Test
	public void handleErrorTestUnknownException() throws IOException {
		assertThrows(SdwanResponseException.class, ()->{
			errorHandler.handleError(null);
		});
		
	}
	

}
