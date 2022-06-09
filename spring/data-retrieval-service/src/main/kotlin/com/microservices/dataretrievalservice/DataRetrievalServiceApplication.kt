package com.microservices.dataretrievalservice

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@SpringBootApplication
class DataRetrievalServiceApplication {
	@Bean
	fun webClient(objectMapper: ObjectMapper): WebClient {
		val exchangeStrategies = ExchangeStrategies.builder()
			.codecs { it.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(objectMapper)) }
			.build()
		return WebClient.builder()
			.exchangeStrategies(exchangeStrategies)
			.build()
	}
}

fun main(args: Array<String>) {
	runApplication<DataRetrievalServiceApplication>(*args)
}
