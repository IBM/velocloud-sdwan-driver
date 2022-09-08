# Configuring Kafka

The NFVO driver expects to find a Kafka endpoint with a pre-existing topic configured or auto-creation of topics enabled. It may be necessary to override the default host.

**Configuring Kafka host**

The Kafka host by default is set to `cp4na-o-events-kafka-bootstrap:9092`. This value can be configured via the Helm values file by setting the following property during the Helm install.

###### Example of values passed to Helm chart during install
```yaml
app:
  config:
    env:
      spring_kafka_bootstrap_servers: cp4na-o-events-kafka-bootstrap:9092
```
Note :-

Depending on the CP4NA versions, kafka host value must be set as follows in values.yaml file of the helm package:

For pre CP4NA v2.3, it must be iaf-system-kafka-bootstrap

For CP4NA v2.3 onwards, it must be cp4na-o-events-kafka-bootstrap