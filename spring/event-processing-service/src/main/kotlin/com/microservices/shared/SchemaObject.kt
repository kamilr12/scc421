package com.microservices.shared

import java.util.*

class SchemaObject(
    val title: String,
    val description: String?,
    val properties: List<SchemaProperty>,
    var id: String? = null,
) {
    fun setId() {
        id = UUID.randomUUID().toString()
    }
}