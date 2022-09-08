package com.ibm.sdwan.velocloud.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibm.sdwan.velocloud.driver.SDWanDriver;
import com.ibm.sdwan.velocloud.model.ExecutionRequest;
import com.ibm.sdwan.velocloud.model.velocloud.EdgeConfiguration;
import com.ibm.sdwan.velocloud.model.velocloud.EdgeModule;
import com.ibm.sdwan.velocloud.model.velocloud.GetEdgeRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import static com.ibm.sdwan.velocloud.utils.Constants.*;

import java.util.*;

@Service
public class OperationsPayloadService {
    private final static Logger logger = LoggerFactory.getLogger(OperationsPayloadService.class);
    private final ObjectMapper mapper;
    private final SDWanDriver sdwanDriver;
    @Autowired
    public OperationsPayloadService(SDWanDriver sdwanDriver, ObjectMapper mapper){
        this.sdwanDriver = sdwanDriver;
        this.mapper = mapper;
    }

    public String buildPayloadForAddVlan(ExecutionRequest executionRequest, String driverRequestId) throws MessageConversionException {
        EdgeModule edgeSpecificProfileDeviceSettings = getEdgeSpecificProfileDeviceSettings(executionRequest, driverRequestId);
        int moduleId = edgeSpecificProfileDeviceSettings.getId();
        logger.info("moduleId : "+ moduleId);

        // Get the required properties from resourceProperties of ExecutionRequest object.
        Map<String, Object> resourceProperties = executionRequest.getProperties();
        String cidrIp = (String) resourceProperties.get("vce_private_subnet_ip");
        if(!StringUtils.hasLength(cidrIp)){
            throw new MissingPropertyException("vce_private_subnet_ip cannot be empty, it is a mandatory property");
        }

        String cidrBlock = (String) resourceProperties.get("private_subnet_cidr");
        if(!StringUtils.hasLength(cidrBlock)){
            throw new MissingPropertyException("private_subnet_cidr cannot be empty, it is a mandatory property");
        }

        String[] cidr = cidrBlock.split("/");
        int cidrPrefix = Integer.parseInt(cidr[1]);
        Boolean advertise = (Boolean) resourceProperties.get("advertise");
        if(advertise == null) {
            advertise = true;
        }
        Boolean dhcpEnabled = (Boolean) resourceProperties.get("dhcpEnabled");
        if(dhcpEnabled == null) {
            dhcpEnabled = false;
        }
        String netmask = cidrToNetmask(cidrPrefix);
        Map<String, Object> resultMap = convertEdgeModuleToMap(edgeSpecificProfileDeviceSettings);
        Map<String, Object> map = (Map)((ArrayList) ((Map)resultMap.get("lan")).get("networks")).get(0);
        map.put("cidrIp", cidrIp);
        map.put("advertise", advertise);
        map.put("cidrPrefix", cidrPrefix);
        map.put("netmask", netmask);
        ((Map)map.get("dhcp")).put("enabled", dhcpEnabled);
        JsonNode jsonNode = this.mapper.valueToTree(resultMap);
        logger.info(jsonNode.get("lan").get("networks").get(0).get("cidrIp").textValue());
        logger.info(jsonNode.get("lan").get("networks").get(0).get("advertise").asText());
        logger.info(jsonNode.get("lan").get("networks").get(0).get("cidrPrefix").textValue());
        logger.info(jsonNode.get("lan").get("networks").get(0).get("netmask").textValue());
        logger.info(jsonNode.get("lan").get("networks").get(0).get("dhcp").get("enabled").asText());
        return buildResponse(resultMap, moduleId);
    }

