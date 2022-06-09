package com.microservices.schemaservice

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(SchemaNotFoundException::class)
    fun handleSchemaNotFoundException(e: SchemaNotFoundException): ResponseEntity<ErrorMessage> {
        return ResponseEntity(ErrorMessage(e.message), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<ErrorMessage> {
        return ResponseEntity(ErrorMessage(e.message), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}