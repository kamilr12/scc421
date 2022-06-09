package com.microservices.eventprocessingservice

import com.microservices.shared.Decision
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class DecisionPublisher(private val kafkaTemplate: KafkaTemplate<String, Any>) {
    fun publishDecision(organisationId: String, decision: Decision) {
        kafkaTemplate.send(
            "decisions",
            organisationId,
            decision
        )
    }
}