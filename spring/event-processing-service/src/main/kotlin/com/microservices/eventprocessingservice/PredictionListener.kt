package com.microservices.eventprocessingservice

import com.fasterxml.jackson.databind.ObjectMapper
import com.microservices.shared.Prediction
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.util.logging.Logger

@Component
class PredictionListener(
    private val decisionRepository: DecisionRepository,
    private val decisionPublisher: DecisionPublisher,
    private val logger: Logger
) {
    @KafkaListener(
        topics = ["predictions"],
        containerFactory = "predictionKafkaListenerContainerFactory",
        groupId = "group2"
    )
    fun listenForSensorReadings(
        consumerRecord: ConsumerRecord<String, Prediction>,
        @Payload message: Prediction
    ) {
        val objectMapper = ObjectMapper()
        logger.info("event-processing-service, ${message.requestId}, start, ${System.currentTimeMillis()}")
        KieUtils.getKieSession()?.setGlobal("decisionRepository", decisionRepository)
        KieUtils.getKieSession()?.setGlobal("decisionPublisher", decisionPublisher)
        KieUtils.getKieSession()?.getEntryPoint("PredictionStream")?.insert(message)
        KieUtils.getKieSession()?.fireAllRules()
        System.out.println(message.deviceId)
        logger.info("event-processing-service, ${message.requestId}, end, ${System.currentTimeMillis()}")
    }
}