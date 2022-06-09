package com.microservices.eventprocessingservice

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.microservices.shared.SensorReadingMapped
import com.microservices.shared.SensorReadingMessage
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class ReadingListener(
    private val decisionRepository: DecisionRepository,
    private val decisionPublisher: DecisionPublisher,
    private val logger: Logger
) {
    @KafkaListener(
        topics = ["readings"],
        containerFactory = "kafkaListenerContainerFactory",
        groupId = "group2"
    )
    fun listenForSensorReadings(
        consumerRecord: ConsumerRecord<String, SensorReadingMessage>,
        @Payload message: SensorReadingMessage
    ) {
        val objectMapper = ObjectMapper()
        logger.info("event-processing-service, ${message.requestId}, start, ${System.currentTimeMillis()}")
        val map = objectMapper.convertValue(message.content, object : TypeReference<Map<String, Any>>() {})
        val deviceType = message.deviceId.split("-")[0]
        val deviceId = message.deviceId.split("-")[1]
        val readingMapped = SensorReadingMapped(consumerRecord.key(), deviceType, deviceId, map)
        KieUtils.getKieSession()?.setGlobal("decisionRepository", decisionRepository)
        KieUtils.getKieSession()?.setGlobal("decisionPublisher", decisionPublisher)
        KieUtils.getKieSession()?.getEntryPoint("ReadingStream")?.insert(readingMapped)
        KieUtils.getKieSession()?.fireAllRules()
        logger.info("event-processing-service, ${message.requestId}, end, ${System.currentTimeMillis()}")
    }
}