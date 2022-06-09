package com.microservices.schemaservice

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.microservices.shared.SchemaObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.logging.Logger

@RestController
@RequestMapping("{organisationId}/schemas")
class SchemaController(
    private val schemaRepository: SchemaRepository,
    private val objectMapper: ObjectMapper,
    private val logger: Logger
) {
    
    @PostMapping
    fun saveSchema(
        @PathVariable("organisationId") organisationId: String,
        @RequestBody schemaObject: SchemaObject
    ): ResponseEntity<JsonNode> {
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(schemaObject))
        val savedNode = schemaRepository.saveSchema(organisationId, schemaObject)
        return ResponseEntity.ok(savedNode)
    }

    @GetMapping
    fun getAllSchemas(@PathVariable("organisationId") organisationId: String): ResponseEntity<Flux<SchemaObject>> {
        return ResponseEntity.ok(schemaRepository.getSchemasByOrganisationId(organisationId))
    }

    @GetMapping("/{schemaId}")
    fun getSchema(
        @PathVariable("organisationId") organisationId: String,
        @PathVariable("schemaId") schemaId: String,
        @RequestHeader(value = "requestId", required = false) requestId: String?
    ): Mono<SchemaObject> {
        if (requestId != null) {
            logger.info("schema-service, $requestId, start, ${System.currentTimeMillis()}")
        }
        val schema = schemaRepository.getSchemaById(organisationId, schemaId)
        if (requestId != null) {
            logger.info("schema-service, $requestId, end, ${System.currentTimeMillis()}")
        }
        return schema
    }
}