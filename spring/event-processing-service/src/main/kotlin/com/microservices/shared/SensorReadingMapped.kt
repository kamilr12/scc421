package com.microservices.shared

class SensorReadingMapped(
    val organisationId: String,
    val sensorType: String,
    val sensorId: String,
    val content: Map<String, Any>
)