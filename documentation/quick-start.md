# Quick start

## Strucutre

The project has three parts which are also folders:

* spring-amqp-demostration-producer - message producer, sends the messages.
* spring-amqp-demostration-consumer - message consumer, listens and consumes the messages.
* spring-amqp-demostration-commons - model of the messages as POJO, property files and common functionalities.

## Requrirements

For running this example you need following stuff on your host:

* Java version 17
* Docker and docker compose
* IDE for loading the projects and do execution like maven install (`mvn install`) and run spring applications (`mvn spring-boot:run`)

## Preconditional exection

1. Install the project common files - maven installation of the **spring-amqp-demostration-commons** is needed by executing the mvn install command: `mvn install`.
2. Start the RabbitMQ application - save the following code as docker-compose.yml file and start it with docker- Docker compose file:
```
services:
  rabbitmq:
    image: rabbitmq:management
    ports:
      - "5672:5672"
      - "15672:15672"
```

## Start the producer project

1. First decide which case you want to test.
2. Find its cron properties by finding the java class witch sends the data to the queue and then its scheduling rate time. 
   1. For instance `class SimpleNewsSender` has the method `reportCurrentTime` and as annotation `@Scheduled(cron = "${simple.news.sender.cron}")`. 
   2. Searching for the term `simple.news.sender.cron` in the project `spring-amqp-demonstration-commons`. 
   3. The term is found in the file 'simple-news.properties'. 
   4. In that file comment out the line with the cron value '-' and comment in the lime with the cron value '*/5 * * * * *'. You can also change that interval for testing purposes.
3. Execute maven install on the **spring-amqp-demonstration-commons** project.
4. Start the **spring-amqp-demonstration-producer** project in the IDE or with the maven command.

**Note**: All exchanges and queues will be created on the first start.

## Start the consumer project

Start the **spring-amqp-demonstration-consumer** project in the IDE or with the maven command.
The listeners of all queues are started. 

**Note**: Errors can occure when the queue was not previously created.