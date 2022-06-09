package com.microservices.shared

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode

class RuleDeserializer() : JsonDeserializer<Rule>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Rule {
        try {
            val node = p.codec.readTree<JsonNode>(p)
            val id = if (node.has("\$id")) node.get("\$id").textValue() else null
            val name = node.get("name").textValue()
            val deviceId = if (node.has("deviceId")) node.get("deviceId").textValue() else null
            val deviceType = node.get("deviceType").textValue()
            val lhs = node.getLeftHandSide()
            val rhs = node.getRightHandSide()

            return Rule(id, name, deviceId, deviceType, lhs, rhs)
        } catch (e: Exception) {
            throw MyJsonProcessingException("Unable to deserialize")
        }
    }

    private fun JsonNode.getRightHandSide(): RuleRightHandSide {
        val rhsNode = this.get("rhs")
        val ruleAction = rhsNode.getRuleAction()
        val customValue = if (rhsNode.has("customValue")) rhsNode.get("customValue").textValue() else null

        return RuleRightHandSide(ruleAction, customValue)
    }

    private fun JsonNode.getLeftHandSide(): RuleLeftHandSide {
        val lhsNode = this.get("lhs")
        val operator = lhsNode.getRuleOperator()
        val left = lhsNode.get("field").textValue()
        val operatorValue =
            if (operator == RuleOperator.TRUE || operator == RuleOperator.FALSE) null else lhsNode.get("value")
                .getOperatorValue()

        return RuleLeftHandSide(operator, left, operatorValue)
    }

    private fun JsonNode.getRuleOperator(): RuleOperator {
        return when (this.get("operator").textValue()) {
            "eq" -> RuleOperator.EQUAL
            "gt" -> RuleOperator.GREATER
            "gte" -> RuleOperator.GREATER_EQUAL
            "ls" -> RuleOperator.LESS
            "lse" -> RuleOperator.LESS_EQUAL
            "true" -> RuleOperator.TRUE
            "false" -> RuleOperator.FALSE
            else -> throw UnknownPropertyTypeException("Unknown property type: ${this.textValue().lowercase()}")
        }
    }

    private fun JsonNode.getOperatorValue(): OperatorValue {
        val operatorType = when (this.get("type").textValue().lowercase()) {
            "constant" -> OperatorType.CONSTANT
            "variable" -> OperatorType.VARIABLE
            else -> throw UnknownPropertyTypeException("Unknown property type: ${this.textValue().lowercase()}")
        }

        if (operatorType == OperatorType.CONSTANT) {
            val valueType = when (this.get("valueType").textValue().lowercase()) {
                "integer" -> OperatorValueType.INTEGER
                "string" -> OperatorValueType.STRING
                "double" -> OperatorValueType.DOUBLE
                "boolean" -> OperatorValueType.BOOLEAN
                else -> throw UnknownPropertyTypeException("Unknown property type: ${this.textValue().lowercase()}")
            }

            return when (valueType) {
                OperatorValueType.INTEGER -> IntegerOperatorValue(this.get("value").intValue())
                OperatorValueType.STRING -> StringOperatorValue(this.get("value").textValue())
                OperatorValueType.DOUBLE -> DoubleOperatorValue(this.get("value").doubleValue())
                OperatorValueType.BOOLEAN -> BooleanOperatorValue(this.get("value").booleanValue())
            }
        }

        return VariableOperatorValue(this.get("value").textValue())
    }

    private fun JsonNode.getRuleAction(): RuleAction {
        val actionType = when (this.get("actionType").textValue().lowercase()) {
            "alarm" -> RuleActionType.ALARM
            else -> throw UnknownPropertyTypeException("Unknown property type: ${this.textValue().lowercase()}")
        }

        val details = if (this.has("actionDetails")) this.get("actionDetails").textValue() else ""

        return RuleAction(actionType, details)
    }
}