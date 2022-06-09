package com.microservices.eventprocessingservice

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("decisions/{organisationId}")
class DecisionController(private val decisionRepository: DecisionRepository) {
    @GetMapping
    fun getData(
        @PathVariable("organisationId") organisationId: String,
    ): Flux<JsonNode> {
        return decisionRepository.getData(organisationId)
    }
}