    public String buildPayloadForVceStaticIP(ExecutionRequest executionRequest, String driverRequestId) throws MessageConversionException {
        EdgeModule edgeSpecificProfileDeviceSettings = getEdgeSpecificProfileDeviceSettings(executionRequest,driverRequestId);
        int moduleId = edgeSpecificProfileDeviceSettings.getId();
        logger.info("moduleId : "+ moduleId);

        // Get the required properties from resourceProperties of ExecutionRequest object.
        Map<String, Object> resourceProperties = executionRequest.getProperties();
        String cidrBlock = (String)resourceProperties.get("static_route_cidr");
        if(!StringUtils.hasLength(cidrBlock)){
            throw new MissingPropertyException("static_route_cidr cannot be empty, it is a mandatory property");
        }
        String[] cidr = cidrBlock.split("/");
        String destination = cidr[0];
        int staticcidrPrefix = Integer.parseInt(cidr[1]);
        String gateway = (String) resourceProperties.get("gateway_ip");
        if(!StringUtils.hasLength(gateway)){
            throw new MissingPropertyException("gateway_ip cannot be empty, it is a mandatory property");
        }

        String netmask = cidrToNetmask(staticcidrPrefix);
        Map<String, Object> staticIp = new HashMap<>();
        staticIp.put("destination", destination);
        staticIp.put("netmask", netmask);
        staticIp.put("sourceIp", null);
        staticIp.put("gateway", gateway);
        staticIp.put("cost", 0);
        staticIp.put("preferred", true);
        staticIp.put("description", "");
        staticIp.put("cidrPrefix", String.valueOf(staticcidrPrefix));
        staticIp.put("wanInterface", "");
        staticIp.put("subinterfaceId", -1);
        staticIp.put("icmpProbeLogicalId", null);
        staticIp.put("vlanId", null);
        staticIp.put("advertise", true);

        Map<String, Object> resultMap = convertEdgeModuleToMap(edgeSpecificProfileDeviceSettings);
        Map<String, Object> map = (Map)((Map) ((ArrayList)resultMap.get("segments")).get(0)).get("routes");
        ArrayList list= (ArrayList) map.get("static");
        list.add(staticIp);
        JsonNode jsonNode = this.mapper.valueToTree(resultMap);
        logger.info(jsonNode.get("segments").get(0).get("routes").get("static").textValue());
        return buildResponse(resultMap, moduleId);
    }


    public String buildPayloadForDeleteVceStaticIP(ExecutionRequest executionRequest, String driverRequestId) throws MessageConversionException {
        EdgeModule edgeSpecificProfileDeviceSettings = getEdgeSpecificProfileDeviceSettings(executionRequest, driverRequestId);
        int moduleId = edgeSpecificProfileDeviceSettings.getId();
        logger.info("moduleId : "+ moduleId);

        Map<String, Object> resultMap = convertEdgeModuleToMap(edgeSpecificProfileDeviceSettings);
        Map<String, Object> map = (Map)((Map) ((ArrayList)resultMap.get("segments")).get(0)).get("routes");
        ArrayList staticIpList = (ArrayList) map.get("static");

        // Get the required properties from resourceProperties of ExecutionRequest object.
        Map<String, Object> resourceProperties = executionRequest.getProperties();
        String staticRouteCidr = (String)resourceProperties.get("static_route_cidr");
        if(!StringUtils.hasLength(staticRouteCidr)){
            throw new MissingPropertyException("static_route_cidr cannot be empty, it is a mandatory property");
        }
        String destination = staticRouteCidr.split("/")[0];
        // Find the destination IP to delete
        for(int i=0; i < staticIpList.size(); i++) {
            Map<String, Object> ipMap = (Map<String, Object>)staticIpList.get(i);
            if( ipMap.get("destination").equals(destination)){
                staticIpList.remove(i);
                break;
            }
        }
        map.put("static", staticIpList);
        JsonNode jsonNode = this.mapper.valueToTree(resultMap);
        logger.info(jsonNode.get("segments").get(0).get("routes").get("static").textValue());
        return buildResponse(resultMap, moduleId);
    }

    private String  cidrToNetmask(int cidr) {
        long mask = (long)(0xffffffff >> (32 - cidr)) << (32 - cidr);
        return ((0xff000000L & mask) >> 24) +
                "." +
                ((0x00ff0000L & mask) >> 16) +
                "." +
                ((0x0000ff00L & mask) >> 8) +
                "." +
                (0x000000ffL & mask);
    }

