package com.microservices.dataservice

import com.microservices.shared.SensorReadingMessage
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class SensorReadingListener(private val logger: Logger, private val dataRepository: DataRepository) {
    @KafkaListener(
        topics = ["readings"],
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "group1"
    )
    fun listenForSensorReadings(
        consumerRecord: ConsumerRecord<String, SensorReadingMessage>,
        @Payload message: SensorReadingMessage
    ) {
        logger.info("data-service, ${message.requestId}, start, ${System.currentTimeMillis()}")
        dataRepository.saveData(consumerRecord.key(), message)
        System.out.println(message.content.toPrettyString())
        logger.info("data-service, ${message.requestId}, end, ${System.currentTimeMillis()}")
    }
}