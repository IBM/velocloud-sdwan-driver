package com.ibm.sdwan.velocloud.model.alm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalResourceInstance {

    @Schema(example = "3cb7822b-fc44-46ab-8072-9c65cd778d1f", description = "Unique identifier for the internal resource, as generated by the Resource Manager or underlying infrastructure")
    private String id;
    @Schema(example = "MGMT-NETWORK", description = "Human-readable (non-unique) name given to the internal resource by the Resource Manager or underlying infrastructure")
    private String name;
    @Schema(example = "OpenDaylight::PrivateNetwork", description = "Type identifier for this internal resource")
    private String type;

    public InternalResourceInstance() {
    }

    public InternalResourceInstance(String id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "InternalResourceInstance{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}