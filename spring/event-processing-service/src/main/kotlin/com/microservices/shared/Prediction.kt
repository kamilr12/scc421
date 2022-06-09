package com.microservices.shared

data class Prediction(
    val organisationId: String,
    val requestId: String,
    val deviceId: String,
    val prediction: String
)