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
* Java 8 or higher

## Features
1. Seamless Integration: The connector integrates smoothly with your existing Kafka cluster, minimizing setup time and allowing you to start leveraging your data faster.
1. Real-Time Insights: By enabling real-time data streaming from Kafka to Rudderstack, the connector ensures that your business always has the most up-to-date information, enabling you to make data-driven decisions quickly.

## Getting started
### Installation 
#### From Source Code
* Clone the repository: git clone https://github.com/rudderlabs/rudder-kafka-sink-connector.git
* Navigate to the project directory: cd rudder-kafka-sink-connector
* Build the project: gradle shadowJar
#### From Released Assets
* Go to https://github.com/rudderlabs/rudder-kafka-sink-connector/releases
* Select the latest or desired version.
* Download `rudderstack-kafka-connector-x.x.x.jar` from Assets.
  ![image](https://github.com/rudderlabs/rudder-kafka-sink-connector/assets/33080863/b4eb8024-bd15-4472-89e3-137351fc594a)

The resulting JAR file in the build/libs directory is the Kafka sink connector that you can use.

### Configuration
Create a rudderstack-kafka-connector-config.properties file with the following details:
```
name=rudderstack-sink
connector.class=com.rudderstack.kafka.connect.RudderSinkConnector
tasks.max=1
topics=blogpost
rudder.data.plane.url=<YOUR_DATA_PLANE_URL>
rudder.write.key=<YOUR_WRITE_KEY>

# Converter settings for key and value
key.converter=org.apache.kafka.connect.json.JsonConverter
value.converter=org.apache.kafka.connect.json.JsonConverter

# Disable schemas for key and value
key.converter.schemas.enable=false
value.converter.schemas.enable=false
```
Replace <YOUR_DATA_PLANE_URL> with your Rudderstack dataplane URL and <YOUR_WRITE_KEY> with your Rudderstack write key.

### Usage
To start the connector, use the following command:

`./bin/connect-standalone.sh config/connect-standalone.properties rudderstack-kafka-connector-config.properties`

Replace rudderstack-kafka-connector-config.properties with the path to your .properties file.

## Contribute

We would love to see you contribute to RudderStack. Get more information on how to contribute [**here**](CONTRIBUTING.md).

## License

The RudderStack \*\*software name\*\* is released under the [**MIT License**](https://opensource.org/licenses/MIT).
