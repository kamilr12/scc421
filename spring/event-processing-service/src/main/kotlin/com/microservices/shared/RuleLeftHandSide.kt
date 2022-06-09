package com.microservices.shared

data class RuleLeftHandSide(val operator: RuleOperator, val left: String, val right: OperatorValue? = null)