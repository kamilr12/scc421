package com.microservices.shared

import sun.awt.image.ImageRepresentation

enum class RuleOperator(val value: String) {
    EQUAL("=="),
    GREATER(">"),
    GREATER_EQUAL(">="),
    LESS("<"),
    LESS_EQUAL("<="),
    TRUE("== true"),
    FALSE("== false")
}