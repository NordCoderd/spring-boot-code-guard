package com.example.demo.a

import dev.protsenko.codeguard.core.springBootRules
import kotlin.test.Test

class SpringBootCodeGuardTest {
    @Test
    fun `spring-boot-code-guard all rules`() {
        springBootRules {
            moduleName = "module-a"
            all()
        }.verify()
    }
}
