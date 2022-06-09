package com.microservices.shared

class RuleAction(val ruleActionType: RuleActionType, val details: String)

enum class RuleActionType {
    ALARM
}