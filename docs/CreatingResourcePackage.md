# CP4NA Resource Package

## Resource Package Structure

Resource packages for CP4NA should contain (at a minimum) the following content for the velocloud-sdwan-driver to work correctly.

```
helloworld.zip
+--- Definitions
|    +--- lm
|         +--- resource.yaml
+--- Docs
|    +--- Readme.md
+--- Lifecycle
     +--- lifecycle.mf
     +--- velocloud-sdwan
          +--- templates
               +--- EdgeProvision.json             
               +--- DeleteEdge.json 
```
The `resource.yaml` file is the resource descriptor used by CP4NA.

The templates in the `lifecycle/velocloud-sdwan/templates` directory are used to create payload for provisioning and deletion of Edge on the Velocloud server.