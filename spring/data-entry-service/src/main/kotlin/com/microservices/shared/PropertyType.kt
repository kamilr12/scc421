package com.microservices.shared

import java.lang.Exception

enum class PropertyType {
    INTEGER,
    STRING,
    FLOAT,
    BOOLEAN,
    DATE
}

class UnknownPropertyTypeException(msg: String): Exception(msg)