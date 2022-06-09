package com.microservices.shared

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

class RuleSerializer() : StdSerializer<Rule>(Rule::class.java) {
    override fun serialize(rule: Rule, jsonGenerator: JsonGenerator, p2: SerializerProvider) {
        jsonGenerator.writeStartObject()
        if (rule.id != null) jsonGenerator.writeStringField("\$id", rule.id)
        jsonGenerator.writeStringField("name", rule.name)
        if (rule.deviceId != null) jsonGenerator.writeStringField("deviceId", rule.deviceId)
        jsonGenerator.writeStringField("deviceType", rule.deviceType)
        jsonGenerator.writeLhs(rule)
        jsonGenerator.writeRhs(rule)
        jsonGenerator.writeEndObject()
    }

    private fun JsonGenerator.writeRhs(rule: Rule) {
        writeFieldName("rhs")
        writeStartObject()
        writeRuleAction(rule.rhs.action)
        if (rule.rhs.customValue != null) {
            writeStringField("customValue", rule.rhs.customValue)
        }
        writeEndObject()
    }

    private fun JsonGenerator.writeRuleAction(ruleAction: RuleAction) {
        val value = when (ruleAction.ruleActionType) {
            RuleActionType.ALARM -> "alarm"
        }

        writeStringField("actionType", value)
        writeStringField("actionDetails", ruleAction.details)
    }

    private fun JsonGenerator.writeLhs(rule: Rule) {
        writeFieldName("lhs")
        writeStartObject()
        writeRuleOperator(rule.lhs.operator)
        writeStringField("field", rule.lhs.left)
        writeFieldName("value")
        writeStartObject()
        writeOperatorValue(rule.lhs.right)
        writeEndObject()
        writeEndObject()
    }

    private fun JsonGenerator.writeRuleOperator(operator: RuleOperator) {
        val value = when (operator) {
            RuleOperator.EQUAL -> "eq"
            RuleOperator.GREATER -> "gt"
            RuleOperator.GREATER_EQUAL -> "gte"
            RuleOperator.LESS -> "ls"
            RuleOperator.LESS_EQUAL -> "lse"
            RuleOperator.TRUE -> "true"
            RuleOperator.FALSE -> "false"
        }

        writeStringField("operator", value)
    }

    private fun JsonGenerator.writeOperatorValue(value: OperatorValue?) {
        if (value == null) return

        if (value.type == OperatorType.CONSTANT) {
            writeStringField("type", "constant")
            writeOperatorValueType(value.valueType)
            writeValue(value)
        } else {
            writeStringField("type", "variable")
            writeStringField("value", (value as VariableOperatorValue).value)
        }
    }

    private fun JsonGenerator.writeOperatorValueType(valueType: OperatorValueType) {
        val value = when (valueType) {
            OperatorValueType.INTEGER -> "integer"
            OperatorValueType.STRING -> "string"
            OperatorValueType.DOUBLE -> "double"
            OperatorValueType.BOOLEAN -> "boolean"
        }

        writeStringField("valueType", value)
    }

    private fun JsonGenerator.writeValue(value: OperatorValue) {
        when (value) {
            is IntegerOperatorValue -> writeNumberField("value", value.value)
            is DoubleOperatorValue -> writeNumberField("value", value.value)
            is BooleanOperatorValue -> writeBooleanField("value", value.value)
            is StringOperatorValue -> writeStringField("value", value.value)
        }
    }
}