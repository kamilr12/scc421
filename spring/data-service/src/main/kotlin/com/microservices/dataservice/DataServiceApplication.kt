package com.microservices.dataservice

import com.microservices.shared.SensorReadingMessage
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.scheduling.annotation.EnableScheduling
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.FileHandler
import java.util.logging.Formatter
import java.util.logging.LogRecord
import java.util.logging.Logger

@EnableScheduling
@SpringBootApplication
class DataServiceApplication(private val kafkaProperties: KafkaProperties) {
    @Bean
    fun consumerFactory(): DefaultKafkaConsumerFactory<String, SensorReadingMessage> {
        val jsonDeserializer = JsonDeserializer<SensorReadingMessage>()
        jsonDeserializer.addTrustedPackages("*")
        return DefaultKafkaConsumerFactory(
            kafkaProperties.buildConsumerProperties(),
            StringDeserializer(),
            jsonDeserializer
        )
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, SensorReadingMessage> {
        return ConcurrentKafkaListenerContainerFactory<String, SensorReadingMessage>().apply {
            consumerFactory = consumerFactory()
        }
    }

    @Bean
    fun logger(): Logger {
        val logger = Logger.getLogger("logger")

        val fileHandler =
            FileHandler(
                "logs/data-service_${
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
    runApplication<DataServiceApplication>(*args)
}
