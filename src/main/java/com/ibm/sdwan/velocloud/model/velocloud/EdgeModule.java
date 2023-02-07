package com.ibm.sdwan.velocloud.model.velocloud;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EdgeModule {
    private String created;
    private String effective;
    private String modified;
    private int id;
    private String name;
    private String type;
    private String description;
    private int configurationId;
    private ObjectNode data;
    private String schemaVersion;
    private String version;
    private Map<String,Object> metadata;
    private Map<String,Object> refs;


    private JsonNode previousData;
    private String previousCreated;
    private JsonNode draftData;
    private String draftCreated;
    private String draftComment;
    private JsonNode jsonData;

}
