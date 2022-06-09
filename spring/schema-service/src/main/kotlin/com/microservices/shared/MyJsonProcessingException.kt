package com.microservices.shared

import com.fasterxml.jackson.core.JsonProcessingException

class MyJsonProcessingException(msg: String) : JsonProcessingException(msg)