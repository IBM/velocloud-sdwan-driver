package com.ibm.sdwan.velocloud.driver.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.ibm.sdwan.velocloud.model.ExecutionAcceptedResponse;
import com.ibm.sdwan.velocloud.model.ExecutionRequest;
import com.ibm.sdwan.velocloud.service.LifecycleManagementService;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ "test" })
public class LifecycleControllerTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private LifecycleManagementService lifecycleManagementService;

    @Test
    public void testExecuteLifecycle() throws Exception {
        final ExecutionRequest executionRequest = new ExecutionRequest(); 
        executionRequest.setLifecycleName("Install");

        when(lifecycleManagementService.executeLifecycle(any(), anyString())).thenReturn(new ExecutionAcceptedResponse(UUID.randomUUID().toString()));
        HttpHeaders headers = new HttpHeaders();
        headers.set("TenantId", "2");      
 
        HttpEntity<ExecutionRequest> request = new HttpEntity<>(executionRequest, headers);
        final ResponseEntity<ExecutionAcceptedResponse> responseEntity = 
        		testRestTemplate.postForEntity("/api/driver/lifecycle/execute", request, ExecutionAcceptedResponse.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(responseEntity.getHeaders().getContentType()).isNotNull();
        assertThat(responseEntity.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON)).isTrue();
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getRequestId()).isNotEmpty();
    }  
    
    @Test
    public void testExecuteLifecyclewithTenantId1() throws Exception {
        final ExecutionRequest executionRequest = new ExecutionRequest(); 
        executionRequest.setLifecycleName("Install");

        when(lifecycleManagementService.executeLifecycle(any(), anyString())).thenReturn(new ExecutionAcceptedResponse(UUID.randomUUID().toString()));
        HttpHeaders headers = new HttpHeaders();
        headers.set("TenantId", "1");      
 
        HttpEntity<ExecutionRequest> request = new HttpEntity<>(executionRequest, headers);
        final ResponseEntity<ExecutionAcceptedResponse> responseEntity = 
        		testRestTemplate.postForEntity("/api/driver/lifecycle/execute", request, ExecutionAcceptedResponse.class);
        assertThat(responseEntity).isNotNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(responseEntity.getHeaders().getContentType()).isNotNull();
        assertThat(responseEntity.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON)).isTrue();
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getRequestId()).isNotEmpty();
    } 

}