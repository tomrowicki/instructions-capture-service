# Setup for local testing

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

# Automatic testing

Due to time constraints, only two integration test suites are present. InstructionsCaptureApplicationTests makes
sure the service starts without issues.

CanonicalTradeKafkaIntegrationTest tests the whole process, intitiated by publishing a
trade instruction message via the Kafka source topic. It uses Spring Boot profile to properly setup
embedded Kafka connections.