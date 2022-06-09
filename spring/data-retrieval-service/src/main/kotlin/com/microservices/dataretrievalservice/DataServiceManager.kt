package com.microservices.dataretrievalservice

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Flux

@Component
class DataServiceManager(private val webClient: WebClient) {
    fun getDataByOrgId(organisationId: String, schemaId: String, deviceId: String?): Flux<JsonNode> {
        var uri = "http://localhost:8081/data/$organisationId/schema/$schemaId"

        if(deviceId != null) {
            uri = "$uri?deviceId=$deviceId"
        }

        return webClient.get().uri(uri).retrieve().bodyToFlux(JsonNode::class.java)
    }
}