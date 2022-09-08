# velocloud-sdwan-driver for CP4NA

## Introduction

velocloud-sdwan-driver implements Create and Delete lifecycles of CP4NA for creating and deleting an edge on the velocloud orchestrator. 
The driver also supports following configuration operations on Velocloud edge.
1. addvlan (Adding Vlan configurations) 
2. addVceStaticIP (Adding VceStaticIP configurations)
3. deleteVceStaticRoutingForCIDR (Deleting VceStaticIP configurations)

Following properties are expected as part of the resource properties for the above operations

```
addvlan:
  description: Add a VLAN for the VCE
  properties:
    vce_private_subnet_ip:
      type: string
    private_subnet_cidr:
      type: string

addVceStaticIP:
  description: Add a static route to the VCE for the identified CIDR
  properties:
    static_route_cidr:
      type: string
    gateway_ip:
      type: string

deleteVceStaticRoutingForCIDR:
  description: If a static route exists for the identified CIDR, delete it.
  properties:
    static_route_cidr:
      type: string
```

## Connecting to a Velocloud orchestrator server:

For information on how to configure the connection details for a Velocloud orchestrator server within CP4NA, please see the guide on Adding a Deployment Location, please see the [Adding a Deployment Location](docs/AddingDeploymentLocation.md)

## Creating a Resource Package

For information on how to create a resource package to load into CP4NA, please see the guide on [Creating a Resource Package](docs/CreatingResourcePackage.md)

## Installation of the driver

For information on how to install and configure the driver for use with CP4NA, please see the [Installation Guide](docs/Installation.md)