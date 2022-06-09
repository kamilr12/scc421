package com.microservices.dataentryservice

import com.microservices.shared.SchemaObject
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import java.util.logging.Logger

@Component
class SchemaManager(private val webClient: WebClient, private val logger: Logger) {

    fun getSchemaById(requestId: String, organisationId: String, schemaId: String): Mono<SchemaObject> {
        try {
            return webClient.get().uri("http://localhost:8082/${organisationId}/schemas/${schemaId}")
                .header("requestId", requestId).retrieve()
                .bodyToMono(SchemaObject::class.java)
        } catch (e: WebClientResponseException) {
            logger.info("data-entry-service, $requestId, end, ${System.currentTimeMillis()}")
            throw SchemaNotFoundException("Schema not found!")
        }
    }
}