package com.microservices.shared

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

class SchemaSerializer() : StdSerializer<SchemaObject>(SchemaObject::class.java) {
    override fun serialize(schemaObject: SchemaObject, jsonGenerator: JsonGenerator, p2: SerializerProvider) {
        jsonGenerator.writeStartObject()
        if (schemaObject.id != null) jsonGenerator.writeStringField("\$id", schemaObject.id)
        jsonGenerator.writeStringField("title", schemaObject.title)
        jsonGenerator.writeStringField("description", schemaObject.description)
        jsonGenerator.writeSchemaProperties(schemaObject)
    }

    private fun JsonGenerator.writeSchemaProperties(schemaObject: SchemaObject) {
        writeFieldName("properties")
        writeStartObject()
        schemaObject.properties.forEach { writeProperty(it) }
        writeEndObject()
        writeFieldName("required")
        writeArray(
            schemaObject.properties.filter { it.isRequired }.map { it.name }.toTypedArray(),
            0,
            schemaObject.properties.count { it.isRequired }
        )
    }

    private fun JsonGenerator.writeProperty(property: SchemaProperty) {
        writeFieldName(property.name)
        writeStartObject()
        writePropertyType(property.type)
        writeEndObject()
    }

    private fun JsonGenerator.writePropertyType(type: PropertyType) {
        val value = when (type) {
            PropertyType.INTEGER -> "integer"
            PropertyType.STRING -> "string"
            PropertyType.FLOAT -> "float"
            PropertyType.BOOLEAN -> "boolean"
            PropertyType.DATE -> "date"
        }

        writeStringField("type", value)
    }
}