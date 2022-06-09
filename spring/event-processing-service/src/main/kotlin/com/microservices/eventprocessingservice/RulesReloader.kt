package com.microservices.eventprocessingservice

import org.kie.internal.io.ResourceFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileWriter

@Service
class RulesReloader(private val ruleRepository: RuleRepository) {
    fun reload(organisationId: String) {
        val rules =
            ruleRepository.getRulesByOrganisationId(organisationId).distinctBy { it.name }.toList()
        val content = DroolsRuleTranslator().organisationRulesToString(organisationId, rules)

        System.out.println(content)

        val kieServices = KieUtils.getKieServices()
        val kieFileSystem = KieUtils.getKieFileSystem()
        val file = File("$organisationId.drl")
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        val fileWriter = FileWriter("$organisationId.drl")
        fileWriter.write(content)
        fileWriter.close()
        kieFileSystem?.delete("$organisationId.drl")
        kieFileSystem?.write(ResourceFactory.newFileResource("$organisationId.drl"))
        file.delete()
        val kb = kieServices.newKieBuilder(kieFileSystem)
        kb.buildAll()
        val kieModule = kb.kieModule
        val kieContainer = kieServices.newKieContainer(kieModule.releaseId).also {
            KieUtils.setKieContainer(it)
        }

        kieContainer.newKieSession().also {
            KieUtils.setKieSession(it)
        }
    }
}