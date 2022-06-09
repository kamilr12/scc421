package com.microservices.dataservice

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@RequestMapping("data/{organisationId}")
class DataController(private val dataRepository: DataRepository) {
    @GetMapping("/schema/{schemaId}")
    fun getData(
        @PathVariable("organisationId") organisationId: String,
        @PathVariable("schemaId") schemaId: String,
        @RequestParam("deviceId", required = false) deviceId: String?
    ): Flux<JsonNode> {
        return dataRepository.getData(organisationId, schemaId, deviceId)
    }
}