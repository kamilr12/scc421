package com.microservices.eventprocessingservice

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.microservices.shared.Decision
import com.mongodb.client.MongoClient
import org.bson.Document
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux

@Component
class DecisionRepository(private val mongoClient: MongoClient, private val objectMapper: ObjectMapper) {
    private val decisionsMap = mutableMapOf<String, MutableList<Decision>>()

    fun store() {

        val database = mongoClient.getDatabase("decisions")

        decisionsMap.forEach { it ->
            if (it.value.size > 500) {
                val documents = it.value.map {
                    val document = Document.parse(objectMapper.writeValueAsString(it))
                    document.append("deviceId", it.deviceId)
                }

                if (documents.isNotEmpty()) {
                    try {
                        database.createCollection(it.key)
                    } catch (e: Exception) {
                        System.out.println("Collection exists!")
                    }

                    database.getCollection(it.key).insertMany(documents)
                    decisionsMap[it.key]!!.clear()
                }
            }
        }
    }

    fun storeDecision(organisationId: String, decision: Decision) {
        if (!decisionsMap.containsKey(organisationId)) {
            decisionsMap[organisationId] = mutableListOf()
        }

        decision.setId()
        decisionsMap[organisationId]!!.add(decision)
        store()
    }

    fun getData(organisationId: String): Flux<JsonNode> {
        val database = mongoClient.getDatabase("decisions")

        return database.getCollection(organisationId).find()
            .map {
                it.remove("_id")
                objectMapper.readValue(it.toJson(), JsonNode::class.java)
            }
            .toFlux()
    }
}