package com.microservices.dataentryservice

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(e: ValidationException): ResponseEntity<ErrorMessage> {
        return ResponseEntity(ErrorMessage(e.message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(SchemaNotFoundException::class)
    fun handleSchemaNotFoundException(e: SchemaNotFoundException): ResponseEntity<ErrorMessage> {
        return ResponseEntity(ErrorMessage(e.message), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<ErrorMessage> {
        return ResponseEntity(ErrorMessage(e.message), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}