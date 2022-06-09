package com.microservices.shared

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

class SchemaDeserializer() : StdDeserializer<SchemaObject>(SchemaObject::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): SchemaObject {
        try {
            val node = p.codec.readTree<JsonNode>(p)
            val title = node.get("title").textValue()
            val id = if (node.has("\$id")) node.get("\$id").textValue() else null
            val description = if (node.has("description")) node.get("description").textValue() else null
            val properties = node.getSchemaProperties()

            return SchemaObject(title, description, properties, id)
        } catch (e: Exception) {
            throw MyJsonProcessingException("Unable to deserialize")
        }
    }

    private fun JsonNode.getSchemaProperties(): List<SchemaProperty> {
        val properties = this.get("properties")
        val requiredNames = this.get("required").map { it.textValue() }

        return properties.fields().asSequence()
            .map { it.value.getSingleProperty(it.key, requiredNames.contains(it.key)) }.toList()
    }

    private fun JsonNode.getSingleProperty(name: String, isRequired: Boolean): SchemaProperty {
        val type = this.get("type").toPropertyType()
        return SchemaProperty(name, type, isRequired)
    }

    private fun JsonNode.toPropertyType(): PropertyType {
        return when (this.textValue().lowercase()) {
            "integer" -> PropertyType.INTEGER
            "boolean" -> PropertyType.BOOLEAN
            "string" -> PropertyType.STRING
            "date" -> PropertyType.DATE
            "float" -> PropertyType.FLOAT
            else -> throw UnknownPropertyTypeException("Unknown property type: ${this.textValue().lowercase()}")
        }
    }
}