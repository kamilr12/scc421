package com.microservices.notificationservice

import com.microservices.shared.Decision
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors


@Component
class DecisionListener(private val mailSender: JavaMailSender) {
    val ruleToTimestamp = mutableMapOf<String, LocalDateTime>()
    private val emailThreadPool = Executors.newScheduledThreadPool(30);

    @KafkaListener(topics = ["decisions"], containerFactory = "kafkaListenerContainerFactory")
    fun listenForDecisions(
        consumerRecord: ConsumerRecord<String, Decision>,
        @Payload message: Decision
    ) {
        val details = message.actionDetails.split(";")

        if(details.size > 1 && details[0].split(":")[1] == "send_email") {
            val mimeMessage = mailSender.createMimeMessage()
            val messageHelper = MimeMessageHelper(mimeMessage, true)

            messageHelper.setTo(details[1].split(":")[1])
            messageHelper.setSubject(details[2].split(":")[1])
            messageHelper.setText(details[3].split(":")[1])
            emailThreadPool.submit {
                if(ruleToTimestamp.containsKey(message.ruleName) && ChronoUnit.MINUTES.between(ruleToTimestamp[message.ruleName], LocalDateTime.now()) < 5) {
                    return@submit
                }
                ruleToTimestamp[message.ruleName] = LocalDateTime.now()
                mailSender.send(mimeMessage)
            }
        }
    }
}