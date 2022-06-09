package com.microservices.eventprocessingservice

import com.fasterxml.jackson.databind.ObjectMapper
import com.microservices.shared.Rule
import com.mongodb.client.MongoClient
import org.bson.Document
import org.springframework.stereotype.Component

@Component
class RuleRepository(private val mongoClient: MongoClient, private val objectMapper: ObjectMapper) {
    fun storeRule(organisationId: String, rule: Rule) {
        val database = mongoClient.getDatabase("rules")
        rule.setId()
        try {
            database.createCollection(organisationId)
        } catch (e: Exception) {
            System.out.println("Collection exists!")
        }

        val document = Document.parse(objectMapper.writeValueAsString(rule))
        database.getCollection(organisationId).insertOne(document)
    }

    fun getAllRules(): List<Rule> {
        val database = mongoClient.getDatabase("rules")
        val allCollections = database.listCollectionNames()
        val rules = mutableListOf<Rule>()
        allCollections.forEach {
            rules.addAll(database.getCollection(it).find().map { objectMapper.readValue(it.toJson(), Rule::class.java) }
                .toList())
        }

        return rules
    }

    fun getRulesByOrganisationId(organisationId: String): List<Rule> {
        val database = mongoClient.getDatabase("rules")
        return database.getCollection(organisationId).find()
            .map { objectMapper.readValue(it.toJson(), Rule::class.java) }.toList()
    }
}