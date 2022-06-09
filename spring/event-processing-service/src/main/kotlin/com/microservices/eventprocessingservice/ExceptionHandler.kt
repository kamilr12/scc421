package com.microservices.eventprocessingservice

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.microservices.shared.ErrorMessage
import com.microservices.shared.MyJsonProcessingException
import org.springframework.core.codec.DecodingException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(MyJsonProcessingException::class)
    fun handleJsonProcessingException(e: MyJsonProcessingException): ResponseEntity<ErrorMessage> {
        return ResponseEntity(ErrorMessage(e.message), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<ErrorMessage> {
        return ResponseEntity(ErrorMessage(e.message), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}