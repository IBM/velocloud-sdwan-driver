package com.ibm.sdwan.velocloud.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.ibm.sdwan.velocloud.model.ExecutionRequest;
import com.ibm.sdwan.velocloud.model.ExecutionRequestPropertyValue;
import com.ibm.sdwan.velocloud.model.GenericExecutionRequestPropertyValue;
import com.ibm.sdwan.velocloud.model.ResourceManagerDeploymentLocation;
import com.ibm.sdwan.velocloud.service.MessageConversionException;
@SpringBootTest
public class JinJavaMessageConversionServiceImplTest {

    private static final String TEMPLATE_PATH = "templates-test";

    @Test
    public void testGenerateMessageFromRequestForCreate() throws MessageConversionException, IOException {
        JinJavaMessageConversionServiceImpl jinJavaMessageConversionService = new JinJavaMessageConversionServiceImpl();
        ExecutionRequest executionRequest = new ExecutionRequest();
        executionRequest.setLifecycleName("Create");
        Map<String, ExecutionRequestPropertyValue> resourceProperties = new HashMap<>();

        resourceProperties.put("enterpriseId", new GenericExecutionRequestPropertyValue("enterpriseId"));
        resourceProperties.put("configurationId", new GenericExecutionRequestPropertyValue("configurationId"));
        resourceProperties.put("name", new GenericExecutionRequestPropertyValue("name"));
        resourceProperties.put("serialNumber", new GenericExecutionRequestPropertyValue("serialNumber"));
        resourceProperties.put("modelNumber", new GenericExecutionRequestPropertyValue("modelNumber"));
        resourceProperties.put("description", new GenericExecutionRequestPropertyValue("description"));
        resourceProperties.put("siteCity", new GenericExecutionRequestPropertyValue("siteCity"));
        resourceProperties.put("siteContactEmail", new GenericExecutionRequestPropertyValue("siteContactEmail"));
        resourceProperties.put("siteContactMobile", new GenericExecutionRequestPropertyValue("siteContactMobile"));
        resourceProperties.put("siteContactName", new GenericExecutionRequestPropertyValue("siteContactName"));
        resourceProperties.put("siteContactPhone", new GenericExecutionRequestPropertyValue("siteContactPhone"));
        resourceProperties.put("siteCountry", new GenericExecutionRequestPropertyValue("siteCountry"));
        resourceProperties.put("siteLat", new GenericExecutionRequestPropertyValue("siteLat"));
        resourceProperties.put("siteId", new GenericExecutionRequestPropertyValue("001"));
        resourceProperties.put("siteCreated", new GenericExecutionRequestPropertyValue("create"));
        resourceProperties.put("siteLogicalId", new GenericExecutionRequestPropertyValue("logicalId"));
        resourceProperties.put("siteTimezone", new GenericExecutionRequestPropertyValue("ETC"));
        resourceProperties.put("siteLon", new GenericExecutionRequestPropertyValue("siteLon"));
        resourceProperties.put("siteLocale", new GenericExecutionRequestPropertyValue("siteLocale"));
        resourceProperties.put("siteShippingSameAsLocation", new GenericExecutionRequestPropertyValue("siteLocaleSameAsLocation"));
        resourceProperties.put("siteShippingContactName", new GenericExecutionRequestPropertyValue("siteShippingSameAsLocation"));
        resourceProperties.put("siteShippingAddress", new GenericExecutionRequestPropertyValue("siteShippingAddress"));
        resourceProperties.put("siteShippingAddres2", new GenericExecutionRequestPropertyValue("siteShippingAddress2"));
        resourceProperties.put("siteShippingCity", new GenericExecutionRequestPropertyValue("siteShippingCity"));
        resourceProperties.put("siteshippingState", new GenericExecutionRequestPropertyValue("siteshippingState"));
        resourceProperties.put("siteShippingCountry", new GenericExecutionRequestPropertyValue("siteShippingCountry"));
        resourceProperties.put("siteName", new GenericExecutionRequestPropertyValue("siteName"));
        resourceProperties.put("siteShippingPostalCode", new GenericExecutionRequestPropertyValue("siteShippingPostalCode"));
        resourceProperties.put("sitePostalCode", new GenericExecutionRequestPropertyValue("sitePostalCode"));
        resourceProperties.put("siteModified", new GenericExecutionRequestPropertyValue("siteModified"));
        resourceProperties.put("analyticsMode", new GenericExecutionRequestPropertyValue("analyticsMode"));
        resourceProperties.put("generateToken", new GenericExecutionRequestPropertyValue("generateToken"));
        resourceProperties.put("siteState", new GenericExecutionRequestPropertyValue("siteState"));
        resourceProperties.put("siteStreetAddress", new GenericExecutionRequestPropertyValue("siteStreetAddress"));
        resourceProperties.put("siteStreetAddress2", new GenericExecutionRequestPropertyValue("siteStreetAddress2"));
        resourceProperties.put("hasEnabled", new GenericExecutionRequestPropertyValue("hasEnabled"));
        resourceProperties.put("generateCertificate", new GenericExecutionRequestPropertyValue("generateCertificate"));
        resourceProperties.put("subjectCN", new GenericExecutionRequestPropertyValue("subjectCN"));
        resourceProperties.put("subjectO", new GenericExecutionRequestPropertyValue("subjectO"));
        resourceProperties.put("subjectOU", new GenericExecutionRequestPropertyValue("subjectOU"));
        resourceProperties.put("challengePassword", new GenericExecutionRequestPropertyValue("challengePassword"));
        resourceProperties.put("privateKeyPassword", new GenericExecutionRequestPropertyValue("privateKeyPassword"));
        resourceProperties.put("edgeLicenseId", new GenericExecutionRequestPropertyValue("edgeLicenseId"));
        resourceProperties.put("customInfo", new GenericExecutionRequestPropertyValue("customInfo"));

        executionRequest.setResourceProperties(resourceProperties);
        ResourceManagerDeploymentLocation deploymentLocation = new ResourceManagerDeploymentLocation();
        deploymentLocation.getProperties().put("enterpriseId", "enterpriseId");
        executionRequest.setDeploymentLocation(deploymentLocation);

        String parsedTemplate = jinJavaMessageConversionService.generateMessageFromRequest("EdgeProvision", executionRequest);

        String expectedTemplate = "";
        try (InputStream inputStream = JinJavaMessageConversionServiceImplTest.class.getResourceAsStream("/" + TEMPLATE_PATH +  "/EdgeProvision.json" )) {
            if (inputStream != null) {
                expectedTemplate = IOUtils.toString(inputStream, Charset.defaultCharset());
            }
        } catch (IOException e) {
            throw e;
        }

        assertEquals(expectedTemplate, parsedTemplate);

    }


