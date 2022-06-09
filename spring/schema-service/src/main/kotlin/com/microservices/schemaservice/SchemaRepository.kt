package com.microservices.schemaservice

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.microservices.shared.SchemaObject
import com.mongodb.client.MongoClient
import com.mongodb.client.model.Filters.eq
import org.bson.Document
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

@Component
class SchemaRepository(private val mongoClient: MongoClient, private val objectMapper: ObjectMapper) {
    fun saveSchema(organisationId: String, schemaObject: SchemaObject): JsonNode {
        schemaObject.setId()
        val database = mongoClient.getDatabase("schema-service")
        try {
            database.createCollection(organisationId)
        } catch (e: Exception) {
            System.out.println("Collection exists!")
        }

        val document = Document.parse(objectMapper.writeValueAsString(schemaObject))
        database.getCollection(organisationId).insertOne(document)
        return ObjectMapper().readTree(document.toJson())
    }

    fun getSchemasByOrganisationId(organisationId: String): Flux<SchemaObject> {
        val database = mongoClient.getDatabase("schema-service")
        return database.getCollection(organisationId).find()
            .map { objectMapper.readValue(it.toJson(), SchemaObject::class.java) }.toFlux()
    }

    fun getSchemaById(organisationId: String, schemaId: String): Mono<SchemaObject> {
        val database = mongoClient.getDatabase("schema-service")
        return database.getCollection(organisationId).find(eq("\$id", schemaId))
            .map { objectMapper.readValue(it.toJson(), SchemaObject::class.java) }
            .asSequence().ifEmpty { throw SchemaNotFoundException("Schema not found") }.first().toMono()
    }
}