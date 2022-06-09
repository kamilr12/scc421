package com.microservices.dataentryservice

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.microservices.shared.SensorReadingMessage
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*
import java.util.logging.Logger

@RestController
@RequestMapping("{organisationId}/readings")
class SensorDataController(
    val kafkaTemplate: KafkaTemplate<String, Any>,
    private val schemaManager: SchemaManager,
    private val objectMapper: ObjectMapper,
    private val logger: Logger
) {


    @PostMapping
    fun submitReading(
        @PathVariable("organisationId") organisationId: String,
        @RequestHeader("schemaId") schemaId: String,
        @RequestBody json: JsonNode
    ): Mono<Void> {
        val requestId = UUID.randomUUID().toString()
        logger.info("data-entry-service, $requestId, start, ${System.currentTimeMillis()}")

        return schemaManager.getSchemaById(requestId, organisationId, schemaId).doOnNext {
            val validator = SchemaValidator(it)
            val errors = validator.validate(json, schemaId)

            if (errors.find { error -> error != "" } == null) {
                kafkaTemplate.send(
                    "readings",
                    organisationId,
                    SensorReadingMessage(requestId, json.get("deviceId").textValue(), schemaId, json)
                )
            }

        }.flatMap {
            logger.info("data-entry-service, $requestId, end, ${System.currentTimeMillis()}")
            Mono.empty()
        }
    }
}