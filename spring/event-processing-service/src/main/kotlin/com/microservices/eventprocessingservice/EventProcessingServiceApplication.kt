package com.microservices.eventprocessingservice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.microservices.shared.*
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.kie.api.KieBase
import org.kie.api.KieServices
import org.kie.api.builder.KieFileSystem
import org.kie.api.conf.EventProcessingOption
import org.kie.api.runtime.KieContainer
import org.kie.api.runtime.KieSession
import org.kie.internal.io.ResourceFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.converter.JsonMessageConverter
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.reactive.function.client.WebClient
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.FileHandler
import java.util.logging.Formatter
import java.util.logging.LogRecord
import java.util.logging.Logger

@Configuration
@EnableKafka
@EnableScheduling
@SpringBootApplication
class EventProcessingServiceApplication(private val kafkaProperties: KafkaProperties) {

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
    fun predictionConsumerFactory(): DefaultKafkaConsumerFactory<String, Prediction> {
        val jsonDeserializer = JsonDeserializer<Prediction>()
        jsonDeserializer.addTrustedPackages("*")
        return DefaultKafkaConsumerFactory(
            kafkaProperties.buildConsumerProperties(),
            StringDeserializer(),
            null
        )
    }

    @Bean
    fun predictionKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Prediction> {
        return ConcurrentKafkaListenerContainerFactory<String, Prediction>().apply {
            consumerFactory = predictionConsumerFactory()
            setMessageConverter(JsonMessageConverter())
        }
    }

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

    /*@Bean(name = [KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME])
    fun kStreamsConfig(): KafkaStreamsConfiguration {
        val properties = mutableMapOf<String, Any>()
        properties[StreamsConfig.APPLICATION_ID_CONFIG] = "default-stream"
        properties[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        properties[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        properties[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name

        return KafkaStreamsConfiguration(properties)
    }*/

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder().build()
    }

    @Bean
    fun mongoClient(): MongoClient {
        return MongoClients.create("mongodb://root:password@host.docker.internal:27017")
    }

    @Bean
    fun kieFilesystem(): KieFileSystem {
        val kieServices = KieServices.Factory.get()
        val kieConfiguration = kieServices.newKieBaseConfiguration()
        kieConfiguration.setOption(EventProcessingOption.STREAM)

        val kieFileSystem = kieServices.newKieFileSystem()
        val mongoClient = mongoClient()
        val database = mongoClient.getDatabase("rules")
        val allCollections = database.listCollectionNames()
        val objectMapper = ObjectMapper().registerModule(SimpleModule().apply {
            addDeserializer(Rule::class.java, RuleDeserializer())
            addSerializer(Rule::class.java, RuleSerializer())
        })
        allCollections.forEach { collectionName ->
            val list = database.getCollection(collectionName).find()
                .map { objectMapper.readValue(it.toJson(), Rule::class.java) }
                .distinctBy { it.name }
                .toList()

            val content = DroolsRuleTranslator().organisationRulesToString(collectionName, list)

            System.out.println(content)

            val file = File("${collectionName}.drl")
            file.createNewFile()
            val fileWriter = FileWriter("${collectionName}.drl")
            fileWriter.write(content)
            fileWriter.close()
            kieFileSystem.write(ResourceFactory.newFileResource("${collectionName}.drl"))
            file.delete()
        }
        /*// TODO change this to call RuleService

        webClient().get().uri("http://localhost:8082/rules").retrieve().bodyToMono<List<RulesObject>>().block(Duration.ofSeconds(15)).also {
            if(it != null) {
                for(i in it.indices) {
                    val file = File("${it[i].organisationId}.drl")
                    file.createNewFile()
                    val fileWriter = FileWriter("${it[i].organisationId}.drl")
                    fileWriter.write(it[i].content)
                    fileWriter.close()
                    kieFileSystem.write(ResourceFactory.newFileResource("${it[i].organisationId}.drl"))
                    file.delete()
                }
            }
        }*/

        KieUtils.setKieFileSystem(kieFileSystem)
        return kieFileSystem
    }

    @Bean
    fun kieContainer(): KieContainer {
        val kieServices = KieServices.Factory.get()
        val kb = kieServices.newKieBuilder(kieFilesystem())
        kb.buildAll()
        val kieModule = kb.kieModule
        return kieServices.newKieContainer(kieModule.releaseId).also {
            KieUtils.setKieContainer(it)
        }
    }

    @Bean
    fun kieBase(): KieBase {
        return kieContainer().kieBase
    }

    @Bean
    fun kieSession(): KieSession {
        return kieContainer().newKieSession().also {
            KieUtils.setKieSession(it)
        }
    }

    @Bean
    fun ruleDeserializer(): SimpleModule {
        return SimpleModule().apply {
            addDeserializer(Rule::class.java, RuleDeserializer())
            addSerializer(Rule::class.java, RuleSerializer())
        }
    }

    @Bean
    fun logger(): Logger {
        val logger = Logger.getLogger("logger")

        val fileHandler =
            FileHandler(
                "logs/event-processing-service_${
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
    runApplication<EventProcessingServiceApplication>(*args)
}
