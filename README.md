# Kafka Notification

A Spring Boot application integrated with Apache Kafka. The project
contains a REST controller, a Kafka producer, and a Kafka consumer.

## Tech Stack

-   Java 21
-   Spring Boot
-   Spring Kafka
-   Apache Kafka 4.1.0
-   Maven
-   Docker Compose

## Project Structure

``` text
kafka-notification
│
├── src
│   └── main
│       ├── java
│       │   └── com/lovish/kafka_notification
│       │       ├── Controller
│       │       │   └── NotificationController.java
│       │       │
│       │       ├── Producer
│       │       │   └── NotificationProducer.java
│       │       │
│       │       ├── Service
│       │       │   └── NotificationConsumer.java
│       │       │
│       │       └── KafkaNotificationApplication.java
│       │
│       └── resources
│           └── application.properties
│
├── docker-compose.yml
├── pom.xml
├── mvnw
└── mvnw.cmd
```

## 1. Start Kafka

Make sure Docker Desktop is running.

``` bash
docker compose up -d
```

Check running containers:

``` bash
docker ps
```

Check Kafka logs:

``` bash
docker logs kafka
```

## 2. Create the Kafka Topic

Create the `notifications` topic with two partitions:

``` bash
docker exec -it kafka /opt/kafka/bin/kafka-topics.sh --create --topic notifications --partitions 2 --replication-factor 1 --bootstrap-server localhost:9092
```

Describe the topic:

``` bash
docker exec -it kafka /opt/kafka/bin/kafka-topics.sh --describe --topic notifications --bootstrap-server localhost:9092
```

List topics:

``` bash
docker exec -it kafka /opt/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

Run the create command only once. If the topic already exists, use the
describe command.

## 3. Application Configuration

`src/main/resources/application.properties`

``` properties
spring.application.name=kafka-notification
spring.kafka.bootstrap-servers=localhost:9092
```

## 4. Run the Spring Boot Application

On Windows CMD:

``` bash
mvnw.cmd spring-boot:run
```

Or, if Maven is installed:

``` bash
mvn spring-boot:run
```

## 5. Kafka Consumer

`Service/NotificationConsumer.java`

``` java
package com.lovish.kafka_notification.Service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    @KafkaListener(
        topics = "notifications",
        groupId = "notification-group"
    )
    public void consumeNotification(String message) {
        System.out.println("Recieved: " + message);
    }
}
```

The consumer listens to the `notifications` topic using the
`notification-group` consumer group. Spring Kafka calls
`consumeNotification()` when a message is received.

## 6. Run Multiple Consumers from the Terminal

Each command below starts a Kafka **console consumer**. Open each
command in a separate Windows CMD window.

### Start Consumer 1 in `group-1`

``` bash
docker exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh --topic notifications --bootstrap-server localhost:9092 --group group-1 --property print.partition=true
```

### Start Consumer 2 in the same `group-1`

Run the same command in another CMD window:

``` bash
docker exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh --topic notifications --bootstrap-server localhost:9092 --group group-1 --property print.partition=true
```

Both consumers are members of `group-1`. Kafka distributes the topic
partitions between active consumers in that group.

### Start Consumer 1 in a different group, `group-2`

``` bash
docker exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh --topic notifications --bootstrap-server localhost:9092 --group group-2 --property print.partition=true
```

### Start Consumer 2 in `group-2` (optional)

``` bash
docker exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh --topic notifications --bootstrap-server localhost:9092 --group group-2 --property print.partition=true
```

Different groups consume independently and maintain their own offsets.

Stop a console consumer with `Ctrl + C` in its terminal.

## 7. Verify Consumer Groups

List consumer groups:

``` bash
docker exec -it kafka /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list
```

Describe `group-1`:

``` bash
docker exec -it kafka /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group group-1
```

Describe `group-2`:

``` bash
docker exec -it kafka /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group group-2
```

Describe the Spring Boot consumer group:

``` bash
docker exec -it kafka /opt/kafka/bin/kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group notification-group
```

Kafka registers a consumer group when a consumer connects with that
group ID. No separate group-creation command is required.

## 8. Observe Consumer Group Rebalancing

1.  Start two console consumers with the same group ID, such as
    `group-1`.
2.  Check their partition assignments using the `--describe` command.
3.  Stop one consumer with `Ctrl + C`.
4.  Wait for Kafka to rebalance the group.
5.  Run the `--describe` command again.

With two partitions and two consumers in the same group, Kafka can
assign one partition to each consumer. If one consumer stops, the
remaining consumer can be assigned both partitions.

A partition is assigned to at most one consumer within the same group at
a time. Different groups can independently consume the same topic
partitions.

## 9. Where Do Messages Come From?

The commands in this README start console consumers, not a console
producer. The console consumers only read messages from Kafka.

Messages must be published to the `notifications` topic by the
application's producer or another producer connected to the broker. The
project also contains the `NotificationProducer` class for the
application-side producer flow.

## 10. Useful Docker Commands

Stop Kafka:

``` bash
docker compose stop
```

Start Kafka again:

``` bash
docker compose start
```

Stop and remove the Compose container and network:

``` bash
docker compose down
```

## Key Kafka Concepts

-   **Broker:** The Kafka server that stores and serves messages.
-   **Topic:** A named stream where messages are stored. This project
    uses `notifications`.
-   **Partition:** A topic is divided into partitions to support
    parallel consumption.
-   **Producer:** Publishes messages to a Kafka topic.
-   **Consumer:** Reads messages from a Kafka topic.
-   **Consumer Group:** A group of consumers that coordinate reading
    topic partitions.
-   **Offset:** Tracks a consumer group's position in a partition.
-   **Rebalancing:** Kafka redistributes partition assignments when
    consumers in a group join or leave.
