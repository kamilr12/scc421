package com.microservices.shared

import com.fasterxml.jackson.databind.JsonNode


data class SensorReadingMessage(
    val requestId: String,
    val deviceId: String,
    val schemaId: String,
    val content: JsonNode
)