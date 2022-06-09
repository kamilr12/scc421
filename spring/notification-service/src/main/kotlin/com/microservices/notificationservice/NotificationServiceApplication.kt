package com.microservices.notificationservice

import com.microservices.shared.Decision
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*


@SpringBootApplication
class NotificationServiceApplication(private val kafkaProperties: KafkaProperties) {
	@Bean
	fun consumerFactory(): DefaultKafkaConsumerFactory<String, Decision> {
		val jsonDeserializer = JsonDeserializer<Decision>()
		jsonDeserializer.addTrustedPackages("*")
		return DefaultKafkaConsumerFactory(
			kafkaProperties.buildConsumerProperties(),
			StringDeserializer(),
			jsonDeserializer
		)
	}

	@Bean
	fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Decision> {
		return ConcurrentKafkaListenerContainerFactory<String, Decision>().apply {
			consumerFactory = consumerFactory()
		}
	}

}

fun main(args: Array<String>) {
	runApplication<NotificationServiceApplication>(*args)
}
