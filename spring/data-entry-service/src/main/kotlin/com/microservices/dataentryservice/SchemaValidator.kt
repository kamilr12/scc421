package com.microservices.dataentryservice

import com.fasterxml.jackson.databind.JsonNode
import com.microservices.shared.PropertyType
import com.microservices.shared.SchemaObject
import com.microservices.shared.SchemaProperty
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class SchemaValidator(private val schemaObject: SchemaObject) {
    fun validate(json: JsonNode, schemaId: String): List<String> {
        val errors = mutableListOf<String>()

        if (!schemaObject.id.equals(schemaId)) {
            throw ValidationException("Invalid schemaId!")
        }

        if (!json.has("deviceId")) {
            errors.add("Missing required property: deviceId")
        }

        schemaObject.properties.forEach {
            val error = json.has(it)
            if (error != "") {
                errors.add(error)
            }
        }

        if (errors.isNotEmpty()) {
            throw ValidationException(errors.joinToString(prefix = "[ ", postfix = " ]") { it })
        }
        return errors
    }

    private fun JsonNode.has(schemaProperty: SchemaProperty): String {
        if (!hasNonNull(schemaProperty.name) && schemaProperty.isRequired) {
            return "Missing required property: ${schemaProperty.name}"
        }

        if (hasNonNull(schemaProperty.name)) {
            val node = get(schemaProperty.name)

            if (!node.isType(schemaProperty.type)) {
                return "Wrong type for property: ${schemaProperty.name}, required: ${schemaProperty.type.name}"
            }
        }

        return ""
    }

    private fun JsonNode.isType(propertyType: PropertyType): Boolean {
        return when (propertyType) {
            PropertyType.FLOAT -> isFloatingPointNumber || isInt
            PropertyType.INTEGER -> isInt
            PropertyType.STRING -> isTextual
            PropertyType.BOOLEAN -> isBoolean
            PropertyType.DATE -> isDate()
        }
    }

    private fun JsonNode.isDate(): Boolean {
        if (isTextual) {
            try {
                LocalDateTime.parse(textValue())
                return true
            } catch (e: DateTimeParseException) {
                return false
            }
        }

        return false
    }
}