    @Test
    @DisplayName("Testing positive scenario for Extract Properties from Message For Create")
    public void testExtractPropertiesFromMessageForCreate(){
        JinJavaMessageConversionServiceImpl jinJavaMessageConversionService = new JinJavaMessageConversionServiceImpl();
        ExecutionRequest executionRequest = new ExecutionRequest();
        Map<String, Object> expectedOutputs = new HashMap<>();
        expectedOutputs.put("id",0);
        expectedOutputs.put("activationKey","myActivationKey");
        Map<String, Object> internalMap = new HashMap<>();
        internalMap.put("certificate","myCertficate");
        internalMap.put("ca-certificate","myCertficate");
        internalMap.put("privateKey","myPrivateKey");
        internalMap.put("privateKeyPassword","myPrivateKeyPassword");
        internalMap.put("csr","myCsr");
        expectedOutputs.put("generatedCertificate",internalMap); 
        String jsonMessage="{\n" +
                "  \"id\": 0,\n" +
                "  \"activationKey\": \"myActivationKey\",\n" +
                "  \"generatedCertificate\": {\n" +
                "    \"certificate\": \"myCertficate\",\n" +
                "    \"ca-certificate\": \"myCaCertificate\",\n" +
                "    \"privateKey\": \"myPrivateKey\",\n" +
                "    \"privateKeyPassword\": \"myPrivateKeyPassword\",\n" +
                "    \"csr\": \"myCsr\"\n" +
                "  }\n" +
                "}";
        Map<String, Object>  receivedOutputs = jinJavaMessageConversionService.extractPropertiesFromMessage(jsonMessage);
        assertEquals(expectedOutputs.get("id"), receivedOutputs.get("id"));
        assertEquals(expectedOutputs.get("activationKey"), receivedOutputs.get("activationKey"));

    }



    @Test
    @DisplayName("Testing exception scenario for Create NotFound")
    public void testErrorScenario() {
        JinJavaMessageConversionServiceImpl jinJavaMessageConversionService = new JinJavaMessageConversionServiceImpl();
        ExecutionRequest executionRequest = new ExecutionRequest();
        executionRequest.setLifecycleName("Create");
        Map<String, ExecutionRequestPropertyValue> resourceProperties = new HashMap<>();

        resourceProperties.put("enterpriseId", new GenericExecutionRequestPropertyValue("enterpriseId"));
        resourceProperties.put("configurationId",new GenericExecutionRequestPropertyValue("configurationId"));
        executionRequest.setResourceProperties(resourceProperties);

        assertThrows(IllegalArgumentException.class, ()->{
            jinJavaMessageConversionService.generateMessageFromRequest("Create-NotFound", executionRequest);
        });
    }

}