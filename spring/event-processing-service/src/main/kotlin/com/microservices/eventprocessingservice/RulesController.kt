package com.microservices.eventprocessingservice

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.microservices.shared.Rule
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rules")
class RulesController(
    private val ruleRepository: RuleRepository,
    private val rulesReloader: RulesReloader,
    private val objectMapper: ObjectMapper,
    private val droolsRuleTranslator: DroolsRuleTranslator
) {
    @PostMapping("/{organisationId}")
    fun addRule(@PathVariable("organisationId") organisationId: String, @RequestBody json: JsonNode) {
        val rule = objectMapper.treeToValue<Rule>(json)
        ruleRepository.storeRule(organisationId, rule)
        rulesReloader.reload(organisationId)
    }

    @GetMapping("/{organisationId}")
    fun getAllRules(@PathVariable("organisationId") organisationId: String): ResponseEntity<List<Rule>> {
        return ResponseEntity.ok(ruleRepository.getRulesByOrganisationId(organisationId))
    }
}