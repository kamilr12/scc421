package com.microservices.eventprocessingservice

import org.kie.api.KieServices
import org.kie.api.builder.KieFileSystem
import org.kie.api.runtime.KieContainer
import org.kie.api.runtime.KieSession

class KieUtils {
    companion object {
        private var kieContainer: KieContainer? = null
        private var kieSession: KieSession? = null
        private var kieFileSystem: KieFileSystem? = null

        fun setKieContainer(kieContainer: KieContainer) {
            KieUtils.kieContainer = kieContainer
            kieSession = kieContainer.newKieSession()
        }

        fun getKieContainer(): KieContainer? = kieContainer

        fun getKieSession(): KieSession? = kieSession

        fun setKieSession(kieSession: KieSession) {
            KieUtils.kieSession = kieSession
        }

        fun getKieFileSystem(): KieFileSystem? = kieFileSystem

        fun setKieFileSystem(kieFileSystem: KieFileSystem) {
            KieUtils.kieFileSystem = kieFileSystem
        }

        fun getKieServices(): KieServices = KieServices.Factory.get()
    }
}