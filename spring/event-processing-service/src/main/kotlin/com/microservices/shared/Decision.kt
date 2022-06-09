package com.microservices.shared

import java.util.*

class Decision(
    var myId: String? = null,
    val actionType: String,
    val actionDetails: String,
    val ruleName: String,
    val deviceId: String
) {
    fun setId() {
        myId = UUID.randomUUID().toString()
    }
}