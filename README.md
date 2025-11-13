# Setup for local testing

This microservice assumes the local Kafka bootstrap-server listens at port 9092.
In the case when the port is different, please update the application-local.yml file accordingly.

## Source topic setup:
```bin/kafka-topics.sh --create --topic instructions.inbound --bootstrap-server localhost:9092```

## Outbound topic setup:
```bin/kafka-topics.sh --create --topic instructions.inbound --bootstrap-server localhost:9092```


# Manual testing procedure

Run the service via ```mvn spring-boot:run``` or IDE execution of the InstructionsCaptureApplication class.

## Writing to the source topic:

```bin/kafka-console-producer.sh --topic instructions.inbound --bootstrap-server localhost:9092```

(files to use with the POST requests are located in the "files" directory contained in this repo)

## REST request:

Postman collection, along with the source files available in the "files" directory contained in this repo. 
These files need to be attached to the BODY of the request under the key "file" -
in "Post JSON file" and "Post CSV file" respectively.

# Automatic testing

CanonicalTradeKafkaIntegrationTest tests the whole process, initiated by publishing a
trade instruction message via the Kafka source topic. It uses Spring Boot profile to properly setup
embedded Kafka connections.

TradeTransformerTest contains a suite of unit tests targeted at the Canonical -> Platform model transformation process.