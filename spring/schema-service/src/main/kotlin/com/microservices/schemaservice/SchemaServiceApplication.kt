package com.microservices.schemaservice

import com.fasterxml.jackson.databind.module.SimpleModule
import com.microservices.shared.SchemaDeserializer
import com.microservices.shared.SchemaObject
import com.microservices.shared.SchemaSerializer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.FileHandler
import java.util.logging.Formatter
import java.util.logging.LogRecord
import java.util.logging.Logger

@SpringBootApplication
class SchemaServiceApplication {
    @Bean
    fun schemaObjectDeserializer(): SimpleModule {
        return SimpleModule().apply {
            addDeserializer(SchemaObject::class.java, SchemaDeserializer())
            addSerializer(SchemaObject::class.java, SchemaSerializer())
        }
    }

    @Bean
    fun logger(): Logger {
        val logger = Logger.getLogger("logger")

        val fileHandler =
            FileHandler(
                "logs/schema-service_${
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd--HH-mm"))
                }.log"
            )
        logger.addHandler(fileHandler)
        fileHandler.formatter = object : Formatter() {
            override fun format(record: LogRecord): String {
                return "${record.message}\n"
            }
        }
        logger.useParentHandlers = false
        return logger
    }
}

fun main(args: Array<String>) {
    runApplication<SchemaServiceApplication>(*args)
}
