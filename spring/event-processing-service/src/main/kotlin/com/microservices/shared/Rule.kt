package com.microservices.shared

import java.util.*

data class Rule(
    var id: String? = null,
    val name: String,
    val deviceId: String? = null,
    val deviceType: String,
    val lhs: RuleLeftHandSide,
    val rhs: RuleRightHandSide
) {
    fun printRightSideOfWhen(): String {
        if (lhs.operator == RuleOperator.TRUE || lhs.operator == RuleOperator.FALSE) {
            return ""
        }

        return if (lhs.right != null) {
            if (lhs.right.type == OperatorType.CONSTANT) {
                when (lhs.right.valueType) {
                    OperatorValueType.INTEGER -> ((lhs.right.value) as Int).toString()
                    OperatorValueType.STRING -> "\"${((lhs.right.value) as String)}\""
                    OperatorValueType.DOUBLE -> ((lhs.right.value) as Double).toString()
                    OperatorValueType.BOOLEAN -> ((lhs.right.value) as Boolean).toString()
                }
            } else {
                "content[\"${lhs.right.value}\"]"
            }
        } else {
            ""
        }
    }

    fun setId() {
        id = UUID.randomUUID().toString()
    }
}