package com.microservices.dataentryservice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.microservices.shared.SchemaDeserializer
import com.microservices.shared.SchemaObject
import com.microservices.shared.SchemaSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.FileHandler
import java.util.logging.Formatter
import java.util.logging.LogRecord
import java.util.logging.Logger

@SpringBootApplication
class DataEntryServiceApplication(private val kafkaProperties: KafkaProperties) {
    @Bean
    fun producerConfigs(): Map<String, Any> {
        return HashMap(kafkaProperties.buildProducerProperties()).apply {
            this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
            this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        }
    }

    @Bean
    fun producerFactory() = DefaultKafkaProducerFactory<String, Any>(producerConfigs())

    @Bean
    fun kafkaTemplate() = KafkaTemplate(producerFactory())

    @Bean
    fun schemaObjectDeserializer(): SimpleModule {
        return SimpleModule().apply {
            addDeserializer(SchemaObject::class.java, SchemaDeserializer())
            addSerializer(SchemaObject::class.java, SchemaSerializer())
        }
    }

    @Bean
    fun webClient(objectMapper: ObjectMapper): WebClient {
        val exchangeStrategies = ExchangeStrategies.builder()
            .codecs { it.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper)) }
            .build()
        return WebClient.builder()
            .exchangeStrategies(exchangeStrategies)
            .build()
    }

    @Bean
    fun logger(): Logger {
        val logger = Logger.getLogger("logger")

        val fileHandler =
            FileHandler(
                "logs/data-entry-service_${
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
    runApplication<DataEntryServiceApplication>(*args)
}