    public String buildPayloadForGetEdgeConfig(ExecutionRequest executionRequest)  {
        String edgeIdStr = (String) executionRequest.getProperties().get("id");
        if (!StringUtils.hasLength(edgeIdStr)) {
            throw new MissingPropertyException("id cannot be empty, it is a mandatory property");
        }
        return "{" + "\"edgeId\":" + Long.parseLong(edgeIdStr) + "}";
    }
    private EdgeModule getEdgeSpecificProfileDeviceSettings(ExecutionRequest executionRequest, String driverRequestId) throws MessageConversionException {
        String payloadForEdgeConfig = buildPayloadForGetEdgeConfig(executionRequest);
        String getEdgeconfig = this.sdwanDriver.getEdgeConfigurationStack(executionRequest.getDeploymentLocation().getProperties(), payloadForEdgeConfig, driverRequestId);
        EdgeConfiguration[] edgeConfigurations;
        try {
            edgeConfigurations = new ObjectMapper().readValue(getEdgeconfig, EdgeConfiguration[].class);
        } catch (JsonProcessingException e) {
            throw new MessageConversionException("Could not convert the response payload to EdgeConfigurations Java class model");
        }
        EdgeConfiguration edgeSpecificProfile = edgeConfigurations[0];
        EdgeModule[] edgeModules = edgeSpecificProfile.getModules();
        Optional<EdgeModule> optionalEdgeModule = Arrays.stream(edgeModules).filter(e -> e.getName().equals("deviceSettings")).findFirst();
        if(!optionalEdgeModule.isPresent()) {
            throw new MessageConversionException("deviceSettings module not found");
        }
        return optionalEdgeModule.get();
    }
    private Map<String, Object> convertEdgeModuleToMap(EdgeModule edgeSpecificProfileDeviceSettings) {
        ObjectNode objectNode = edgeSpecificProfileDeviceSettings.getData();
        ObjectNode edgeSpecificProfileDeviceSettingsData = objectNode.deepCopy();
        return this.mapper.convertValue(edgeSpecificProfileDeviceSettingsData, Map.class);
    }

    private String buildResponse(Map<String, Object> resultMap, int moduleId) throws MessageConversionException {
        Map<String, Object> testResponse = new HashMap<>();
        testResponse.put("data", resultMap);
        Map<String, Object> params3 = new HashMap<>();
        params3.put("id", moduleId);
        params3.put("returnData", true);
        params3.put("_update",  testResponse);
        params3.put("name","deviceSettings");
        try {
            return this.mapper.writeValueAsString(params3);
        } catch (JsonProcessingException e) {
            throw new MessageConversionException("could not convert Map to Json string");
        }
    }

    public Boolean isEdgeAvailable(ExecutionRequest executionRequest) throws MessageConversionException {
        String getEdgeRequestPayload;
        int edgeId;
        try {
            if (executionRequest.getProperties().get(EDGE_ID) instanceof String) {
                if (!StringUtils.hasLength((String) executionRequest.getProperties().get(EDGE_ID))) {
                    throw new MissingPropertyException(" id property of an edge is mandatory and it is missing");
                }
                edgeId = (Integer.parseInt((String) executionRequest.getProperties().get(EDGE_ID)));
            } else {
                if (executionRequest.getProperties().get(EDGE_ID) == null) {
                    throw new MissingPropertyException(" id property of an edge is mandatory and it is missing");
                }
                edgeId = ((int) executionRequest.getProperties().get(EDGE_ID));
            }     
            getEdgeRequestPayload = buildPayloadForGetEdge(
                    executionRequest.getDeploymentLocation().getProperties(), edgeId,
                    (String) executionRequest.getProperties().get(ACTIVATION_KEY));
            return this.sdwanDriver.getEdgeExecuteAPI(
                executionRequest.getDeploymentLocation().getProperties(), getEdgeRequestPayload);
            
        } catch (MessageConversionException e) {
            throw new MessageConversionException("Error while checking edge existence ");
        }
         
    }

    public String buildPayloadForGetEdge(Map<String, Object> deploymentLocationProperties, int id, String activationKey)
            throws MessageConversionException {
        GetEdgeRequest getEdgeRequest = new GetEdgeRequest();
        if (deploymentLocationProperties.get("enterpriseId") instanceof String) {
            getEdgeRequest.setEnterpriseId(Integer.parseInt((String) deploymentLocationProperties.get("enterpriseId")));
        } else {
            getEdgeRequest.setEnterpriseId((int) deploymentLocationProperties.get("enterpriseId"));
        }
        getEdgeRequest.setId(id);
        getEdgeRequest.setActivationKey(activationKey);
        logger.debug("Payload for getEdge API call " + getEdgeRequest.toString());
        try {
            return mapper.writeValueAsString(getEdgeRequest);
        } catch (JsonProcessingException jsonProcessingException) {
            throw new MessageConversionException("could not convert Map to Json string");
        }
    }
}
