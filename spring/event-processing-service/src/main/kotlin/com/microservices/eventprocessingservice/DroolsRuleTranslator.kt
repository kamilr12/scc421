package com.microservices.eventprocessingservice

import com.microservices.shared.Rule
import org.springframework.stereotype.Service

@Service
class DroolsRuleTranslator {

    fun organisationRulesToString(organisationId: String, list: List<Rule>): String {
        val stringBuilder = StringBuilder()
        stringBuilder.appendWithNewLine("package rules")
        stringBuilder.appendWithNewLine("import com.microservices.shared.SensorReadingMapped")
        stringBuilder.appendWithNewLine("import com.microservices.eventprocessingservice.DecisionRepository")
        stringBuilder.appendWithNewLine("import com.microservices.eventprocessingservice.DecisionPublisher")
        stringBuilder.appendWithNewLine("import com.microservices.shared.Decision")
        stringBuilder.appendWithNewLine("import com.microservices.shared.Prediction")
        stringBuilder.append("\n")
        stringBuilder.appendWithNewLine("global DecisionRepository decisionRepository")
        stringBuilder.appendWithNewLine("global DecisionPublisher decisionPublisher")
        stringBuilder.append("\n")
        stringBuilder.appendWithNewLine("declare SensorReadingMapped")
        stringBuilder.appendWithNewLine("\t@role(event)")
        stringBuilder.appendWithNewLine("end")
        stringBuilder.append("\n")
        stringBuilder.appendWithNewLine("declare window LastReadings")
        stringBuilder.appendWithNewLine("\tSensorReadingMapped() over window:length(20) from entry-point \"ReadingStream\"")
        stringBuilder.appendWithNewLine("end")
        stringBuilder.append("\n")
        stringBuilder.appendWithNewLine("declare Prediction")
        stringBuilder.appendWithNewLine("\t@role(event)")
        stringBuilder.appendWithNewLine("end")
        stringBuilder.append("\n")
        stringBuilder.appendWithNewLine("declare window LastPredictions")
        stringBuilder.appendWithNewLine("\tPrediction() over window:length(20) from entry-point \"PredictionStream\"")
        stringBuilder.appendWithNewLine("end")
        stringBuilder.append("\n")
        stringBuilder.appendWithNewLine("rule \"${organisationId}-prediction\"")
        stringBuilder.appendWithNewLine("\tno-loop true")
        stringBuilder.appendWithNewLine("\tlock-on-active true")
        stringBuilder.appendWithNewLine("\tsalience 1")
        stringBuilder.appendWithNewLine("\twhen")
        stringBuilder.appendWithNewLine("\t\t\$prediction : Prediction(organisationId == \"${organisationId}\", prediction == \"Alarm\") from entry-point \"PredictionStream\"")
        stringBuilder.appendWithNewLine("\tthen")
        stringBuilder.appendWithNewLine("\t\tSystem.out.println(\"xddddddddddd\");")
        stringBuilder.appendWithNewLine("\t\tDecision decision = new Decision(\"\", \"PREDICTION\", \"do:send_email;to:rogoda.kamil@gmail.com;topic:test alarm message;content:please it is urgent, \$prediction.getDeviceId()\", drools.getRule().getName(), \$prediction.getDeviceId());")
        stringBuilder.appendWithNewLine("\t\tdecisionPublisher.publishDecision(\"${organisationId}\", decision);")
        stringBuilder.appendWithNewLine("\t\tdecisionRepository.storeDecision(\"${organisationId}\", decision);")
        stringBuilder.appendWithNewLine("end")
        list.forEach { stringBuilder.appendRule(organisationId, it) }
        return stringBuilder.toString()
    }

    fun StringBuilder.appendWithNewLine(value: String) {
        this.append(value + "\n")
    }

    fun StringBuilder.appendRule(organisationId: String, rule: Rule) {
        this.append("\n")
        if (rule.deviceId != null) {
            this.appendWithNewLine("rule \"${organisationId}-${rule.deviceType}-${rule.deviceId}-${rule.name}\"")
        } else {
            this.appendWithNewLine("rule \"${organisationId}-${rule.deviceType}-${rule.name}\"")
        }
        this.appendWithNewLine("\tno-loop true")
        this.appendWithNewLine("\tlock-on-active true")
        this.appendWithNewLine("\tsalience 1")
        this.appendWithNewLine("\twhen")

        if (rule.deviceId != null) {
            this.appendWithNewLine("\t\t\$reading : SensorReadingMapped(organisationId == \"${organisationId}\", sensorType == \"${rule.deviceType}\", sensorId == \"${rule.deviceId}\", content[\"${rule.lhs.left}\"] ${rule.lhs.operator.value} ${rule.printRightSideOfWhen()}) from entry-point \"ReadingStream\"")
        } else {
            this.appendWithNewLine("\t\t\$reading : SensorReadingMapped(organisationId == \"${organisationId}\", sensorType == \"${rule.deviceType}\", content[\"${rule.lhs.left}\"] ${rule.lhs.operator.value} ${rule.printRightSideOfWhen()}) from entry-point \"ReadingStream\"")
        }
        this.appendWithNewLine("\tthen")
        this.appendWithNewLine("\t\tDecision decision = new Decision(\"\", \"${rule.rhs.action.ruleActionType.name}\", \"${rule.rhs.action.details}\", drools.getRule().getName(), \$reading.getSensorId());")
        this.appendWithNewLine("\t\tdecisionPublisher.publishDecision(\"${organisationId}\", decision);")
        this.appendWithNewLine("\t\tdecisionRepository.storeDecision(\"${organisationId}\", decision);")
        this.appendWithNewLine("end")
    }
}