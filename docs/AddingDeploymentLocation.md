# Adding Velocloud server configurations as Deployment Location in CP4NA

The deployment location for the target Velocloud server can be added in the CP4NA UI, supplying the following information for infrastructure properties.

###### Example of JSON structure for Deployment Location
```jsonc
{
    "SDWANServerUrl": "http://velocloud_host",    
    "apiContext" : "/portal/rest",
    "apiAuthToken":  "Add API Authorization token here",
    "enterpriseId":  "Add enterpriseId here"
}
```