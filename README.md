<a href="https://codecov.io/gh/rudderlabs/rudder-kafka-sink-connector" >
 <img src="https://codecov.io/gh/rudderlabs/rudder-kafka-sink-connector/graph/badge.svg?token=eThyQCVghX"/>
</a>

<p align="center">
  <a href="https://rudderstack.com/">
    <img src="https://user-images.githubusercontent.com/59817155/121357083-1c571300-c94f-11eb-8cc7-ce6df13855c9.png">
  </a>
</p>

<p align="center"><b>The Customer Data Platform for Developers</b></p>

<p align="center">
  <b>
    <a href="https://rudderstack.com">Website</a>
    ·
    <a href="">Documentation</a>
    ·
    <a href="https://rudderstack.com/join-rudderstack-slack-community">Community Slack</a>
  </b>
</p>

---

# Rudder Kafka Sink Connector

This Kafka sink connector is designed to send data from Kafka topics to Rudderstack. It allows you to stream data in real-time from Kafka to Rudderstack, a customer data platform that routes data from your apps, websites, and servers to the destinations where you'll use your data.

## Prerequisites

Before you start, ensure you have the following:

* A running Kafka cluster
* Access to Rudderstack
* Java 21 or higher

## Features
1. Seamless Integration: The connector integrates smoothly with your existing Kafka cluster, minimizing setup time and allowing you to start leveraging your data faster.
1. Real-Time Insights: By enabling real-time data streaming from Kafka to Rudderstack, the connector ensures that your business always has the most up-to-date information, enabling you to make data-driven decisions quickly.

## Getting started
### Installation
#### From Source Code
* Clone the repository: `git clone https://github.com/rudderlabs/rudder-kafka-sink-connector.git`
* Navigate to the project directory: `cd rudder-kafka-sink-connector`
* Build the project: `./gradlew shadowJar`
* The resulting JAR file in the build/libs directory is the Kafka sink connector you can use.

#### From Released Assets
* Go to https://github.com/rudderlabs/rudder-kafka-sink-connector/releases
* Select the latest or desired version.
* Download `rudderstack-kafka-connector-x.x.x.jar` from Assets.
  ![image](https://github.com/rudderlabs/rudder-kafka-sink-connector/assets/33080863/b4eb8024-bd15-4472-89e3-137351fc594a)

Copy the **rudderstack-kafka-connector-x.x.x.jar** to your Kafka libs directory.

### Configuration
#### JSON Messages
Create a rudderstack-kafka-connector-config.properties file with the following details for JSON messages:
```
# You should change the following configration according to your setup
name=rudderstack-json-sink
tasks.max=1
topics=<your-topic>
rudder.data.plane.url=<YOUR_DATA_PLANE_URL>
rudder.write.key=<YOUR_WRITE_KEY>

# DONT CHANGE THE FOLLOWING
connector.class=com.rudderstack.kafka.connect.RudderstackSinkConnector

# Converter settings for key and value
key.converter=org.apache.kafka.connect.json.JsonConverter
value.converter=org.apache.kafka.connect.json.JsonConverter

# Disable schemas for key and value
key.converter.schemas.enable=false
value.converter.schemas.enable=false
```
#### Avro Messages
Create a rudderstack-kafka-connector-config.properties file with the following details for AVRO messages:
```
# You should change the following configration according to your setup
name=rudderstack-avro-sink
tasks.max=1
topics=<your-topic>
rudder.data.plane.url=<YOUR_DATA_PLANE_URL>
rudder.write.key=<YOUR_WRITE_KEY>
key.converter.schema.registry.url=http://localhost:8081
value.converter.schema.registry.url=http://localhost:8081

# DONT CHANGE THE FOLLOWING

connector.class=com.rudderstack.kafka.connect.RudderstackSinkConnector

# Converter settings for key and value
key.converter=io.confluent.connect.avro.AvroConverter
value.converter=io.confluent.connect.avro.AvroConverter
key.converter.schemas.enable=true
value.converter.schemas.enable=true
```
Replace <YOUR_DATA_PLANE_URL> with your Rudderstack data plane URL and <YOUR_WRITE_KEY> with your Rudderstack write key.

### Usage
To start the connector, use any of the following commands:
* `./bin/connect-standalone.sh config/connect-standalone.properties rudderstack-kafka-avro-connector-config.properties`
* If you have multiple message types: `./bin/connect-standalone.sh config/connect-standalone.properties rudderstack-kafka-avro-connector-config.properties rudderstack-kafka-json-connector-config.properties`

## Contribute
### How does it work
The [RudderstackSender](src/main/java/com/rudderstack/kafka/connect/RudderstackSender.java) Java class facilitates the sending of Kafka SinkRecords to [Rudderstack](https://rudderstack.com/) for processing. It initializes RudderstackAnalytics with provided configuration settings and handles JSON conversion using JsonConverter and ObjectMapper. Conversion methods translate SinkRecords into Map representations and compute user IDs and timestamps. The send method iterates through a collection of SinkRecords, constructing TrackMessages for each with details such as topic, timestamp, user ID, and context. These messages are then enqueued in RudderstackAnalytics for further processing, thus providing a streamlined mechanism for sending Kafka data to Rudderstack.
### How to extend it
To extend the functionality of this sink connector, you can begin by forking the GitHub [repository](https://github.com/rudderlabs/rudder-kafka-sink-connector). Once forked, navigate to the RudderstackSender.java file located at src/main/java/com/rudderstack/kafka/connect/ within your forked repository. Update this file according to your specific requirements, such as adding new features, modifying existing functionality, or integrating with other systems.

After making the necessary changes, you'll need to build the connector. Ensure that you have the required build tools and dependencies set up according to the project's documentation. Once everything is configured, execute the build process to generate the updated connector artifact.

With the updated connector built, you can now deploy it within your Kafka setup. Depending on your Kafka deployment method, this may involve installing the connector on your Kafka Connect cluster or distributing the connector JAR to individual Kafka Connect worker nodes. Refer to Kafka's documentation for instructions on deploying and managing connectors.

We would love to see you contribute to RudderStack. Get more information on how to contribute [**here**](CONTRIBUTING.md).

## License

The RudderStack Kafka Sink Connector is released under the [**MIT License**](https://opensource.org/licenses/MIT).
