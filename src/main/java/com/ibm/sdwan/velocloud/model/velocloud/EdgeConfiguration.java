package com.ibm.sdwan.velocloud.model.velocloud;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EdgeConfiguration {
    private String configurationType;
    private String bastionState;
    private String created;
    private String description;
    private int edgeCount;
    private String effective;
    private int id;
    private String logicalId;
    private String modified;
    private EdgeModule modules[];
    private String name;
    private String schemaVersion;
    private String version;
    private int isStaging;
}
