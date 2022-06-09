package com.microservices.dataservice

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.microservices.shared.SensorReadingMessage
import com.mongodb.client.MongoClient
import com.mongodb.client.model.Filters.eq
import org.bson.Document
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux

@Component
class DataRepository(private val mongoClient: MongoClient, private val objectMapper: ObjectMapper) {
    private val messageMap = mutableMapOf<String, MutableList<SensorReadingMessage>>()

    @Scheduled(fixedDelay = 5000)
    fun store() {
        val database = mongoClient.getDatabase("data-service")

        messageMap.forEach { it ->
            val documents = it.value.map {
                val document = Document.parse(it.content.toString())
                document.append("deviceId", it.deviceId)
            }

            if (documents.isNotEmpty()) {
                try {
                    database.createCollection(it.key)
                } catch (e: Exception) {
                    System.out.println("Collection exists!")
                }

                database.getCollection(it.key).insertMany(documents)
                messageMap[it.key] = mutableListOf()
            }
        }
    }

    fun saveData(organisationId: String, message: SensorReadingMessage) {
        val key = "${organisationId}.${message.schemaId}"

        if (!messageMap.containsKey(key)) {
            messageMap[key] = mutableListOf()
        }

        messageMap[key]!!.add(message)
    }

    fun getData(organisationId: String, schemaId: String, deviceId: String?): Flux<JsonNode> {
        val database = mongoClient.getDatabase("data-service")
        return if (deviceId != null) {
            database.getCollection("$organisationId.$schemaId").find(eq("deviceId", deviceId))
                .map {
                    it.remove("_id")
                    objectMapper.readValue(it.toJson(), JsonNode::class.java)
                }
                .toFlux()
        } else {
            database.getCollection("$organisationId.$schemaId").find()
                .map {
                    it.remove("_id")
                    objectMapper.readValue(it.toJson(), JsonNode::class.java)
                }
                .toFlux()
        }
    }
}