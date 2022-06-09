package com.microservices.shared

abstract class OperatorValue(val type: OperatorType, val valueType: OperatorValueType, open val value: Any)

class IntegerOperatorValue(override val value: Int) :
    OperatorValue(OperatorType.CONSTANT, OperatorValueType.INTEGER, value)

class StringOperatorValue(override val value: String) :
    OperatorValue(OperatorType.CONSTANT, OperatorValueType.STRING, value)

class DoubleOperatorValue(override val value: Double) :
    OperatorValue(OperatorType.CONSTANT, OperatorValueType.DOUBLE, value)

class BooleanOperatorValue(override val value: Boolean) :
    OperatorValue(OperatorType.CONSTANT, OperatorValueType.BOOLEAN, value)

class VariableOperatorValue(override val value: String) :
    OperatorValue(OperatorType.VARIABLE, OperatorValueType.STRING, value)

enum class OperatorType {
    CONSTANT,
    VARIABLE
}

enum class OperatorValueType {
    INTEGER,
    STRING,
    DOUBLE,
    BOOLEAN
}